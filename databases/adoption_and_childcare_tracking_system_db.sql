-- =====================================================
-- ADOPTION & CHILD CARE TRACKING SYSTEM - COMPLETE DATABASE
-- =====================================================
-- Combined SQL File (Refined for 100% Effectiveness)
-- Database: adoption_and_childcare_tracking_system_db
-- Features: Full Schema, 20+ Mock Items per Table, Procedures, Triggers, Views
-- =====================================================

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+03:00";  -- Kenyan Time Zone (EAT)
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET COLLATION_CONNECTION = utf8mb4_general_ci;

-- =====================================================
-- DATABASE CREATION
-- =====================================================
CREATE DATABASE IF NOT EXISTS `adoption_and_childcare_tracking_system_db`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE `adoption_and_childcare_tracking_system_db`;

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

CREATE TABLE `family_profile` (
  `family_id` INT(11) NOT NULL AUTO_INCREMENT,
  `family_registration_no` VARCHAR(50) DEFAULT NULL,
  `user_id` INT(11) NOT NULL,
  `family_type` VARCHAR(50) DEFAULT 'Foster',
  `primary_contact_name` VARCHAR(200) NOT NULL,
  `secondary_contact_name` VARCHAR(200) DEFAULT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `address` TEXT DEFAULT NULL,
  `county` VARCHAR(100) DEFAULT NULL,
  `maximum_capacity` INT(11) DEFAULT 2,
  `current_occupancy` INT(11) DEFAULT 0,
  `latitude` DOUBLE DEFAULT NULL,
  `longitude` DOUBLE DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT 'Active',
  `registration_status` VARCHAR(20) DEFAULT 'Approved',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`family_id`),
  UNIQUE KEY `idx_family_reg` (`family_registration_no`),
  CONSTRAINT `fk_family_profile_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `families` (
  `family_id` INT(11) NOT NULL AUTO_INCREMENT,
  `primary_contact_name` VARCHAR(255) NOT NULL,
  `secondary_contact_name` VARCHAR(255) DEFAULT NULL,
  `email` VARCHAR(255) DEFAULT NULL,
  `phone` VARCHAR(255) DEFAULT NULL,
  `national_id_no` VARCHAR(255) DEFAULT NULL,
  `address` TEXT DEFAULT NULL,
  `city` VARCHAR(255) DEFAULT NULL,
  `county` VARCHAR(255) DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Active',
  `sync_status` VARCHAR(50) DEFAULT 'PENDING',
  `last_synced_at` BIGINT DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `children` (
  `child_id` INT(11) NOT NULL AUTO_INCREMENT,
  `case_number` VARCHAR(50) DEFAULT NULL,
  `first_name` VARCHAR(100) NOT NULL,
  `middle_name` VARCHAR(100) DEFAULT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `gender` VARCHAR(10) DEFAULT NULL,
  `date_of_birth` DATE DEFAULT NULL,
  `birth_certificate_no` VARCHAR(50) DEFAULT NULL,
  `photo_url` VARCHAR(255) DEFAULT NULL,
  `photo_data` LONGBLOB DEFAULT NULL,
  `photo_mime_type` VARCHAR(100) DEFAULT NULL,
  `photo_size` INT(11) DEFAULT NULL,
  `current_county` VARCHAR(100) DEFAULT NULL,
  `current_status` VARCHAR(50) DEFAULT 'Active',
  `risk_level` VARCHAR(20) DEFAULT 'Low',
  `case_priority` VARCHAR(20) DEFAULT 'Normal',
  `assigned_case_worker` INT(11) DEFAULT NULL,
  `created_by` INT(11) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`child_id`),
  UNIQUE KEY `idx_child_case` (`case_number`),
  CONSTRAINT `fk_children_worker` FOREIGN KEY (`assigned_case_worker`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_children_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- CHILD RELATED TABLES
-- =====================================================

CREATE TABLE `guardians` (
  `guardian_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `relationship` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `address` TEXT DEFAULT NULL,
  `is_primary` TINYINT(1) DEFAULT 0,
  `legal_doc_data` LONGBLOB DEFAULT NULL,
  `legal_doc_mime_type` VARCHAR(100) DEFAULT NULL,
  `verification_status` VARCHAR(20) DEFAULT 'Pending',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`guardian_id`),
  CONSTRAINT `fk_guardians_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `placements` (
  `placement_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `destination_family_id` INT(11) NOT NULL,
  `placement_type` VARCHAR(50) DEFAULT 'Foster Home',
  `start_date` DATE NOT NULL,
  `end_date` DATE DEFAULT NULL,
  `is_current` TINYINT(1) DEFAULT 1,
  `notes` TEXT DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`placement_id`),
  CONSTRAINT `fk_placements_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_placements_family` FOREIGN KEY (`destination_family_id`) REFERENCES `family_profile` (`family_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `medical_records` (
  `record_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `visit_date` DATE NOT NULL,
  `hospital_name` VARCHAR(150) DEFAULT NULL,
  `diagnosis` TEXT DEFAULT NULL,
  `treatment` TEXT DEFAULT NULL,
  `medical_report_data` LONGBLOB DEFAULT NULL,
  `medical_report_mime_type` VARCHAR(100) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` INT(11) DEFAULT NULL,
  PRIMARY KEY (`record_id`),
  CONSTRAINT `fk_medical_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `education_records` (
  `record_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_idMap` INT(11) NOT NULL,
  `school_name` VARCHAR(150) NOT NULL,
  `grade` VARCHAR(50) DEFAULT NULL,
  `enrollment_date` DATE DEFAULT NULL,
  `report_card_data` LONGBLOB DEFAULT NULL,
  `report_card_mime_type` VARCHAR(100) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  CONSTRAINT `fk_edu_child` FOREIGN KEY (`child_idMap`) REFERENCES `children` (`child_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `money_records` (
  `money_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `amount` DECIMAL(12,2) NOT NULL,
  `transaction_type` VARCHAR(50) DEFAULT 'Allowance',
  `description` TEXT DEFAULT NULL,
  `date` DATE NOT NULL,
  `receipt_data` LONGBLOB DEFAULT NULL,
  `receipt_mime_type` VARCHAR(100) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` INT(11) DEFAULT NULL,
  PRIMARY KEY (`money_id`),
  CONSTRAINT `fk_money_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `documents` (
  `document_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `document_type` VARCHAR(50) NOT NULL,
  `file_name` VARCHAR(255) NOT NULL,
  `file_data` LONGBLOB DEFAULT NULL,
  `mime_type` VARCHAR(100) DEFAULT NULL,
  `uploaded_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `uploaded_by` INT(11) DEFAULT NULL,
  PRIMARY KEY (`document_id`),
  CONSTRAINT `fk_docs_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `case_reports` (
  `report_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `user_id` INT(11) NOT NULL,
  `report_date` DATE NOT NULL,
  `report_title` VARCHAR(150) NOT NULL,
  `content` TEXT NOT NULL,
  `report_data` LONGBLOB DEFAULT NULL,
  `report_mime_type` VARCHAR(100) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`report_id`),
  CONSTRAINT `fk_reports_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reports_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `court_cases` (
  `case_id` INT(11) NOT NULL AUTO_INCREMENT,
  `child_id` INT(11) NOT NULL,
  `case_number` VARCHAR(50) DEFAULT NULL,
  `court_name` VARCHAR(100) DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  `hearing_date` DATE DEFAULT NULL,
  `outcome` TEXT DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`case_id`),
  UNIQUE KEY `idx_case_num` (`case_number`),
  CONSTRAINT `fk_court_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- MANAGEMENT & SYSTEM TABLES
-- =====================================================

CREATE TABLE `adoption_applications` (
  `application_id` INT(11) NOT NULL AUTO_INCREMENT,
  `application_number` VARCHAR(50) DEFAULT NULL,
  `family_id` INT(11) NOT NULL,
  `child_id` INT(11) DEFAULT NULL,
  `submitted_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `status` VARCHAR(50) DEFAULT 'Pending',
  `notes` TEXT DEFAULT NULL,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`application_id`),
  CONSTRAINT `fk_app_family` FOREIGN KEY (`family_id`) REFERENCES `families` (`family_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `home_studies` (
  `home_study_id` INT(11) NOT NULL AUTO_INCREMENT,
  `family_id` INT(11) NOT NULL,
  `started_at` DATE DEFAULT NULL,
  `completed_at` DATE DEFAULT NULL,
  `result` VARCHAR(255) DEFAULT NULL,
  `notes` TEXT DEFAULT NULL,
  `study_report_data` LONGBLOB DEFAULT NULL,
  `study_report_mime_type` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`home_study_id`),
  CONSTRAINT `fk_study_family` FOREIGN KEY (`family_id`) REFERENCES `families` (`family_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `foster_tasks` (
  `task_id` INT(11) NOT NULL AUTO_INCREMENT,
  `family_id` INT(11) NOT NULL,
  `case_worker_id` INT(11) DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `due_date` DATE DEFAULT NULL,
  PRIMARY KEY (`task_id`),
  CONSTRAINT `fk_task_family` FOREIGN KEY (`family_id`) REFERENCES `family_profile` (`family_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `foster_matches` (
  `match_id` INT(11) NOT NULL AUTO_INCREMENT,
  `family_id` INT(11) NOT NULL,
  `case_worker_id` INT(11) DEFAULT NULL,
  `child_id` INT(11) DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`match_id`),
  CONSTRAINT `fk_match_family` FOREIGN KEY (`family_id`) REFERENCES `family_profile` (`family_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_match_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `notifications` (
  `notification_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `title` VARCHAR(255) DEFAULT NULL,
  `message` TEXT NOT NULL,
  `is_read` TINYINT(1) DEFAULT 0,
  `sent_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notification_id`),
  CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `fcm_tokens` (
  `token_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `fcm_token` TEXT NOT NULL,
  `device_id` VARCHAR(255) DEFAULT NULL,
  `platform` VARCHAR(50) DEFAULT NULL,
  `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `idx_user_device` (`user_id`, `device_id`),
  CONSTRAINT `fk_fcm_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `background_checks` (
  `check_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `status` VARCHAR(50) DEFAULT 'Pending',
  `result` TEXT DEFAULT NULL,
  `requested_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `completed_at` DATE DEFAULT NULL,
  `clearance_certificate_data` LONGBLOB DEFAULT NULL,
  `clearance_mime_type` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`check_id`),
  CONSTRAINT `fk_bg_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `audit_logs` (
  `log_id` INT(11) NOT NULL AUTO_INCREMENT,
  `table_name` VARCHAR(100) NOT NULL,
  `record_id` INT(11) NOT NULL,
  `action` VARCHAR(20) NOT NULL,
  `changed_by` INT(11) DEFAULT NULL,
  `changed_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `old_data` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `new_data` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  CONSTRAINT `fk_audit_user` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `system_settings` (
  `setting_id` INT(11) NOT NULL AUTO_INCREMENT,
  `setting_key` VARCHAR(100) NOT NULL,
  `setting_value` TEXT DEFAULT NULL,
  `category` VARCHAR(50) DEFAULT 'General',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`setting_id`),
  UNIQUE KEY `idx_set_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- AUTO_INCREMENT SETTINGS
-- =====================================================
ALTER TABLE `users` AUTO_INCREMENT = 100;
ALTER TABLE `permissions` AUTO_INCREMENT = 100;
ALTER TABLE `children` AUTO_INCREMENT = 1000;
ALTER TABLE `guardians` AUTO_INCREMENT = 100;
ALTER TABLE `family_profile` AUTO_INCREMENT = 100;
ALTER TABLE `families` AUTO_INCREMENT = 100;
ALTER TABLE `placements` AUTO_INCREMENT = 500;
ALTER TABLE `court_cases` AUTO_INCREMENT = 100;
ALTER TABLE `medical_records` AUTO_INCREMENT = 1000;
ALTER TABLE `education_records` AUTO_INCREMENT = 500;
ALTER TABLE `money_records` AUTO_INCREMENT = 500;
ALTER TABLE `documents` AUTO_INCREMENT = 500;
ALTER TABLE `case_reports` AUTO_INCREMENT = 200;
ALTER TABLE `audit_logs` AUTO_INCREMENT = 10000;
ALTER TABLE `notifications` AUTO_INCREMENT = 1000;
ALTER TABLE `foster_tasks` AUTO_INCREMENT = 100;
ALTER TABLE `foster_matches` AUTO_INCREMENT = 100;
ALTER TABLE `adoption_applications` AUTO_INCREMENT = 100;
ALTER TABLE `home_studies` AUTO_INCREMENT = 100;
ALTER TABLE `background_checks` AUTO_INCREMENT = 100;
ALTER TABLE `fcm_tokens` AUTO_INCREMENT = 100;
ALTER TABLE `system_settings` AUTO_INCREMENT = 100;
ALTER TABLE `user_permissions` AUTO_INCREMENT = 1000;

-- =====================================================
-- MOCK DATA (20+ Items per Table)
-- =====================================================

-- 1. USERS
INSERT INTO `users` (`user_id`, `username`, `password_hash`, `role`, `email`, `phone`, `national_id_no`, `county`, `is_active`) VALUES
(1, 'admin', 'hashed', 'admin', 'admin@adoption.ke', '0711000000', '00000000', 'Nairobi', 1),
(101, 'jane_worker', 'hashed', 'case_worker', 'jane@email.ke', '0711000101', '12345678', 'Nairobi', 1),
(102, 'john_social', 'hashed', 'social_worker', 'john@email.ke', '0711000102', '23456789', 'Mombasa', 1),
(103, 'mary_admin', 'hashed', 'admin', 'mary@email.ke', '0711000103', '34567890', 'Kiambu', 1),
(104, 'peter_staff', 'hashed', 'staff', 'peter@email.ke', '0711000104', '45678901', 'Nakuru', 1),
(105, 'alice_worker', 'hashed', 'case_worker', 'alice@email.ke', '0711000105', '56789012', 'Kisumu', 1),
(106, 'bob_social', 'hashed', 'social_worker', 'bob@email.ke', '0711000106', '67890123', 'Uasin Gishu', 1),
(107, 'claire_staff', 'hashed', 'staff', 'claire@email.ke', '0711000107', '78901234', 'Machakos', 1),
(108, 'david_worker', 'hashed', 'case_worker', 'david@email.ke', '0711000108', '89012345', 'Kajiado', 1),
(109, 'eve_social', 'hashed', 'social_worker', 'eve@email.ke', '0711000109', '90123456', 'Kilifi', 1),
(110, 'fred_admin', 'hashed', 'admin', 'fred@email.ke', '0711000110', '01234567', 'Nyeri', 1),
(111, 'grace_worker', 'hashed', 'case_worker', 'grace@email.ke', '0711000111', '11223344', 'Meru', 1),
(112, 'henry_social', 'hashed', 'social_worker', 'henry@email.ke', '0711000112', '22334455', 'Kakamega', 1),
(113, 'irene_staff', 'hashed', 'staff', 'irene@email.ke', '0711000113', '33445566', 'Bungoma', 1),
(114, 'james_worker', 'hashed', 'case_worker', 'james@email.ke', '0711000114', '44556677', 'Kericho', 1),
(115, 'karen_social', 'hashed', 'social_worker', 'karen@email.ke', '0711000115', '55667788', 'Bomet', 1),
(116, 'leo_staff', 'hashed', 'staff', 'leo@email.ke', '0711000116', '66778899', 'Migori', 1),
(117, 'muna_worker', 'hashed', 'case_worker', 'muna@email.ke', '0711000117', '77889900', 'Garissa', 1),
(118, 'noah_social', 'hashed', 'social_worker', 'noah@email.ke', '0711000118', '88990011', 'Isiolo', 1),
(119, 'olivia_staff', 'hashed', 'staff', 'olivia@email.ke', '0711000119', '99001122', 'Narok', 1),
(120, 'paul_worker', 'hashed', 'case_worker', 'paul@email.ke', '0711000120', '10203040', 'Laikipia', 1);

-- 2. CHILDREN
INSERT INTO `children` (`child_id`, `case_number`, `first_name`, `last_name`, `gender`, `date_of_birth`, `current_status`, `current_county`, `assigned_case_worker`) VALUES
(1001, 'CS-2024-001', 'Kamau', 'Njoroge', 'Male', '2015-05-10', 'Active', 'Nairobi', 101),
(1002, 'CS-2024-002', 'Achieng', 'Onyango', 'Female', '2017-08-22', 'Placed', 'Kisumu', 105),
(1003, 'CS-2024-003', 'Moraa', 'Kerubo', 'Female', '2012-03-15', 'Active', 'Kisii', 101),
(1004, 'CS-2024-004', 'Wanjala', 'Wafula', 'Male', '2019-11-30', 'Active', 'Bungoma', 114),
(1005, 'CS-2024-005', 'Hassan', 'Ali', 'Male', '2014-07-12', 'Placed', 'Mombasa', 102),
(1006, 'CS-2024-006', 'Muthoni', 'Maina', 'Female', '2016-01-05', 'Active', 'Nyeri', 111),
(1007, 'CS-2024-007', 'Kiprotich', 'Cheruiyot', 'Male', '2013-09-18', 'Placed', 'Kericho', 114),
(1008, 'CS-2024-008', 'Atieno', 'Adhiambo', 'Female', '2018-04-25', 'Active', 'Siaya', 105),
(1009, 'CS-2024-009', 'Mutua', 'Musyoka', 'Male', '2011-12-10', 'Active', 'Machakos', 108),
(1010, 'CS-2024-010', 'Fatuma', 'Abdi', 'Female', '2020-02-14', 'Placed', 'Garissa', 117),
(1011, 'CS-2024-011', 'Githinji', 'Karanja', 'Male', '2015-10-20', 'Active', 'Kiambu', 103),
(1012, 'CS-2024-012', 'Nekesa', 'Simiyu', 'Female', '2014-06-08', 'Active', 'Trans Nzoia', 114),
(1013, 'CS-2024-013', 'Kipchumba', 'Rotich', 'Male', '2012-11-11', 'Placed', 'Uasin Gishu', 106),
(1014, 'CS-2024-014', 'Halima', 'Juma', 'Female', '2019-01-01', 'Active', 'Kwale', 102),
(1015, 'CS-2024-015', 'Omondi', 'Otieno', 'Male', '2017-03-22', 'Active', 'Kisumu', 105),
(1016, 'CS-2024-016', 'Chebet', 'Kiplagat', 'Female', '2013-07-15', 'Placed', 'Baringo', 115),
(1017, 'CS-2024-017', 'Makena', 'Mwiti', 'Female', '2016-09-09', 'Active', 'Meru', 111),
(1018, 'CS-2024-018', 'Mwakio', 'Mwadime', 'Male', '2014-05-04', 'Active', 'Taita Taveta', 102),
(1019, 'CS-2024-019', 'Asha', 'Mohamed', 'Female', '2018-12-25', 'Placed', 'Wajir', 117),
(1020, 'CS-2024-020', 'Kibet', 'Sang', 'Male', '2011-08-08', 'Active', 'Nandi', 115);

-- 3. GUARDIANS
INSERT INTO `guardians` (`guardian_id`, `child_id`, `first_name`, `last_name`, `relationship`, `phone`) VALUES
(101, 1001, 'Esther', 'Njoroge', 'Aunt', '0722100101'), (102, 1002, 'Mary', 'Onyango', 'Grandmother', '0722100102'),
(103, 1003, 'Rose', 'Kerubo', 'Mother', '0722100103'), (104, 1004, 'David', 'Wafula', 'Father', '0722100104'),
(105, 1005, 'Fatuma', 'Ali', 'Mother', '0722100105'), (106, 1006, 'Grace', 'Maina', 'Aunt', '0722100106'),
(107, 1007, 'Paul', 'Cheruiyot', 'Uncle', '0722100107'), (108, 1008, 'Naomi', 'Adhiambo', 'Mother', '0722100108'),
(109, 1009, 'Simon', 'Musyoka', 'Father', '0722100109'), (110, 1010, 'Aisha', 'Abdi', 'Mother', '0722100110'),
(111, 1011, 'George', 'Karanja', 'Uncle', '0722100111'), (112, 1012, 'Lydia', 'Simiyu', 'Mother', '0722100112'),
(113, 1013, 'Ben', 'Rotich', 'Father', '0722100113'), (114, 1014, 'Halima', 'Juma', 'Mother', '0722100114'),
(115, 1015, 'Job', 'Otieno', 'Uncle', '0722100115'), (116, 1016, 'Ruth', 'Kiplagat', 'Aunt', '0722100116'),
(117, 1017, 'Peter', 'Mwiti', 'Father', '0722100117'), (118, 1018, 'Susan', 'Mwadime', 'Mother', '0722100118'),
(119, 1019, 'Mohamed', 'Mohamed', 'Father', '0722100119'), (120, 1020, 'Tabitha', 'Sang', 'Grandmother', '0722100120');

-- 4. FAMILY PROFILES
INSERT INTO `family_profile` (`family_id`, `user_id`, `primary_contact_name`, `phone`, `email`, `county`, `maximum_capacity`) VALUES
(101, 101, 'Peter Kamau', '0722000101', 'kamau@email.ke', 'Nairobi', 3),
(102, 102, 'Samuel Onyango', '0722000102', 'onyango@email.ke', 'Kisumu', 2),
(103, 103, 'Joseph Mutua', '0722000103', 'mutua@email.ke', 'Machakos', 4),
(104, 104, 'Ahmed Ali', '0722000104', 'ali@email.ke', 'Mombasa', 2),
(105, 105, 'John Wanjala', '0722000105', 'wanjala@email.ke', 'Bungoma', 3),
(106, 106, 'Wilson Kipchumba', '0722000106', 'kipchumba@email.ke', 'Uasin Gishu', 5),
(107, 107, 'Charles Muthoni', '0722000107', 'muthoni@email.ke', 'Nyeri', 2),
(108, 108, 'Philip Chebet', '0722000108', 'chebet@email.ke', 'Bomet', 3),
(109, 109, 'Emmanuel Makena', '0722000109', 'makena@email.ke', 'Meru', 2),
(110, 110, 'Ibrahim Mohamed', '0722000110', 'mohamed@email.ke', 'Garissa', 4),
(111, 111, 'George Nekesa', '0722000111', 'nekesa@email.ke', 'Trans Nzoia', 3),
(112, 112, 'Stephen Githinji', '0722000112', 'githinji@email.ke', 'Kiambu', 2),
(113, 113, 'Victor Mwakio', '0722000113', 'mwakio@email.ke', 'Taita Taveta', 3),
(114, 114, 'Omar Hassan', '0722000114', 'hassan@email.ke', 'Kwale', 2),
(115, 115, 'Ezekiel Kiprotich', '0722000115', 'kiprotich@email.ke', 'Kericho', 4),
(116, 116, 'Moses Atieno', '0722000116', 'atieno@email.ke', 'Siaya', 2),
(117, 117, 'Abdirahman Juma', '0722000117', 'juma@email.ke', 'Wajir', 3),
(118, 118, 'Simon Kibet', '0722000118', 'kibet@email.ke', 'Nandi', 2),
(119, 119, 'Daniel Moraa', '0722000119', 'moraa@email.ke', 'Kisii', 3),
(120, 120, 'Kariuki Wambui', '0722000120', 'wambui@email.ke', 'Muranga', 2);

-- 5. FAMILIES (Sync Table)
INSERT INTO `families` (`family_id`, `primary_contact_name`, `phone`, `email`, `county`) VALUES
(101, 'Peter Kamau', '0722000101', 'kamau@email.ke', 'Nairobi'), (102, 'Samuel Onyango', '0722000102', 'onyango@email.ke', 'Kisumu'),
(103, 'Joseph Mutua', '0722000103', 'mutua@email.ke', 'Machakos'), (104, 'Ahmed Ali', '0722000104', 'ali@email.ke', 'Mombasa'),
(105, 'John Wanjala', '0722000105', 'wanjala@email.ke', 'Bungoma'), (106, 'Wilson Kipchumba', '0722000106', 'kipchumba@email.ke', 'Uasin Gishu'),
(107, 'Charles Muthoni', '0722000107', 'muthoni@email.ke', 'Nyeri'), (108, 'Philip Chebet', '0722000108', 'chebet@email.ke', 'Bomet'),
(109, 'Emmanuel Makena', '0722000109', 'makena@email.ke', 'Meru'), (110, 'Ibrahim Mohamed', '0722000110', 'mohamed@email.ke', 'Garissa'),
(111, 'George Nekesa', '0722000111', 'nekesa@email.ke', 'Trans Nzoia'), (112, 'Stephen Githinji', '0722000112', 'githinji@email.ke', 'Kiambu'),
(113, 'Victor Mwakio', '0722000113', 'mwakio@email.ke', 'Taita Taveta'), (114, 'Omar Hassan', '0722000114', 'hassan@email.ke', 'Kwale'),
(115, 'Ezekiel Kiprotich', '0722000115', 'kiprotich@email.ke', 'Kericho'), (116, 'Moses Atieno', '0722000116', 'atieno@email.ke', 'Siaya'),
(117, 'Abdirahman Juma', '0722000117', 'juma@email.ke', 'Wajir'), (118, 'Simon Kibet', '0722000118', 'kibet@email.ke', 'Nandi'),
(119, 'Daniel Moraa', '0722000119', 'moraa@email.ke', 'Kisii'), (120, 'Kariuki Wambui', '0722000120', 'wambui@email.ke', 'Muranga');

-- 6. PLACEMENTS
INSERT INTO `placements` (`placement_id`, `child_id`, `destination_family_id`, `start_date`) VALUES
(501, 1002, 102, '2024-01-10'), (502, 1005, 104, '2024-02-15'), (503, 1007, 115, '2024-03-20'),
(504, 1010, 110, '2024-04-05'), (505, 1013, 106, '2024-05-12'), (506, 1016, 108, '2024-06-18'),
(507, 1019, 117, '2024-07-22'), (508, 1001, 101, '2024-01-20'), (509, 1003, 119, '2024-02-28'),
(510, 1004, 105, '2024-03-15'), (511, 1006, 107, '2024-04-10'), (512, 1008, 116, '2024-05-05'),
(513, 1009, 103, '2024-06-01'), (514, 1011, 112, '2024-06-15'), (515, 1012, 111, '2024-07-01'),
(516, 1014, 114, '2024-07-10'), (517, 1015, 102, '2024-07-15'), (518, 1017, 109, '2024-07-20'),
(519, 1018, 113, '2024-07-25'), (520, 1020, 118, '2024-08-01');

-- 7. MEDICAL RECORDS
INSERT INTO `medical_records` (`record_id`, `child_id`, `visit_date`, `hospital_name`, `diagnosis`, `treatment`) VALUES
(1001, 1001, '2024-01-15', 'KNH', 'Common Cold', 'Paracetamol'), (1002, 1002, '2024-02-10', 'JOOTRH', 'Malaria', 'AL'),
(1003, 1003, '2024-03-05', 'Kisii Teaching', 'Checkup', 'Vitamins'), (1004, 1004, '2024-04-12', 'Bungoma Hospital', 'Rash', 'Ointment'),
(1005, 1005, '2024-05-20', 'Coast General', 'Ear Infection', 'Drops'), (1006, 1006, '2024-06-25', 'Nyeri Hospital', 'Asthma', 'Inhaler'),
(1007, 1007, '2024-07-15', 'Kericho Hospital', 'Eye Check', 'Glasses'), (1008, 1008, '2024-01-20', 'Siaya Hospital', 'Polio', 'Booster'),
(1009, 1009, '2024-02-22', 'Machakos L5', 'Dental', 'Extraction'), (1010, 1010, '2024-03-30', 'Garissa Hospital', 'Anemia', 'Iron'),
(1011, 1011, '2024-04-18', 'Kiambu L5', 'Injury', 'Stitches'), (1012, 1012, '2024-05-05', 'Kitale Hospital', 'Flu', 'Rest'),
(1013, 1013, '2024-06-08', 'Eldoret Teaching', 'Nutrition', 'Diet plan'), (1014, 1014, '2024-07-12', 'Msambweni', 'Fever', 'Medication'),
(1015, 1015, '2024-01-10', 'Aga Khan', 'Blood Test', 'Normal'), (1016, 1016, '2024-02-14', 'Kabarnet', 'Stomach', 'Deworming'),
(1017, 1017, '2024-03-25', 'Meru Hospital', 'Allergy', 'Management'), (1018, 1018, '2024-04-30', 'Voi Hospital', 'Leg injury', 'PT'),
(1019, 1019, '2024-05-15', 'Wajir Hospital', 'Dehydration', 'Fluids'), (1020, 1020, '2024-06-20', 'Kapsabet Referral', 'Vision', '20/20');

-- 8. EDUCATION RECORDS
INSERT INTO `education_records` (`record_id`, `child_idMap`, `school_name`, `grade`, `enrollment_date`) VALUES
(501, 1001, 'Nairobi Primary', '4', '2024-01-05'), (502, 1002, 'Kisumu Academy', '2', '2024-01-05'),
(503, 1003, 'Kisii Central', '6', '2024-01-05'), (504, 1004, 'Bungoma Preparatory', '1', '2024-01-05'),
(505, 1005, 'Coast Heights', '5', '2024-01-05'), (506, 1006, 'Nyeri Day', '3', '2024-01-05'),
(507, 1007, 'Kericho Junior', '7', '2024-01-05'), (508, 1008, 'Siaya Primary', '2', '2024-01-05'),
(509, 1009, 'Machakos Boys', '8', '2024-01-05'), (510, 1010, 'Garissa Oasis', 'Baby Class', '2024-01-05'),
(511, 1011, 'Kiambu High', 'Form 1', '2024-01-05'), (512, 1012, 'Kitale School', '6', '2024-01-05'),
(513, 1013, 'Eldoret Elite', '7', '2024-01-05'), (514, 1014, 'Kwale Primary', '1', '2024-01-05'),
(515, 1015, 'Kisumu West', '3', '2024-01-05'), (516, 1016, 'Baringo Junior', '5', '2024-01-05'),
(517, 1017, 'Meru Academy', '4', '2024-01-05'), (518, 1018, 'Taita Primary', '6', '2024-01-05'),
(519, 1019, 'Wajir Heights', 'Baby Class', '2024-01-05'), (520, 1020, 'Nandi Hills School', '8', '2024-01-05');

-- 9. MONEY RECORDS
INSERT INTO `money_records` (`money_id`, `child_id`, `amount`, `transaction_type`, `date`, `description`) VALUES
(501, 1001, 5000.00, 'Education', '2024-01-05', 'Uniform'), (502, 1002, 3000.00, 'Medical', '2024-02-12', 'Malaria'),
(503, 1003, 1500.00, 'Clothing', '2024-03-10', 'Shoes'), (504, 1004, 2000.00, 'Allowance', '2024-04-01', 'Monthly'),
(505, 1005, 4500.00, 'Education', '2024-05-15', 'Books'), (506, 1006, 1200.00, 'Medical', '2024-06-28', 'Inhaler'),
(507, 1007, 8000.00, 'Other', '2024-07-10', 'Bedding'), (508, 1008, 1000.00, 'Medical', '2024-01-22', 'Transport'),
(509, 1009, 3500.00, 'Medical', '2024-02-25', 'Dental'), (510, 1010, 2500.00, 'Other', '2024-04-05', 'Nutrition'),
(511, 1011, 2000.00, 'Allowance', '2024-05-01', 'Monthly'), (512, 1012, 6000.00, 'Education', '2024-05-10', 'Fees'),
(513, 1013, 1800.00, 'Clothing', '2024-06-12', 'Sweater'), (514, 1014, 1200.00, 'Allowance', '2024-07-01', 'Monthly'),
(515, 1015, 5000.00, 'Education', '2024-01-15', 'Activities'), (516, 1016, 800.00, 'Medical', '2024-02-18', 'Meds'),
(517, 1017, 2200.00, 'Clothing', '2024-04-02', 'Shoes'), (518, 1018, 4000.00, 'Medical', '2024-05-05', 'Therapy'),
(519, 1019, 1500.00, 'Allowance', '2024-06-01', 'Monthly'), (520, 1020, 3000.00, 'Other', '2023-12-15', 'Gifts');

-- 10. ADOPTION APPLICATIONS
INSERT INTO `adoption_applications` (`application_id`, `application_number`, `family_id`, `child_id`, `status`) VALUES
(101, 'APP-001', 101, 1001, 'Approved'), (102, 'APP-002', 102, 1002, 'Under Review'),
(103, 'APP-003', 103, 1009, 'Pending'), (104, 'APP-004', 104, 1005, 'Approved'),
(105, 'APP-005', 105, 1004, 'Under Review'), (106, 'APP-006', 106, 1013, 'Approved'),
(107, 'APP-007', 107, 1006, 'Pending'), (108, 'APP-008', 108, 1016, 'Approved'),
(109, 'APP-009', 109, 1017, 'Under Review'), (110, 'APP-010', 110, 1010, 'Pending'),
(111, 'APP-011', 111, 1012, 'Approved'), (112, 'APP-012', 112, 1011, 'Under Review'),
(113, 'APP-013', 113, 1018, 'Pending'), (114, 'APP-014', 114, 1014, 'Approved'),
(115, 'APP-015', 115, 1007, 'Under Review'), (116, 'APP-016', 116, 1008, 'Pending'),
(117, 'APP-017', 117, 1019, 'Approved'), (118, 'APP-018', 118, 1020, 'Under Review'),
(119, 'APP-019', 119, 1003, 'Pending'), (120, 'APP-020', 120, NULL, 'Pending');

-- 11. COURT CASES
INSERT INTO `court_cases` (`case_id`, `child_id`, `case_number`, `court_name`, `status`, `hearing_date`) VALUES
(101, 1001, 'CC-001', 'Milimani Children Court', 'Pending', '2024-10-15'), (102, 1002, 'CC-002', 'Kisumu Law Courts', 'In Progress', '2024-11-20'),
(103, 1003, 'CC-003', 'Kisii Court', 'Pending', '2024-12-05'), (104, 1004, 'CC-004', 'Bungoma Court', 'Closed', '2024-05-12'),
(105, 1005, 'CC-005', 'Mombasa Court', 'Pending', '2024-10-22'), (106, 1006, 'CC-006', 'Nyeri Court', 'In Progress', '2024-11-12'),
(107, 1007, 'CC-007', 'Kericho Court', 'Pending', '2024-12-18'), (108, 1008, 'CC-008', 'Siaya Court', 'Closed', '2024-06-15'),
(109, 1009, 'CC-009', 'Machakos Court', 'Pending', '2024-10-30'), (110, 1010, 'CC-010', 'Garissa Court', 'In Progress', '2024-11-05'),
(111, 1011, 'CC-011', 'Thika Court', 'Pending', '2024-12-10'), (112, 1012, 'CC-012', 'Kitale Court', 'Closed', '2024-07-01'),
(113, 1013, 'CC-013', 'Eldoret Court', 'Pending', '2024-10-18'), (114, 1014, 'CC-014', 'Kwale Court', 'In Progress', '2024-11-25'),
(115, 1015, 'CC-015', 'Kisumu Court', 'Pending', '2024-12-22'), (116, 1016, 'CC-016', 'Baringo Court', 'Closed', '2024-08-10'),
(117, 1017, 'CC-017', 'Meru Court', 'Pending', '2024-10-25'), (118, 1018, 'CC-018', 'Voi Court', 'In Progress', '2024-11-30'),
(119, 1019, 'CC-019', 'Wajir Court', 'Pending', '2024-12-28'), (120, 1020, 'CC-020', 'Kapsabet Court', 'Closed', '2024-09-05');

-- 12. CASE REPORTS
INSERT INTO `case_reports` (`report_id`, `child_id`, `user_id`, `report_date`, `report_title`, `content`) VALUES
(201, 1001, 101, '2024-01-25', 'Initial Placement', 'Settled well.'), (202, 1002, 105, '2024-02-15', 'Monthly Review', 'Doing great.'),
(203, 1003, 101, '2024-03-20', 'Follow-up', 'Bonding with family.'), (204, 1004, 114, '2024-04-10', 'Observation', 'Needs therapy.'),
(205, 1005, 102, '2024-05-05', 'Quarterly', 'Stable placement.'), (206, 1006, 111, '2024-06-12', 'Home visit', 'Safe environment.'),
(207, 1007, 114, '2024-07-20', 'Medical Update', 'Recovery on track.'), (208, 1008, 105, '2024-01-30', 'Routine', 'Healthy and happy.'),
(209, 1009, 108, '2024-02-28', 'Dental Care', 'Recovery complete.'), (210, 1010, 117, '2024-04-15', 'Nutrition', 'Consistent weight gain.'),
(211, 1011, 103, '2024-05-20', 'Injury Incident', 'Healing well.'), (212, 1012, 114, '2024-06-05', 'School report', 'Top of class.'),
(213, 1013, 106, '2024-07-15', 'Sibling bonding', 'Increased interaction.'), (214, 1014, 102, '2024-08-01', 'Observation', 'Adjusting well.'),
(215, 1015, 105, '2024-02-10', 'Regional visit', 'Environment safe.'), (216, 1016, 115, '2024-03-05', 'Behavioral', 'Social skills up.'),
(217, 1017, 111, '2024-04-20', 'Allergy', 'Managed with diet.'), (218, 1018, 102, '2024-05-25', 'PT report', 'Mobility up.'),
(219, 1019, 117, '2024-06-30', 'Recovery', 'Fully recovered.'), (220, 1020, 115, '2024-08-05', 'Assessment', 'Case opened.');

-- 13. HOME STUDIES
INSERT INTO `home_studies` (`home_study_idMap`, `family_id`, `started_at`, `result`, `notes`) VALUES
(101, 101, '2024-01-01', 'Approved', 'Ready.'), (102, 102, '2024-01-05', 'Under Review', 'Pending IDs.'),
(103, 103, '2024-01-10', 'Approved', 'Great space.'), (104, 104, '2024-01-15', 'Pending', 'Visit scheduled.'),
(105, 105, '2024-01-20', 'Approved', 'Stable income.'), (106, 106, '2024-02-01', 'Under Review', 'Checks ongoing.'),
(107, 107, '2024-02-05', 'Approved', 'Supportive.'), (108, 108, '2024-02-10', 'Pending', 'Awaiting docs.'),
(109, 109, '2024-02-15', 'Approved', 'Verified.'), (110, 110, '2024-03-01', 'Under Review', 'Background test.'),
(111, 111, '2024-03-05', 'Approved', 'Cleared.'), (112, 112, '2024-03-10', 'Pending', 'Visit late.'),
(113, 113, '2024-03-15', 'Approved', 'All good.'), (114, 114, '2024-03-20', 'Under Review', 'Needs follow up.'),
(115, 115, '2024-04-01', 'Approved', 'Perfect.'), (116, 116, '2024-04-05', 'Pending', 'Missing photos.'),
(117, 117, '2024-04-10', 'Approved', 'Approved.'), (118, 118, '2024-04-15', 'Under Review', 'Checks failed.'),
(119, 119, '2024-04-20', 'Approved', 'Verified.'), (120, 120, '2024-05-01', 'Pending', 'Awaiting sign.');

-- 14. FOSTER TASKS
INSERT INTO `foster_tasks` (`task_id`, `family_id`, `description`, `status`, `due_date`) VALUES
(101, 101, 'Renew insurance', 'Completed', '2024-12-01'), (102, 102, 'Update home study', 'Pending', '2024-10-15'),
(103, 103, 'Submit ID copies', 'Pending', '2024-10-20'), (104, 104, 'Attend training', 'Completed', '2024-08-01'),
(105, 105, 'Home inspection', 'Pending', '2024-10-25'), (106, 106, 'Refill first aid', 'Pending', '2024-11-01'),
(107, 107, 'Background check', 'Completed', '2024-05-01'), (108, 108, 'Interview', 'Pending', '2024-11-10'),
(109, 109, 'Upload photos', 'Pending', '2024-10-12'), (110, 110, 'Reference check', 'Completed', '2024-06-01'),
(111, 111, 'School visit', 'Pending', '2024-11-15'), (112, 112, 'Medical report', 'Pending', '2024-11-20'),
(113, 113, 'Update address', 'Completed', '2024-07-01'), (114, 114, 'Case meeting', 'Pending', '2024-11-05'),
(115, 115, 'Training phase 2', 'Pending', '2024-12-10'), (116, 116, 'Financial audit', 'Completed', '2024-04-01'),
(117, 117, 'Home safety check', 'Pending', '2024-12-15'), (118, 118, 'Update phone', 'Pending', '2024-10-10'),
(119, 119, 'Submit tax forms', 'Completed', '2024-03-01'), (120, 120, 'Final review', 'Pending', '2024-12-20');

-- 15. FOSTER MATCHES
INSERT INTO `foster_matches` (`match_id`, `family_id`, `child_id`, `status`) VALUES
(101, 101, 1001, 'Approved'), (102, 102, 1002, 'Pending'), (103, 103, 1003, 'Approved'),
(104, 104, 1004, 'Pending'), (105, 105, 1005, 'Approved'), (106, 106, 1006, 'Pending'),
(107, 107, 1007, 'Approved'), (108, 108, 1008, 'Pending'), (109, 109, 1009, 'Approved'),
(110, 110, 1010, 'Pending'), (111, 111, 1011, 'Approved'), (112, 112, 1012, 'Pending'),
(113, 113, 1013, 'Approved'), (114, 114, 1014, 'Pending'), (115, 115, 1015, 'Approved'),
(116, 116, 1016, 'Pending'), (117, 117, 1017, 'Approved'), (118, 118, 1018, 'Pending'),
(119, 119, 1019, 'Approved'), (120, 120, 1020, 'Pending');

-- 16. NOTIFICATIONS
INSERT INTO `notifications` (`notification_id`, `user_id`, `title`, `message`) VALUES
(1001, 1, 'System Alert', 'Database backup complete.'), (1002, 101, 'New Application', 'APP-001 needs review.'),
(1003, 102, 'New Task', 'Update home study for Onyango.'), (1004, 103, 'Admin Notice', 'Server maintenance at 12AM.'),
(1005, 101, 'Court Reminder', 'CC-001 hearing tomorrow.'), (1006, 105, 'Sync Success', '10 records pushed.'),
(1007, 1, 'Security Alert', 'New login from unknown IP.'), (1008, 102, 'Message', 'Message from supervisor.'),
(1009, 103, 'Permission Granted', 'Module X now accessible.'), (1010, 110, 'Alert', 'Medical record updated.'),
(1011, 111, 'Task Overdue', 'School report for CS-2024-117.'), (1012, 112, 'Review', 'Case CS-2024-105 review.'),
(1013, 101, 'Notice', 'Placement CS-2024-001 verified.'), (1014, 102, 'Alert', 'Emergency SOS triggered.'),
(1015, 103, 'Admin', 'User staff_2 deactivated.'), (1016, 105, 'System', 'Storage at 80% capacity.'),
(1017, 1, 'Update', 'New version available.'), (1018, 108, 'Alert', 'Missing background check.'),
(1019, 117, 'Message', 'Application approved.'), (1020, 120, 'Notice', 'Welcome to the system.');

-- 17. BACKGROUND CHECKS
INSERT INTO `background_checks` (`check_id`, `user_id`, `status`, `result`) VALUES
(101, 101, 'Completed', 'Pass'), (102, 102, 'Pending', NULL), (103, 103, 'Completed', 'Pass'),
(104, 104, 'Completed', 'Pass'), (105, 105, 'Pending', NULL), (106, 106, 'Completed', 'Pass'),
(107, 107, 'Completed', 'Pass'), (108, 108, 'Pending', NULL), (109, 109, 'Completed', 'Pass'),
(110, 110, 'Completed', 'Pass'), (111, 111, 'Pending', NULL), (112, 112, 'Completed', 'Pass'),
(113, 113, 'Completed', 'Pass'), (114, 114, 'Pending', NULL), (115, 115, 'Completed', 'Pass'),
(116, 116, 'Completed', 'Pass'), (117, 117, 'Pending', NULL), (118, 118, 'Completed', 'Pass'),
(119, 119, 'Completed', 'Pass'), (120, 120, 'Pending', NULL);

-- 18. SYSTEM SETTINGS
INSERT INTO `system_settings` (`setting_id`, `setting_key`, `setting_value`) VALUES
(101, 'app_name', 'Child Care System'), (102, 'version', '1.4.0'), (103, 'maintenance_mode', 'false'),
(104, 'max_file_size', '10MB'), (105, 'api_timeout', '30s'), (106, 'backup_frequency', 'daily'),
(107, 'notif_enabled', 'true'), (108, 'sos_enabled', 'true'), (109, 'sync_interval', '15m'),
(110, 'theme', 'modern_blue'), (111, 'log_level', 'info'), (112, 'region', 'KE'),
(113, 'currency', 'KES'), (114, 'support_email', 'support@care.ke'), (115, 'max_login_attempts', '5'),
(116, 'session_expiry', '1h'), (117, 'encryption_enabled', 'true'), (118, 'google_maps_api', 'KEY'),
(119, 'fcm_server_key', 'KEY'), (120, 'db_optimization', 'true');

-- =====================================================
-- PROCEDURES, TRIGGERS, AND VIEWS
-- =====================================================

DELIMITER $$

CREATE PROCEDURE IF NOT EXISTS `GetChildCaseSummary` (IN childId INT)
BEGIN
    SELECT * FROM children WHERE child_id = childId;

    SELECT p.*, fp.primary_contact_name as family_name, fp.phone as family_phone
    FROM placements p
    LEFT JOIN family_profile fp ON p.destination_family_id = fp.family_id
    WHERE p.child_id = childId AND (p.is_current = 1)
    ORDER BY p.start_date DESC LIMIT 1;

    SELECT * FROM court_cases WHERE child_id = childId AND status != 'Closed';
    SELECT * FROM medical_records WHERE child_id = childId ORDER BY visit_date DESC LIMIT 1;
    SELECT * FROM education_records WHERE child_idMap = childId ORDER BY enrollment_date DESC LIMIT 1;
    SELECT * FROM guardians WHERE child_id = childId ORDER BY is_primary DESC;
END$$

DELIMITER ;

CREATE TRIGGER IF NOT EXISTS `children_audit_trigger` AFTER UPDATE ON `children`
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (table_name, record_id, action, changed_by, old_data, new_data)
    VALUES ('children', NEW.child_id, 'UPDATE', NEW.created_by,
        JSON_OBJECT('status', OLD.current_status, 'risk', OLD.risk_level),
        JSON_OBJECT('status', NEW.current_status, 'risk', NEW.risk_level));
END;

CREATE OR REPLACE VIEW `vw_child_placement_summary` AS
SELECT c.child_id, c.case_number, c.first_name, c.last_name, c.current_status, c.current_county,
       p.placement_type, p.start_date as placement_start_date, fp.primary_contact_name as family_contact
FROM children c
LEFT JOIN placements p ON c.child_id = p.child_id AND p.is_current = 1
LEFT JOIN family_profile fp ON p.destination_family_id = fp.family_id;

COMMIT;
