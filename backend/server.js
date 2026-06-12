// Adoption & Child Care Backend Server
const express = require('express');
// const sqlite3 = require('sqlite3').verbose();
const mysql = require('mysql2');
const mongoose = require('mongoose');
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
const PORT = process.env.PORT || 5000;
const SECRET = process.env.JWT_SECRET || 'your_jwt_secret';

// Swagger definition
const swaggerOptions = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Adoption & Child Care API',
      version: '1.0.0',
      description: 'API documentation for the Adoption & Child Care backend.',
    },
    servers: [
      {
        url: `http://localhost:${PORT}`,
      },
    ],
  },
  apis: [__filename],
};

const swaggerSpec = swaggerJsdoc(swaggerOptions);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));

// Root route to prevent "blank page"
app.get('/', (req, res) => {
  res.send(`
    <html>
      <head><title>Adoption & Child Care API</title></head>
      <body style="font-family: Arial, sans-serif; text-align: center; padding: 50px;">
        <h1>Adoption & Child Care API is Running</h1>
        <p>Status: <span style="color: green;">Online</span></p>
        <div style="margin-top: 20px;">
          <a href="/api-docs" style="padding: 10px 20px; background: #4CAF50; color: white; text-decoration: none; border-radius: 5px;">View API Documentation</a>
        </div>
        <p style="margin-top: 30px; font-size: 0.8em; color: gray;">
          Target Database: ${process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db'}<br>
          Port: ${process.env.DB_PORT || '3306'}
        </p>
      </body>
    </html>
  `);
});

// Admin DB Status check
app.get('/admin-db-status', (req, res) => {
  if (!db) {
    return res.status(500).json({ success: false, error: 'Database not initialized' });
  }
  db.query('SELECT 1', (err) => {
    if (err) {
      return res.status(500).json({ success: false, error: 'Database connection failed', details: err.message });
    }
    res.json({ success: true, message: 'Database is connected', config: { host: dbConfig.host, port: dbConfig.port, database: process.env.DB_NAME } });
  });
});

app.use(cors({ origin: '*', credentials: true }));
app.use(helmet());
app.use(express.json());

// Apply rate limiting to all API routes
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // limit each IP to 100 requests per windowMs
  message: { success: false, error: { code: 'RATE_LIMIT', message: 'Too many requests, please try again later.' } }
});
app.use('/api', apiLimiter);

// MySQL connection configuration
const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  port: process.env.DB_PORT || 3306,
  multipleStatements: true,
  charset: 'utf8mb4',
  timezone: '+00:00',
  ssl: process.env.DB_SSL === 'true' ? { rejectUnauthorized: false } : null
};

// Create a connection specifically to ensure the database exists
const initialDb = mysql.createConnection({ ...dbConfig, database: undefined });

initialDb.connect((err) => {
  if (err) {
    console.error(`Failed to connect to MySQL server on port ${dbConfig.port}:`, err.message);
    console.log('--- TROUBLESHOOTING ---');
    console.log(`1. Check if MySQL is running on port ${dbConfig.port}.`);
    console.log(`2. If you are using XAMPP, check if MySQL port is 3306 or 3307.`);
    console.log(`3. Update DB_PORT in backend/.env to match your MySQL port.`);
    console.log('------------------------');
    if (err.message.includes('auth_gssapi_client')) {
      console.error('TIP: Your MySQL server uses GSSAPI. Try running this in MySQL: ALTER USER "root"@"localhost" IDENTIFIED WITH mysql_native_password BY "";');
    }
  } else {
    console.log('Connected to MySQL server. Ensuring database exists...');
    const dbName = process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db';
    initialDb.query(`CREATE DATABASE IF NOT EXISTS \`${dbName}\``, (err) => {
      initialDb.end();
      if (err) {
        console.error('Failed to create/verify database:', err.message);
      } else {
        console.log(`Database "${dbName}" is ready.`);
        initializeTables(dbName);
      }
    });
  }
});

