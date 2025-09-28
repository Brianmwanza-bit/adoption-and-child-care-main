package com.example.adoption_and_childcare.di

import android.content.Context
import androidx.room.Room
import com.example.adoption_and_childcare.data.db.AppDatabase
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "adoption_childcare.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideChildDao(database: AppDatabase): ChildDao = database.childDao()

    @Provides
    fun provideDocumentDao(database: AppDatabase): DocumentDao = database.documentDao()

    @Provides
    fun providePlacementDao(database: AppDatabase): PlacementDao = database.placementDao()

    @Provides
    fun provideFamilyDao(database: AppDatabase): FamilyDao = database.familyDao()

    @Provides
    fun provideAdoptionApplicationDao(database: AppDatabase): AdoptionApplicationDao = database.adoptionApplicationDao()

    @Provides
    fun provideHomeStudyDao(database: AppDatabase): HomeStudyDao = database.homeStudyDao()

    @Provides
    fun provideCaseReportDao(database: AppDatabase): CaseReportDao = database.caseReportDao()

    @Provides
    fun provideEducationRecordDao(database: AppDatabase): EducationRecordDao = database.educationRecordDao()

    @Provides
    fun provideMedicalRecordDao(database: AppDatabase): MedicalRecordDao = database.medicalRecordDao()

    @Provides
    fun provideMoneyRecordDao(database: AppDatabase): MoneyRecordDao = database.moneyRecordDao()

    @Provides
    fun provideAuditLogDao(database: AppDatabase): AuditLogDao = database.auditLogDao()

    @Provides
    fun provideCourtCaseDao(database: AppDatabase): CourtCaseDao = database.courtCaseDao()

    @Provides
    fun provideGuardianDao(database: AppDatabase): GuardianDao = database.guardianDao()

    @Provides
    fun providePermissionDao(database: AppDatabase): PermissionDao = database.permissionDao()

    @Provides
    fun provideUserPermissionDao(database: AppDatabase): UserPermissionDao = database.userPermissionDao()

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao = database.notificationDao()
}
