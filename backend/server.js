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
const rateLimit = require('express-rate-limit');
const swaggerJsdoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');

const app = express();
const PORT = process.env.PORT || 50000;
const SECRET = process.env.JWT_SECRET || 'your_jwt_secret';

app.use(cors({ origin: '*', credentials: true }));
app.use(helmet());
app.use(express.json());
// REMOVE static file serving from here
// app.use(express.static(path.join(__dirname, '../src')));
// app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// Apply rate limiting to all API routes
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // limit each IP to 100 requests per windowMs
  message: { success: false, error: { code: 'RATE_LIMIT', message: 'Too many requests, please try again later.' } }
});
app.use('/api', apiLimiter); // If your routes are not prefixed with /api, use app.use(apiLimiter);

// MySQL connection
const db = mysql.createConnection({
  host: 'localhost',
  user: 'NUN',
  password: 'NUN',
  database: 'adoption_and_childcare_tracking_system_db',
  port: 3306, // Change to 80 or 8080 if your MySQL server uses a non-standard port
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
      CREATE TABLE IF NOT EXISTS family_profile (
        family_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        address VARCHAR(255),
        household_size INT,
        notes TEXT,
        FOREIGN KEY (user_id) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS foster_tasks (
        task_id INT AUTO_INCREMENT PRIMARY KEY,
        family_id INT NOT NULL,
        case_worker_id INT,
        description TEXT,
        status VARCHAR(50),
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        due_date DATETIME,
        FOREIGN KEY (family_id) REFERENCES family_profile(family_id),
        FOREIGN KEY (case_worker_id) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS foster_matches (
        match_id INT AUTO_INCREMENT PRIMARY KEY,
        family_id INT NOT NULL,
        case_worker_id INT,
        task_id INT,
        status VARCHAR(50),
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (family_id) REFERENCES family_profile(family_id),
        FOREIGN KEY (case_worker_id) REFERENCES users(user_id),
        FOREIGN KEY (task_id) REFERENCES foster_tasks(task_id)
      );
      CREATE TABLE IF NOT EXISTS background_checks (
        check_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        status VARCHAR(50),
        result TEXT,
        requested_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        completed_at DATETIME,
        FOREIGN KEY (user_id) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS notifications (
        notification_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        message TEXT NOT NULL,
        is_read TINYINT(1) DEFAULT 0,
        sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (user_id) REFERENCES users(user_id)
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

// Add location fields to family_profile and users if not present
// (Migration SQL, run once)
db.query('ALTER TABLE family_profile ADD COLUMN latitude DOUBLE', () => {});
db.query('ALTER TABLE family_profile ADD COLUMN longitude DOUBLE', () => {});
db.query('ALTER TABLE users ADD COLUMN latitude DOUBLE', () => {});
db.query('ALTER TABLE users ADD COLUMN longitude DOUBLE', () => {});

// Endpoint to update family location
app.put('/family_profile/:id/location', authenticateToken, (req, res, next) => {
  const { latitude, longitude } = req.body;
  db.query('UPDATE family_profile SET latitude=?, longitude=? WHERE family_id=?', [latitude, longitude, req.params.id], (err, result) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true });
  });
});
// Endpoint to fetch all family locations
app.get('/family_profile/locations', authenticateToken, (req, res, next) => {
  db.query('SELECT family_id, latitude, longitude FROM family_profile', (err, rows) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(rows);
  });
});
// Endpoint to update user location
app.put('/users/:id/location', authenticateToken, (req, res, next) => {
  const { latitude, longitude } = req.body;
  db.query('UPDATE users SET latitude=?, longitude=? WHERE user_id=?', [latitude, longitude, req.params.id], (err, result) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true });
  });
});
// Endpoint to fetch all user locations
app.get('/users/locations', authenticateToken, (req, res, next) => {
  db.query('SELECT user_id, role, latitude, longitude FROM users', (err, rows) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(rows);
  });
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
const memoryUpload = multer({ storage: multer.memoryStorage() });

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

// --- RBAC Middleware ---
function requireRole(...roles) {
  return (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];
    if (!token) return res.sendStatus(401);
    jwt.verify(token, SECRET, (err, user) => {
      if (err) return res.sendStatus(403);
      if (!roles.includes(user.role)) return res.sendStatus(403);
      req.user = user;
      next();
    });
  };
}

