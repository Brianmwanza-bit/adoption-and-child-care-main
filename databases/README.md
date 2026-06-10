# Adoption & Child Care Tracking System - Database Setup

This directory contains the core SQL definitions for the Adoption & Child Care Tracking System.

## Database File
- **`adoption_and_childcare_tracking_system_db.sql`**: A comprehensive SQL script that includes:
    - Complete Database Schema (Version 3.0)
    - 20+ Detailed Mock Records for every table (Kenyan context)
    - Stored Procedures (e.g., `GetChildCaseSummary`)
    - Database Triggers (Auditing and Timestamps)
    - Reporting Views (e.g., `vw_child_placement_summary`)

## How to Import
You can import this database using one of the following methods:

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

## Features Included
- **Kenyan Context**: Counties, hospital names, and currency (KES) are integrated.
- **BLOB Support**: Tables are pre-configured to store photos and PDF documents.
- **Audit Trail**: Every update to a child's record is automatically logged in the `audit_logs` table.
- **Analytics Ready**: Built-in views provide immediate summaries of placements, financial spend, and worker workloads.
