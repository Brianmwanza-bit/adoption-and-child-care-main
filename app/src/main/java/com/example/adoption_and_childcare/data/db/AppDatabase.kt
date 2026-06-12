package com.example.adoption_and_childcare.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.adoption_and_childcare.data.db.dao.AdoptionApplicationDao
import com.example.adoption_and_childcare.data.db.dao.AuditLogDao
import com.example.adoption_and_childcare.data.db.dao.BackgroundCheckDao
import com.example.adoption_and_childcare.data.db.dao.CaseReportDao
import com.example.adoption_and_childcare.data.db.dao.ChildDao
import com.example.adoption_and_childcare.data.db.dao.CourtCaseDao
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
import com.example.adoption_and_childcare.data.db.dao.PermissionDao
import com.example.adoption_and_childcare.data.db.dao.PlacementDao
import com.example.adoption_and_childcare.data.db.dao.SystemSettingDao
import com.example.adoption_and_childcare.data.db.dao.UserDao
import com.example.adoption_and_childcare.data.db.dao.UserPermissionDao
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import com.example.adoption_and_childcare.data.db.entities.AuditLogEntity
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import com.example.adoption_and_childcare.data.db.entities.CaseReportEntity
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity
import com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.data.db.entities.GuardianEntity
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity
import com.example.adoption_and_childcare.data.db.entities.NotificationEntity
import com.example.adoption_and_childcare.data.db.entities.PermissionEntity
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.adoption_and_childcare.data.db.dao.SOSLocationDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.SOSLocationEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity

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
    ],
    version = 9,
    exportSchema = false
)
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

        /**
         * Returns the singleton instance of [AppDatabase].
         *
         * @param context Application context.
         * @return The database instance.
         */
        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "adoption_childcare.db"
            )
                .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                .fallbackToDestructiveMigration(true)
                .build()
                .also { INSTANCE = it }
        }
    }
}