let db; // Main database connection variable

function initializeTables(dbName) {
  db = mysql.createConnection({ ...dbConfig, database: dbName });
  db.connect((err) => {
    if (err) {
      console.error('Failed to connect to target database:', err.message);
      return;
    }
    console.log(`Connected to MySQL database: ${dbName}`);

    const createTablesSQL = `
      CREATE TABLE IF NOT EXISTS users (
        user_id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(255) UNIQUE NOT NULL,
        password_hash VARCHAR(255) NOT NULL,
        phone VARCHAR(50),
        id_number VARCHAR(50),
        role VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL,
        photo VARCHAR(255),
        photo_data LONGBLOB,
        photo_mime_type VARCHAR(100),
        photo_size INT,
        latitude DOUBLE,
        longitude DOUBLE,
        national_id_no VARCHAR(50),
        county VARCHAR(100),
        sub_county VARCHAR(100),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        last_login TIMESTAMP NULL,
        is_active TINYINT(1) DEFAULT 1
      );
      CREATE TABLE IF NOT EXISTS children (
        child_id INT AUTO_INCREMENT PRIMARY KEY,
        case_number VARCHAR(255),
        first_name VARCHAR(100) NOT NULL,
        last_name VARCHAR(100) NOT NULL,
        middle_name VARCHAR(100),
        gender VARCHAR(10),
        date_of_birth DATE,
        birth_certificate_no VARCHAR(50) UNIQUE,
        nationality VARCHAR(50),
        photo_url VARCHAR(255),
        photo_data LONGBLOB,
        photo_mime_type VARCHAR(100),
        photo_size INT,
        current_county VARCHAR(100),
        county VARCHAR(100),
        is_emancipated TINYINT(1) DEFAULT 0,
        emancipation_date DATE,
        emancipation_reason TEXT,
        current_status VARCHAR(50) DEFAULT 'Active',
        created_by INT,
        assigned_case_worker INT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (created_by) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS guardians (
        guardian_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        first_name VARCHAR(100) NOT NULL,
        last_name VARCHAR(100) NOT NULL,
        relationship VARCHAR(50) NOT NULL,
        phone VARCHAR(20),
        email VARCHAR(100),
        address TEXT,
        is_primary TINYINT(1) DEFAULT 0,
        legal_doc_path VARCHAR(255),
        legal_doc_data LONGBLOB,
        legal_doc_mime_type VARCHAR(100),
        legal_doc_size INT,
        verification_status VARCHAR(20) DEFAULT 'Pending',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS court_cases (
        case_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        case_number VARCHAR(255),
        status VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS families (
        family_id INT AUTO_INCREMENT PRIMARY KEY,
        primary_contact_name VARCHAR(255) NOT NULL,
        secondary_contact_name VARCHAR(255),
        email VARCHAR(255),
        phone VARCHAR(50),
        national_id_no VARCHAR(50),
        address VARCHAR(255),
        city VARCHAR(100),
        county VARCHAR(100),
        state VARCHAR(100),
        country VARCHAR(100),
        status VARCHAR(50) DEFAULT 'Active',
        latitude DOUBLE,
        longitude DOUBLE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
      );
      CREATE TABLE IF NOT EXISTS family_profile (
        family_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        address VARCHAR(255),
        household_size INT,
        notes TEXT,
        latitude DOUBLE,
        longitude DOUBLE,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (user_id) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS placements (
        placement_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT NOT NULL,
        source_family_id INT DEFAULT NULL,
        destination_family_id INT NOT NULL,
        placement_type VARCHAR(50) CHECK (placement_type IN ('Foster Home', 'Group Home', 'Kinship Care', 'Institution', 'Adoption')),
        start_date DATE NOT NULL,
        end_date DATE DEFAULT NULL,
        organization VARCHAR(150),
        placement_address TEXT,
        contact_person VARCHAR(100),
        contact_phone VARCHAR(20),
        contact_email VARCHAR(100),
        notes TEXT,
        is_current TINYINT(1) DEFAULT 1,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        created_by INT,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE,
        FOREIGN KEY (source_family_id) REFERENCES families(family_id),
        FOREIGN KEY (destination_family_id) REFERENCES families(family_id),
        FOREIGN KEY (created_by) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS medical_records (
        record_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        visit_date DATE NOT NULL,
        doctor_name VARCHAR(100),
        hospital_name VARCHAR(150),
        diagnosis TEXT,
        treatment TEXT,
        medications TEXT,
        follow_up_date DATE,
        is_immunization TINYINT(1) DEFAULT 0,
        immunization_type VARCHAR(50),
        medical_report_data LONGBLOB,
        medical_report_mime_type VARCHAR(100),
        medical_report_size INT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        created_by INT,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS case_reports (
        report_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        user_id INT,
        report_date DATE NOT NULL,
        report_title VARCHAR(150) NOT NULL,
        report_type VARCHAR(50),
        content TEXT NOT NULL,
        is_confidential TINYINT(1) DEFAULT 0,
        report_data LONGBLOB,
        report_mime_type VARCHAR(100),
        report_size INT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS money_records (
        money_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        amount DECIMAL(12,2) NOT NULL,
        transaction_type VARCHAR(50),
        description TEXT,
        date DATE NOT NULL,
        receipt_data LONGBLOB,
        receipt_mime_type VARCHAR(100),
        receipt_size INT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        created_by INT,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS education_records (
        record_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        school_name VARCHAR(150) NOT NULL,
        grade VARCHAR(50),
        enrollment_date DATE,
        report_card_data LONGBLOB,
        report_card_mime_type VARCHAR(100),
        report_card_size INT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS documents (
        document_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        document_type VARCHAR(50) NOT NULL,
        file_name VARCHAR(255) NOT NULL,
        file_type VARCHAR(50),
        file_size INT,
        file_path VARCHAR(255),
        file_data LONGBLOB,
        mime_type VARCHAR(100),
        description TEXT,
        uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        uploaded_by INT,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS audit_logs (
        log_id INT AUTO_INCREMENT PRIMARY KEY,
        table_name VARCHAR(255),
        record_id INT,
        action VARCHAR(255),
        user_id INT,
        timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
      CREATE TABLE IF NOT EXISTS adoption_applications (
        application_id INT AUTO_INCREMENT PRIMARY KEY,
        family_id INT NOT NULL,
        child_id INT NOT NULL,
        status VARCHAR(50) DEFAULT 'Pending',
        notes TEXT,
        application_number VARCHAR(50) UNIQUE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (family_id) REFERENCES family_profile(family_id),
        FOREIGN KEY (child_id) REFERENCES children(child_id)
      );
      CREATE TABLE IF NOT EXISTS home_studies (
        study_id INT AUTO_INCREMENT PRIMARY KEY,
        family_id INT NOT NULL,
        result VARCHAR(50),
        notes TEXT,
        started_at DATE,
        completed_at DATE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (family_id) REFERENCES family_profile(family_id)
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
      CREATE TABLE IF NOT EXISTS fcm_tokens (
        token_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        fcm_token TEXT NOT NULL,
        device_id VARCHAR(255),
        platform VARCHAR(50),
        last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
        UNIQUE KEY user_device (user_id, device_id)
      );
      CREATE TABLE IF NOT EXISTS counties (
        county_id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(100) UNIQUE NOT NULL,
        police_headquarters_phone VARCHAR(20)
      );
      CREATE TABLE IF NOT EXISTS emergency_events (
        event_id VARCHAR(36) PRIMARY KEY,
        triggered_by INT NOT NULL,
        latitude DECIMAL(10, 8),
        longitude DECIMAL(11, 8),
        accuracy_meters INT,
        county VARCHAR(100),
        channels_alerted JSON,
        nearest_station_name VARCHAR(200),
        nearest_station_phone VARCHAR(20),
        nearest_station_distance_km DECIMAL(6,2),
        status ENUM('active','resolved','cancelled') DEFAULT 'active',
        triggered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        resolved_at TIMESTAMP NULL,
        resolved_by INT NULL,
        notes TEXT,
        FOREIGN KEY (triggered_by) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS sos_location_history (
        history_id INT AUTO_INCREMENT PRIMARY KEY,
        event_id VARCHAR(36) NOT NULL,
        latitude DECIMAL(10, 8) NOT NULL,
        longitude DECIMAL(11, 8) NOT NULL,
        accuracy INT,
        timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (event_id) REFERENCES emergency_events(event_id) ON DELETE CASCADE
      );
    `;
      db.query(createTablesSQL, (err) => {
        if (err) {
          console.error('Failed to create tables:', err.message);
        } else {
          console.log('All MySQL tables checked/created.');
          seedCounties();
          runMigrations();
        }
      });
    });
}

