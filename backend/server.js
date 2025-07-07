// Adoption & Child Care Backend Server
const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const multer = require('multer');
const helmet = require('helmet');
require('dotenv').config();
const path = require('path');

const app = express();
const PORT = process.env.PORT || 5000;
const SECRET = process.env.JWT_SECRET || 'your_jwt_secret';

app.use(cors());
app.use(helmet());
app.use(express.json());
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// MySQL connection
const db = mysql.createConnection({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASS || '',
  database: process.env.DB_NAME || 'adoption_child_care'
});

db.connect(err => {
  if (err) throw err;
  console.log('Connected to MySQL');
});

// Multer setup for file uploads
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, path.join(__dirname, 'uploads'));
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + path.extname(file.originalname));
  }
});
const upload = multer({ storage: storage });

// JWT middleware
function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.sendStatus(401);
  jwt.verify(token, SECRET, (err, user) => {
    if (err) return res.sendStatus(403);
    req.user = user;
    next();
  });
}

// --- AUTH ---
app.post('/register', async (req, res) => {
  const { username, password, role, email } = req.body;
  const password_hash = await bcrypt.hash(password, 10);
  db.query('INSERT INTO users (username, password_hash, role, email) VALUES (?, ?, ?, ?)',
    [username, password_hash, role, email],
    (err, result) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ id: result.insertId, username, role, email });
    });
});

app.post('/login', (req, res) => {
  const { username, password } = req.body;
  db.query('SELECT * FROM users WHERE username = ?', [username], async (err, results) => {
    if (err || results.length === 0) return res.status(401).json({ error: 'Invalid credentials' });
    const user = results[0];
    const valid = await bcrypt.compare(password, user.password_hash);
    if (!valid) return res.status(401).json({ error: 'Invalid credentials' });
    const token = jwt.sign({ user_id: user.user_id, role: user.role }, SECRET, { expiresIn: '1h' });
    res.json({ token });
  });
});

// --- CRUD Endpoints for All Tables ---
// Helper to generate CRUD endpoints
dbTables = [
  { name: 'children', pk: 'child_id' },
  { name: 'guardians', pk: 'guardian_id' },
  { name: 'court_cases', pk: 'case_id' },
  { name: 'placements', pk: 'placement_id' },
  { name: 'medical_records', pk: 'record_id' },
  { name: 'case_reports', pk: 'report_id' },
  { name: 'money_records', pk: 'money_id' },
  { name: 'education_records', pk: 'record_id' },
  { name: 'documents', pk: 'document_id' },
  { name: 'users', pk: 'user_id' },
  { name: 'audit_logs', pk: 'log_id' },
  { name: 'permissions', pk: 'permission_id' },
  { name: 'user_permissions', pk: 'user_id' }
];

dbTables.forEach(table => {
  // Get all
  app.get(`/${table.name}`, authenticateToken, (req, res) => {
    db.query(`SELECT * FROM ${table.name}`, (err, results) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json(results);
    });
  });
  // Get one
  app.get(`/${table.name}/:id`, authenticateToken, (req, res) => {
    db.query(`SELECT * FROM ${table.name} WHERE ${table.pk} = ?`, [req.params.id], (err, results) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json(results[0]);
    });
  });
  // Create
  app.post(`/${table.name}`, authenticateToken, (req, res) => {
    db.query(`INSERT INTO ${table.name} SET ?`, req.body, (err, result) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ id: result.insertId, ...req.body });
    });
  });
  // Update
  app.put(`/${table.name}/:id`, authenticateToken, (req, res) => {
    db.query(`UPDATE ${table.name} SET ? WHERE ${table.pk} = ?`, [req.body, req.params.id], (err) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ message: 'Updated' });
    });
  });
  // Delete
  app.delete(`/${table.name}/:id`, authenticateToken, (req, res) => {
    db.query(`DELETE FROM ${table.name} WHERE ${table.pk} = ?`, [req.params.id], (err) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ message: 'Deleted' });
    });
  });
});

// --- File Upload for Documents ---
app.post('/documents/upload', authenticateToken, upload.single('file'), (req, res) => {
  const { child_id } = req.body;
  const file = req.file;
  if (!file) return res.status(400).json({ error: 'No file uploaded' });
  db.query(
    'INSERT INTO documents (child_id, file_name, file_type, file_path) VALUES (?, ?, ?, ?)',
    [child_id, file.originalname, file.mimetype, file.path],
    (err, result) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ id: result.insertId, file: file.filename });
    }
  );
});

// --- Get Children by Guardian (Stored Procedure) ---
app.get('/children/by-guardian/:guardianId', authenticateToken, (req, res) => {
  db.query('CALL GetChildrenByGuardian(?)', [req.params.guardianId], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(results[0]);
  });
});
// --- Permissions Management ---
app.post('/permissions/assign', authenticateToken, (req, res) => {
  const { user_id, permission_id } = req.body;
  db.query('INSERT INTO user_permissions (user_id, permission_id) VALUES (?, ?)', [user_id, permission_id], (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ message: 'Permission assigned' });
  });
});
app.delete('/permissions/revoke', authenticateToken, (req, res) => {
  const { user_id, permission_id } = req.body;
  db.query('DELETE FROM user_permissions WHERE user_id = ? AND permission_id = ?', [user_id, permission_id], (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ message: 'Permission revoked' });
  });
});
// --- Get User Permissions ---
app.get('/users/:id/permissions', authenticateToken, (req, res) => {
  db.query('SELECT p.* FROM permissions p JOIN user_permissions up ON p.permission_id = up.permission_id WHERE up.user_id = ?', [req.params.id], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(results);
  });
});
// --- Get Audit Logs for a Table/Record ---
app.get('/audit_logs/:table/:record_id', authenticateToken, (req, res) => {
  db.query('SELECT * FROM audit_logs WHERE table_name = ? AND record_id = ?', [req.params.table, req.params.record_id], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(results);
  });
});

// --- Error Handling Middleware ---
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: 'Something went wrong!' });
});

app.listen(PORT, () => {
  console.log(`Backend server running on port ${PORT}`);
}); 