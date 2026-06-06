-- Database Schema Updates for Modern Dashboard

-- 1. TASKS TABLE (for task management)
CREATE TABLE IF NOT EXISTS tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(50) NOT NULL, -- urgent, high, normal, low
    status VARCHAR(50) NOT NULL, -- pending, in_progress, completed, overdue
    due_date DATE NOT NULL,
    assigned_to INT,
    created_by INT,
    related_entity_type VARCHAR(100), -- child, family, placement, case
    related_entity_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_to) REFERENCES users(user_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- 2. ACTION ITEMS TABLE (high-priority tasks visible on dashboard)
CREATE TABLE IF NOT EXISTS action_items (
    action_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    priority VARCHAR(50) NOT NULL, -- urgent, high, normal
    due_date DATE NOT NULL,
    assignee_id INT,
    related_case_id INT,
    status VARCHAR(50) NOT NULL, -- pending, in_progress, completed
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignee_id) REFERENCES users(user_id),
    FOREIGN KEY (related_case_id) REFERENCES case_reports(case_id)
);

-- 3. DASHBOARD METRICS TABLE (for KPI tracking)
CREATE TABLE IF NOT EXISTS dashboard_metrics (
    metric_id INT AUTO_INCREMENT PRIMARY KEY,
    metric_name VARCHAR(100) NOT NULL, -- children_count, families_count, placement_stability, etc.
    metric_value DECIMAL(10, 2),
    previous_value DECIMAL(10, 2),
    trend_percentage DECIMAL(5, 2),
    calculated_date DATE NOT NULL,
    date_range_days INT, -- e.g., 7 for last 7 days, 30 for last 30 days
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. USER TASK PREFERENCES TABLE (for dashboard customization)
CREATE TABLE IF NOT EXISTS dashboard_preferences (
    preference_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    layout_type VARCHAR(50) DEFAULT 'compact', -- compact, normal, spacious
    show_metrics BOOLEAN DEFAULT TRUE,
    show_alerts BOOLEAN DEFAULT TRUE,
    show_action_items BOOLEAN DEFAULT TRUE,
    show_recent_updates BOOLEAN DEFAULT TRUE,
    dark_mode BOOLEAN DEFAULT FALSE,
    notification_frequency VARCHAR(50) DEFAULT 'immediate', -- immediate, daily, weekly
    quiet_hours_enabled BOOLEAN DEFAULT FALSE,
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 5. CRITICAL DATES TABLE (for compliance tracking)
CREATE TABLE IF NOT EXISTS critical_dates (
    date_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT,
    date_type VARCHAR(100) NOT NULL, -- birth_date, entry_date, 30day_review, 90day_review, 12month_permanency_hearing, etc.
    event_date DATE NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id)
);

-- 6. WORKER MESSAGING TABLE (for caseworker collaboration)
CREATE TABLE IF NOT EXISTS worker_messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    recipient_id INT NOT NULL,
    case_id INT,
    title VARCHAR(255),
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (recipient_id) REFERENCES users(user_id),
    FOREIGN KEY (case_id) REFERENCES case_reports(case_id)
);

-- 7. RISK ASSESSMENT TABLE (for safety tracking)
CREATE TABLE IF NOT EXISTS risk_assessments (
    assessment_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    assessment_date DATE NOT NULL,
    safety_score INT, -- 1-100 scale
    risk_level VARCHAR(50), -- low, medium, high, critical
    maltreatment_risk_indicators JSON, -- JSON array of risk factors
    behavioral_concerns TEXT,
    medical_health_risks TEXT,
    educational_gaps TEXT,
    assessment_by INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id),
    FOREIGN KEY (assessment_by) REFERENCES users(user_id)
);

-- 8. PERMANENCY PLANNING TABLE (for case permanency tracking)
CREATE TABLE IF NOT EXISTS permanency_plans (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    plan_number INT, -- 1st plan, 2nd plan, 3rd plan
    primary_goal VARCHAR(100) NOT NULL, -- adoption, reunification, kinship, guardianship, emancipation
    secondary_goal VARCHAR(100),
    tertiary_goal VARCHAR(100),
    start_date DATE NOT NULL,
    review_date DATE,
    completion_date DATE,
    status VARCHAR(50), -- active, on_track, at_risk, completed, changed
    concurrent_planning BOOLEAN DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id)
);

