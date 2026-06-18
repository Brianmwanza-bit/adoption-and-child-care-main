-- =====================================================
-- Adoption & Child Care Tracking System Database
-- Version: 3.0
-- Database: adoption_and_childcare_tracking_system_db
-- =====================================================

-- =====================================================
-- DROP ALL TABLES (Ordered for Foreign Key Safety)
-- =====================================================
DROP TABLE IF EXISTS `system_settings`;
DROP TABLE IF EXISTS `fcm_tokens`;
DROP TABLE IF EXISTS `background_checks`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `foster_matches`;
DROP TABLE IF EXISTS `foster_tasks`;
DROP TABLE IF EXISTS `adoption_applications`;
DROP TABLE IF EXISTS `home_studies`;
DROP TABLE IF EXISTS `user_permissions`;
DROP TABLE IF EXISTS `permissions`;
DROP TABLE IF EXISTS `audit_logs`;
DROP TABLE IF EXISTS `placements`;
DROP TABLE IF EXISTS `money_records`;
DROP TABLE IF EXISTS `documents`;
DROP TABLE IF EXISTS `education_records`;
DROP TABLE IF EXISTS `medical_records`;
DROP TABLE IF EXISTS `guardians`;
DROP TABLE IF EXISTS `court_cases`;
DROP TABLE IF EXISTS `case_reports`;
DROP TABLE IF EXISTS `children`;
DROP TABLE IF EXISTS `family_profile`;
DROP TABLE IF EXISTS `families`;
DROP TABLE IF EXISTS `users`;

-- =====================================================
-- CORE TABLES
-- =====================================================

