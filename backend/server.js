// Adoption & Child Care Backend Server
const express = require('express');
// const sqlite3 = require('sqlite3').verbose();
const mysql = require('mysql2');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const multer = require('multer');
const helmet = require('helmet');
require('dotenv').config();
const path = require('path');
const fs = require('fs');
const uploadsDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(uploadsDir)) {
  fs.mkdirSync(uploadsDir);
}

const app = express();
const PORT = process.env.PORT || 50000;
const SECRET = process.env.JWT_SECRET || 'your_jwt_secret';

app.use(cors({ origin: '*', credentials: true }));
app.use(helmet());
app.use(express.json());
// REMOVE static file serving from here
// app.use(express.static(path.join(__dirname, '../src')));
// app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// MySQL connection
const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '',
  database: 'adoption_and_childcare_tracking_system_db',
  multipleStatements: true
});
db.connect((err) => {
  if (err) {
    console.error('Failed to connect to MySQL:', err.message);
    process.exit(1);
  } else {
    console.log('Connected to MySQL database.');
    // --- Ensure users table exists ---
    const createTablesSQL = `
      CREATE TABLE IF NOT EXISTS users (
        user_id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(255) UNIQUE NOT NULL,
        password_hash VARCHAR(255) NOT NULL,
        phone VARCHAR(50),
        id_number VARCHAR(50),
        role VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL,
        photo VARCHAR(255)
      );
      CREATE TABLE IF NOT EXISTS children (
        child_id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        dob VARCHAR(255),
        gender VARCHAR(255),
        guardian_id INT
      );
      CREATE TABLE IF NOT EXISTS guardians (
        guardian_id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        phone VARCHAR(255),
        address VARCHAR(255)
      );
      CREATE TABLE IF NOT EXISTS court_cases (
        case_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        case_number VARCHAR(255),
        status VARCHAR(255)
      );
      CREATE TABLE IF NOT EXISTS placements (
        placement_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        guardian_id INT,
        start_date VARCHAR(255),
        end_date VARCHAR(255)
      );
      CREATE TABLE IF NOT EXISTS medical_records (
        record_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        description TEXT,
        date VARCHAR(255)
      );
      CREATE TABLE IF NOT EXISTS case_reports (
        report_id INT AUTO_INCREMENT PRIMARY KEY,
        case_id INT,
        report_text TEXT,
        date VARCHAR(255)
      );
      CREATE TABLE IF NOT EXISTS money_records (
        money_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        amount DOUBLE,
        date VARCHAR(255),
        description TEXT
      );
      CREATE TABLE IF NOT EXISTS education_records (
        record_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        school VARCHAR(255),
        grade VARCHAR(255),
        year VARCHAR(255)
      );
      CREATE TABLE IF NOT EXISTS documents (
        document_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        file_name VARCHAR(255),
        file_type VARCHAR(255),
        file_path VARCHAR(255)
      );
      CREATE TABLE IF NOT EXISTS audit_logs (
        log_id INT AUTO_INCREMENT PRIMARY KEY,
        table_name VARCHAR(255),
        record_id INT,
        action VARCHAR(255),
        user_id INT,
        timestamp VARCHAR(255)
      );
      CREATE TABLE IF NOT EXISTS permissions (
        permission_id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL
      );
      CREATE TABLE IF NOT EXISTS user_permissions (
        user_id INT,
        permission_id INT,
        PRIMARY KEY (user_id, permission_id)
      );
    `;
    db.query(createTablesSQL, (err) => {
      if (err) {
        console.error('Failed to create tables:', err.message);
        process.exit(1);
      } else {
      console.log('All tables checked/created.');
      }
    });
  }
});