// Helper to log audit actions
function logAudit(table, record_id, action, user_id) {
  db.query('INSERT INTO audit_logs (table_name, record_id, action, user_id, timestamp) VALUES (?, ?, ?, ?, NOW())',
    [table, record_id, action, user_id],
    (err) => { if (err) console.error('Audit log error:', err.message); });
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

// Automated matching endpoint: assigns first available case worker to a family/task
app.post('/match', authenticateToken, (req, res, next) => {
  db.query('SELECT user_id FROM users WHERE role="case_worker" LIMIT 1', (err, workers) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    if (!workers.length) return res.status(404).json({ error: 'No case workers available' });
    const caseWorkerId = workers[0].user_id;
    const { family_id, task_id } = req.body;
    db.query('INSERT INTO foster_matches (family_id, case_worker_id, task_id, status) VALUES (?, ?, ?, ?)',
      [family_id, caseWorkerId, task_id, 'assigned'],
      function (err, results) {
        if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
        res.json({ success: true, match_id: results.insertId, case_worker_id: caseWorkerId });
      }
    );
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
      logAudit(table.name, results.insertId, 'create', req.user.user_id);
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
      logAudit(table.name, req.params.id, 'update', req.user.user_id);
      res.json({ success: true, message: 'Updated' });
    });
  });
  // Delete
  app.delete(`/${table.name}/:id`, authenticateToken, (req, res, next) => {
    db.query(`DELETE FROM ${table.name} WHERE ${table.pk} = ?`, [req.params.id], function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      if (results.affectedRows === 0) return res.status(404).json(formatError('NOT_FOUND', `${table.name.slice(0, -1)} not found`));
      logAudit(table.name, req.params.id, 'delete', req.user.user_id);
      res.json({ success: true, message: 'Deleted' });
    });
  });
});

// --- NEW TABLE ENDPOINTS ---
app.get('/family_profile', authenticateToken, (req, res, next) => {
  db.query('SELECT * FROM family_profile', (err, results) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(results);
  });
});

app.get('/foster_tasks', authenticateToken, (req, res, next) => {
  db.query('SELECT * FROM foster_tasks', (err, results) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(results);
  });
});

app.get('/foster_matches', authenticateToken, (req, res, next) => {
  db.query('SELECT * FROM foster_matches', (err, results) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(results);
  });
});

app.get('/background_checks', authenticateToken, (req, res, next) => {
  db.query('SELECT * FROM background_checks', (err, results) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(results);
  });
});

// --- POST ENDPOINTS FOR NEW TABLES ---
app.post('/family_profile', authenticateToken, requireRole('admin', 'case_worker'), (req, res, next) => {
  const { user_id, address, household_size, notes } = req.body;
  db.query('INSERT INTO family_profile (user_id, address, household_size, notes) VALUES (?, ?, ?, ?)',
    [user_id, address, household_size, notes],
    function (err, result) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      logAudit('family_profile', result.insertId, 'create', req.user.user_id);
      res.json({ success: true, family_id: result.insertId });
    }
  );
});

app.post('/foster_tasks', authenticateToken, (req, res, next) => {
  const { family_id, case_worker_id, description, status, created_at, due_date } = req.body;
  db.query('INSERT INTO foster_tasks (family_id, case_worker_id, description, status, created_at, due_date) VALUES (?, ?, ?, ?, ?, ?)',
    [family_id, case_worker_id, description, status, created_at, due_date],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, task_id: results.insertId });
    }
  );
});

app.post('/foster_matches', authenticateToken, (req, res, next) => {
  const { family_id, case_worker_id, task_id, status, created_at } = req.body;
  db.query('INSERT INTO foster_matches (family_id, case_worker_id, task_id, status, created_at) VALUES (?, ?, ?, ?, ?)',
    [family_id, case_worker_id, task_id, status, created_at],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, match_id: results.insertId });
    }
  );
});