-- 9. BACKGROUND CHECK TABLE (for licensing compliance)
CREATE TABLE IF NOT EXISTS background_checks (
    check_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    check_type VARCHAR(50) NOT NULL, -- FBI, State, Local, Motor Vehicle, etc.
    requested_date DATE NOT NULL,
    completed_date DATE,
    result VARCHAR(50), -- pending, passed, failed, expired
    expiration_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 10. CASELOAD TABLE (for workload management)
CREATE TABLE IF NOT EXISTS caseload (
    caseload_id INT AUTO_INCREMENT PRIMARY KEY,
    worker_id INT NOT NULL,
    date DATE NOT NULL,
    active_cases INT,
    pending_reviews INT,
    overdue_tasks INT,
    capacity_percentage INT, -- calculate: (active_cases / recommended_max) * 100
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (worker_id) REFERENCES users(user_id)
);

-- Sample indexes for performance
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to);
CREATE INDEX idx_action_items_priority ON action_items(priority);
CREATE INDEX idx_action_items_due_date ON action_items(due_date);
CREATE INDEX idx_critical_dates_child ON critical_dates(child_id);
CREATE INDEX idx_critical_dates_type ON critical_dates(date_type);
CREATE INDEX idx_permanency_plans_child ON permanency_plans(child_id);
CREATE INDEX idx_risk_assessments_child ON risk_assessments(child_id);
CREATE INDEX idx_background_checks_user ON background_checks(user_id);

-- ============================================================
-- ADDITIONAL TABLES FOR MODERN DASHBOARD ENHANCEMENTS
-- ============================================================

-- 11. CASE URGENCY FLAGS
CREATE TABLE IF NOT EXISTS case_urgency_flags (
    flag_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    flag_type ENUM('critical', 'high', 'normal') DEFAULT 'normal',
    reason VARCHAR(500) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INT,
    resolved_at TIMESTAMP NULL,
    resolved_by INT,
    INDEX idx_case_id (case_id),
    INDEX idx_flag_type (flag_type),
    INDEX idx_created_at (created_at)
);

-- 12. CASE ACTIVITIES DETAILED LOG
CREATE TABLE IF NOT EXISTS case_activities (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    activity_type ENUM('home_visit', 'contact_call', 'contact_email', 'approval', 'review', 'legal_action', 'placement_change', 'document_update', 'other') NOT NULL,
    activity_date DATE NOT NULL,
    activity_time TIME,
    title VARCHAR(255),
    notes TEXT,
    caseworker_id INT,
    location VARCHAR(255),
    duration_minutes INT,
    outcome VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_case_id (case_id),
    INDEX idx_activity_date (activity_date),
    INDEX idx_activity_type (activity_type),
    INDEX idx_caseworker_id (caseworker_id)
);

-- 13. CASE DEADLINES TRACKING
CREATE TABLE IF NOT EXISTS case_deadlines (
    deadline_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    deadline_type ENUM('home_study_review', 'placement_decision', 'legal_review', 'court_hearing', 'documentation_renewal', 'family_contact_required', 'health_checkup', 'school_enrollment', 'background_check_renewal', 'other') NOT NULL,
    due_date DATE NOT NULL,
    title VARCHAR(255),
    description TEXT,
    status ENUM('pending', 'completed', 'overdue', 'extended', 'waived') DEFAULT 'pending',
    priority ENUM('critical', 'high', 'normal') DEFAULT 'normal',
    responsible_party INT,
    extension_date DATE,
    extension_reason TEXT,
    completion_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    INDEX idx_case_id (case_id),
    INDEX idx_due_date (due_date),
    INDEX idx_status (status),
    INDEX idx_priority (priority)
);