// Multer setup for file uploads
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, uploadsDir);
  },
  filename: function (req, file, cb) {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    cb(null, uniqueSuffix + '-' + file.originalname);
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
app.post('/register', upload.single('photo'), async (req, res, next) => {
  try {
    // If multipart/form-data, fields are in req.body, file in req.file
    const { username, password, phone, id_number, role, email } = req.body;
    const roleLower = role && role.toLowerCase();
    if (!username || !password || !phone || !id_number || !role || !email) {
      return res.status(400).json(formatError('VALIDATION_ERROR', 'Missing required fields', { fields: ['username', 'password', 'phone', 'id_number', 'role', 'email'] }));
    }
    let photoPath = null;
    if (req.file) {
      photoPath = `/uploads/${req.file.filename}`;
    } else if (req.body.photo) {
      photoPath = req.body.photo;
    }
  const password_hash = await bcrypt.hash(password, 10);
    db.query('INSERT INTO users (username, password_hash, phone, id_number, role, email, photo) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [username, password_hash, phone, id_number, roleLower, email, photoPath],
      function (err, results) {
        if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
        res.json({ success: true, id: results.insertId, username, role: roleLower, email, phone, id_number, photo: photoPath });
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
    // Get the user and their role from users table ONLY
    db.query('SELECT * FROM users WHERE username = ?', [username], async (err, results) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      const user = results[0];
      if (!user) return res.status(401).json(formatError('AUTH_ERROR', 'Invalid credentials'));
    const valid = await bcrypt.compare(password, user.password_hash);
      if (!valid) return res.status(401).json(formatError('AUTH_ERROR', 'Invalid credentials'));
    const token = jwt.sign({ user_id: user.user_id, role: user.role }, SECRET, { expiresIn: '1h' });
      // Always include photo in the user object
      res.json({ success: true, token, user: { user_id: user.user_id, username: user.username, role: user.role, email: user.email, photo: user.photo || null } });
  });
  } catch (err) {
    next({ code: 'INTERNAL_ERROR', message: err.message, details: err });
  }
});

// Endpoint to update user photo (expects { photo: base64 or file path })
app.put('/users/:id/photo', authenticateToken, (req, res, next) => {
  const user_id = req.params.id;
  const { photo } = req.body;
  if (!photo) return res.status(400).json(formatError('VALIDATION_ERROR', 'Missing photo'));
  db.query('UPDATE users SET photo = ? WHERE user_id = ?', [photo, user_id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    if (results.affectedRows === 0) return res.status(404).json(formatError('NOT_FOUND', 'User not found'));
    res.json({ success: true, message: 'Photo updated' });
  });
});

app.post('/upload-photo', authenticateToken, upload.single('photo'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ success: false, error: { message: 'No file uploaded' } });
  }
  const photoUrl = `/uploads/${req.file.filename}`;
  const userId = req.user.user_id;
  db.query('UPDATE users SET photo = ? WHERE user_id = ?', [photoUrl, userId], function (err, results) {
    if (err) {
      return res.status(500).json({ success: false, error: { message: 'Failed to update user photo in database' } });
    }
    res.json({ success: true, photoUrl });
  });
});

app.post('/user-exists', (req, res) => {
  const { username } = req.body;
  if (!username) return res.status(400).json({ exists: false, error: 'Missing username' });
  db.query('SELECT 1 FROM users WHERE username = ?', [username], (err, results) => {
    if (err) return res.status(500).json({ exists: false, error: err.message });
    res.json({ exists: !!results.length });
  });
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
    db.query(`SELECT * FROM ${table.name}`, (err, results) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, data: results });
    });
  });
  // Get one
  app.get(`/${table.name}/:id`, authenticateToken, (req, res, next) => {
    db.query(`SELECT * FROM ${table.name} WHERE ${table.pk} = ?`, [req.params.id], (err, results) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      const result = results[0];
      if (!result) return res.status(404).json(formatError('NOT_FOUND', `${table.name.slice(0, -1)} not found`));
      res.json({ success: true, data: result });
    });
  });
  // Create
  app.post(`/${table.name}`, authenticateToken, (req, res, next) => {
    const keys = Object.keys(req.body);
    const values = Object.values(req.body);
    const placeholders = keys.map(() => '?').join(', ');
    db.query(`INSERT INTO ${table.name} (${keys.join(', ')}) VALUES (${placeholders})`, values, function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, id: results.insertId, ...req.body });
    });
  });
  // Update
  app.put(`/${table.name}/:id`, authenticateToken, (req, res, next) => {
    const keys = Object.keys(req.body);
    const values = Object.values(req.body);
    const setClause = keys.map(key => `${key} = ?`).join(', ');
    db.query(`UPDATE ${table.name} SET ${setClause} WHERE ${table.pk} = ?`, [...values, req.params.id], function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      if (results.affectedRows === 0) return res.status(404).json(formatError('NOT_FOUND', `${table.name.slice(0, -1)} not found`));
      res.json({ success: true, message: 'Updated' });
    });
  });
  // Delete
  app.delete(`/${table.name}/:id`, authenticateToken, (req, res, next) => {
    db.query(`DELETE FROM ${table.name} WHERE ${table.pk} = ?`, [req.params.id], function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      if (results.affectedRows === 0) return res.status(404).json(formatError('NOT_FOUND', `${table.name.slice(0, -1)} not found`));
      res.json({ success: true, message: 'Deleted' });
    });
  });
});

// Place static file serving AFTER all API routes
app.use(express.static(path.join(__dirname, '../src')));
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

app.use((err, req, res, next) => {
  console.error(err);
  // If headers already sent, delegate to default Express handler
  if (res.headersSent) {
    return next(err);
  }
  // Always respond with JSON
  res.status(err.status || 500).json({
    success: false,
    error: {
      code: err.code || 'INTERNAL_ERROR',
      message: err.message || 'Internal server error',
      details: err.details || null
    }
  });
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server listening on port ${PORT}`);
}); 