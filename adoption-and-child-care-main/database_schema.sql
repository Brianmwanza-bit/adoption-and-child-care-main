-- ===============================
-- Table: users
-- ===============================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    email VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- Table: children
-- ===============================
CREATE TABLE children (
    child_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    gender VARCHAR(10),
    date_of_birth DATE,
    birth_certificate_no VARCHAR(50) UNIQUE,
    nationality VARCHAR(50),
    photo_url VARCHAR(255),
    is_emancipated BOOLEAN DEFAULT FALSE,
    emancipation_date DATE,
    emancipation_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ===============================
-- Table: court_cases
-- ===============================
CREATE TABLE court_cases (
    case_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    case_number VARCHAR(50),
    court_name VARCHAR(100),
    judge_name VARCHAR(100),
    hearing_date DATE,
    outcome TEXT,
    next_hearing_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id)
);

-- ===============================
-- Table: placements
-- ===============================
CREATE TABLE placements (
    placement_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    placement_type VARCHAR(50),
    start_date DATE,
    end_date DATE,
    organization VARCHAR(150),
    placement_address TEXT,
    contact_person VARCHAR(100),
    contact_phone VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id)
);

-- ===============================
-- Table: medical_records
-- ===============================
CREATE TABLE medical_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    visit_date DATE,
    doctor_name VARCHAR(100),
    hospital_name VARCHAR(150),
    diagnosis TEXT,
    treatment TEXT,
    follow_up_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id)
);

-- ===============================
-- Table: guardians
-- ===============================
CREATE TABLE guardians (
    guardian_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    relationship VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    legal_doc_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id)
);

-- ===============================
-- Table: case_reports
-- ===============================
CREATE TABLE case_reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    user_id INT NOT NULL,
    report_date DATE,
    report_title VARCHAR(150),
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ===============================
-- Table: money_records
-- ===============================
CREATE TABLE money_records (
    money_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    amount DECIMAL(12,2),
    transaction_type VARCHAR(50),
    description TEXT,
    date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id)
);

-- ===============================
-- Table: education_records
-- ===============================
CREATE TABLE education_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    school_name VARCHAR(150),
    grade VARCHAR(50),
    performance TEXT,
    teacher_contact VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id)
);

-- ===============================
-- Table: documents
-- ===============================
CREATE TABLE documents (
    document_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    file_name VARCHAR(255),
    file_type VARCHAR(50),
    file_path VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id)
); 

CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    record_id INT NOT NULL,
    action VARCHAR(20) NOT NULL, -- INSERT, UPDATE, DELETE
    changed_by INT,              -- user_id (FK)
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    old_data TEXT,               -- JSON of old values
    new_data TEXT,               -- JSON of new values
    FOREIGN KEY (changed_by) REFERENCES users(user_id)
);

DELIMITER $$
CREATE TRIGGER children_update_audit
AFTER UPDATE ON children
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (table_name, record_id, action, changed_by, old_data, new_data)
    VALUES (
        'children',
        NEW.child_id,
        'UPDATE',
        NULL, -- Set this to the user_id in your app logic if possible
        JSON_OBJECT(
            'first_name', OLD.first_name,
            'last_name', OLD.last_name,
            'gender', OLD.gender
            -- add more fields as needed
        ),
        JSON_OBJECT(
            'first_name', NEW.first_name,
            'last_name', NEW.last_name,
            'gender', NEW.gender
            -- add more fields as needed
        )
    );
END $$
DELIMITER ; 

CREATE TABLE permissions (
    permission_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE user_permissions (
    user_id INT,
    permission_id INT,
    PRIMARY KEY (user_id, permission_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id)
);

ALTER TABLE children ADD COLUMN middle_name VARCHAR(100) AFTER first_name;
ALTER TABLE court_cases ADD COLUMN status VARCHAR(50) AFTER outcome;

DELIMITER $$
CREATE TRIGGER children_update_timestamp
BEFORE UPDATE ON children
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE GetChildrenByGuardian(IN guardianId INT)
BEGIN
    SELECT c.*
    FROM children c
    JOIN guardians g ON c.child_id = g.child_id
    WHERE g.guardian_id = guardianId;
END $$
DELIMITER ; 