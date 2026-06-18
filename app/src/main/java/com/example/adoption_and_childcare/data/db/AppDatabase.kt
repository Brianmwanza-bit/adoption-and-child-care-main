package com.example.adoption_and_childcare.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.adoption_and_childcare.data.db.dao.ActionItemDao
import com.example.adoption_and_childcare.data.db.dao.AdoptionApplicationDao
import com.example.adoption_and_childcare.data.db.dao.AuditLogDao
import com.example.adoption_and_childcare.data.db.dao.BackgroundCheckDao
import com.example.adoption_and_childcare.data.db.dao.CaseActivityDao
import com.example.adoption_and_childcare.data.db.dao.CaseApprovalDao
import com.example.adoption_and_childcare.data.db.dao.CaseDeadlineDao
import com.example.adoption_and_childcare.data.db.dao.CaseReportDao
import com.example.adoption_and_childcare.data.db.dao.CaseUrgencyFlagDao
import com.example.adoption_and_childcare.data.db.dao.CaseloadDao
import com.example.adoption_and_childcare.data.db.dao.ChildDao
import com.example.adoption_and_childcare.data.db.dao.CourtCaseDao
import com.example.adoption_and_childcare.data.db.dao.CriticalDateDao
import com.example.adoption_and_childcare.data.db.dao.DashboardMetricDao
import com.example.adoption_and_childcare.data.db.dao.DashboardPreferenceDao
import com.example.adoption_and_childcare.data.db.dao.DocumentDao
import com.example.adoption_and_childcare.data.db.dao.EducationRecordDao
import com.example.adoption_and_childcare.data.db.dao.FamilyDao
import com.example.adoption_and_childcare.data.db.dao.FosterMatchDao
import com.example.adoption_and_childcare.data.db.dao.FosterTaskDao
import com.example.adoption_and_childcare.data.db.dao.GuardianDao
import com.example.adoption_and_childcare.data.db.dao.HomeStudyDao
import com.example.adoption_and_childcare.data.db.dao.MedicalRecordDao
import com.example.adoption_and_childcare.data.db.dao.MoneyRecordDao
import com.example.adoption_and_childcare.data.db.dao.NotificationDao
import com.example.adoption_and_childcare.data.db.dao.PermanencyPlanDao
import com.example.adoption_and_childcare.data.db.dao.PermissionDao
import com.example.adoption_and_childcare.data.db.dao.PlacementCompatibilityDao
import com.example.adoption_and_childcare.data.db.dao.PlacementDao
import com.example.adoption_and_childcare.data.db.dao.RiskAssessmentDao
import com.example.adoption_and_childcare.data.db.dao.SystemSettingDao
import com.example.adoption_and_childcare.data.db.dao.TaskDao
import com.example.adoption_and_childcare.data.db.dao.UserDao
import com.example.adoption_and_childcare.data.db.dao.UserPermissionDao
import com.example.adoption_and_childcare.data.db.dao.WorkerMessageDao
import com.example.adoption_and_childcare.data.db.dao.WorkloadTrackingDao
import com.example.adoption_and_childcare.data.db.entities.ActionItemEntity
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import com.example.adoption_and_childcare.data.db.entities.AuditLogEntity
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import com.example.adoption_and_childcare.data.db.entities.CaseActivityEntity
import com.example.adoption_and_childcare.data.db.entities.CaseApprovalEntity
import com.example.adoption_and_childcare.data.db.entities.CaseDeadlineEntity
import com.example.adoption_and_childcare.data.db.entities.CaseReportEntity
import com.example.adoption_and_childcare.data.db.entities.CaseUrgencyFlagEntity
import com.example.adoption_and_childcare.data.db.entities.CaseloadEntity
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import com.example.adoption_and_childcare.data.db.entities.CriticalDateEntity
import com.example.adoption_and_childcare.data.db.entities.DashboardMetricEntity
import com.example.adoption_and_childcare.data.db.entities.DashboardPreferenceEntity
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity
import com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity
import com.example.adoption_and_childcare.data.db.entities.GuardianEntity
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity
import com.example.adoption_and_childcare.data.db.entities.NotificationEntity
import com.example.adoption_and_childcare.data.db.entities.PermanencyPlanEntity
import com.example.adoption_and_childcare.data.db.entities.PermissionEntity
import com.example.adoption_and_childcare.data.db.entities.PlacementCompatibilityEntity
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import com.example.adoption_and_childcare.data.db.entities.RiskAssessmentEntity
import com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity
import com.example.adoption_and_childcare.data.db.entities.TaskEntity
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity
import com.example.adoption_and_childcare.data.db.entities.WorkerMessageEntity
import com.example.adoption_and_childcare.data.db.entities.WorkloadTrackingEntity

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.TypeConverters
import com.example.adoption_and_childcare.data.db.converters.Converters
import com.example.adoption_and_childcare.data.db.dao.SOSLocationDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.SOSLocationEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.example.adoption_and_childcare.data.db.dao.*
import com.example.adoption_and_childcare.data.db.entities.*

