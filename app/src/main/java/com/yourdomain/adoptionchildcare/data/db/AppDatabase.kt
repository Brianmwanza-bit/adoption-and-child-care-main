package com.yourdomain.adoptionchildcare.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.RoomDatabase
import com.yourdomain.adoptionchildcare.data.db.dao.*
import com.yourdomain.adoptionchildcare.data.db.entities.AuditLogEntity
import com.yourdomain.adoptionchildcare.data.db.entities.IconEntity
import com.yourdomain.adoptionchildcare.data.db.entities.CaseReportEntity
import com.yourdomain.adoptionchildcare.data.db.entities.ChildEntity
import com.yourdomain.adoptionchildcare.data.db.entities.FamilyEntity
import com.yourdomain.adoptionchildcare.data.db.entities.CourtCaseEntity
import com.yourdomain.adoptionchildcare.data.db.entities.DocumentEntity
import com.yourdomain.adoptionchildcare.data.db.entities.EducationRecordEntity
import com.yourdomain.adoptionchildcare.data.db.entities.GuardianEntity
import com.yourdomain.adoptionchildcare.data.db.entities.AdoptionApplicationEntity
import com.yourdomain.adoptionchildcare.data.db.entities.HomeStudyEntity
import com.yourdomain.adoptionchildcare.data.db.entities.MedicalRecordEntity
import com.yourdomain.adoptionchildcare.data.db.entities.MoneyRecordEntity
import com.yourdomain.adoptionchildcare.data.db.entities.PermissionEntity
import com.yourdomain.adoptionchildcare.data.db.entities.PlacementEntity
import com.yourdomain.adoptionchildcare.data.db.entities.UserEntity
import com.yourdomain.adoptionchildcare.data.db.entities.UserPermissionEntity

@Database(
    entities = [
        UserEntity::class,
    IconEntity::class,
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
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun childDao(): ChildDao
    abstract fun documentDao(): DocumentDao
    abstract fun placementDao(): PlacementDao
    abstract fun familyDao(): FamilyDao
    abstract fun adoptionApplicationDao(): AdoptionApplicationDao
    abstract fun homeStudyDao(): HomeStudyDao
    abstract fun caseReportDao(): CaseReportDao
    abstract fun educationRecordDao(): EducationRecordDao
    abstract fun medicalRecordDao(): MedicalRecordDao
    abstract fun moneyRecordDao(): MoneyRecordDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun courtCaseDao(): CourtCaseDao
    abstract fun guardianDao(): GuardianDao
    abstract fun permissionDao(): PermissionDao
    abstract fun userPermissionDao(): UserPermissionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns with NULL default so existing rows are preserved
                try {
                    database.execSQL("ALTER TABLE users ADD COLUMN profile_photo_uri TEXT")
                } catch (e: Exception) { /* column may already exist */ }
                try {
                    database.execSQL("ALTER TABLE case_reports ADD COLUMN adoption_status TEXT")
                } catch (e: Exception) { }
                try {
                    database.execSQL("ALTER TABLE families ADD COLUMN adoption_eligibility TEXT")
                } catch (e: Exception) { }
                try {
                    database.execSQL("ALTER TABLE audit_logs ADD COLUMN call_card TEXT")
                } catch (e: Exception) { }

                // Create icons table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS icons (
                      icon_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      `key` TEXT,
                      url TEXT,
                      provider TEXT,
                      created_at TEXT
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "adoption_childcare.db"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { INSTANCE = it }
        }
    }
}
