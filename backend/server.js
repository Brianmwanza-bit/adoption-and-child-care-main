// Adoption & Child Care Backend Server
const express = require('express');
const sqlite3 = require('sqlite3').verbose();
const cors = require('cors');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const multer = require('multer');
const helmet = require('helmet');
require('dotenv').config();
const path = require('path');

const app = express();
const PORT = 3000;
const SECRET = process.env.JWT_SECRET || 'your_jwt_secret';

app.use(cors());
app.use(helmet());
app.use(express.json());
app.use(express.static(path.join(__dirname, '../src')));
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// SQLite connection
const db = new sqlite3.Database(path.join(__dirname, 'adoption_child_care.db'), (err) => {
  if (err) {
    console.error('Failed to connect to SQLite:', err.message);
    process.exit(1);
  } else {
    console.log('Connected to SQLite database.');
  }
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

// Utility for standardized error responses
function formatError(code, message, details) {
  return { success: false, error: { code, message, details } };
}

// --- AUTH ---
app.post('/register', async (req, res, next) => {
  try {
  const { username, password, role, email } = req.body;
    if (!username || !password || !role || !email) {
      return res.status(400).json(formatError('VALIDATION_ERROR', 'Missing required fields', { fields: ['username', 'password', 'role', 'email'] }));
    }
  const password_hash = await bcrypt.hash(password, 10);
    db.run('INSERT INTO users (username, password_hash, role, email) VALUES (?, ?, ?, ?)',
    [username, password_hash, role, email],
      function (err) {
        if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
        res.json({ success: true, id: this.lastID, username, role, email });
      }
    );
  } catch (err) {
    next({ code: 'INTERNAL_ERROR', message: err.message, details: err });
  }
});

app.post('/login', async (req, res, next) => {
  try {
  const { username, password } = req.body;
    if (!username || !password) {
      return res.status(400).json(formatError('VALIDATION_ERROR', 'Missing username or password'));
    }
    db.get('SELECT * FROM users WHERE username = ?', [username], async (err, user) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      if (!user) return res.status(401).json(formatError('AUTH_ERROR', 'Invalid credentials'));
    const valid = await bcrypt.compare(password, user.password_hash);
      if (!valid) return res.status(401).json(formatError('AUTH_ERROR', 'Invalid credentials'));
    const token = jwt.sign({ user_id: user.user_id, role: user.role }, SECRET, { expiresIn: '1h' });
      res.json({ success: true, token });
  });
  } catch (err) {
    next({ code: 'INTERNAL_ERROR', message: err.message, details: err });
  }
});

// --- CRUD Endpoints for All Tables ---
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
  app.get(`/${table.name}`, authenticateToken, (req, res, next) => {
    db.all(`SELECT * FROM ${table.name}`, (err, results) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, data: results });
    });
  });
  // Get one
  app.get(`/${table.name}/:id`, authenticateToken, (req, res, next) => {
    db.get(`SELECT * FROM ${table.name} WHERE ${table.pk} = ?`, [req.params.id], (err, result) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      if (!result) return res.status(404).json(formatError('NOT_FOUND', `${table.name.slice(0, -1)} not found`));
      res.json({ success: true, data: result });
    });
  });
  // Create
  app.post(`/${table.name}`, authenticateToken, (req, res, next) => {
    const keys = Object.keys(req.body);
    const values = Object.values(req.body);
    const placeholders = keys.map(() => '?').join(', ');
    db.run(`INSERT INTO ${table.name} (${keys.join(', ')}) VALUES (${placeholders})`, values, function (err) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, id: this.lastID, ...req.body });
    });
  });
  // Update
  app.put(`/${table.name}/:id`, authenticateToken, (req, res, next) => {
    const keys = Object.keys(req.body);
    const values = Object.values(req.body);
    const setClause = keys.map(key => `${key} = ?`).join(', ');
    db.run(`UPDATE ${table.name} SET ${setClause} WHERE ${table.pk} = ?`, [...values, req.params.id], function (err) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      if (this.changes === 0) return res.status(404).json(formatError('NOT_FOUND', `${table.name.slice(0, -1)} not found`));
      res.json({ success: true, message: 'Updated' });
    });
  });
  // Delete
  app.delete(`/${table.name}/:id`, authenticateToken, (req, res, next) => {
    db.run(`DELETE FROM ${table.name} WHERE ${table.pk} = ?`, [req.params.id], function (err) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      if (this.changes === 0) return res.status(404).json(formatError('NOT_FOUND', `${table.name.slice(0, -1)} not found`));
      res.json({ success: true, message: 'Deleted' });
    });
  });
});

// --- File Upload for Documents ---
app.post('/documents/upload', authenticateToken, upload.single('file'), (req, res, next) => {
  try {
  const { child_id } = req.body;
  const file = req.file;
    if (!file) return res.status(400).json(formatError('VALIDATION_ERROR', 'No file uploaded'));
    db.run(
    'INSERT INTO documents (child_id, file_name, file_type, file_path) VALUES (?, ?, ?, ?)',
    [child_id, file.originalname, file.mimetype, file.path],
      function (err) {
        if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
        res.json({ success: true, id: this.lastID, file: file.filename });
    }
  );
  } catch (err) {
    next({ code: 'INTERNAL_ERROR', message: err.message, details: err });
  }
});

// --- Get Children by Guardian (Stored Procedure) ---
app.get('/children/by-guardian/:guardianId', authenticateToken, (req, res, next) => {
  db.all('SELECT * FROM children WHERE guardian_id = ?', [req.params.guardianId], (err, results) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, data: results });
  });
});
// --- Permissions Management ---
app.post('/permissions/assign', authenticateToken, (req, res, next) => {
  const { user_id, permission_id } = req.body;
  db.run('INSERT INTO user_permissions (user_id, permission_id) VALUES (?, ?)', [user_id, permission_id], function (err) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, message: 'Permission assigned' });
  });
});
app.delete('/permissions/revoke', authenticateToken, (req, res, next) => {
  const { user_id, permission_id } = req.body;
  db.run('DELETE FROM user_permissions WHERE user_id = ? AND permission_id = ?', [user_id, permission_id], function (err) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, message: 'Permission revoked' });
  });
});

// --- Get Audit Logs for a Table/Record ---
app.get('/audit_logs/:table/:record_id', authenticateToken, (req, res, next) => {
  db.all('SELECT * FROM audit_logs WHERE table_name = ? AND record_id = ?', [req.params.table, req.params.record_id], (err, results) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, data: results });
  });
});

// --- Error Handling Middleware ---
app.use((err, req, res, next) => {
  console.error(err.stack || err);
  const code = err.code || 'INTERNAL_ERROR';
  const message = err.message || 'Something went wrong!';
  const details = err.details || undefined;
  res.status(500).json(formatError(code, message, details));
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
}); 