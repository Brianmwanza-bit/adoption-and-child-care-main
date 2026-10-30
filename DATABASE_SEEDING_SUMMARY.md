# Database Seeding Summary Report

## Overview
The MySQL database (`adoption_and_childcare_tracking_system_db`) hosted via XAMPP has been successfully reset, structured, and seeded with **precisely 20 items per table** across all **64 database tables**, totaling **17,160 cells** of validated test and mock data.

---

## Seeding Statistics
- **Total Tables**: 64 tables
- **Rows per Table**: Exactly 20 rows
- **Total Cells Seeded**: 17,160 cells

---

## Table Cell Breakdown

| Table Name | Rows | Columns | Total Cells |
| :--- | :---: | :---: | :---: |
| `action_items` | 20 | 12 | 240 |
| `adoption_applications` | 20 | 18 | 360 |
| `aftercare_plans` | 20 | 13 | 260 |
| `audit_logs` | 20 | 9 | 180 |
| `background_checks` | 20 | 20 | 400 |
| `budget_allocations` | 20 | 8 | 160 |
| `case_activities` | 20 | 11 | 220 |
| `case_approvals` | 20 | 11 | 220 |
| `case_deadlines` | 20 | 12 | 240 |
| `case_reports` | 20 | 23 | 460 |
| `case_urgency_flags` | 20 | 12 | 240 |
| `caseload` | 20 | 11 | 220 |
| `child_behavior_assessments` | 20 | 14 | 280 |
| `child_services_referrals` | 20 | 15 | 300 |
| `child_welfare_incidents` | 20 | 14 | 280 |
| `children` | 20 | 33 | 660 |
| `consent_records` | 20 | 13 | 260 |
| `counties` | 20 | 6 | 120 |
| `county_offices` | 20 | 20 | 400 |
| `court_cases` | 20 | 19 | 380 |
| `critical_dates` | 20 | 9 | 180 |
| `dashboard_metrics` | 20 | 6 | 120 |
| `dashboard_preferences` | 20 | 7 | 140 |
| `documents` | 20 | 18 | 360 |
| `donor_funding` | 20 | 9 | 180 |
| `education_records` | 20 | 16 | 320 |
| `emergency_events` | 20 | 11 | 220 |
| `families` | 20 | 18 | 360 |
| `family_profile` | 20 | 29 | 580 |
| `fcm_tokens` | 20 | 7 | 140 |
| `foster_family_training` | 20 | 11 | 220 |
| `foster_matches` | 20 | 9 | 180 |
| `foster_tasks` | 20 | 11 | 220 |
| `global_document_storage` | 20 | 12 | 240 |
| `guardians` | 20 | 25 | 500 |
| `home_studies` | 20 | 21 | 420 |
| `inter_county_transfers` | 20 | 12 | 240 |
| `investigations` | 20 | 14 | 280 |
| `medical_records` | 20 | 24 | 480 |
| `money_records` | 20 | 22 | 440 |
| `notifications` | 20 | 9 | 180 |
| `organization_partners` | 20 | 11 | 220 |
| `permanency_plans` | 20 | 12 | 240 |
| `permissions` | 20 | 6 | 120 |
| `placement_compatibility` | 20 | 15 | 300 |
| `placement_disruptions` | 20 | 12 | 240 |
| `placements` | 20 | 19 | 380 |
| `referrals` | 20 | 12 | 240 |
| `reports_generated` | 20 | 10 | 200 |
| `risk_assessments` | 20 | 15 | 300 |
| `service_plan_goals` | 20 | 9 | 180 |
| `service_plans` | 20 | 13 | 260 |
| `service_providers` | 20 | 12 | 240 |
| `siblings` | 20 | 8 | 160 |
| `sos_location_history` | 20 | 10 | 200 |
| `system_settings` | 20 | 6 | 120 |
| `tasks` | 20 | 12 | 240 |
| `user_permissions` | 20 | 5 | 100 |
| `users` | 20 | 19 | 380 |
| `vaccination_records` | 20 | 12 | 240 |
| `visitation_schedules` | 20 | 12 | 240 |
| `worker_location_tracking` | 20 | 9 | 180 |
| `worker_messages` | 20 | 10 | 200 |
| `workload_tracking` | 20 | 15 | 300 |

---

## Data Population Specification
1. **Foreign & Primary Keys (`*_id`)**: Sequentially mapped from `1` to `20` across relational references.
2. **Usernames & Accounts**: `user_1` to `user_20` with bcrypt hashed passwords (`password123`).
3. **Emails & Contacts**: Formatted emails (`user1_xxxx@adoption-care.org`) and sequential phone numbers (`0710000001` - `0710000020`).
4. **Dates & Timestamps**: Generated past/future date strings and SQL timestamps.
5. **Text & Descriptions**: Indexed sample description text (`Sample text description item #1` through `#20`).
6. **Statuses & Enums**: Standardized operational values (`Active`, `Pending`, `Closed`).
