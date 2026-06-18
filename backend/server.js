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

// Root route - serve index.html
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, '..', 'src', 'index.html'));
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
app.use(helmet({
  contentSecurityPolicy: false,
  crossOriginEmbedderPolicy: false
}));
app.use(express.json());

// Serve static frontend files
app.use(express.static(path.join(__dirname, '..', 'src')));

// Apply rate limiting to all API routes
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // limit each IP to 100 requests per windowMs
  message: { success: false, error: { code: 'RATE_LIMIT', message: 'Too many requests, please try again later.' } }
});
app.use('/api', apiLimiter);

// MySQL connection configuration
const dbConfig = {
  host: '127.0.0.1',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  port: parseInt(process.env.DB_PORT) || 3306,
  multipleStatements: true,
  charset: 'utf8mb4',
  timezone: '+00:00',
  connectTimeout: 30000, // 30 seconds
  ssl: process.env.DB_SSL === 'true' ? { rejectUnauthorized: false } : null
};

console.log(`[DB CONFIG] Attempting connection to 127.0.0.1:${dbConfig.port} (User: ${dbConfig.user})`);

// Create a connection specifically to ensure the database exists
const initialDb = mysql.createConnection({ ...dbConfig, database: undefined });

function connectWithRetry(connection, retries = 5) {
  connection.connect((err) => {
    if (err) {
      if (retries > 0) {
        // Try toggling between 127.0.0.1 and localhost on retry
        const nextHost = connection.config.host === '127.0.0.1' ? 'localhost' : '127.0.0.1';
        connection.config.host = nextHost;
        console.log(`Connection failed (${err.code}). Retrying with ${nextHost}... (${retries} retries left)`);
        setTimeout(() => connectWithRetry(connection, retries - 1), 2000);
      } else {
        console.error(`Failed to connect to MySQL server on port ${dbConfig.port}:`, err.message);
        console.log('--- TROUBLESHOOTING ---');
        console.log(`1. Check if MySQL is running on port ${dbConfig.port} in XAMPP.`);
        console.log('2. Ensure no other MySQL instance is blocking the port.');
        console.log('------------------------');
      }
    } else {
      console.log('✔ Connected to MySQL server.');
      const dbName = process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db';
      connection.query(`CREATE DATABASE IF NOT EXISTS \`${dbName}\``, (err) => {
        connection.end();
        if (err) {
          console.error('Failed to create/verify database:', err.message);
        } else {
          console.log(`✔ Database "${dbName}" is ready.`);
          initializeTables(dbName);
        }
      });
    }
  });
}

