# Database Schema Mapping: MySQL ↔ Room Database ↔ Dashboard UI

## Overview
This document maps all MySQL database tables to their corresponding Room database entities and dashboard UI screens.

## Database Tables (37 Total)

### Core Entities
| # | MySQL Table | Primary Key | Room Entity | Dashboard Screen | API Endpoint |
|---|-------------|-------------|-------------|------------------|--------------|
| 1 | `users` | `user_id` | UserEntity | UserManagementScreen | `/users` |
| 2 | `children` | `child_id` | ChildEntity | ChildrenListScreen | `/children` |
| 3 | `families` | `family_id` | FamilyEntity | FamiliesScreen | `/families` |
| 4 | `family_profile` | `family_id` | FamilyEntity | FamiliesScreen | `/family_profile` |
| 5 | `placements` | `placement_id` | PlacementEntity | PlacementsScreen | `/placements` |
| 6 | `guardians` | `guardian_id` | GuardianEntity | GuardiansScreen | `/guardians` |

### Records & Documentation
| # | MySQL Table | Primary Key | Room Entity | Dashboard Screen | API Endpoint |
|---|-------------|-------------|-------------|------------------|--------------|
| 7 | `medical_records` | `record_id` | MedicalRecordEntity | MedicalScreen | `/medical_records` |
| 8 | `education_records` | `record_id` | EducationRecordEntity | EducationScreen | `/education_records` |
| 9 | `money_records` | `money_id` | MoneyRecordEntity | FinanceScreen | `/money_records` |
| 10 | `case_reports` | `report_id` | CaseReportEntity | CaseReportsScreen | `/case_reports` |
| 11 | `documents` | `document_id` | DocumentEntity | DocumentsScreen | `/documents` |

### Legal & Cases
| # | MySQL Table | Primary Key | Room Entity | Dashboard Screen | API Endpoint |
|---|-------------|-------------|-------------|------------------|--------------|
| 12 | `court_cases` | `case_id` | CourtCaseEntity | CourtCasesScreen | `/court_cases` |
| 13 | `adoption_applications` | `application_id` | AdoptionApplicationEntity | AdoptionApplicationsScreen | `/adoption_applications` |
| 14 | `home_studies` | `study_id` | HomeStudyEntity | HomeStudiesScreen | `/home_studies` |
| 15 | `background_checks` | `check_id` | BackgroundCheckEntity | BackgroundChecksScreen | `/background_checks` |

### Foster Care
| # | MySQL Table | Primary Key | Room Entity | Dashboard Screen | API Endpoint |
|---|-------------|-------------|-------------|------------------|--------------|
| 16 | `foster_tasks` | `task_id` | FosterTaskEntity | FosterTasksScreen | `/foster_tasks` |
| 17 | `foster_matches` | `match_id` | FosterMatchEntity | FosterMatchesScreen | `/foster_matches` |

### Dashboard Enhancement Tables
| # | MySQL Table | Primary Key | Room Entity | Dashboard Screen | API Endpoint |
|---|-------------|-------------|-------------|------------------|--------------|
| 18 | `tasks` | `task_id` | TaskEntity | TasksScreen | `/tasks` |
| 19 | `action_items` | `action_id` | ActionItemEntity | ActionItemsScreen | `/action_items` |
| 20 | `dashboard_metrics` | `metric_id` | DashboardMetricEntity | DashboardScreen | `/dashboard_metrics` |
| 21 | `dashboard_preferences` | `preference_id` | DashboardPreferenceEntity | DashboardPreferencesScreen | `/dashboard_preferences` |
| 22 | `critical_dates` | `date_id` | CriticalDateEntity | CriticalDatesScreen | `/critical_dates` |
| 23 | `worker_messages` | `message_id` | WorkerMessageEntity | WorkerMessagesScreen | `/worker_messages` |
| 24 | `risk_assessments` | `assessment_id` | RiskAssessmentEntity | RiskAssessmentsScreen | `/risk_assessments` |
| 25 | `permanency_plans` | `plan_id` | PermanencyPlanEntity | PermanencyPlansScreen | `/permanency_plans` |
| 26 | `caseload` | `caseload_id` | CaseloadEntity | WorkloadDashboardScreen | `/caseload` |
| 27 | `case_urgency_flags` | `flag_id` | CaseUrgencyFlagEntity | CaseUrgencyFlagsScreen | `/case_urgency_flags` |
| 28 | `case_activities` | `activity_id` | CaseActivityEntity | CaseActivitiesScreen | `/case_activities` |
| 29 | `case_deadlines` | `deadline_id` | CaseDeadlineEntity | CaseDeadlinesScreen | `/case_deadlines` |
| 30 | `case_approvals` | `approval_id` | CaseApprovalEntity | CaseApprovalsScreen | `/case_approvals` |
| 31 | `placement_compatibility` | `compatibility_id` | PlacementCompatibilityEntity | PlacementCompatibilityScreen | `/placement_compatibility` |
| 32 | `workload_tracking` | `workload_id` | WorkloadTrackingEntity | WorkloadDashboardScreen | `/workload_tracking` |