app.post('/background_checks', authenticateToken, (req, res, next) => {
  const { user_id, status, result, requested_at, completed_at } = req.body;
  db.query('INSERT INTO background_checks (user_id, status, result, requested_at, completed_at) VALUES (?, ?, ?, ?, ?)',
    [user_id, status, result, requested_at, completed_at],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, check_id: results.insertId });
    }
  );
});

// --- PUT ENDPOINTS FOR NEW TABLES ---
app.put('/family_profile/:id', authenticateToken, requireRole('admin', 'case_worker'), (req, res, next) => {
  const { address, household_size, notes } = req.body;
  db.query('UPDATE family_profile SET address=?, household_size=?, notes=? WHERE family_id=?',
    [address, household_size, notes, req.params.id],
    function (err, result) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      logAudit('family_profile', req.params.id, 'update', req.user.user_id);
      res.json({ success: true });
    });
});

app.put('/foster_tasks/:id', authenticateToken, (req, res, next) => {
  const { family_id, case_worker_id, description, status, created_at, due_date } = req.body;
  db.query('UPDATE foster_tasks SET family_id=?, case_worker_id=?, description=?, status=?, created_at=?, due_date=? WHERE task_id=?',
    [family_id, case_worker_id, description, status, created_at, due_date, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/foster_matches/:id', authenticateToken, (req, res, next) => {
  const { family_id, case_worker_id, task_id, status, created_at } = req.body;
  db.query('UPDATE foster_matches SET family_id=?, case_worker_id=?, task_id=?, status=?, created_at=? WHERE match_id=?',
    [family_id, case_worker_id, task_id, status, created_at, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/background_checks/:id', authenticateToken, (req, res, next) => {
  const { user_id, status, result, requested_at, completed_at } = req.body;
  db.query('UPDATE background_checks SET user_id=?, status=?, result=?, requested_at=?, completed_at=? WHERE check_id=?',
    [user_id, status, result, requested_at, completed_at, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

// --- DELETE ENDPOINTS FOR NEW TABLES ---
app.delete('/family_profile/:id', authenticateToken, requireRole('admin', 'case_worker'), (req, res, next) => {
  db.query('DELETE FROM family_profile WHERE family_id=?', [req.params.id], function (err, result) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    logAudit('family_profile', req.params.id, 'delete', req.user.user_id);
    res.json({ success: true });
  });
});

app.delete('/foster_tasks/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM foster_tasks WHERE task_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/foster_matches/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM foster_matches WHERE match_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/background_checks/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM background_checks WHERE check_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

// --- PUT ENDPOINTS FOR EXISTING TABLES ---
app.put('/children/:id', authenticateToken, (req, res, next) => {
  const { name, dob, gender, guardian_id } = req.body;
  db.query('UPDATE children SET name=?, dob=?, gender=?, guardian_id=? WHERE child_id=?',
    [name, dob, gender, guardian_id, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/users/:id', authenticateToken, (req, res, next) => {
  const { username, password_hash, phone, id_number, role, email, photo } = req.body;
  db.query('UPDATE users SET username=?, password_hash=?, phone=?, id_number=?, role=?, email=?, photo=? WHERE user_id=?',
    [username, password_hash, phone, id_number, role, email, photo, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/guardians/:id', authenticateToken, (req, res, next) => {
  const { name, phone, address } = req.body;
  db.query('UPDATE guardians SET name=?, phone=?, address=? WHERE guardian_id=?',
    [name, phone, address, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/court_cases/:id', authenticateToken, (req, res, next) => {
  const { child_id, case_number, status } = req.body;
  db.query('UPDATE court_cases SET child_id=?, case_number=?, status=? WHERE case_id=?',
    [child_id, case_number, status, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/placements/:id', authenticateToken, (req, res, next) => {
  const { child_id, guardian_id, start_date, end_date } = req.body;
  db.query('UPDATE placements SET child_id=?, guardian_id=?, start_date=?, end_date=? WHERE placement_id=?',
    [child_id, guardian_id, start_date, end_date, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/medical_records/:id', authenticateToken, (req, res, next) => {
  const { child_id, description, date } = req.body;
  db.query('UPDATE medical_records SET child_id=?, description=?, date=? WHERE record_id=?',
    [child_id, description, date, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/case_reports/:id', authenticateToken, (req, res, next) => {
  const { case_id, report_text, date } = req.body;
  db.query('UPDATE case_reports SET case_id=?, report_text=?, date=? WHERE report_id=?',
    [case_id, report_text, date, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/money_records/:id', authenticateToken, (req, res, next) => {
  const { child_id, amount, date, description } = req.body;
  db.query('UPDATE money_records SET child_id=?, amount=?, date=?, description=? WHERE money_id=?',
    [child_id, amount, date, description, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/education_records/:id', authenticateToken, (req, res, next) => {
  const { child_id, school, grade, year } = req.body;
  db.query('UPDATE education_records SET child_id=?, school=?, grade=?, year=? WHERE record_id=?',
    [child_id, school, grade, year, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/documents/:id', authenticateToken, (req, res, next) => {
  const { child_id, file_name, file_type, file_path } = req.body;
  db.query('UPDATE documents SET child_id=?, file_name=?, file_type=?, file_path=? WHERE document_id=?',
    [child_id, file_name, file_type, file_path, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/audit_logs/:id', authenticateToken, (req, res, next) => {
  const { table_name, record_id, action, user_id, timestamp } = req.body;
  db.query('UPDATE audit_logs SET table_name=?, record_id=?, action=?, user_id=?, timestamp=? WHERE log_id=?',
    [table_name, record_id, action, user_id, timestamp, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/permissions/:id', authenticateToken, (req, res, next) => {
  const { name } = req.body;
  db.query('UPDATE permissions SET name=? WHERE permission_id=?',
    [name, req.params.id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

app.put('/user_permissions/:user_id/:permission_id', authenticateToken, (req, res, next) => {
  db.query('UPDATE user_permissions SET user_id=?, permission_id=? WHERE user_id=? AND permission_id=?',
    [req.body.user_id, req.body.permission_id, req.params.user_id, req.params.permission_id],
    function (err, results) {
      if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
      res.json({ success: true, affectedRows: results.affectedRows });
    }
  );
});

// --- DELETE ENDPOINTS FOR EXISTING TABLES ---
app.delete('/children/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM children WHERE child_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/users/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM users WHERE user_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/guardians/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM guardians WHERE guardian_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/court_cases/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM court_cases WHERE case_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/placements/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM placements WHERE placement_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/medical_records/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM medical_records WHERE record_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/case_reports/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM case_reports WHERE report_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/money_records/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM money_records WHERE money_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/education_records/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM education_records WHERE record_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/documents/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM documents WHERE document_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/audit_logs/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM audit_logs WHERE log_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/permissions/:id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM permissions WHERE permission_id=?', [req.params.id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

app.delete('/user_permissions/:user_id/:permission_id', authenticateToken, (req, res, next) => {
  db.query('DELETE FROM user_permissions WHERE user_id=? AND permission_id=?', [req.params.user_id, req.params.permission_id], function (err, results) {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, affectedRows: results.affectedRows });
  });
});

// Background check: trigger and fetch status
app.post('/background_checks/:user_id/trigger', authenticateToken, (req, res, next) => {
  db.query('INSERT INTO background_checks (user_id, status) VALUES (?, ?)', [req.params.user_id, 'pending'], (err, result) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, check_id: result.insertId });
  });
});
app.get('/background_checks/:user_id', authenticateToken, (req, res, next) => {
  db.query('SELECT * FROM background_checks WHERE user_id=? ORDER BY requested_at DESC LIMIT 1', [req.params.user_id], (err, rows) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(rows[0] || {});
  });
});

// --- Analytics Endpoints for Admin Dashboard ---
app.get('/analytics/summary', authenticateToken, (req, res, next) => {
  db.query(`
    SELECT (SELECT COUNT(*) FROM users) AS user_count,
           (SELECT COUNT(*) FROM family_profile) AS family_count,
           (SELECT COUNT(*) FROM foster_tasks) AS task_count,
           (SELECT COUNT(*) FROM foster_matches) AS match_count,
           (SELECT COUNT(*) FROM background_checks) AS background_check_count
  `, (err, rows) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(rows[0]);
  });
});

// --- Mock Notification Endpoint ---
app.post('/notifications/send', authenticateToken, (req, res, next) => {
  // For now, just echo the notification
  const { user_id, message } = req.body;
  res.json({ success: true, delivered: true, user_id, message });
});

// --- Additional Analytics Endpoints ---
app.get('/analytics/roles', authenticateToken, requireRole('admin'), (req, res, next) => {
  db.query(`SELECT role, COUNT(*) as count FROM users GROUP BY role`, (err, rows) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(rows);
  });
});

app.get('/analytics/recent-activity', authenticateToken, requireRole('admin'), (req, res, next) => {
  db.query(`SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 20`, (err, rows) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(rows);
  });
});

app.get('/analytics/pending-background-checks', authenticateToken, requireRole('admin'), (req, res, next) => {
  db.query(`SELECT * FROM background_checks WHERE status='pending' ORDER BY requested_at DESC`, (err, rows) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json(rows);
  });
});

// --- Notification Endpoints ---
// Create notification
app.post('/notifications', authenticateToken, (req, res, next) => {
  const { user_id, message } = req.body;
  if (!user_id || !message) return res.status(400).json({ success: false, error: { code: 'VALIDATION_ERROR', message: 'Missing user_id or message' } });
  db.query('INSERT INTO notifications (user_id, message) VALUES (?, ?)', [user_id, message], (err, result) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, notification_id: result.insertId });
  });
});
// Fetch notifications for current user
app.get('/notifications', authenticateToken, (req, res, next) => {
  const userId = req.user.user_id;
  db.query('SELECT * FROM notifications WHERE user_id=? ORDER BY sent_at DESC LIMIT 50', [userId], (err, rows) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, data: rows });
  });
});
// Mark notification as read
app.put('/notifications/:id/read', authenticateToken, (req, res, next) => {
  const userId = req.user.user_id;
  const notificationId = req.params.id;
  db.query('UPDATE notifications SET is_read=1 WHERE notification_id=? AND user_id=?', [notificationId, userId], (err, result) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    if (result.affectedRows === 0) return res.status(404).json({ success: false, error: { code: 'NOT_FOUND', message: 'Notification not found' } });
    res.json({ success: true });
  });
});
// Get unread count for current user
app.get('/notifications/unread-count', authenticateToken, (req, res, next) => {
  const userId = req.user.user_id;
  db.query('SELECT COUNT(*) as unread FROM notifications WHERE user_id=? AND is_read=0', [userId], (err, rows) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message, details: err });
    res.json({ success: true, unread: rows[0].unread });
  });
});

// Place static file serving AFTER all API routes
app.use(express.static(path.join(__dirname, '../src')));
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// --- Automated Schema Migration for BLOB columns ---
function ensureColumnExists(table, column, type, callback) {
  db.query(`SHOW COLUMNS FROM \`${table}\` LIKE ?`, [column], (err, results) => {
    if (err) return callback && callback(err);
    if (results.length === 0) {
      // Column does not exist, add it
      db.query(`ALTER TABLE \`${table}\` ADD COLUMN \`${column}\` ${type}`, callback);
    } else {
      if (callback) callback();
    }
  });
}

// Ensure BLOB columns for storing actual photo/file data
const migrations = [
  { table: 'users', column: 'photo_data', type: 'LONGBLOB' },
  { table: 'documents', column: 'file_data', type: 'LONGBLOB' }
];

migrations.forEach(mig => {
  ensureColumnExists(mig.table, mig.column, mig.type, (err) => {
    if (err) {
      console.error(`Migration error for ${mig.table}.${mig.column}:`, err.message);
    } else {
      console.log(`Ensured column ${mig.column} exists in ${mig.table}`);
    }
  });
});

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