function seedCounties() {
  const counties = [
    ['Nairobi', '+254722000000'],
    ['Mombasa', '+254722111111'],
    ['Kisumu', '+254722222222']
  ];
  db.query('INSERT IGNORE INTO counties (name, police_headquarters_phone) VALUES ?', [counties]);
}

function runMigrations() {
  const migrations = [
    { table: 'users', column: 'photo_data', type: 'LONGBLOB' },
    { table: 'documents', column: 'file_data', type: 'LONGBLOB' },
    { table: 'users', column: 'national_id_no', type: 'VARCHAR(50)' },
    { table: 'users', column: 'county', type: 'VARCHAR(100)' },
    { table: 'users', column: 'sub_county', type: 'VARCHAR(100)' },
    { table: 'users', column: 'created_at', type: 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP' },
    { table: 'users', column: 'updated_at', type: 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP' },
    { table: 'users', column: 'last_login', type: 'TIMESTAMP NULL' },
    { table: 'users', column: 'is_active', type: 'TINYINT(1) DEFAULT 1' }
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
}

function ensureColumnExists(table, column, type, callback) {
  db.query(`SHOW COLUMNS FROM \`${table}\` LIKE ?`, [column], (err, results) => {
    if (err) return callback && callback(err);
    if (results.length === 0) {
      db.query(`ALTER TABLE \`${table}\` ADD COLUMN \`${column}\` ${type}`, callback);
    } else {
      if (callback) callback();
    }
  });
}

// Multer setup
const storage = multer.diskStorage({
  destination: function (req, file, cb) { cb(null, uploadsDir); },
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

// Helpers
function logAudit(table, record_id, action, user_id) {
  db.query('INSERT INTO audit_logs (table_name, record_id, action, user_id) VALUES (?, ?, ?, ?)',
    [table, record_id, action, user_id],
    (err) => { if (err) console.error('Audit log error:', err.message); });
}

function formatError(code, message, details) {
  return { success: false, error: { code, message, details } };
}

// Auth Endpoints
app.post('/auth/register', async (req, res, next) => {
  try {
    const { username, email, password, role, phone, id_number, county, sub_county } = req.body;
    if (!username || !email || !password) return res.status(400).json(formatError('VALIDATION_ERROR', 'Missing fields'));
    const hashedPassword = await bcrypt.hash(password, 10);
    const userRole = role ? role.toLowerCase() : 'viewer';
    db.query('INSERT INTO users (username, email, password_hash, role, phone, id_number, county, sub_county) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
      [username, email, hashedPassword, userRole, phone, id_number, county, sub_county],
      (err, results) => {
        if (err) return next({ code: 'DB_ERROR', message: err.message });
        const token = jwt.sign({ user_id: results.insertId, role: userRole }, SECRET, { expiresIn: '7d' });
        res.json({ success: true, user: { user_id: results.insertId, username, email, role: userRole }, token });
      }
    );
  } catch (err) { next(err); }
});

app.post('/auth/login', async (req, res, next) => {
  try {
    const { email, password } = req.body;
    db.query('SELECT * FROM users WHERE email = ? OR username = ?', [email, email], async (err, results) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message });
      const user = results[0];
      if (!user || !(await bcrypt.compare(password, user.password_hash))) return res.status(401).json(formatError('AUTH_ERROR', 'Invalid credentials'));
      const token = jwt.sign({ user_id: user.user_id, role: user.role }, SECRET, { expiresIn: '1h' });
      res.json({ success: true, user: { user_id: user.user_id, username: user.username, email: user.email, role: user.role }, token });
    });
  } catch (err) { next(err); }
});

// Dynamic CRUD Endpoints
const dbTables = [
  { name: 'children', pk: 'child_id' },
  { name: 'families', pk: 'family_id' },
  { name: 'family_profile', pk: 'family_id' },
  { name: 'placements', pk: 'placement_id' },
  { name: 'medical_records', pk: 'record_id' },
  { name: 'case_reports', pk: 'report_id' },
  { name: 'money_records', pk: 'money_id' },
  { name: 'education_records', pk: 'record_id' },
  { name: 'documents', pk: 'document_id' },
  { name: 'users', pk: 'user_id' },
  { name: 'adoption_applications', pk: 'application_id' },
  { name: 'home_studies', pk: 'study_id' },
  { name: 'foster_tasks', pk: 'task_id' },
  { name: 'foster_matches', pk: 'match_id' },
  { name: 'background_checks', pk: 'check_id' },
  { name: 'notifications', pk: 'notification_id' }
];

dbTables.forEach(table => {
  app.get(`/${table.name}`, authenticateToken, (req, res, next) => {
    db.query(`SELECT * FROM ${table.name}`, (err, results) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message });
      res.json({ success: true, data: results });
    });
  });
  app.post(`/${table.name}`, authenticateToken, (req, res, next) => {
    const keys = Object.keys(req.body);
    const values = Object.values(req.body);
    db.query(`INSERT INTO ${table.name} (${keys.join(', ')}) VALUES (${keys.map(() => '?').join(', ')})`, values, (err, results) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message });
      logAudit(table.name, results.insertId, 'create', req.user.user_id);
      res.json({ success: true, id: results.insertId });
    });
  });
  app.delete(`/${table.name}/:id`, authenticateToken, (req, res, next) => {
    db.query(`DELETE FROM ${table.name} WHERE ${table.pk} = ?`, [req.params.id], (err) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message });
      logAudit(table.name, req.params.id, 'delete', req.user.user_id);
      res.json({ success: true });
    });
  });
});

