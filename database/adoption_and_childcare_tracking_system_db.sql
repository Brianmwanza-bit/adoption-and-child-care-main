-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 10, 2025 at 05:36 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `adoption_and_childcare_tracking_system_db`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetChildCaseSummary` (IN `childId` INT)   BEGIN
    -- Basic child info
    SELECT * FROM children WHERE child_id = childId;
    
    -- Current placement
    SELECT * FROM placements 
    WHERE child_id = childId AND (end_date IS NULL OR end_date >= CURDATE())
    ORDER BY start_date DESC LIMIT 1;
    
    -- Active court cases
    SELECT * FROM court_cases 
    WHERE child_id = childId AND status NOT IN ('Closed', 'Dismissed');
    
    -- Most recent medical record
    SELECT * FROM medical_records 
    WHERE child_id = childId 
    ORDER BY visit_date DESC LIMIT 1;
    
    -- Current education record
    SELECT * FROM education_records 
    WHERE child_id = childId AND (exit_date IS NULL OR exit_date >= CURDATE())
    ORDER BY enrollment_date DESC LIMIT 1;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetChildrenByGuardian` (IN `guardianId` INT)   BEGIN
    SELECT c.*
    FROM children c
    JOIN guardians g ON c.child_id = g.child_id
    WHERE g.guardian_id = guardianId
    ORDER BY c.last_name, c.first_name;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `audit_logs`
--

CREATE TABLE `audit_logs` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `table_name` varchar(100) NOT NULL,
  `record_id` int(11) NOT NULL,
  `action` varchar(20) NOT NULL CHECK (`action` in ('INSERT','UPDATE','DELETE')),
  `changed_by` int(11) DEFAULT NULL,
  `changed_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` text DEFAULT NULL,
  `old_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`old_data`)),
  `new_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`new_data`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='System audit trail for all data changes';

-- --------------------------------------------------------

--
-- Table structure for table `case_reports`
--

