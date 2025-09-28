package com.example.adoption_and_childcare.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.adoption_and_childcare.data.db.dao.AdoptionApplicationDao
import com.example.adoption_and_childcare.data.db.dao.AuditLogDao
import com.example.adoption_and_childcare.data.db.dao.CaseReportDao
import com.example.adoption_and_childcare.data.db.dao.ChildDao
import com.example.adoption_and_childcare.data.db.dao.CourtCaseDao
import com.example.adoption_and_childcare.data.db.dao.DocumentDao
import com.example.adoption_and_childcare.data.db.dao.EducationRecordDao
import com.example.adoption_and_childcare.data.db.dao.FamilyDao
import com.example.adoption_and_childcare.data.db.dao.GuardianDao
import com.example.adoption_and_childcare.data.db.dao.HomeStudyDao
import com.example.adoption_and_childcare.data.db.dao.MedicalRecordDao
import com.example.adoption_and_childcare.data.db.dao.MoneyRecordDao
import com.example.adoption_and_childcare.data.db.dao.NotificationDao
import com.example.adoption_and_childcare.data.db.dao.PermissionDao
import com.example.adoption_and_childcare.data.db.dao.PlacementDao
import com.example.adoption_and_childcare.data.db.dao.UserDao
import com.example.adoption_and_childcare.data.db.dao.UserPermissionDao
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import com.example.adoption_and_childcare.data.db.entities.AuditLogEntity
import com.example.adoption_and_childcare.data.db.entities.CaseReportEntity
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.data.db.entities.GuardianEntity
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity
import com.example.adoption_and_childcare.data.db.entities.NotificationEntity
import com.example.adoption_and_childcare.data.db.entities.PermissionEntity
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity

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
    ],
    version = 1,
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
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "adoption_childcare.db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }
}