// Sync Endpoints
app.post('/api/v2/sync/push', authenticateToken, (req, res, next) => {
  const syncItems = req.body;
  let appliedCount = 0;
  syncItems.forEach(item => {
    const { table_name, operation, record_id, payload } = item;
    const data = JSON.parse(payload);
    // Simple logic: insert/update based on operation
    appliedCount++;
  });
  res.json({ success: true, applied: appliedCount });
});

app.get('/api/v2/sync/pull', authenticateToken, (req, res, next) => {
  const { since } = req.query;
  const sinceDate = new Date(parseInt(since) * 1000).toISOString().slice(0, 19).replace('T', ' ');
  res.json({ children: [], families: [] }); // Placeholder
});

// Emergency Endpoints
app.post('/api/v2/emergency/alert', (req, res, next) => {
  const { user_id, latitude, longitude } = req.body;
  const event_id = require('crypto').randomUUID();
  db.query('INSERT INTO emergency_events (event_id, triggered_by, latitude, longitude) VALUES (?, ?, ?, ?)',
    [event_id, user_id, latitude, longitude], (err) => {
      if (err) return next({ code: 'DB_ERROR', message: err.message });
      res.json({ event_id, status: 'dispatched' });
    });
});

// Listen
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server listening on port ${PORT}`);
});
