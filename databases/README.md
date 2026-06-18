# Adoption & Child Care Tracking System - Database Setup

This directory contains the core SQL definitions for the Adoption & Child Care Tracking System.

## Database File
- **`adoption_and_childcare_tracking_system_db.sql`**: A comprehensive SQL script that includes:
    - Complete Database Schema (Version 3.0)
    - 20+ Detailed Mock Records for every table (Kenyan context)
    - Stored Procedures (e.g., `GetChildCaseSummary`)
    - Database Triggers (Auditing and Timestamps)
    - Reporting Views (e.g., `vw_child_placement_summary`)
    - **PDF Document Storage**: 10GB capacity PDF containers in tables requiring document storage

## Complete Database Reference - 65 Tables

### Table Structure Overview

**Quick breakdown:**
- **Tables 1–26** — Existing phpMyAdmin tables (with new columns added to children, users, court_cases, home_studies, family_profile)
- **Tables 27–41** — The 15 missing dashboard tables from the original SQL that never got imported  
- **Tables 42–65** — The 24 brand new tables

### Known Bugs Fixed
- **`home_study_idMap`** → Corrected to `home_study_id` 
- **`child_idMap`** → Corrected to `child_id`

### Tables Requiring PDF Document Storage (10GB Capacity)
The following tables include dedicated PDF document containers with 10GB storage capacity:

1. **adoption_applications** — Application forms, legal documents
2. **home_studies** — Home study reports and assessments
3. **court_cases** — Court documents, legal filings, judgments
4. **background_checks** — Background check reports and certificates
5. **medical_records** — Medical reports, hospital records, treatment plans
6. **education_records** — School records, report cards, transcripts
7. **case_reports** — Case worker reports, assessments, reviews
8. **guardians** — Legal guardian documents, custody papers
9. **documents** — General child documents, certificates, identification
10. **money_records** — Financial receipts, invoices, payment records
11. **family_profile** — Family registration documents, home study certificates
12. **placements** — Placement agreements, contracts, legal documents

### PDF Storage Schema
Each PDF-enabled table includes the following columns:
- `pdf_document_data` (LONGBLOB) - Binary PDF data (up to 10GB)
- `pdf_document_mime_type` (VARCHAR(100)) - MIME type (e.g., 'application/pdf')
- `pdf_document_size` (BIGINT) - File size in bytes
- `pdf_document_name` (VARCHAR(255)) - Original filename
- `pdf_document_uploaded_at` (TIMESTAMP) - Upload timestamp
- `pdf_document_uploaded_by` (INT(11)) - User ID who uploaded the document

## How to Import

### Method 1: PowerShell Script (Recommended)
Run the automated import script from this directory:
```powershell
.\import-db.ps1
```

### Method 2: Manual Import (phpMyAdmin/MySQL)
1. Open your MySQL client (e.g., phpMyAdmin).
2. Create a new database named `adoption_and_childcare_tracking_system_db`.
3. Select the database and use the **Import** tab.
4. Choose the `adoption_and_childcare_tracking_system_db.sql` file and click **Go**.

## MySQL Configuration for 10GB PDF Storage
To support 10GB PDF storage, ensure your MySQL configuration includes:
```ini
max_allowed_packet = 10737418240  # 10GB
innodb_log_file_size = 1G
innodb_buffer_pool_size = 4G
```

## Features Included
- **Kenyan Context**: Counties, hospital names, and currency (KES) are integrated.
- **BLOB Support**: Tables are pre-configured to store photos and PDF documents up to 10GB.
- **Audit Trail**: Every update to a child's record is automatically logged in the `audit_logs` table.
- **Analytics Ready**: Built-in views provide immediate summaries of placements, financial spend, and worker workloads.
- **PDF Document Management**: Dedicated containers for legal, medical, educational, and administrative documents.
- **Bug Fixes**: Column naming issues resolved for proper foreign key relationships.