-- 14. CASE APPROVALS WORKFLOW
CREATE TABLE IF NOT EXISTS case_approvals (
    approval_id INT PRIMARY KEY AUTO_INCREMENT,
    case_id INT NOT NULL,
    approval_type ENUM('home_study', 'placement_authorization', 'document_verification', 'legal_review', 'supervisor_sign_off', 'family_approval', 'child_readiness') NOT NULL,
    status ENUM('pending', 'approved', 'rejected', 'needs_revision', 'escalated') DEFAULT 'pending',
    submitted_by INT,
    reviewed_by INT,
    submission_comments TEXT,
    review_comments TEXT,
    revision_requested_on TIMESTAMP NULL,
    submitted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_date TIMESTAMP NULL,
    required_approval BOOLEAN DEFAULT TRUE,
    INDEX idx_case_id (case_id),
    INDEX idx_approval_type (approval_type),
    INDEX idx_status (status),
    INDEX idx_reviewed_by (reviewed_by)
);

-- 15. PLACEMENT COMPATIBILITY ASSESSMENT
CREATE TABLE IF NOT EXISTS placement_compatibility (
    compatibility_id INT PRIMARY KEY AUTO_INCREMENT,
    child_id INT NOT NULL,
    family_id INT NOT NULL,
    compatibility_score INT,
    medical_needs_support BOOLEAN,
    behavioral_needs_support BOOLEAN,
    educational_needs_support BOOLEAN,
    emotional_support_capacity INT,
    geographic_preferences_match BOOLEAN,
    religious_preference_match BOOLEAN,
    cultural_fit_score INT,
    special_considerations TEXT,
    notes TEXT,
    assessment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assessed_by INT,
    last_reviewed DATE,
    INDEX idx_child_id (child_id),
    INDEX idx_family_id (family_id),
    INDEX idx_compatibility_score (compatibility_score),
    INDEX idx_assessment_date (assessment_date)
);