CREATE TABLE `case_reports` (
  `report_id` int(11) NOT NULL AUTO_INCREMENT,
  `child_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `report_date` date NOT NULL,
  `report_title` varchar(150) NOT NULL,
  `report_type` varchar(50) DEFAULT NULL,
  `content` text NOT NULL,
  `is_confidential` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Case worker reports and documentation';

-- --------------------------------------------------------

--
-- Table structure for table `children`
--

CREATE TABLE `children` (
  `child_id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(100) NOT NULL,
  `middle_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) NOT NULL,
  `gender` varchar(10) DEFAULT NULL CHECK (`gender` in ('Male','Female','Other','Unknown')),
  `date_of_birth` date DEFAULT NULL,
  `birth_certificate_no` varchar(50) DEFAULT NULL,
  `nationality` varchar(50) DEFAULT NULL,
  `photo_url` varchar(255) DEFAULT NULL,
  `is_emancipated` tinyint(1) DEFAULT 0,
  `emancipation_date` date DEFAULT NULL,
  `emancipation_reason` text DEFAULT NULL,
  `current_status` varchar(50) DEFAULT 'Active',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `created_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Basic information about children in the system';

--
-- Triggers `children`
--
DELIMITER $$
CREATE TRIGGER `children_audit_trigger` AFTER UPDATE ON `children` FOR EACH ROW BEGIN
    INSERT INTO audit_logs (table_name, record_id, action, changed_by, old_data, new_data)
    VALUES (
        'children',
        NEW.child_id,
        'UPDATE',
        NULL, -- Should be set by application
        JSON_OBJECT(
            'first_name', OLD.first_name,
            'last_name', OLD.last_name,
            'gender', OLD.gender,
            'date_of_birth', OLD.date_of_birth,
            'current_status', OLD.current_status
        ),
        JSON_OBJECT(
            'first_name', NEW.first_name,
            'last_name', NEW.last_name,
            'gender', NEW.gender,
            'date_of_birth', NEW.date_of_birth,
            'current_status', NEW.current_status
        )
    );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `children_update_timestamp` BEFORE UPDATE ON `children` FOR EACH ROW BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `court_cases`
--

CREATE TABLE `court_cases` (
  `case_id` int(11) NOT NULL AUTO_INCREMENT,
  `child_id` int(11) NOT NULL,
  `case_number` varchar(50) DEFAULT NULL,
  `court_name` varchar(100) DEFAULT NULL,
  `judge_name` varchar(100) DEFAULT NULL,
  `case_type` varchar(50) DEFAULT NULL,
  `filing_date` date DEFAULT NULL,
  `hearing_date` date DEFAULT NULL,
  `outcome` text DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Pending',
  `next_hearing_date` date DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Legal cases involving children in the system';

-- --------------------------------------------------------

--
-- Table structure for table `documents`
--

CREATE TABLE `documents` (
  `document_id` int(11) NOT NULL AUTO_INCREMENT,
  `child_id` int(11) NOT NULL,
  `document_type` varchar(50) NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_type` varchar(50) DEFAULT NULL,
  `file_size` int(11) DEFAULT NULL,
  `file_path` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `uploaded_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `uploaded_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Digital documents and files associated with children';

-- --------------------------------------------------------

--
-- Table structure for table `education_records`
--

CREATE TABLE `education_records` (
  `record_id` int(11) NOT NULL AUTO_INCREMENT,
  `child_id` int(11) NOT NULL,
  `school_name` varchar(150) NOT NULL,
  `grade` varchar(50) DEFAULT NULL,
  `enrollment_date` date DEFAULT NULL,
  `exit_date` date DEFAULT NULL,
  `performance` text DEFAULT NULL,
  `special_needs` text DEFAULT NULL,
  `teacher_contact` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Education history and school records';

-- --------------------------------------------------------

--
-- Table structure for table `guardians`
--

CREATE TABLE `guardians` (
  `guardian_id` int(11) NOT NULL AUTO_INCREMENT,
  `child_id` int(11) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `relationship` varchar(50) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `is_primary` tinyint(1) DEFAULT 0,
  `legal_doc_path` varchar(255) DEFAULT NULL,
  `verification_status` varchar(20) DEFAULT 'Pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Legal guardians and caregivers for children';

-- --------------------------------------------------------

--
-- Table structure for table `medical_records`
--

CREATE TABLE `medical_records` (
  `record_id` int(11) NOT NULL AUTO_INCREMENT,
  `child_id` int(11) NOT NULL,
  `visit_date` date NOT NULL,
  `doctor_name` varchar(100) DEFAULT NULL,
  `hospital_name` varchar(150) DEFAULT NULL,
  `diagnosis` text DEFAULT NULL,
  `treatment` text DEFAULT NULL,
  `medications` text DEFAULT NULL,
  `follow_up_date` date DEFAULT NULL,
  `is_immunization` tinyint(1) DEFAULT 0,
  `immunization_type` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Medical history and healthcare records for children';

-- --------------------------------------------------------

--
-- Table structure for table `money_records`
--

CREATE TABLE `money_records` (
  `money_id` int(11) NOT NULL AUTO_INCREMENT,
  `child_id` int(11) NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `transaction_type` varchar(50) DEFAULT NULL CHECK (`transaction_type` in ('Allowance','Education','Medical','Clothing','Other')),
  `description` text DEFAULT NULL,
  `date` date NOT NULL,
  `receipt_path` varchar(255) DEFAULT NULL,
  `approved_by` int(11) DEFAULT NULL,
  `approved_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Financial transactions and expenses for children';

-- --------------------------------------------------------

--
-- Table structure for table `permissions`
--

CREATE TABLE `permissions` (
  `permission_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='System permissions for role-based access control';

--
-- Dumping data for table `permissions`
--

INSERT INTO `permissions` (`permission_id`, `name`, `description`, `category`) VALUES
(1, 'view_child_records', 'View basic child information', 'Child Records'),
(2, 'edit_child_records', 'Edit child information', 'Child Records'),
(3, 'view_medical_records', 'View medical information', 'Medical'),
(4, 'edit_medical_records', 'Edit medical records', 'Medical'),
(5, 'view_education_records', 'View education records', 'Education'),
(6, 'manage_users', 'Create and manage system users', 'Administration');

-- --------------------------------------------------------

--
-- Table structure for table `placements`
--

CREATE TABLE `placements` (
  `placement_id` int(11) NOT NULL AUTO_INCREMENT,
  `child_id` int(11) NOT NULL,
  `placement_type` varchar(50) DEFAULT NULL CHECK (`placement_type` in ('Foster Home','Group Home','Kinship Care','Institution','Adoption')),
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `organization` varchar(150) DEFAULT NULL,
  `placement_address` text DEFAULT NULL,
  `contact_person` varchar(100) DEFAULT NULL,
  `contact_phone` varchar(20) DEFAULT NULL,
  `contact_email` varchar(100) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `is_current` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Current and past placements of children';

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_login` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='System users';

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password_hash`, `role`, `email`, `created_at`, `last_login`, `is_active`) VALUES
(1, 'admin', '$2a$10$xJwL5v5Jz5U6Zz5U6Zz5U.9Zz5U6Zz5U6Zz5U6Zz5U6Zz5U6Zz5U', 'admin', 'admin@childwelfare.org', '2025-07-10 00:07:58', NULL, 1),
(2, 'testuser', 'hashed_password', 'case_worker', 'test@example.com', '2025-07-10 00:31:01', NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `user_permissions`
--

CREATE TABLE `user_permissions` (
  `user_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  `granted_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `granted_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Mapping of permissions to users';

--
-- Dumping data for table `user_permissions`
--

INSERT INTO `user_permissions` (`user_id`, `permission_id`, `granted_at`, `granted_by`) VALUES
(1, 1, '2025-07-10 00:07:58', 1),
(1, 2, '2025-07-10 00:07:58', 1),
(1, 3, '2025-07-10 00:07:58', 1),
(1, 4, '2025-07-10 00:07:58', 1),
(1, 5, '2025-07-10 00:07:58', 1),
(1, 6, '2025-07-10 00:07:58', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `changed_by` (`changed_by`),
  ADD KEY `idx_audit_table` (`table_name`),
  ADD KEY `idx_audit_date` (`changed_at`);

--
-- Indexes for table `case_reports`
--
ALTER TABLE `case_reports`
  ADD PRIMARY KEY (`report_id`),
  ADD KEY `fk_case_reports_child` (`child_id`),
  ADD KEY `fk_case_reports_user` (`user_id`),
  ADD KEY `idx_report_date` (`report_date`);

--
-- Indexes for table `children`
--
ALTER TABLE `children`
  ADD PRIMARY KEY (`child_id`),
  ADD UNIQUE KEY `birth_certificate_no` (`birth_certificate_no`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `court_cases`
--
ALTER TABLE `court_cases`
  ADD PRIMARY KEY (`case_id`),
  ADD UNIQUE KEY `case_number` (`case_number`),
  ADD KEY `child_id` (`child_id`),
  ADD KEY `idx_case_status` (`status`),
  ADD KEY `idx_hearing_date` (`hearing_date`);

--
-- Indexes for table `documents`
--
ALTER TABLE `documents`
  ADD PRIMARY KEY (`document_id`),
  ADD KEY `child_id` (`child_id`),
  ADD KEY `uploaded_by` (`uploaded_by`),
  ADD KEY `idx_document_type` (`document_type`);

--
-- Indexes for table `education_records`
--
ALTER TABLE `education_records`
  ADD PRIMARY KEY (`record_id`),
  ADD KEY `idx_school_enrollment` (`child_id`,`enrollment_date`);

--
-- Indexes for table `guardians`
--
ALTER TABLE `guardians`
  ADD PRIMARY KEY (`guardian_id`),
  ADD KEY `idx_primary_guardian` (`child_id`,`is_primary`);

--
-- Indexes for table `medical_records`
--
ALTER TABLE `medical_records`
  ADD PRIMARY KEY (`record_id`),
  ADD KEY `child_id` (`child_id`),
  ADD KEY `created_by` (`created_by`),
  ADD KEY `idx_visit_date` (`visit_date`);

--
-- Indexes for table `money_records`
--
ALTER TABLE `money_records`
  ADD PRIMARY KEY (`money_id`),
  ADD KEY `child_id` (`child_id`),
  ADD KEY `approved_by` (`approved_by`),
  ADD KEY `created_by` (`created_by`),
  ADD KEY `idx_transaction_date` (`date`);

--
-- Indexes for table `permissions`
--
ALTER TABLE `permissions`
  ADD PRIMARY KEY (`permission_id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `placements`
--
ALTER TABLE `placements`
  ADD PRIMARY KEY (`placement_id`),
  ADD KEY `child_id` (`child_id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_permissions`
--
ALTER TABLE `user_permissions`
  ADD PRIMARY KEY (`user_id`,`permission_id`),
  ADD KEY `permission_id` (`permission_id`),
  ADD KEY `granted_by` (`granted_by`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `audit_logs`
--
ALTER TABLE `audit_logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `case_reports`
--
ALTER TABLE `case_reports`
  MODIFY `report_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `children`
--
ALTER TABLE `children`
  MODIFY `child_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `court_cases`
--
ALTER TABLE `court_cases`
  MODIFY `case_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `documents`
--
ALTER TABLE `documents`
  MODIFY `document_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `education_records`
--
ALTER TABLE `education_records`
  MODIFY `record_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guardians`
--
ALTER TABLE `guardians`
  MODIFY `guardian_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `medical_records`
--
ALTER TABLE `medical_records`
  MODIFY `record_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `money_records`
--
ALTER TABLE `money_records`
  MODIFY `money_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `permissions`
--
ALTER TABLE `permissions`
  MODIFY `permission_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `placements`
--
ALTER TABLE `placements`
  MODIFY `placement_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD CONSTRAINT `audit_logs_ibfk_1` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `case_reports`
--
ALTER TABLE `case_reports`
  ADD CONSTRAINT `fk_case_reports_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`),
  ADD CONSTRAINT `fk_case_reports_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `children`
--
ALTER TABLE `children`
  ADD CONSTRAINT `children_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `court_cases`
--
ALTER TABLE `court_cases`
  ADD CONSTRAINT `court_cases_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE;

--
-- Constraints for table `documents`
--
ALTER TABLE `documents`
  ADD CONSTRAINT `documents_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `documents_ibfk_2` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `education_records`
--
ALTER TABLE `education_records`
  ADD CONSTRAINT `education_records_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE;

--
-- Constraints for table `guardians`
--
ALTER TABLE `guardians`
  ADD CONSTRAINT `guardians_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE;

--
-- Constraints for table `medical_records`
--
ALTER TABLE `medical_records`
  ADD CONSTRAINT `medical_records_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `medical_records_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `money_records`
--
ALTER TABLE `money_records`
  ADD CONSTRAINT `money_records_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `money_records_ibfk_2` FOREIGN KEY (`approved_by`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `money_records_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `placements`
--
ALTER TABLE `placements`
  ADD CONSTRAINT `placements_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `placements_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `user_permissions`
--
ALTER TABLE `user_permissions`
  ADD CONSTRAINT `user_permissions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `user_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `user_permissions_ibfk_3` FOREIGN KEY (`granted_by`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