CREATE TABLE `users` (
  `user_id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(100) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  `email` VARCHAR(150) DEFAULT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `national_id_no` VARCHAR(50) DEFAULT NULL,
  `county` VARCHAR(100) DEFAULT NULL,
  `sub_county` VARCHAR(100) DEFAULT NULL,
  `photo_url` VARCHAR(255) DEFAULT NULL,
  `photo_data` LONGBLOB DEFAULT NULL,
  `photo_mime_type` VARCHAR(100) DEFAULT NULL,
  `photo_size` INT(11) DEFAULT NULL,
  `latitude` DOUBLE DEFAULT NULL,
  `longitude` DOUBLE DEFAULT NULL,
  `is_active` TINYINT(1) DEFAULT 1,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_email` (`email`),
  UNIQUE KEY `idx_national_id` (`national_id_no`),
  INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `permissions` (
  `permission_id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `category` VARCHAR(50) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `idx_permission_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `user_permissions` (
  `user_permission_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `permission_id` INT(11) NOT NULL,
  `granted_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `granted_by` INT(11) DEFAULT NULL,
  PRIMARY KEY (`user_permission_id`),
  UNIQUE KEY `idx_user_permission` (`user_id`, `permission_id`),
  CONSTRAINT `fk_user_permissions_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_permissions_perm` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- FAMILY TABLES
-- =====================================================

CREATE TABLE `family_profile` (
  `family_id` INT(11) NOT NULL AUTO_INCREMENT,
  `family_registration_no` VARCHAR(50) DEFAULT NULL,
  `user_id` INT(11) NOT NULL,
  `family_type` VARCHAR(50) DEFAULT 'Foster',
  `primary_contact_name` VARCHAR(200) NOT NULL,
  `secondary_contact_name` VARCHAR(200) DEFAULT NULL,
  `email` VARCHAR(150) DEFAULT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `alternative_phone` VARCHAR(20) DEFAULT NULL,
  `address` TEXT DEFAULT NULL,
  `county` VARCHAR(100) DEFAULT NULL,
  `sub_county` VARCHAR(100) DEFAULT NULL,
  `ward` VARCHAR(100) DEFAULT NULL,
  `family_size` INT(11) DEFAULT NULL,
  `income_level` DECIMAL(10,2) DEFAULT NULL,
  `housing_type` VARCHAR(100) DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Active',
  `registration_date` DATE DEFAULT NULL,
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_name` VARCHAR(255) DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`family_id`),
  UNIQUE KEY `idx_family_reg` (`family_registration_no`),
  CONSTRAINT `fk_family_profile_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_family_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `families` (
  `family_id` INT(11) NOT NULL AUTO_INCREMENT,
  `primary_contact_name` VARCHAR(255) NOT NULL,
  `secondary_contact_name` VARCHAR(255) DEFAULT NULL,
  `email` VARCHAR(255) DEFAULT NULL,
  `phone` VARCHAR(255) DEFAULT NULL,
  `address` TEXT DEFAULT NULL,
  `county` VARCHAR(100) DEFAULT NULL,
  `sub_county` VARCHAR(100) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- CHILD RELATED TABLES
-- =====================================================

CREATE TABLE `children` (
  `child_id` INT(11) NOT NULL AUTO_INCREMENT,
  `case_number` VARCHAR(50) DEFAULT NULL,
  `first_name` VARCHAR(100) NOT NULL,
  `middle_name` VARCHAR(100) DEFAULT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `date_of_birth` DATE NOT NULL,
  `gender` VARCHAR(20) NOT NULL,
  `place_of_birth` VARCHAR(150) DEFAULT NULL,
  `county_of_origin` VARCHAR(100) DEFAULT NULL,
  `sub_county_of_origin` VARCHAR(100) DEFAULT NULL,
  `national_id_no` VARCHAR(50) DEFAULT NULL,
  `birth_certificate_no` VARCHAR(50) DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Active',
  `admission_date` DATE DEFAULT NULL,
  `photo_url` VARCHAR(255) DEFAULT NULL,
  `photo_data` LONGBLOB DEFAULT NULL,
  `photo_mime_type` VARCHAR(100) DEFAULT NULL,
  `photo_size` INT(11) DEFAULT NULL,
  `special_needs` TEXT DEFAULT NULL,
  `medical_conditions` TEXT DEFAULT NULL,
  `school_level` VARCHAR(50) DEFAULT NULL,
  `current_placement` VARCHAR(50) DEFAULT NULL,
  `case_worker_id` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`child_id`),
  UNIQUE KEY `idx_case_number` (`case_number`),
  UNIQUE KEY `idx_birth_cert` (`birth_certificate_no`),
  INDEX `idx_status` (`status`),
  INDEX `idx_case_worker` (`case_worker_id`),
  CONSTRAINT `fk_children_case_worker` FOREIGN KEY (`case_worker_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `guardians` (
  `guardian_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `relationship` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(150) DEFAULT NULL,
  `address` TEXT DEFAULT NULL,
  `county` VARCHAR(100) DEFAULT NULL,
  `sub_county` VARCHAR(100) DEFAULT NULL,
  `national_id_no` VARCHAR(50) DEFAULT NULL,
  `is_active` TINYINT(1) DEFAULT 1,
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_name` VARCHAR(255) DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`guardian_id`),
  INDEX `idx_guardians_child` (`child_id`),
  CONSTRAINT `fk_guardians_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_guardians_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `placements` (
  `placement_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `destination_family_id` INT(11) NOT NULL,
  `placement_type` VARCHAR(50) DEFAULT 'Foster Home',
  `start_date` DATE NOT NULL,
  `end_date` DATE DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Active',
  `reason` TEXT DEFAULT NULL,
  `case_worker_id` INT(11) DEFAULT NULL,
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_name` VARCHAR(255) DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`placement_id`),
  INDEX `idx_placements_child` (`child_id`),
  INDEX `idx_placements_family` (`destination_family_id`),
  INDEX `idx_placements_status` (`status`),
  CONSTRAINT `fk_placements_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_placements_family` FOREIGN KEY (`destination_family_id`) REFERENCES `family_profile` (`family_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_placements_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `medical_records` (
  `record_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `visit_date` DATE NOT NULL,
  `hospital_name` VARCHAR(150) DEFAULT NULL,
  `diagnosis` TEXT DEFAULT NULL,
  `treatment` TEXT DEFAULT NULL,
  `doctor_name` VARCHAR(100) DEFAULT NULL,
  `prescription` TEXT DEFAULT NULL,
  `follow_up_date` DATE DEFAULT NULL,
  `cost` DECIMAL(10,2) DEFAULT NULL,
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_name` VARCHAR(255) DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  INDEX `idx_medical_child` (`child_id`),
  INDEX `idx_medical_date` (`visit_date`),
  CONSTRAINT `fk_medical_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_medical_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `education_records` (
  `record_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `school_name` VARCHAR(150) NOT NULL,
  `grade` VARCHAR(50) DEFAULT NULL,
  `enrollment_date` DATE DEFAULT NULL,
  `completion_date` DATE DEFAULT NULL,
  `performance` TEXT DEFAULT NULL,
  `fees` DECIMAL(10,2) DEFAULT NULL,
  `address` TEXT DEFAULT NULL,
  `contact_person` VARCHAR(100) DEFAULT NULL,
  `contact_phone` VARCHAR(20) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  INDEX `idx_education_child` (`child_id`),
  INDEX `idx_education_school` (`school_name`),
  CONSTRAINT `fk_education_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- FINANCIAL AND DOCUMENT TABLES
-- =====================================================

CREATE TABLE `money_records` (
  `record_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) DEFAULT NULL,
  `transaction_type` VARCHAR(50) NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `currency` VARCHAR(10) DEFAULT 'KES',
  `transaction_date` DATE NOT NULL,
  `description` TEXT DEFAULT NULL,
  `category` VARCHAR(50) DEFAULT NULL,
  `payment_method` VARCHAR(50) DEFAULT NULL,
  `receipt_number` VARCHAR(50) DEFAULT NULL,
  `performed_by` INT(11) DEFAULT NULL,
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_name` VARCHAR(255) DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  INDEX `idx_money_child` (`child_id`),
  INDEX `idx_money_date` (`transaction_date`),
  INDEX `idx_money_type` (`transaction_type`),
  CONSTRAINT `fk_money_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_money_performed_by` FOREIGN KEY (`performed_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_money_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `documents` (
  `record_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `document_type` VARCHAR(100) NOT NULL,
  `document_name` VARCHAR(255) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `issue_date` DATE DEFAULT NULL,
  `expiry_date` DATE DEFAULT NULL,
  `issuing_authority` VARCHAR(150) DEFAULT NULL,
  `document_number` VARCHAR(50) DEFAULT NULL,
  `file_path` VARCHAR(255) DEFAULT NULL,
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  INDEX `idx_documents_child` (`child_id`),
  INDEX `idx_documents_type` (`document_type`),
  CONSTRAINT `fk_documents_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_documents_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `case_reports` (
  `report_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `worker_id` INT(11) NOT NULL,
  `report_date` DATE NOT NULL,
  `report_type` VARCHAR(50) NOT NULL,
  `findings` TEXT DEFAULT NULL,
  `recommendations` TEXT DEFAULT NULL,
  `follow_up_required` TINYINT(1) DEFAULT 0,
  `follow_up_date` DATE DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Draft',
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_name` VARCHAR(255) DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`report_id`),
  INDEX `idx_reports_child` (`child_id`),
  INDEX `idx_reports_worker` (`worker_id`),
  INDEX `idx_reports_date` (`report_date`),
  CONSTRAINT `fk_reports_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reports_worker` FOREIGN KEY (`worker_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reports_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- LEGAL AND ADOPTION TABLES
-- =====================================================

CREATE TABLE `court_cases` (
  `case_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `case_number` VARCHAR(50) NOT NULL,
  `court_name` VARCHAR(150) NOT NULL,
  `case_type` VARCHAR(50) NOT NULL,
  `filing_date` DATE NOT NULL,
  `hearing_date` DATE DEFAULT NULL,
  `judge_name` VARCHAR(100) DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  `outcome` TEXT DEFAULT NULL,
  `next_hearing_date` DATE DEFAULT NULL,
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_name` VARCHAR(255) DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`case_id`),
  UNIQUE KEY `idx_court_case_number` (`case_number`),
  INDEX `idx_court_child` (`child_id`),
  INDEX `idx_court_status` (`status`),
  CONSTRAINT `fk_court_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_court_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `adoption_applications` (
  `application_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `family_id` INT(11) NOT NULL,
  `application_date` DATE NOT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  `case_worker_id` INT(11) DEFAULT NULL,
  `home_study_id` INT(11) DEFAULT NULL,
  `court_case_id` INT(11) DEFAULT NULL,
  `approval_date` DATE DEFAULT NULL,
  `finalization_date` DATE DEFAULT NULL,
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_name` VARCHAR(255) DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`application_id`),
  INDEX `idx_adoption_child` (`child_id`),
  INDEX `idx_adoption_family` (`family_id`),
  INDEX `idx_adoption_status` (`status`),
  CONSTRAINT `fk_adoption_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_adoption_family` FOREIGN KEY (`family_id`) REFERENCES `family_profile` (`family_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_adoption_worker` FOREIGN KEY (`case_worker_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_adoption_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `home_studies` (
  `study_id` INT(11) NOT NULL AUTO_INCREMENT,
  `family_id` INT(11) NOT NULL,
  `conducted_by` INT(11) NOT NULL,
  `study_date` DATE NOT NULL,
  `completion_date` DATE DEFAULT NULL,
  `recommendations` TEXT DEFAULT NULL,
  `approval_status` VARCHAR(50) DEFAULT 'Pending',
  `findings` TEXT DEFAULT NULL,
  `house_visit_notes` TEXT DEFAULT NULL,
  `background_check_status` VARCHAR(50) DEFAULT 'Pending',
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_name` VARCHAR(255) DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`study_id`),
  INDEX `idx_homestudy_family` (`family_id`),
  INDEX `idx_homestudy_conductor` (`conducted_by`),
  INDEX `idx_homestudy_status` (`approval_status`),
  CONSTRAINT `fk_homestudy_family` FOREIGN KEY (`family_id`) REFERENCES `family_profile` (`family_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_homestudy_conductor` FOREIGN KEY (`conducted_by`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_homestudy_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- FOSTER CARE TABLES
-- =====================================================

CREATE TABLE `foster_tasks` (
  `task_id` INT(11) NOT NULL AUTO_INCREMENT,
  `placement_id` INT(11) NOT NULL,
  `task_type` VARCHAR(100) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `due_date` DATE DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  `assigned_to` INT(11) DEFAULT NULL,
  `completion_date` DATE DEFAULT NULL,
  `notes` TEXT DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`task_id`),
  INDEX `idx_tasks_placement` (`placement_id`),
  INDEX `idx_tasks_status` (`status`),
  CONSTRAINT `fk_tasks_placement` FOREIGN KEY (`placement_id`) REFERENCES `placements` (`placement_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_tasks_assigned` FOREIGN KEY (`assigned_to`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `foster_matches` (
  `match_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `family_id` INT(11) NOT NULL,
  `match_date` DATE NOT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  `priority_level` VARCHAR(20) DEFAULT 'Medium',
  `case_worker_notes` TEXT DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`match_id`),
  INDEX `idx_matches_child` (`child_id`),
  INDEX `idx_matches_family` (`family_id`),
  INDEX `idx_matches_status` (`status`),
  CONSTRAINT `fk_matches_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_matches_family` FOREIGN KEY (`family_id`) REFERENCES `family_profile` (`family_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- SYSTEM TABLES
-- =====================================================

CREATE TABLE `notifications` (
  `notification_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `notification_type` VARCHAR(50) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `message` TEXT NOT NULL,
  `is_read` TINYINT(1) DEFAULT 0,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `related_entity_id` INT(11) DEFAULT NULL,
  `related_entity_type` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`notification_id`),
  INDEX `idx_notifications_user` (`user_id`),
  INDEX `idx_notifications_read` (`is_read`),
  INDEX `idx_notifications_date` (`created_at`),
  CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `fcm_tokens` (
  `token_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `token` TEXT NOT NULL,
  `device_info` TEXT DEFAULT NULL,
  `is_active` TINYINT(1) DEFAULT 1,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`token_id`),
  INDEX `idx_fcm_user` (`user_id`),
  INDEX `idx_fcm_active` (`is_active`),
  CONSTRAINT `fk_fcm_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `background_checks` (
  `check_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `check_type` VARCHAR(50) NOT NULL,
  `request_date` DATE NOT NULL,
  `completion_date` DATE DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  `results` TEXT DEFAULT NULL,
  `conducted_by` VARCHAR(100) DEFAULT NULL,
  `reference_number` VARCHAR(50) DEFAULT NULL,
  `pdf_document_data` LONGBLOB DEFAULT NULL,
  `pdf_document_mime_type` VARCHAR(100) DEFAULT NULL,
  `pdf_document_size` BIGINT DEFAULT NULL,
  `pdf_document_name` VARCHAR(255) DEFAULT NULL,
  `pdf_document_uploaded_at` TIMESTAMP NULL DEFAULT NULL,
  `pdf_document_uploaded_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`check_id`),
  INDEX `idx_background_user` (`user_id`),
  INDEX `idx_background_status` (`status`),
  INDEX `idx_background_type` (`check_type`),
  CONSTRAINT `fk_background_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_background_pdf_uploader` FOREIGN KEY (`pdf_document_uploaded_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `audit_logs` (
  `log_id` INT(11) NOT NULL AUTO_INCREMENT,
  `table_name` VARCHAR(100) NOT NULL,
  `record_id` INT(11) NOT NULL,
  `action` VARCHAR(50) NOT NULL,
  `performed_by` INT(11) NOT NULL,
  `previous_values` TEXT DEFAULT NULL,
  `new_values` TEXT DEFAULT NULL,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_address` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  INDEX `idx_audit_table` (`table_name`),
  INDEX `idx_audit_record` (`record_id`),
  INDEX `idx_audit_action` (`action`),
  INDEX `idx_audit_timestamp` (`timestamp`),
  INDEX `idx_audit_performed_by` (`performed_by`),
  CONSTRAINT `fk_audit_performed_by` FOREIGN KEY (`performed_by`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `system_settings` (
  `setting_id` INT(11) NOT NULL AUTO_INCREMENT,
  `setting_key` VARCHAR(100) NOT NULL,
  `setting_value` TEXT DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `category` VARCHAR(50) DEFAULT NULL,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`setting_id`),
  UNIQUE KEY `idx_setting_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- SAMPLE DATA INSERTIONS
-- =====================================================

-- Insert Sample Users
INSERT INTO `users` (`username`, `password_hash`, `role`, `email`, `phone`, `county`) VALUES
('admin', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', 'admin@adoption.gov.ke', '+254700000000', 'Nairobi'),
('socialworker1', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Social Worker', 'worker1@adoption.gov.ke', '+254700000001', 'Kiambu'),
('casemanager1', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Case Manager', 'manager1@adoption.gov.ke', '+254700000002', 'Nakuru'),
('fosterparent1', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Foster Parent', 'foster1@email.com', '+254700000003', 'Mombasa');

-- Insert Sample Permissions
INSERT INTO `permissions` (`name`, `description`, `category`) VALUES
('view_children', 'View children records', 'Children'),
('edit_children', 'Edit children records', 'Children'),
('view_families', 'View family profiles', 'Families'),
('edit_families', 'Edit family profiles', 'Families'),
('view_placements', 'View placement records', 'Placements'),
('edit_placements', 'Edit placement records', 'Placements'),
('view_reports', 'View case reports', 'Reports'),
('edit_reports', 'Edit case reports', 'Reports'),
('manage_users', 'Manage system users', 'Admin'),
('view_financial', 'View financial records', 'Financial');

-- Insert Sample Family Profile
INSERT INTO `family_profile` (`user_id`, `family_type`, `primary_contact_name`, `phone`, `county`, `status`) VALUES
(4, 'Foster', 'John Kamau', '+254700000003', 'Mombasa', 'Active');

-- Insert Sample Children
INSERT INTO `children` (`case_number`, `first_name`, `last_name`, `date_of_birth`, `gender`, `county_of_origin`, `status`) VALUES
('CASE-2024-001', 'Mary', 'Wanjiku', '2018-05-15', 'Female', 'Nairobi', 'Active'),
('CASE-2024-002', 'James', 'Omondi', '2017-08-22', 'Male', 'Kisumu', 'Active'),
('CASE-2024-003', 'Grace', 'Muthoni', '2019-02-10', 'Female', 'Kiambu', 'Active');

-- =====================================================
-- END OF SCHEMA
-- =====================================================