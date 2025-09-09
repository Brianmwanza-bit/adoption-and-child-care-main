package com.yourdomain.adoptionchildcare.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yourdomain.adoptionchildcare.data.dao.*
import com.yourdomain.adoptionchildcare.data.entities.*

@Database(
    entities = [
        UserEntity::class,
        ChildEntity::class,
        AuditLogEntity::class,
        CaseReportEntity::class,
        CourtCaseEntity::class,
        DocumentEntity::class,
        EducationRecordEntity::class,
        GuardianEntity::class,
        MedicalRecordEntity::class,
        MoneyRecordEntity::class,
        PermissionEntity::class,
        PlacementEntity::class,
        UserPermissionEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun caseReportDao(): CaseReportDao
    abstract fun courtCaseDao(): CourtCaseDao
    abstract fun documentDao(): DocumentDao
    abstract fun educationRecordDao(): EducationRecordDao
    abstract fun guardianDao(): GuardianDao
    abstract fun medicalRecordDao(): MedicalRecordDao
    abstract fun moneyRecordDao(): MoneyRecordDao
    abstract fun permissionDao(): PermissionDao
    abstract fun placementDao(): PlacementDao
    abstract fun userPermissionDao(): UserPermissionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "adoption_childcare.db"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}