### System & Infrastructure
| # | MySQL Table | Primary Key | Room Entity | Dashboard Screen | API Endpoint |
|---|-------------|-------------|-------------|------------------|--------------|
| 33 | `notifications` | `notification_id` | NotificationEntity | NotificationsScreen | `/notifications` |
| 34 | `audit_logs` | `log_id` | AuditLogEntity | AuditLogsScreen | `/audit_logs` |
| 35 | `permissions` | `permission_id` | PermissionEntity | UserRolesScreen | `/permissions` |
| 36 | `user_permissions` | (composite) | UserPermissionEntity | UserRolesScreen | `/user_permissions` |
| 37 | `system_settings` | `setting_id` | SystemSettingEntity | SettingsScreen | `/system_settings` |

### Emergency & Location
| # | MySQL Table | Primary Key | Room Entity | Dashboard Screen | API Endpoint |
|---|-------------|-------------|-------------|------------------|--------------|
| 38 | `counties` | `county_id` | (No entity yet) | - | `/counties` |
| 39 | `emergency_events` | `event_id` | (No entity yet) | - | `/emergency_events` |
| 40 | `sos_location_history` | `history_id` | SOSLocationEntity | - | `/sos_locations` |
| 41 | `fcm_tokens` | `token_id` | (No entity yet) | - | `/fcm_tokens` |

### Sync
| # | MySQL Table | Primary Key | Room Entity | Dashboard Screen | API Endpoint |
|---|-------------|-------------|-------------|------------------|--------------|
| 42 | `sync_queue` (local only) | `queue_id` | SyncQueueEntity | - | N/A |

## Dashboard Navigation Routes

### From DashboardScreenModern.kt

#### Metric Cards (4 cards)
1. **Children** → `children` route → ChildrenListScreen
2. **Families** → `families` route → FamiliesScreen  
3. **Placements** → `placements` route → PlacementsScreen
4. **Adoption Apps** → `adoption_applications` route → AdoptionApplicationsScreen

#### Quick Access Modules (6 modules)
5. **Medical** → `medical` route → MedicalScreen
6. **Education** → `education` route → EducationScreen
7. **Finance** → `finance` route → FinanceScreen
8. **Documents** → `documents` route → DocumentsScreen
9. **Camera** → `camera` route → CameraScreen
10. **Settings** → `settings` route → SettingsScreen

#### All Screens Grid (20+ screens)
11. Children
12. Families
13. Placements
14. Adoption Applications
15. Home Studies
16. Medical
17. Education
18. Finance
19. Documents
20. Foster Tasks
21. Foster Matches
22. Background Checks
23. Analytics
24. Guardians
25. Court Cases
26. Tasks
27. Action Items
28. Risk Assessments
29. Permanency Plans
30. Case Activities
31. Case Deadlines
32. Case Approvals
33. Case Urgency Flags
34. Critical Dates
35. Workload Dashboard
36. Worker Messages
37. Placement Compatibility
38. Dashboard Preferences
39. User Management
40. Notifications
41. Audit Logs
42. Search
43. Camera
44. User Roles

## Current Status

### ✅ Fully Implemented (Room Entity + DAO + Screen)
- users
- children
- families / family_profile
- placements
- guardians
- court_cases
- medical_records
- education_records
- money_records
- case_reports
- documents
- adoption_applications
- home_studies
- foster_tasks
- foster_matches
- background_checks
- notifications
- audit_logs
- permissions
- user_permissions
- system_settings
- tasks
- action_items
- dashboard_metrics
- dashboard_preferences
- critical_dates
- worker_messages
- risk_assessments
- permanency_plans
- caseload
- case_urgency_flags
- case_activities
- case_deadlines
- case_approvals
- placement_compatibility
- workload_tracking

### ⚠️ Needs API Integration
All screens currently use local Room database only. Need to:
1. Fetch data from API on screen load
2. Sync local changes to API on CRUD operations
3. Handle offline mode gracefully
4. Show loading/error states

### ❌ Missing Room Entities
- counties (reference data)
- emergency_events
- fcm_tokens

## Next Steps

1. **Update all repositories** to extend BaseRepository with API integration
2. **Update all screens** to fetch from API first, fallback to local DB
3. **Add loading states** and error handling to all screens
4. **Implement pull-to-refresh** on all list screens
5. **Add sync indicators** showing online/offline status
6. **Test all CRUD operations** with backend API