-- 16. CASEWORKER WORKLOAD TRACKING
CREATE TABLE IF NOT EXISTS workload_tracking (
    workload_id INT PRIMARY KEY AUTO_INCREMENT,
    caseworker_id INT NOT NULL,
    tracking_date DATE NOT NULL,
    total_active_cases INT,
    cases_with_urgent_flags INT,
    overdue_tasks_count INT,
    scheduled_activities_today INT,
    completed_activities INT,
    documents_processed INT,
    approvals_pending INT,
    time_logged_hours DECIMAL(5,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_caseworker_id (caseworker_id),
    INDEX idx_tracking_date (tracking_date),
    UNIQUE KEY unique_caseworker_date (caseworker_id, tracking_date)
);

-- ============================================================
-- ALTER EXISTING TABLES WITH NEW COLUMNS
-- ============================================================

ALTER TABLE cases ADD COLUMN IF NOT EXISTS urgency_level ENUM('critical', 'high', 'normal') DEFAULT 'normal';
ALTER TABLE cases ADD COLUMN IF NOT EXISTS days_in_status INT DEFAULT 0;
ALTER TABLE cases ADD COLUMN IF NOT EXISTS next_deadline DATE;
ALTER TABLE cases ADD COLUMN IF NOT EXISTS next_deadline_type VARCHAR(50);
ALTER TABLE cases ADD COLUMN IF NOT EXISTS assigned_supervisor_id INT;
ALTER TABLE cases ADD COLUMN IF NOT EXISTS last_activity_date TIMESTAMP;
ALTER TABLE cases ADD COLUMN IF NOT EXISTS case_priority INT DEFAULT 5;

ALTER TABLE children ADD COLUMN IF NOT EXISTS medical_needs TEXT;
ALTER TABLE children ADD COLUMN IF NOT EXISTS behavioral_needs TEXT;
ALTER TABLE children ADD COLUMN IF NOT EXISTS educational_notes TEXT;
ALTER TABLE children ADD COLUMN IF NOT EXISTS emergency_contact_name VARCHAR(255);
ALTER TABLE children ADD COLUMN IF NOT EXISTS emergency_contact_phone VARCHAR(20);
ALTER TABLE children ADD COLUMN IF NOT EXISTS emergency_contact_relation VARCHAR(50);
ALTER TABLE children ADD COLUMN IF NOT EXISTS health_status ENUM('healthy', 'at_risk', 'critical', 'receiving_treatment') DEFAULT 'healthy';
ALTER TABLE children ADD COLUMN IF NOT EXISTS disability_info TEXT;
ALTER TABLE children ADD COLUMN IF NOT EXISTS vaccination_status ENUM('complete', 'partial', 'not_started', 'exempt') DEFAULT 'not_started';
ALTER TABLE children ADD COLUMN IF NOT EXISTS school_enrolled BOOLEAN DEFAULT FALSE;
ALTER TABLE children ADD COLUMN IF NOT EXISTS school_name VARCHAR(255);
ALTER TABLE children ADD COLUMN IF NOT EXISTS school_grade VARCHAR(20);

ALTER TABLE families ADD COLUMN IF NOT EXISTS experience_with_special_needs BOOLEAN DEFAULT FALSE;
ALTER TABLE families ADD COLUMN IF NOT EXISTS max_children_accepted INT;
ALTER TABLE families ADD COLUMN IF NOT EXISTS min_age_preferred INT;
ALTER TABLE families ADD COLUMN IF NOT EXISTS max_age_preferred INT;
ALTER TABLE families ADD COLUMN IF NOT EXISTS preferred_gender ENUM('male', 'female', 'either') DEFAULT 'either';
ALTER TABLE families ADD COLUMN IF NOT EXISTS placement_type_preferences VARCHAR(255);
ALTER TABLE families ADD COLUMN IF NOT EXISTS home_study_status ENUM('pending', 'approved', 'conditional', 'expired', 'in_progress') DEFAULT 'pending';
ALTER TABLE families ADD COLUMN IF NOT EXISTS home_study_date DATE;
ALTER TABLE families ADD COLUMN IF NOT EXISTS home_study_expiry_date DATE;
ALTER TABLE families ADD COLUMN IF NOT EXISTS home_study_assessor_id INT;
ALTER TABLE families ADD COLUMN IF NOT EXISTS family_stability_score INT;
ALTER TABLE families ADD COLUMN IF NOT EXISTS previous_placements_count INT DEFAULT 0;
ALTER TABLE families ADD COLUMN IF NOT EXISTS previous_placements_successful INT DEFAULT 0;
ALTER TABLE families ADD COLUMN IF NOT EXISTS family_income_level VARCHAR(50);
ALTER TABLE families ADD COLUMN IF NOT EXISTS employment_status VARCHAR(255);
ALTER TABLE families ADD COLUMN IF NOT EXISTS support_system_rating INT;
ALTER TABLE families ADD COLUMN IF NOT EXISTS cultural_background VARCHAR(255);
ALTER TABLE families ADD COLUMN IF NOT EXISTS religious_affiliation VARCHAR(255);
ALTER TABLE families ADD COLUMN IF NOT EXISTS languages_spoken VARCHAR(255);

-- ============================================================
-- VIEWS FOR DASHBOARD QUERIES
-- ============================================================

-- Active Cases with Urgency View
CREATE OR REPLACE VIEW vw_active_cases_urgency AS
SELECT
    c.case_id,
    c.assigned_caseworker_id,
    c.status,
    c.urgency_level,
    c.next_deadline,
    c.days_in_status,
    COUNT(DISTINCT cuf.flag_id) AS urgent_flag_count
FROM cases c
LEFT JOIN case_urgency_flags cuf ON c.case_id = cuf.case_id AND cuf.resolved_at IS NULL
WHERE c.status IN ('active', 'pending', 'in_progress')
GROUP BY c.case_id;

-- Caseworker Workload Summary View
CREATE OR REPLACE VIEW vw_caseworker_workload AS
SELECT
    u.user_id,
    u.username,
    COUNT(DISTINCT c.case_id) AS total_cases,
    SUM(CASE WHEN c.urgency_level = 'critical' THEN 1 ELSE 0 END) AS critical_cases,
    SUM(CASE WHEN c.urgency_level = 'high' THEN 1 ELSE 0 END) AS high_priority_cases,
    COUNT(DISTINCT cd.deadline_id) AS pending_deadlines,
    COUNT(DISTINCT CASE WHEN cd.status = 'overdue' THEN cd.deadline_id END) AS overdue_count
FROM users u
LEFT JOIN cases c ON u.user_id = c.assigned_caseworker_id
LEFT JOIN case_deadlines cd ON c.case_id = cd.case_id
WHERE u.role IN ('case_manager', 'social_worker', 'caseworker')
GROUP BY u.user_id, u.username;