/**
 * The main database for the Adoption and Child Care application.
 *
 * This class provides access to all Data Access Objects (DAOs) and handles database migrations.
 */
@Database(
    entities = [
        UserEntity::class,
        ChildEntity::class,
        DocumentEntity::class,
        PlacementEntity::class,
        CaseReportEntity::class,
        CourtCaseEntity::class,
        EducationRecordEntity::class,
        GuardianEntity::class,
        MedicalRecordEntity::class,
        MoneyRecordEntity::class,
        PermissionEntity::class,
        UserPermissionEntity::class,
        AuditLogEntity::class,
        FamilyEntity::class,
        AdoptionApplicationEntity::class,
        HomeStudyEntity::class,
        NotificationEntity::class,
        SyncQueueEntity::class,
        SOSLocationEntity::class,
        BackgroundCheckEntity::class,
        FosterTaskEntity::class,
        FosterMatchEntity::class,
        SystemSettingEntity::class,
        TaskEntity::class,
        ActionItemEntity::class,
        DashboardMetricEntity::class,
        DashboardPreferenceEntity::class,
        CriticalDateEntity::class,
        WorkerMessageEntity::class,
        RiskAssessmentEntity::class,
        PermanencyPlanEntity::class,
        CaseloadEntity::class,
        CaseUrgencyFlagEntity::class,
        CaseActivityEntity::class,
        CaseDeadlineEntity::class,
        CaseApprovalEntity::class,
        PlacementCompatibilityEntity::class,
        WorkloadTrackingEntity::class,
        ChildBehaviorAssessmentEntity::class,
        ChildWelfareIncidentEntity::class,
        VaccinationRecordEntity::class,
        SiblingEntity::class,
        ConsentRecordEntity::class,
        ChildServicesReferralEntity::class,
        InvestigationEntity::class,
        ServicePlanEntity::class,
        ServicePlanGoalEntity::class,
        VisitationScheduleEntity::class,
        ReferralEntity::class,
        AftercarePlanEntity::class,
        OrganizationPartnerEntity::class,
        ServiceProviderEntity::class,
        DonorFundingEntity::class,
        BudgetAllocationEntity::class,
        CountyEntity::class,
        CountyOfficeEntity::class,
        PlacementDisruptionEntity::class,
        FosterFamilyTrainingEntity::class,
        ReportGeneratedEntity::class,
        EmergencyEventEntity::class,
        GlobalDocumentStorageEntity::class,
        InterCountyTransferEntity::class,
        WorkerLocationTrackingEntity::class,
    ],
    version = 18,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /** @return The DAO for interacting with users. */
    abstract fun userDao(): UserDao
    /** @return The DAO for interacting with children. */
    abstract fun childDao(): ChildDao
    /** @return The DAO for interacting with documents. */
    abstract fun documentDao(): DocumentDao
    /** @return The DAO for interacting with placements. */
    abstract fun placementDao(): PlacementDao
    /** @return The DAO for interacting with families. */
    abstract fun familyDao(): FamilyDao
    /** @return The DAO for interacting with adoption applications. */
    abstract fun adoptionApplicationDao(): AdoptionApplicationDao
    /** @return The DAO for interacting with home studies. */
    abstract fun homeStudyDao(): HomeStudyDao
    /** @return The DAO for interacting with case reports. */
    abstract fun caseReportDao(): CaseReportDao
    /** @return The DAO for interacting with education records. */
    abstract fun educationRecordDao(): EducationRecordDao
    /** @return The DAO for interacting with medical records. */
    abstract fun medicalRecordDao(): MedicalRecordDao
    /** @return The DAO for interacting with money records. */
    abstract fun moneyRecordDao(): MoneyRecordDao
    /** @return The DAO for interacting with audit logs. */
    abstract fun auditLogDao(): AuditLogDao
    /** @return The DAO for interacting with court cases. */
    abstract fun courtCaseDao(): CourtCaseDao
    /** @return The DAO for interacting with guardians. */
    abstract fun guardianDao(): GuardianDao
    /** @return The DAO for interacting with permissions. */
    abstract fun permissionDao(): PermissionDao
    /** @return The DAO for interacting with user permissions. */
    abstract fun userPermissionDao(): UserPermissionDao
    /** @return The DAO for interacting with notifications. */
    abstract fun notificationDao(): NotificationDao
    /** @return The DAO for interacting with the sync queue. */
    abstract fun syncQueueDao(): SyncQueueDao
    /** @return The DAO for interacting with SOS locations. */
    abstract fun sosLocationDao(): SOSLocationDao
    /** @return The DAO for interacting with background checks. */
    abstract fun backgroundCheckDao(): BackgroundCheckDao
    /** @return The DAO for interacting with foster tasks. */
    abstract fun fosterTaskDao(): FosterTaskDao
    /** @return The DAO for interacting with foster matches. */
    abstract fun fosterMatchDao(): FosterMatchDao
    /** @return The DAO for interacting with system settings. */
    abstract fun systemSettingDao(): SystemSettingDao
    /** @return The DAO for interacting with tasks. */
    abstract fun taskDao(): TaskDao
    /** @return The DAO for interacting with action items. */
    abstract fun actionItemDao(): ActionItemDao
    /** @return The DAO for interacting with dashboard metrics. */
    abstract fun dashboardMetricDao(): DashboardMetricDao
    /** @return The DAO for interacting with dashboard preferences. */
    abstract fun dashboardPreferenceDao(): DashboardPreferenceDao
    /** @return The DAO for interacting with critical dates. */
    abstract fun criticalDateDao(): CriticalDateDao
    /** @return The DAO for interacting with worker messages. */
    abstract fun workerMessageDao(): WorkerMessageDao
    /** @return The DAO for interacting with risk assessments. */
    abstract fun riskAssessmentDao(): RiskAssessmentDao
    /** @return The DAO for interacting with permanency plans. */
    abstract fun permanencyPlanDao(): PermanencyPlanDao
    /** @return The DAO for interacting with caseloads. */
    abstract fun caseloadDao(): CaseloadDao
    /** @return The DAO for interacting with case urgency flags. */
    abstract fun caseUrgencyFlagDao(): CaseUrgencyFlagDao
    /** @return The DAO for interacting with case activities. */
    abstract fun caseActivityDao(): CaseActivityDao
    /** @return The DAO for interacting with case deadlines. */
    abstract fun caseDeadlineDao(): CaseDeadlineDao
    /** @return The DAO for interacting with case approvals. */
    abstract fun caseApprovalDao(): CaseApprovalDao
    /** @return The DAO for interacting with placement compatibility. */
    abstract fun placementCompatibilityDao(): PlacementCompatibilityDao
    /** @return The DAO for interacting with workload tracking. */
    abstract fun workloadTrackingDao(): WorkloadTrackingDao
    abstract fun childBehaviorAssessmentDao(): ChildBehaviorAssessmentDao
    abstract fun childWelfareIncidentDao(): ChildWelfareIncidentDao
    abstract fun vaccinationRecordDao(): VaccinationRecordDao
    abstract fun siblingDao(): SiblingDao
    abstract fun consentRecordDao(): ConsentRecordDao
    abstract fun childServicesReferralDao(): ChildServicesReferralDao
    abstract fun investigationDao(): InvestigationDao
    abstract fun servicePlanDao(): ServicePlanDao
    abstract fun servicePlanGoalDao(): ServicePlanGoalDao
    abstract fun visitationScheduleDao(): VisitationScheduleDao
    abstract fun referralDao(): ReferralDao
    abstract fun aftercarePlanDao(): AftercarePlanDao
    abstract fun organizationPartnerDao(): OrganizationPartnerDao
    abstract fun serviceProviderDao(): ServiceProviderDao
    abstract fun donorFundingDao(): DonorFundingDao
    abstract fun budgetAllocationDao(): BudgetAllocationDao
    abstract fun countyDao(): CountyDao
    abstract fun countyOfficeDao(): CountyOfficeDao
    abstract fun placementDisruptionDao(): PlacementDisruptionDao
    abstract fun fosterFamilyTrainingDao(): FosterFamilyTrainingDao
    abstract fun reportGeneratedDao(): ReportGeneratedDao
    abstract fun emergencyEventDao(): EmergencyEventDao
    abstract fun globalDocumentStorageDao(): GlobalDocumentStorageDao
    abstract fun interCountyTransferDao(): InterCountyTransferDao
    abstract fun workerLocationTrackingDao(): WorkerLocationTrackingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        /** Migration from v3 to v4: Adds `sync_queue` table. */
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `sync_queue` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `table_name` TEXT NOT NULL, 
                        `operation` TEXT NOT NULL, 
                        `record_id` TEXT NOT NULL, 
                        `payload` TEXT NOT NULL, 
                        `created_at` INTEGER NOT NULL, 
                        `synced` INTEGER NOT NULL DEFAULT 0, 
                        `retry_count` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        /** Migration from v4 to v5: Adds `sos_locations` table. */
        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `sos_locations` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `sos_event_id` TEXT NOT NULL, 
                        `latitude` REAL NOT NULL, 
                        `longitude` REAL NOT NULL, 
                        `accuracy` REAL, 
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        /** Migration from v5 to v6: Version bump only. */
        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // No schema changes, version bump for consistency
            }
        }

        /** Migration from v6 to v7: Adds multiple background management tables. */
        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create background_checks table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `background_checks` (
                        `check_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `user_id` INTEGER NOT NULL,
                        `status` TEXT,
                        `result` TEXT,
                        `requested_at` TEXT,
                        `completed_at` TEXT,
                        `clearance_certificate_path` TEXT,
                        `clearance_certificate_data` BLOB,
                        `clearance_mime_type` TEXT,
                        `clearance_size` INTEGER
                    )
                """.trimIndent())

                // Create foster_tasks table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `foster_tasks` (
                        `task_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `family_id` INTEGER NOT NULL,
                        `case_worker_id` INTEGER,
                        `description` TEXT,
                        `status` TEXT,
                        `created_at` TEXT,
                        `due_date` TEXT,
                        `completed_at` TEXT
                    )
                """.trimIndent())

                // Create foster_matches table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `foster_matches` (
                        `match_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `family_id` INTEGER NOT NULL,
                        `case_worker_id` INTEGER,
                        `child_id` INTEGER,
                        `status` TEXT,
                        `created_at` TEXT,
                        `matched_at` TEXT,
                        `notes` TEXT
                    )
                """.trimIndent())

                // Create system_settings table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `system_settings` (
                        `setting_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `setting_key` TEXT NOT NULL,
                        `setting_value` TEXT,
                        `category` TEXT,
                        `created_at` TEXT,
                        `updated_at` TEXT
                    )
                """.trimIndent())
            }
        }

        /** Migration from v7 to v8: Version bump. */
        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Version bump
            }
        }

        /** Migration from v8 to v9: Version bump. */
        val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Version bump
            }
        }

        /** Migration from v9 to v10: Adds 15 dashboard enhancement tables. */
        val MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `tasks` (
                        `task_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT,
                        `priority` TEXT,
                        `status` TEXT,
                        `due_date` TEXT,
                        `assigned_to` INTEGER,
                        `created_by` INTEGER,
                        `related_entity_type` TEXT,
                        `related_entity_id` INTEGER,
                        `created_at` TEXT,
                        `updated_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `action_items` (
                        `action_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `priority` TEXT,
                        `due_date` TEXT,
                        `assignee_id` INTEGER,
                        `related_case_id` INTEGER,
                        `status` TEXT,
                        `created_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `dashboard_metrics` (
                        `metric_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `metric_name` TEXT NOT NULL,
                        `metric_value` REAL NOT NULL,
                        `previous_value` REAL,
                        `trend_percentage` REAL,
                        `calculated_date` TEXT,
                        `date_range_days` INTEGER,
                        `created_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `dashboard_preferences` (
                        `preference_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `user_id` INTEGER NOT NULL,
                        `layout_type` TEXT,
                        `show_metrics` INTEGER NOT NULL DEFAULT 1,
                        `show_alerts` INTEGER NOT NULL DEFAULT 1,
                        `show_action_items` INTEGER NOT NULL DEFAULT 1,
                        `show_recent_updates` INTEGER NOT NULL DEFAULT 1,
                        `dark_mode` INTEGER NOT NULL DEFAULT 0,
                        `notification_frequency` TEXT,
                        `quiet_hours_enabled` INTEGER NOT NULL DEFAULT 0,
                        `quiet_hours_start` TEXT,
                        `quiet_hours_end` TEXT,
                        `created_at` TEXT,
                        `updated_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `critical_dates` (
                        `date_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `child_id` INTEGER NOT NULL,
                        `date_type` TEXT NOT NULL,
                        `event_date` TEXT NOT NULL,
                        `is_completed` INTEGER NOT NULL DEFAULT 0,
                        `completed_date` TEXT,
                        `notes` TEXT,
                        `created_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `worker_messages` (
                        `message_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `sender_id` INTEGER NOT NULL,
                        `recipient_id` INTEGER NOT NULL,
                        `case_id` INTEGER,
                        `title` TEXT NOT NULL,
                        `content` TEXT,
                        `is_read` INTEGER NOT NULL DEFAULT 0,
                        `read_at` TEXT,
                        `created_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `risk_assessments` (
                        `assessment_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `child_id` INTEGER NOT NULL,
                        `assessment_date` TEXT NOT NULL,
                        `safety_score` INTEGER,
                        `risk_level` TEXT,
                        `maltreatment_risk_indicators` TEXT,
                        `behavioral_concerns` TEXT,
                        `medical_health_risks` TEXT,
                        `educational_gaps` TEXT,
                        `assessment_by` INTEGER,
                        `notes` TEXT,
                        `created_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `permanency_plans` (
                        `plan_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `child_id` INTEGER NOT NULL,
                        `plan_number` TEXT,
                        `primary_goal` TEXT,
                        `secondary_goal` TEXT,
                        `tertiary_goal` TEXT,
                        `start_date` TEXT,
                        `review_date` TEXT,
                        `completion_date` TEXT,
                        `status` TEXT,
                        `concurrent_planning` INTEGER NOT NULL DEFAULT 0,
                        `notes` TEXT,
                        `created_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `caseload` (
                        `caseload_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `worker_id` INTEGER NOT NULL,
                        `date` TEXT NOT NULL,
                        `active_cases` INTEGER NOT NULL,
                        `pending_reviews` INTEGER NOT NULL,
                        `overdue_tasks` INTEGER NOT NULL,
                        `capacity_percentage` REAL NOT NULL,
                        `created_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `case_urgency_flags` (
                        `flag_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `case_id` INTEGER NOT NULL,
                        `flag_type` TEXT NOT NULL,
                        `reason` TEXT,
                        `description` TEXT,
                        `created_at` TEXT,
                        `created_by` INTEGER,
                        `resolved_at` TEXT,
                        `resolved_by` INTEGER
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `case_activities` (
                        `activity_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `case_id` INTEGER NOT NULL,
                        `activity_type` TEXT NOT NULL,
                        `activity_date` TEXT,
                        `activity_time` TEXT,
                        `title` TEXT NOT NULL,
                        `notes` TEXT,
                        `caseworker_id` INTEGER,
                        `location` TEXT,
                        `duration_minutes` INTEGER,
                        `outcome` TEXT,
                        `created_at` TEXT,
                        `updated_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `case_deadlines` (
                        `deadline_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `case_id` INTEGER NOT NULL,
                        `deadline_type` TEXT NOT NULL,
                        `due_date` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT,
                        `status` TEXT,
                        `priority` TEXT,
                        `responsible_party` TEXT,
                        `extension_date` TEXT,
                        `extension_reason` TEXT,
                        `completion_notes` TEXT,
                        `created_at` TEXT,
                        `completed_at` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `case_approvals` (
                        `approval_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `case_id` INTEGER NOT NULL,
                        `approval_type` TEXT NOT NULL,
                        `status` TEXT,
                        `submitted_by` INTEGER,
                        `reviewed_by` INTEGER,
                        `submission_comments` TEXT,
                        `review_comments` TEXT,
                        `revision_requested_on` TEXT,
                        `submitted_date` TEXT,
                        `reviewed_date` TEXT,
                        `required_approval` INTEGER NOT NULL DEFAULT 1
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `placement_compatibility` (
                        `compatibility_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `child_id` INTEGER NOT NULL,
                        `family_id` INTEGER NOT NULL,
                        `compatibility_score` REAL,
                        `medical_needs_support` REAL,
                        `behavioral_needs_support` REAL,
                        `educational_needs_support` REAL,
                        `emotional_support_capacity` REAL,
                        `geographic_preferences_match` REAL,
                        `religious_preference_match` REAL,
                        `cultural_fit_score` REAL,
                        `special_considerations` TEXT,
                        `notes` TEXT,
                        `assessment_date` TEXT,
                        `assessed_by` INTEGER,
                        `last_reviewed` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `workload_tracking` (
                        `workload_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `caseworker_id` INTEGER NOT NULL,
                        `tracking_date` TEXT NOT NULL,
                        `total_active_cases` INTEGER NOT NULL,
                        `cases_with_urgent_flags` INTEGER NOT NULL,
                        `overdue_tasks_count` INTEGER NOT NULL,
                        `scheduled_activities_today` INTEGER NOT NULL,
                        `completed_activities` INTEGER NOT NULL,
                        `documents_processed` INTEGER NOT NULL,
                        `approvals_pending` INTEGER NOT NULL,
                        `time_logged_hours` REAL NOT NULL,
                        `notes` TEXT,
                        `created_at` TEXT
                    )
                """.trimIndent())
            }
        }

        /** Migration from v10 to v11: Empty version bump. */
        val MIGRATION_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {}
        }

        /** Migration from v11 to v12: Empty version bump. */
        val MIGRATION_11_12: Migration = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {}
        }

        /** Migration from v12 to v13: Empty version bump. */
        val MIGRATION_12_13: Migration = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {}
        }

        /** Migration from v13 to v14: Empty version bump. */
        val MIGRATION_13_14: Migration = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {}
        }

        /**
         * Returns the singleton instance of [AppDatabase].
         *
         * @param context Application context.
         * @return The database instance.
         */
        /** Migration from v14 to v15: Adds 25 new tables for missing UI coverage. */
        val MIGRATION_14_15: Migration = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `child_behavior_assessments` (`assessment_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `assessment_date` TEXT NOT NULL, `assessment_tool` TEXT, `behavioral_score` INTEGER, `emotional_score` INTEGER, `social_score` INTEGER, `academic_behavior_score` INTEGER, `overall_score` INTEGER, `strengths` TEXT, `challenges` TEXT, `recommendations` TEXT, `assessed_by` INTEGER, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `child_welfare_incidents` (`incident_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `incident_date` TEXT NOT NULL, `incident_type` TEXT, `severity` TEXT, `description` TEXT, `location` TEXT, `reported_by` INTEGER, `actions_taken` TEXT, `police_involved` INTEGER NOT NULL DEFAULT 0, `police_report_no` TEXT, `follow_up_required` INTEGER NOT NULL DEFAULT 0, `resolved_date` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `vaccination_records` (`vaccination_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `vaccine_name` TEXT NOT NULL, `dose_number` INTEGER, `administration_date` TEXT NOT NULL, `next_due_date` TEXT, `administered_by` TEXT, `facility_name` TEXT, `batch_number` TEXT, `reactions` TEXT, `status` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `siblings` (`sibling_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `sibling_child_id` INTEGER NOT NULL, `relationship_type` TEXT, `same_placement` INTEGER NOT NULL DEFAULT 0, `contact_allowed` INTEGER NOT NULL DEFAULT 1, `notes` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `consent_records` (`consent_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `consent_type` TEXT NOT NULL, `provided_by` TEXT, `relationship_to_child` TEXT, `consent_date` TEXT NOT NULL, `expiry_date` TEXT, `consent_form_file` TEXT, `witness_name` TEXT, `is_valid` INTEGER NOT NULL DEFAULT 1, `revoked_date` TEXT, `revoked_reason` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `child_services_referrals` (`referral_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `service_type` TEXT NOT NULL, `provider_id` INTEGER, `referral_date` TEXT NOT NULL, `authorization_date` TEXT, `start_date` TEXT, `end_date` TEXT, `frequency` TEXT, `status` TEXT, `authorized_by` INTEGER, `cost_estimate` REAL, `actual_cost` REAL, `notes` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `investigations` (`investigation_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `case_number` TEXT, `investigation_type` TEXT, `opened_date` TEXT NOT NULL, `closed_date` TEXT, `status` TEXT, `allegation` TEXT, `findings` TEXT, `recommendations` TEXT, `investigator_id` INTEGER, `supervisor_id` INTEGER, `report_file` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `service_plans` (`plan_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `plan_name` TEXT NOT NULL, `start_date` TEXT NOT NULL, `end_date` TEXT, `status` TEXT, `goals_summary` TEXT, `created_by` INTEGER, `approved_by` INTEGER, `approval_date` TEXT, `review_date` TEXT, `next_review_date` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `service_plan_goals` (`goal_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `plan_id` INTEGER NOT NULL, `goal_description` TEXT NOT NULL, `target_date` TEXT, `status` TEXT, `completion_date` TEXT, `completion_notes` TEXT, `created_by` INTEGER, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `visitation_schedules` (`visitation_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `visitor_name` TEXT NOT NULL, `visitor_relationship` TEXT, `visitation_date` TEXT NOT NULL, `start_time` TEXT, `end_time` TEXT, `location` TEXT, `supervised_by` INTEGER, `status` TEXT, `notes` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `referrals` (`referral_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER, `referral_type` TEXT, `referred_to` TEXT, `reason` TEXT, `referral_date` TEXT, `outcome` TEXT, `notes` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `aftercare_plans` (`aftercare_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `plan_name` TEXT NOT NULL, `start_date` TEXT NOT NULL, `end_date` TEXT, `support_services` TEXT, `housing_arrangement` TEXT, `education_employment` TEXT, `financial_support` TEXT, `mentorship_assigned` TEXT, `caseworker_id` INTEGER, `status` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `organization_partners` (`partner_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `partner_name` TEXT NOT NULL, `partner_type` TEXT, `contact_person` TEXT, `phone` TEXT, `email` TEXT, `mou_date` TEXT, `mou_expiry` TEXT, `services_provided` TEXT, `is_active` INTEGER NOT NULL DEFAULT 1, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `service_providers` (`provider_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `provider_name` TEXT NOT NULL, `provider_type` TEXT, `contact_person` TEXT, `phone` TEXT, `email` TEXT, `address` TEXT, `county` TEXT, `is_active` INTEGER NOT NULL DEFAULT 1, `contract_start` TEXT, `contract_end` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `donor_funding` (`donation_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `donor_name` TEXT NOT NULL, `donor_type` TEXT, `amount` REAL NOT NULL, `donation_date` TEXT NOT NULL, `purpose` TEXT, `reference_number` TEXT, `received_by` INTEGER, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `budget_allocations` (`allocation_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `financial_year` TEXT, `category` TEXT, `allocated_amount` REAL, `utilized_amount` REAL DEFAULT 0.0, `remaining_amount` REAL, `notes` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `counties` (`county_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `county_name` TEXT NOT NULL, `county_code` TEXT, `region` TEXT, `is_active` INTEGER NOT NULL DEFAULT 1, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `county_offices` (`office_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `office_name` TEXT NOT NULL, `office_code` TEXT, `county` TEXT NOT NULL, `sub_county` TEXT, `address` TEXT, `po_box` TEXT, `phone` TEXT, `alt_phone` TEXT, `email` TEXT, `head_officer_name` TEXT, `head_officer_title` TEXT, `head_officer_user_id` INTEGER, `head_officer_phone` TEXT, `is_active` INTEGER NOT NULL DEFAULT 1, `latitude` REAL, `longitude` REAL, `notes` TEXT, `created_at` TEXT, `updated_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `placement_disruptions` (`disruption_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `placement_id` INTEGER NOT NULL, `child_id` INTEGER NOT NULL, `disruption_date` TEXT NOT NULL, `disruption_type` TEXT, `reason` TEXT, `child_behavior_factor` TEXT, `family_factor` TEXT, `agency_factor` TEXT, `reunification_attempted` INTEGER NOT NULL DEFAULT 0, `new_placement_id` INTEGER, `caseworker_id` INTEGER, `notes` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `foster_family_training` (`training_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `family_id` INTEGER NOT NULL, `training_name` TEXT NOT NULL, `training_date` TEXT NOT NULL, `completion_date` TEXT, `status` TEXT, `trainer_name` TEXT, `certificate_issued` INTEGER NOT NULL DEFAULT 0, `certificate_number` TEXT, `notes` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `reports_generated` (`report_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `report_name` TEXT NOT NULL, `report_type` TEXT, `generated_by` INTEGER, `generated_date` TEXT, `file_path` TEXT, `file_size` INTEGER, `download_count` INTEGER DEFAULT 0, `is_deleted` INTEGER NOT NULL DEFAULT 0, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `emergency_events` (`event_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER, `reported_by` INTEGER, `event_type` TEXT, `event_date` TEXT, `description` TEXT, `action_taken` TEXT, `status` TEXT, `resolved_by` INTEGER, `resolved_at` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `global_document_storage` (`document_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `document_name` TEXT NOT NULL, `document_category` TEXT, `document_type` TEXT, `file_path` TEXT, `file_size` INTEGER, `mime_type` TEXT, `uploaded_by` INTEGER, `is_public` INTEGER NOT NULL DEFAULT 0, `description` TEXT, `version` INTEGER DEFAULT 1, `uploaded_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `inter_county_transfers` (`transfer_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `child_id` INTEGER NOT NULL, `from_county` TEXT, `to_county` TEXT, `transfer_date` TEXT NOT NULL, `reason` TEXT, `authorized_by` INTEGER, `receiving_officer` INTEGER, `documents_transferred` INTEGER NOT NULL DEFAULT 0, `transfer_status` TEXT, `notes` TEXT, `created_at` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `worker_location_tracking` (`tracking_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `user_id` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `accuracy` REAL, `tracking_time` TEXT NOT NULL, `activity_type` TEXT, `notes` TEXT, `created_at` TEXT)")
            }
        }

        /** Migration from v15 to v16: Adds sync metadata to the users table. */
        val MIGRATION_15_16: Migration = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `users` ADD COLUMN `remote_id` TEXT")
                db.execSQL("ALTER TABLE `users` ADD COLUMN `sync_status` TEXT NOT NULL DEFAULT 'PENDING'")
                db.execSQL("ALTER TABLE `users` ADD COLUMN `last_synced_at` INTEGER")
            }
        }

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "adoption_childcare.db"
            )
                .addMigrations(
                    MIGRATION_3_4, 
                    MIGRATION_4_5, 
                    MIGRATION_5_6, 
                    MIGRATION_6_7, 
                    MIGRATION_7_8,
                    MIGRATION_8_9,
                    MIGRATION_9_10,
                    MIGRATION_10_11,
                    MIGRATION_11_12,
                    MIGRATION_12_13,
                    MIGRATION_13_14,
                    MIGRATION_14_15,
                    MIGRATION_15_16
                )
                .fallbackToDestructiveMigration(true)
                .build()
                .also { INSTANCE = it }
        }
    }
}
