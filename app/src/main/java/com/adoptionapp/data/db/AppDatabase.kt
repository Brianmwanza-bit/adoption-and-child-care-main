package com.adoptionapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adoptionapp.data.db.dao.*
import com.adoptionapp.data.db.entities.AuditLogEntity
import com.adoptionapp.data.db.entities.CaseReportEntity
import com.adoptionapp.data.db.entities.ChildEntity
import com.adoptionapp.data.db.entities.FamilyEntity
import com.adoptionapp.data.db.entities.CourtCaseEntity
import com.adoptionapp.data.db.entities.DocumentEntity
import com.adoptionapp.data.db.entities.EducationRecordEntity
import com.adoptionapp.data.db.entities.GuardianEntity
import com.adoptionapp.data.db.entities.AdoptionApplicationEntity
import com.adoptionapp.data.db.entities.HomeStudyEntity
import com.adoptionapp.data.db.entities.MedicalRecordEntity
import com.adoptionapp.data.db.entities.MoneyRecordEntity
import com.adoptionapp.data.db.entities.PermissionEntity
import com.adoptionapp.data.db.entities.PlacementEntity
import com.adoptionapp.data.db.entities.UserEntity
import com.adoptionapp.data.db.entities.UserPermissionEntity

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
    ],
    version = 1,
    exportSchema = true
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