connectWithRetry(initialDb);

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
        place_of_birth VARCHAR(255),
        trauma_notes TEXT,
        allergies TEXT,
        blood_type VARCHAR(20),
        primary_physician VARCHAR(255),
        special_needs TEXT,
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
        sub_county VARCHAR(100),
        state VARCHAR(100),
        country VARCHAR(100),
        status VARCHAR(50) DEFAULT 'Active',
        license_number VARCHAR(100),
        license_issue_date DATE,
        license_expiration_date DATE,
        license_status VARCHAR(50),
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
        table_name VARCHAR(100) NOT NULL,
        record_id INT NOT NULL,
        action VARCHAR(20) NOT NULL,
        changed_by INT,
        changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        old_data LONGTEXT,
        new_data LONGTEXT
      );
      CREATE TABLE IF NOT EXISTS permissions (
        permission_id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL
      );
      CREATE TABLE IF NOT EXISTS user_permissions (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        permission_id INT NOT NULL,
        granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        UNIQUE KEY (user_id, permission_id)
      );
      CREATE TABLE IF NOT EXISTS system_settings (
        setting_id INT AUTO_INCREMENT PRIMARY KEY,
        setting_key VARCHAR(100) UNIQUE NOT NULL,
        setting_value TEXT,
        category VARCHAR(50),
        description TEXT,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
      );
      CREATE TABLE IF NOT EXISTS tasks (
        task_id INT AUTO_INCREMENT PRIMARY KEY,
        assigned_to INT,
        title VARCHAR(200),
        description TEXT,
        due_date DATETIME,
        status VARCHAR(50),
        related_entity_type VARCHAR(50),
        related_entity_id INT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
      CREATE TABLE IF NOT EXISTS action_items (
        action_id INT AUTO_INCREMENT PRIMARY KEY,
        task_id INT,
        description TEXT,
        is_completed TINYINT(1) DEFAULT 0,
        completed_at DATETIME,
        FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS dashboard_metrics (
        metric_id INT AUTO_INCREMENT PRIMARY KEY,
        metric_name VARCHAR(100) UNIQUE,
        metric_value DOUBLE,
        previous_value DOUBLE,
        trend_percentage DOUBLE,
        calculated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
      CREATE TABLE IF NOT EXISTS dashboard_preferences (
        preference_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT UNIQUE,
        layout_type VARCHAR(50),
        show_metrics TINYINT(1) DEFAULT 1,
        show_alerts TINYINT(1) DEFAULT 1,
        dark_mode TINYINT(1) DEFAULT 0,
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS critical_dates (
        date_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        date_type VARCHAR(50),
        event_date DATE,
        description TEXT,
        is_completed TINYINT(1) DEFAULT 0,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS worker_messages (
        message_id INT AUTO_INCREMENT PRIMARY KEY,
        sender_id INT,
        recipient_id INT,
        content TEXT,
        is_read TINYINT(1) DEFAULT 0,
        sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (sender_id) REFERENCES users(user_id),
        FOREIGN KEY (recipient_id) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS risk_assessments (
        assessment_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        assessment_date DATE,
        safety_score INT,
        risk_level VARCHAR(50),
        notes TEXT,
        assessed_by INT,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE,
        FOREIGN KEY (assessed_by) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS permanency_plans (
        plan_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        primary_goal VARCHAR(100),
        secondary_goal VARCHAR(100),
        review_date DATE,
        status VARCHAR(50),
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS caseload (
        caseload_id INT AUTO_INCREMENT PRIMARY KEY,
        worker_id INT UNIQUE,
        active_cases INT DEFAULT 0,
        pending_reviews INT DEFAULT 0,
        capacity_percentage INT DEFAULT 0,
        FOREIGN KEY (worker_id) REFERENCES users(user_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS case_urgency_flags (
        flag_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        flag_type VARCHAR(50),
        description TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS case_activities (
        activity_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        activity_type VARCHAR(50),
        activity_date DATE,
        duration_minutes INT,
        notes TEXT,
        caseworker_id INT,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE,
        FOREIGN KEY (caseworker_id) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS case_deadlines (
        deadline_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        deadline_type VARCHAR(100),
        due_date DATE,
        status VARCHAR(50),
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS case_approvals (
        approval_id INT AUTO_INCREMENT PRIMARY KEY,
        related_entity_type VARCHAR(50),
        related_entity_id INT,
        submitted_by INT,
        reviewed_by INT,
        status VARCHAR(50) DEFAULT 'Pending',
        submission_comments TEXT,
        review_comments TEXT,
        submitted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        reviewed_date TIMESTAMP NULL,
        FOREIGN KEY (submitted_by) REFERENCES users(user_id),
        FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
      );
      CREATE TABLE IF NOT EXISTS placement_compatibility (
        compatibility_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT,
        family_id INT,
        compatibility_score INT,
        notes TEXT,
        last_reviewed DATE,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE,
        FOREIGN KEY (family_id) REFERENCES family_profile(family_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS workload_tracking (
        workload_id INT AUTO_INCREMENT PRIMARY KEY,
        caseworker_id INT,
        tracking_date DATE,
        total_active_cases INT,
        overdue_tasks_count INT,
        time_logged_hours DECIMAL(5,2),
        FOREIGN KEY (caseworker_id) REFERENCES users(user_id) ON DELETE CASCADE
      );
      CREATE TABLE IF NOT EXISTS adoption_applications (
        application_id INT AUTO_INCREMENT PRIMARY KEY,
        family_id INT NOT NULL,
        child_id INT DEFAULT NULL,
        status VARCHAR(50) DEFAULT 'Pending',
        notes TEXT,
        application_number VARCHAR(50) UNIQUE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (family_id) REFERENCES families(family_id),
        FOREIGN KEY (child_id) REFERENCES children(child_id)
      );
      CREATE TABLE IF NOT EXISTS home_studies (
        home_study_id INT AUTO_INCREMENT PRIMARY KEY,
        family_id INT NOT NULL,
        result VARCHAR(50),
        notes TEXT,
        started_at DATE,
        completed_at DATE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (family_id) REFERENCES families(family_id)
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
        county_name VARCHAR(100) UNIQUE NOT NULL,
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
      CREATE TABLE IF NOT EXISTS siblings (
        sibling_id INT AUTO_INCREMENT PRIMARY KEY,
        child_id INT NOT NULL,
        sibling_child_id INT NOT NULL,
        relationship_type VARCHAR(50),
        same_placement TINYINT(1) DEFAULT 0,
        contact_allowed TINYINT(1) DEFAULT 1,
        notes TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE,
        FOREIGN KEY (sibling_child_id) REFERENCES children(child_id) ON DELETE CASCADE
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
    ['Nairobi', 'NBI', 'Nairobi'],
    ['Mombasa', 'MSA', 'Coast'],
    ['Kisumu', 'KSM', 'Nyanza']
  ];
  // Check if county_name column exists and seed accordingly
  db.query('SHOW COLUMNS FROM counties', (err, columns) => {
    if (err) {
      console.error('Error checking counties columns:', err.message);
      return;
    }
    const colNames = columns.map(c => c.Field);
    if (colNames.includes('county_code') && colNames.includes('region')) {
      db.query('INSERT IGNORE INTO counties (county_name, county_code, region) VALUES ?', [counties], (err) => {
        if (err) console.error('Error seeding counties:', err.message);
        else console.log('Counties seeded successfully.');
      });
    } else if (colNames.includes('police_headquarters_phone')) {
      const countiesOld = [['Nairobi', '+254722000000'], ['Mombasa', '+254722111111'], ['Kisumu', '+254722222222']];
      db.query('INSERT IGNORE INTO counties (county_name, police_headquarters_phone) VALUES ?', [countiesOld], (err) => {
        if (err) console.error('Error seeding counties:', err.message);
        else console.log('Counties seeded successfully.');
      });
    } else {
      const countiesSimple = [['Nairobi'], ['Mombasa'], ['Kisumu']];
      db.query('INSERT IGNORE INTO counties (county_name) VALUES ?', [countiesSimple], (err) => {
        if (err) console.error('Error seeding counties:', err.message);
        else console.log('Counties seeded successfully.');
      });
    }
  });
}

function runMigrations() {
  const migrations = [
    { table: 'users', column: 'photo_data', type: 'LONGBLOB' },
    { table: 'documents', column: 'file_data', type: 'LONGBLOB' },
    { table: 'users', column: 'national_id_no', type: 'VARCHAR(50)' },
    { table: 'users', column: 'county', type: 'VARCHAR(100)' },
    { table: 'users', column: 'sub_county', type: 'VARCHAR(100)' },
    { table: 'users', column: 'is_active', type: 'TINYINT(1) DEFAULT 1' },
    { table: 'children', column: 'photo_data', type: 'LONGBLOB' },
    { table: 'children', column: 'photo_mime_type', type: 'VARCHAR(100)' },
    { table: 'children', column: 'photo_size', type: 'INT' },
    { table: 'children', column: 'current_county', type: 'VARCHAR(100)' },
    { table: 'children', column: 'is_emancipated', type: 'TINYINT(1) DEFAULT 0' },
    { table: 'children', column: 'emancipation_date', type: 'DATE' },
    { table: 'children', column: 'emancipation_reason', type: 'TEXT' },
    { table: 'families', column: 'latitude', type: 'DOUBLE' },
    { table: 'families', column: 'longitude', type: 'DOUBLE' },
    { table: 'families', column: 'national_id_no', type: 'VARCHAR(50)' },
    { table: 'family_profile', column: 'latitude', type: 'DOUBLE' },
    { table: 'family_profile', column: 'longitude', type: 'DOUBLE' },
    { table: 'family_profile', column: 'household_size', type: 'INT' },
    { table: 'guardians', column: 'legal_doc_data', type: 'LONGBLOB' },
    { table: 'guardians', column: 'legal_doc_mime_type', type: 'VARCHAR(100)' },
    { table: 'guardians', column: 'legal_doc_size', type: 'INT' },
    { table: 'guardians', column: 'verification_status', type: "VARCHAR(20) DEFAULT 'Pending'" },
    { table: 'medical_records', column: 'medical_report_data', type: 'LONGBLOB' },
    { table: 'medical_records', column: 'medical_report_mime_type', type: 'VARCHAR(100)' },
    { table: 'medical_records', column: 'medical_report_size', type: 'INT' },
    { table: 'case_reports', column: 'report_data', type: 'LONGBLOB' },
    { table: 'case_reports', column: 'report_mime_type', type: 'VARCHAR(100)' },
    { table: 'case_reports', column: 'report_size', type: 'INT' },
    { table: 'money_records', column: 'receipt_data', type: 'LONGBLOB' },
    { table: 'money_records', column: 'receipt_mime_type', type: 'VARCHAR(100)' },
    { table: 'money_records', column: 'receipt_size', type: 'INT' },
    { table: 'education_records', column: 'report_card_data', type: 'LONGBLOB' },
    { table: 'education_records', column: 'report_card_mime_type', type: 'VARCHAR(100)' },
    { table: 'education_records', column: 'report_card_size', type: 'INT' },
    { table: 'home_studies', column: 'study_report_data', type: 'LONGBLOB' },
    { table: 'home_studies', column: 'study_report_mime_type', type: 'VARCHAR(100)' },
    { table: 'home_studies', column: 'study_report_size', type: 'INT' },
    // Extensive Child Details
    { table: 'children', column: 'place_of_birth', type: 'VARCHAR(255)' },
    { table: 'children', column: 'trauma_notes', type: 'TEXT' },
    { table: 'children', column: 'allergies', type: 'TEXT' },
    { table: 'children', column: 'blood_type', type: 'VARCHAR(20)' },
    { table: 'children', column: 'primary_physician', type: 'VARCHAR(255)' },
    { table: 'children', column: 'special_needs', type: 'TEXT' },
    // Extensive Family Details
    { table: 'families', column: 'license_number', type: 'VARCHAR(100)' },
    { table: 'families', column: 'license_issue_date', type: 'DATE' },
    { table: 'families', column: 'license_expiration_date', type: 'DATE' },
    { table: 'families', column: 'license_status', type: 'VARCHAR(50)' }
  ];

  migrations.forEach(mig => {
    ensureColumnExists(mig.table, mig.column, mig.type, (err) => {
      if (err) {
        console.error(`Migration error for ${mig.table}.${mig.column}:`, err.message);
      } else {
        // console.log(`Ensured column ${mig.column} exists in ${mig.table}`);
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
    const { username, email, password, role, phone, id_number, national_id_no, county, sub_county } = req.body;
    if (!username || !email || !password) return res.status(400).json(formatError('VALIDATION_ERROR', 'Missing fields'));

    const hashedPassword = await bcrypt.hash(password, 10);
    const userRole = role || 'Social Worker'; // Match the table's default or common role

    // We include national_id_no as it exists in the table.
    // We use sub_county as well.
    const sql = 'INSERT INTO users (username, email, password_hash, role, phone, national_id_no, county, sub_county) VALUES (?, ?, ?, ?, ?, ?, ?, ?)';
    const params = [username, email, hashedPassword, userRole, phone, national_id_no, county, sub_county];

    db.query(sql, params, (err, results) => {
        if (err) {
            console.error('Registration error:', err.message);
            return next({ code: 'DB_ERROR', message: err.message });
        }
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
      const { password_hash, ...userWithoutHash } = user;
      res.json({ success: true, user: userWithoutHash, token });
    });
  } catch (err) { next(err); }
});

// Analytics summary endpoint
app.get('/analytics/summary', authenticateToken, (req, res, next) => {
  const queries = {
    totalChildren: 'SELECT COUNT(*) as count FROM children',
    totalFamilies: 'SELECT COUNT(*) as count FROM families',
    totalUsers: 'SELECT COUNT(*) as count FROM users',
    activePlacements: 'SELECT COUNT(*) as count FROM placements WHERE is_current = 1',
    pendingApplications: "SELECT COUNT(*) as count FROM adoption_applications WHERE status = 'pending'",
    pendingBackgroundChecks: "SELECT COUNT(*) as count FROM background_checks WHERE status = 'Pending' OR status = 'pending'"
  };
  const result = {};
  let completed = 0;
  const total = Object.keys(queries).length;
  for (const [key, query] of Object.entries(queries)) {
    db.query(query, (err, rows) => {
      completed++;
      result[key] = err ? 0 : rows[0].count;
      if (completed === total) {
        res.json({ success: true, ...result });
      }
    });
  }
});

// Recent activity endpoint
app.get('/analytics/recent-activity', authenticateToken, (req, res, next) => {
  db.query('SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT 20', (err, results) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message });
    const activities = results.map(r => ({
      id: r.log_id,
      action: r.action,
      tableName: r.table_name,
      recordId: r.record_id,
      changedAt: r.created_at,
      changedBy: r.user_id ? r.user_id.toString() : null
    }));
    res.json({ success: true, data: activities });
  });
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
  { name: 'home_studies', pk: 'home_study_id' },
  { name: 'foster_tasks', pk: 'task_id' },
  { name: 'foster_matches', pk: 'match_id' },
  { name: 'background_checks', pk: 'check_id' },
  { name: 'notifications', pk: 'notification_id' },
  { name: 'guardians', pk: 'guardian_id' },
  { name: 'court_cases', pk: 'case_id' },
  { name: 'counties', pk: 'county_id' },
  { name: 'permissions', pk: 'permission_id' },
  { name: 'user_permissions', pk: 'id' },
  { name: 'system_settings', pk: 'setting_id' },
  { name: 'audit_logs', pk: 'log_id' },
  { name: 'fcm_tokens', pk: 'token_id' },
  { name: 'emergency_events', pk: 'event_id' },
  { name: 'sos_location_history', pk: 'history_id' },
  { name: 'tasks', pk: 'task_id' },
  { name: 'action_items', pk: 'action_id' },
  { name: 'dashboard_metrics', pk: 'metric_id' },
  { name: 'dashboard_preferences', pk: 'preference_id' },
  { name: 'critical_dates', pk: 'date_id' },
  { name: 'worker_messages', pk: 'message_id' },
  { name: 'risk_assessments', pk: 'assessment_id' },
  { name: 'permanency_plans', pk: 'plan_id' },
  { name: 'caseload', pk: 'caseload_id' },
  { name: 'case_urgency_flags', pk: 'flag_id' },
  { name: 'case_activities', pk: 'activity_id' },
  { name: 'case_deadlines', pk: 'deadline_id' },
  { name: 'case_approvals', pk: 'approval_id' },
  { name: 'placement_compatibility', pk: 'compatibility_id' },
  { name: 'workload_tracking', pk: 'workload_id' }
];

// Helper: convert table name to hyphenated route (e.g. court_cases → court-cases)
function toHyphenated(name) {
  return name.replace(/_/g, '-');
}

dbTables.forEach(table => {
  const hyphenRoute = toHyphenated(table.name);
  const underscoreRoute = table.name;

  // Register routes for BOTH underscore and hyphenated URL styles
  [underscoreRoute, hyphenRoute].forEach(route => {
    // Skip if route would be duplicate (no underscores)
    if (route === hyphenRoute && route === underscoreRoute && route !== table.name) return;

    app.get(`/${route}`, authenticateToken, (req, res, next) => {
      db.query(`SELECT * FROM ${table.name}`, (err, results) => {
        if (err) return next({ code: 'DB_ERROR', message: err.message });
        res.json({ success: true, data: results });
      });
    });

    app.get(`/${route}/:id`, authenticateToken, (req, res, next) => {
      db.query(`SELECT * FROM ${table.name} WHERE ${table.pk} = ?`, [req.params.id], (err, results) => {
        if (err) return next({ code: 'DB_ERROR', message: err.message });
        if (results.length === 0) return res.status(404).json(formatError('NOT_FOUND', `${table.name} record not found`));
        res.json({ success: true, data: results[0] });
      });
    });

    app.post(`/${route}`, authenticateToken, (req, res, next) => {
      const keys = Object.keys(req.body);
      const values = Object.values(req.body);
      db.query(`INSERT INTO ${table.name} (${keys.join(', ')}) VALUES (${keys.map(() => '?').join(', ')})`, values, (err, results) => {
        if (err) return next({ code: 'DB_ERROR', message: err.message });
        logAudit(table.name, results.insertId, 'create', req.user.user_id);
        res.json({ success: true, id: results.insertId });
      });
    });

    app.put(`/${route}/:id`, authenticateToken, (req, res, next) => {
      const keys = Object.keys(req.body);
      const values = Object.values(req.body);
      const setClause = keys.map(k => `${k} = ?`).join(', ');
      db.query(`UPDATE ${table.name} SET ${setClause} WHERE ${table.pk} = ?`, [...values, req.params.id], (err, results) => {
        if (err) return next({ code: 'DB_ERROR', message: err.message });
        logAudit(table.name, parseInt(req.params.id), 'update', req.user.user_id);
        res.json({ success: true, affected: results.affectedRows });
      });
    });

    app.delete(`/${route}/:id`, authenticateToken, (req, res, next) => {
      db.query(`DELETE FROM ${table.name} WHERE ${table.pk} = ?`, [req.params.id], (err) => {
        if (err) return next({ code: 'DB_ERROR', message: err.message });
        logAudit(table.name, parseInt(req.params.id), 'delete', req.user.user_id);
        res.json({ success: true });
      });
    });
  });
});

// Notification-specific routes
app.get('/notifications/unread-count', authenticateToken, (req, res, next) => {
  const userId = req.user.user_id;
  db.query('SELECT COUNT(*) as unread FROM notifications WHERE user_id = ? AND is_read = 0', [userId], (err, results) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message });
    res.json({ success: true, unread: results[0].unread });
  });
});

app.post('/notifications/:id/read', authenticateToken, (req, res, next) => {
  db.query('UPDATE notifications SET is_read = 1 WHERE notification_id = ?', [req.params.id], (err) => {
    if (err) return next({ code: 'DB_ERROR', message: err.message });
    res.json({ success: true });
  });
});

// Sync Endpoints

// Map camelCase Room entity keys to MySQL snake_case columns
const columnMap = {
  userId: 'user_id', username: 'username', passwordHash: 'password_hash', role: 'role',
  email: 'email', phone: 'phone', nationalIdNo: 'national_id_no', idNumber: 'id_number',
  county: 'county', subCounty: 'sub_county', photo: 'photo', photoUrl: 'photo_url',
  photoMimeType: 'photo_mime_type', photoSize: 'photo_size', latitude: 'latitude', longitude: 'longitude',
  createdAt: 'created_at', updatedAt: 'updated_at', lastLogin: 'last_login', isActive: 'is_active',
  childId: 'child_id', caseNumber: 'case_number', firstName: 'first_name', lastName: 'last_name',
  middleName: 'middle_name', gender: 'gender', dateOfBirth: 'date_of_birth',
  birthCertificateNo: 'birth_certificate_no', nationality: 'nationality',
  currentCounty: 'current_county', currentStatus: 'current_status',
  isEmancipated: 'is_emancipated', emancipationDate: 'emancipation_date',
  emancipationReason: 'emancipation_reason', createdBy: 'created_by',
  assignedCaseWorker: 'assigned_case_worker', riskLevel: 'risk_level', casePriority: 'case_priority',
  familyId: 'family_id', primaryContactName: 'primary_contact_name',
  secondaryContactName: 'secondary_contact_name', address: 'address', city: 'city',
  state: 'state', country: 'country', status: 'status', householdSize: 'household_size',
  maximumCapacity: 'maximum_capacity', currentOccupancy: 'current_occupancy',
  familyRegistrationNo: 'family_registration_no', familyType: 'family_type',
  registrationStatus: 'registration_status', syncStatus: 'sync_status',
  lastSyncedAt: 'last_synced_at', guardianId: 'guardian_id', relationship: 'relationship',
  isPrimary: 'is_primary', legalDocPath: 'legal_doc_path', legalDocMimeType: 'legal_doc_mime_type',
  legalDocSize: 'legal_doc_size', verificationStatus: 'verification_status',
  placementId: 'placement_id', placementType: 'placement_type', startDate: 'start_date',
  endDate: 'end_date', isCurrent: 'is_current', sourceFamilyId: 'source_family_id',
  destinationFamilyId: 'destination_family_id', organization: 'organization',
  placementAddress: 'placement_address', contactPerson: 'contact_person',
  contactPhone: 'contact_phone', contactEmail: 'contact_email', notes: 'notes',
  recordId: 'record_id', visitDate: 'visit_date', doctorName: 'doctor_name',
  hospitalName: 'hospital_name', diagnosis: 'diagnosis', treatment: 'treatment',
  medications: 'medications', followUpDate: 'follow_up_date',
  isImmunization: 'is_immunization', immunizationType: 'immunization_type',
  medicalReportMimeType: 'medical_report_mime_type', medicalReportSize: 'medical_report_size',
  reportId: 'report_id', reportDate: 'report_date', reportTitle: 'report_title',
  reportType: 'report_type', content: 'content', isConfidential: 'is_confidential',
  reportMimeType: 'report_mime_type', reportSize: 'report_size',
  moneyId: 'money_id', amount: 'amount', transactionType: 'transaction_type',
  description: 'description', date: 'date', receiptMimeType: 'receipt_mime_type',
  receiptSize: 'receipt_size', schoolName: 'school_name', grade: 'grade',
  enrollmentDate: 'enrollment_date', reportCardMimeType: 'report_card_mime_type',
  reportCardSize: 'report_card_size', documentId: 'document_id', documentType: 'document_type',
  fileName: 'file_name', fileType: 'file_type', fileSize: 'file_size', filePath: 'file_path',
  mimeType: 'mime_type', uploadedAt: 'uploaded_at', uploadedBy: 'uploaded_by',
  caseId: 'case_id', courtName: 'court_name', hearingDate: 'hearing_date', outcome: 'outcome',
  applicationId: 'application_id', applicationNumber: 'application_number',
  submittedAt: 'submitted_at', studyId: 'study_id', homeStudyIdMap: 'home_study_idMap',
  startedAt: 'started_at', completedAt: 'completed_at', result: 'result',
  studyReportMimeType: 'study_report_mime_type', studyReportSize: 'study_report_size',
  taskId: 'task_id', caseWorkerId: 'case_worker_id', dueDate: 'due_date',
  matchId: 'match_id', matchedAt: 'matched_at', checkId: 'check_id',
  clearanceCertificatePath: 'clearance_certificate_path',
  clearanceMimeType: 'clearance_mime_type', clearanceSize: 'clearance_size',
  logId: 'log_id', tableName: 'table_name', changedBy: 'changed_by', changedAt: 'changed_at',
  oldData: 'old_data', newData: 'new_data', action: 'action',
  notificationId: 'notification_id', title: 'title', message: 'message',
  isRead: 'is_read', sentAt: 'sent_at', permissionId: 'permission_id',
  name: 'name', settingId: 'setting_id', settingKey: 'setting_key',
  settingValue: 'setting_value', category: 'category',
  eventId: 'event_id', triggeredBy: 'triggered_by', accuracyMeters: 'accuracy_meters',
  channelsAlerted: 'channels_alerted', nearestStationName: 'nearest_station_name',
  nearestStationPhone: 'nearest_station_phone', nearestStationDistanceKm: 'nearest_station_distance_km',
  resolvedAt: 'resolved_at', resolvedBy: 'resolved_by',
  sosEventId: 'sos_event_id', accuracy: 'accuracy', timestamp: 'timestamp',
  // Dashboard enhancement table columns
  taskId: 'task_id', assignedTo: 'assigned_to', relatedEntityType: 'related_entity_type',
  relatedEntityId: 'related_entity_id',
  actionId: 'action_id', assigneeId: 'assignee_id', relatedCaseId: 'related_case_id',
  metricId: 'metric_id', metricName: 'metric_name', metricValue: 'metric_value',
  previousValue: 'previous_value', trendPercentage: 'trend_percentage',
  calculatedDate: 'calculated_date', dateRangeDays: 'date_range_days',
  preferenceId: 'preference_id', layoutType: 'layout_type', showMetrics: 'show_metrics',
  showAlerts: 'show_alerts', showActionItems: 'show_action_items',
  showRecentUpdates: 'show_recent_updates', darkMode: 'dark_mode',
  notificationFrequency: 'notification_frequency', quietHoursEnabled: 'quiet_hours_enabled',
  quietHoursStart: 'quiet_hours_start', quietHoursEnd: 'quiet_hours_end',
  dateId: 'date_id', dateType: 'date_type', eventDate: 'event_date',
  isCompleted: 'is_completed', completedDate: 'completed_date',
  messageId: 'message_id', senderId: 'sender_id', recipientId: 'recipient_id',
  assessmentId: 'assessment_id', assessmentDate: 'assessment_date',
  safetyScore: 'safety_score', maltreatmentRiskIndicators: 'maltreatment_risk_indicators',
  behavioralConcerns: 'behavioral_concerns', medicalHealthRisks: 'medical_health_risks',
  educationalGaps: 'educational_gaps', assessmentBy: 'assessment_by',
  planId: 'plan_id', planNumber: 'plan_number', primaryGoal: 'primary_goal',
  secondaryGoal: 'secondary_goal', tertiaryGoal: 'tertiary_goal',
  reviewDate: 'review_date', completionDate: 'completion_date',
  concurrentPlanning: 'concurrent_planning',
  caseloadId: 'caseload_id', workerId: 'worker_id',
  activeCases: 'active_cases', pendingReviews: 'pending_reviews',
  capacityPercentage: 'capacity_percentage',
  flagId: 'flag_id', flagType: 'flag_type',
  activityId: 'activity_id', activityType: 'activity_type',
  activityDate: 'activity_date', activityTime: 'activity_time',
  caseworkerId: 'caseworker_id', durationMinutes: 'duration_minutes',
  deadlineId: 'deadline_id', deadlineType: 'deadline_type',
  responsibleParty: 'responsible_party', extensionDate: 'extension_date',
  extensionReason: 'extension_reason', completionNotes: 'completion_notes',
  approvalId: 'approval_id', approvalType: 'approval_type',
  submittedBy: 'submitted_by', reviewedBy: 'reviewed_by',
  submissionComments: 'submission_comments', reviewComments: 'review_comments',
  revisionRequestedOn: 'revision_requested_on', submittedDate: 'submitted_date',
  reviewedDate: 'reviewed_date', requiredApproval: 'required_approval',
  compatibilityId: 'compatibility_id', compatibilityScore: 'compatibility_score',
  medicalNeedsSupport: 'medical_needs_support',
  behavioralNeedsSupport: 'behavioral_needs_support',
  educationalNeedsSupport: 'educational_needs_support',
  emotionalSupportCapacity: 'emotional_support_capacity',
  geographicPreferencesMatch: 'geographic_preferences_match',
  religiousPreferenceMatch: 'religious_preference_match',
  culturalFitScore: 'cultural_fit_score', specialConsiderations: 'special_considerations',
  assessedBy: 'assessed_by', lastReviewed: 'last_reviewed',
  workloadId: 'workload_id', caseworkerIdMap: 'caseworker_id',
  trackingDate: 'tracking_date', totalActiveCases: 'total_active_cases',
  casesWithUrgentFlags: 'cases_with_urgent_flags', overdueTasksCount: 'overdue_tasks_count',
  scheduledActivitiesToday: 'scheduled_activities_today',
  completedActivities: 'completed_activities', documentsProcessed: 'documents_processed',
  approvalsPending: 'approvals_pending', timeLoggedHours: 'time_logged_hours',
};

// Fields that are BLOB/binary and should be excluded from JSON sync
const blobFields = new Set([
  'photoData', 'photo_data', 'legalDocData', 'legal_doc_data', 'fileData', 'file_data',
  'medicalReportData', 'medical_report_data', 'reportData', 'report_data',
  'receiptData', 'receipt_data', 'reportCardData', 'report_card_data',
  'studyReportData', 'study_report_data', 'clearanceCertificateData', 'clearance_certificate_data'
]);

// Known primary keys for each table
const tablePkMap = {};
dbTables.forEach(t => { tablePkMap[t.name] = t.pk; });
tablePkMap['court_cases'] = 'case_id';
tablePkMap['guardians'] = 'guardian_id';
tablePkMap['audit_logs'] = 'log_id';
tablePkMap['permissions'] = 'permission_id';
tablePkMap['user_permissions'] = 'id';
tablePkMap['system_settings'] = 'setting_id';
tablePkMap['families'] = 'family_id';
tablePkMap['emergency_events'] = 'event_id';
tablePkMap['sos_location_history'] = 'history_id';
tablePkMap['fcm_tokens'] = 'token_id';
  tablePkMap['counties'] = 'county_id';
  tablePkMap['tasks'] = 'task_id';
  tablePkMap['action_items'] = 'action_id';
  tablePkMap['dashboard_metrics'] = 'metric_id';
  tablePkMap['dashboard_preferences'] = 'preference_id';
  tablePkMap['critical_dates'] = 'date_id';
  tablePkMap['worker_messages'] = 'message_id';
  tablePkMap['risk_assessments'] = 'assessment_id';
  tablePkMap['permanency_plans'] = 'plan_id';
  tablePkMap['caseload'] = 'caseload_id';
  tablePkMap['case_urgency_flags'] = 'flag_id';
  tablePkMap['case_activities'] = 'activity_id';
  tablePkMap['case_deadlines'] = 'deadline_id';
  tablePkMap['case_approvals'] = 'approval_id';
  tablePkMap['placement_compatibility'] = 'compatibility_id';
  tablePkMap['workload_tracking'] = 'workload_id';

function toColumnName(key) {
  if (columnMap[key]) return columnMap[key];
  if (blobFields.has(key)) return null;
  return key.replace(/[A-Z]/g, letter => '_' + letter.toLowerCase());
}

function processSyncItem(item, callback) {
  const { table_name, operation, record_id, payload } = item;
  let data;
  try {
    data = JSON.parse(payload);
  } catch (e) {
    return callback(null, { id: record_id, message: 'Invalid JSON payload' });
  }
  const columns = [];
  const values = [];
  for (const [key, val] of Object.entries(data)) {
    if (blobFields.has(key)) continue;
    const colName = toColumnName(key);
    if (!colName) continue;
    const pk = tablePkMap[table_name];
    if (operation === 'INSERT' && colName === pk && (val === 0 || val === null)) continue;
    columns.push(colName);
    values.push(val);
  }
  if (columns.length === 0) {
    return callback(null, { id: record_id, message: 'No data fields to sync' });
  }
  const pk = tablePkMap[table_name] || 'id';
  if (operation === 'DELETE') {
    db.query(`DELETE FROM ${table_name} WHERE ${pk} = ?`, [record_id], (err) => {
      if (err) return callback(err);
      callback(null);
    });
  } else if (operation === 'UPDATE') {
    const setClause = columns.map(c => `${c} = ?`).join(', ');
    db.query(`UPDATE ${table_name} SET ${setClause} WHERE ${pk} = ?`, [...values, record_id], (err) => {
      if (err) {
        const insertClause = columns.join(', ');
        const placeholders = columns.map(() => '?').join(', ');
        db.query(`INSERT INTO ${table_name} (${insertClause}) VALUES (${placeholders})`, values, (insertErr) => {
          if (insertErr) return callback(insertErr);
          callback(null);
        });
      } else {
        callback(null);
      }
    });
  } else {
    const insertClause = columns.join(', ');
    const placeholders = columns.map(() => '?').join(', ');
    db.query(`INSERT INTO ${table_name} (${insertClause}) VALUES (${placeholders})`, values, (err) => {
      if (err) {
        if (err.code === 'ER_DUP_ENTRY') {
          const setClause = columns.map(c => `${c} = ?`).join(', ');
          db.query(`UPDATE ${table_name} SET ${setClause} WHERE ${pk} = ?`, [...values, record_id], (updateErr) => {
            if (updateErr) return callback(updateErr);
            callback(null);
          });
        } else {
          return callback(err);
        }
      } else {
        callback(null);
      }
    });
  }
}

app.post('/api/v2/sync/push', authenticateToken, (req, res, next) => {
  const syncItems = req.body;
  if (!Array.isArray(syncItems)) {
    return res.status(400).json({ success: false, applied: 0, errors: ['Request body must be an array'] });
  }
  let appliedCount = 0;
  let errors = [];
  let completed = 0;
  if (syncItems.length === 0) {
    return res.json({ success: true, applied: 0, errors: [] });
  }
  syncItems.forEach((item) => {
    processSyncItem(item, (err, syncError) => {
      completed++;
      if (err) {
        console.error(`Sync push error for ${item.table_name}/${item.record_id}:`, err.message);
        errors.push(`${item.table_name}/${item.record_id}: ${err.message}`);
      } else if (syncError) {
        errors.push(`${item.table_name}/${item.record_id}: ${syncError.message}`);
      } else {
        appliedCount++;
        logAudit(item.table_name, parseInt(item.record_id), item.operation.toLowerCase(), req.user.user_id);
      }
      if (completed === syncItems.length) {
        res.json({ success: true, applied: appliedCount, errors });
      }
    });
  });
});

app.get('/api/v2/sync/pull', authenticateToken, (req, res, next) => {
  const { since } = req.query;
  if (!db) {
    return res.status(500).json({ success: false, error: 'Database not initialized' });
  }
  const sinceDate = since ? new Date(parseInt(since) * 1000).toISOString().slice(0, 19).replace('T', ' ') : null;
  const pullQueries = [
    { table: 'users', key: 'users' },
    { table: 'children', key: 'children' },
    { table: 'families', key: 'families' },
    { table: 'family_profile', key: 'family_profile' },
    { table: 'placements', key: 'placements' },
    { table: 'medical_records', key: 'medical_records' },
    { table: 'education_records', key: 'education_records' },
    { table: 'money_records', key: 'money_records' },
    { table: 'documents', key: 'documents' },
    { table: 'case_reports', key: 'case_reports' },
    { table: 'court_cases', key: 'court_cases' },
    { table: 'guardians', key: 'guardians' },
    { table: 'adoption_applications', key: 'adoption_applications' },
    { table: 'home_studies', key: 'home_studies' },
    { table: 'foster_tasks', key: 'foster_tasks' },
    { table: 'foster_matches', key: 'foster_matches' },
    { table: 'background_checks', key: 'background_checks' },
    { table: 'notifications', key: 'notifications' },
    { table: 'audit_logs', key: 'audit_logs' },
    { table: 'permissions', key: 'permissions' },
    { table: 'user_permissions', key: 'user_permissions' },
    { table: 'system_settings', key: 'system_settings' },
    { table: 'tasks', key: 'tasks' },
    { table: 'action_items', key: 'action_items' },
    { table: 'dashboard_metrics', key: 'dashboard_metrics' },
    { table: 'dashboard_preferences', key: 'dashboard_preferences' },
    { table: 'critical_dates', key: 'critical_dates' },
    { table: 'worker_messages', key: 'worker_messages' },
    { table: 'risk_assessments', key: 'risk_assessments' },
    { table: 'permanency_plans', key: 'permanency_plans' },
    { table: 'caseload', key: 'caseload' },
    { table: 'case_urgency_flags', key: 'case_urgency_flags' },
    { table: 'case_activities', key: 'case_activities' },
    { table: 'case_deadlines', key: 'case_deadlines' },
    { table: 'case_approvals', key: 'case_approvals' },
    { table: 'placement_compatibility', key: 'placement_compatibility' },
    { table: 'workload_tracking', key: 'workload_tracking' },
  ];
  const result = {};
  let completed = 0;
  const total = pullQueries.length;
  pullQueries.forEach(({ table, key }) => {
    const safeWhereClause = sinceDate ? 'WHERE updated_at >= ?' : '';
    const query = `SELECT * FROM ${table} ${safeWhereClause}`;
    const params = sinceDate ? [sinceDate] : [];
    db.query(query, params, (err, rows) => {
      completed++;
      if (err) {
        console.error(`Sync pull error for ${table}:`, err.message);
        result[key] = [];
      } else {
        result[key] = rows.map(row => {
          const converted = {};
          for (const [col, val] of Object.entries(row)) {
            const camelKey = Object.entries(columnMap).find(([ck, cc]) => cc === col);
            if (camelKey) {
              converted[camelKey[0]] = val;
            } else {
              converted[col.replace(/_([a-z])/g, (_, c) => c.toUpperCase())] = val;
            }
          }
          return converted;
        });
      }
      if (completed === total) {
        res.json({ success: true, ...result });
      }
    });
  });
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
const server = app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server listening on port ${PORT}`);
});

server.on('error', (e) => {
  if (e.code === 'EADDRINUSE') {
    const nextPort = Number(PORT) + 1;
    console.log(`Port ${PORT} is busy, trying ${nextPort}...`);
    app.listen(nextPort, '0.0.0.0');
  }
});
