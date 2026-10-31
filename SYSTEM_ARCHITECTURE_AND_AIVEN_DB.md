# 🌐 System Architecture & Aiven Cloud Database Guide

## 1. Overview
The **Adoption & Child Care Tracking System** is built on an **Offline-First Android & Node.js API Pipeline** connected to a live, cloud-hosted **Aiven MySQL Database**.

```
📱 Android Mobile App (Jetpack Compose + Room Local DB)
       │
       ▼ (HTTP / JSON API + SSL)
🌐 Node.js Express Backend Server (Port 50000 / Reverse Proxy)
       │
       ▼ (MySQL Protocol + TLSv1.2 SSL CA Certificate)
🗄️ Aiven Cloud MySQL Database (64 Tables, 20+ records per table)
```

---

## 2. Database Connection Credentials
- **Host:** `mysql-2e009ff8-addoptionandchildcaretrackingsystem.j.aivencloud.com`
- **Port:** `13255`
- **User:** `avnadmin`
- **Database:** `defaultdb`
- **SSL Certificate:** `backend/config-aiven-ca.pem` (Pinned TLSv1.2 Trust Anchor)

---

## 3. Database Schema Overview (64 Tables)
All 64 tables are seeded and populated in the live Aiven Cloud DB:

### A. Children & Adoption Core
- `children` (Child profiles, DOB, gender, current status, county)
- `families` & `family_profile` (Family background, contacts, status)
- `guardians` & `siblings` (Legal guardians & sibling relationships)
- `home_studies` & `adoption_applications` (Home study evaluations & applications)
- `placements`, `placement_compatibility`, `placement_disruptions` (Placements)

### B. Health, Education & Welfare
- `medical_records` & `vaccination_records` (Medical history & immunizations)
- `education_records` (School enrolment, performance, grades)
- `child_behavior_assessments` & `child_welfare_incidents` (Behavioral & incident reports)
- `consent_records` & `child_services_referrals` (Consents & welfare service referrals)

### C. Case Management & Operations
- `case_reports`, `court_cases`, `investigations` (Legal filings & investigations)
- `permanency_plans`, `aftercare_plans`, `service_plans`, `service_plan_goals`
- `case_activities`, `case_approvals`, `case_deadlines`, `critical_dates`, `visitation_schedules`

### D. Finance & Administration
- `money_records`, `budget_allocations`, `donor_funding` (Finances & stipends)
- `organization_partners`, `service_providers`, `county_offices`, `counties`, `inter_county_transfers`

### E. Security & System
- `users`, `permissions`, `user_permissions`, `audit_logs`
- `documents` & `global_document_storage` (PDFs and file attachments)
- `notifications`, `emergency_events`, `sos_location_history`, `worker_location_tracking`, `worker_messages`

---

## 4. Database Link Verification
To verify live connection to Aiven MySQL at any time, run:
```bash
cd backend
node check-db-link.js
node inspect-tables.js
```
