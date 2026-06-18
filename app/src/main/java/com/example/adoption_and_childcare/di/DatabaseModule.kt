package com.example.adoption_and_childcare.di

import android.content.Context
import androidx.room.Room
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.dao.*
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
        return AppDatabase.getInstance(context)
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

    @Provides
    fun provideSyncQueueDao(database: AppDatabase): SyncQueueDao = database.syncQueueDao()

    @Provides
    fun provideSOSLocationDao(database: AppDatabase): SOSLocationDao = database.sosLocationDao()

    @Provides
    fun provideSystemSettingDao(database: AppDatabase): SystemSettingDao = database.systemSettingDao()

    @Provides
    fun provideBackgroundCheckDao(database: AppDatabase): BackgroundCheckDao = database.backgroundCheckDao()

    @Provides
    fun provideFosterTaskDao(database: AppDatabase): FosterTaskDao = database.fosterTaskDao()

    @Provides
    fun provideFosterMatchDao(database: AppDatabase): FosterMatchDao = database.fosterMatchDao()

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideActionItemDao(database: AppDatabase): ActionItemDao = database.actionItemDao()

    @Provides
    fun provideDashboardMetricDao(database: AppDatabase): DashboardMetricDao = database.dashboardMetricDao()

    @Provides
    fun provideDashboardPreferenceDao(database: AppDatabase): DashboardPreferenceDao = database.dashboardPreferenceDao()

    @Provides
    fun provideCriticalDateDao(database: AppDatabase): CriticalDateDao = database.criticalDateDao()

    @Provides
    fun provideWorkerMessageDao(database: AppDatabase): WorkerMessageDao = database.workerMessageDao()

    @Provides
    fun provideRiskAssessmentDao(database: AppDatabase): RiskAssessmentDao = database.riskAssessmentDao()

    @Provides
    fun providePermanencyPlanDao(database: AppDatabase): PermanencyPlanDao = database.permanencyPlanDao()

    @Provides
    fun provideCaseloadDao(database: AppDatabase): CaseloadDao = database.caseloadDao()

    @Provides
    fun provideCaseUrgencyFlagDao(database: AppDatabase): CaseUrgencyFlagDao = database.caseUrgencyFlagDao()

    @Provides
    fun provideCaseActivityDao(database: AppDatabase): CaseActivityDao = database.caseActivityDao()

    @Provides
    fun provideCaseDeadlineDao(database: AppDatabase): CaseDeadlineDao = database.caseDeadlineDao()

    @Provides
    fun provideCaseApprovalDao(database: AppDatabase): CaseApprovalDao = database.caseApprovalDao()

    @Provides
    fun providePlacementCompatibilityDao(database: AppDatabase): PlacementCompatibilityDao = database.placementCompatibilityDao()

    @Provides
    fun provideWorkloadTrackingDao(database: AppDatabase): WorkloadTrackingDao = database.workloadTrackingDao()

    @Provides
    fun provideChildBehaviorAssessmentDao(database: AppDatabase): ChildBehaviorAssessmentDao = database.childBehaviorAssessmentDao()

    @Provides
    fun provideChildWelfareIncidentDao(database: AppDatabase): ChildWelfareIncidentDao = database.childWelfareIncidentDao()

    @Provides
    fun provideVaccinationRecordDao(database: AppDatabase): VaccinationRecordDao = database.vaccinationRecordDao()

    @Provides
    fun provideSiblingDao(database: AppDatabase): SiblingDao = database.siblingDao()

    @Provides
    fun provideConsentRecordDao(database: AppDatabase): ConsentRecordDao = database.consentRecordDao()

    @Provides
    fun provideChildServicesReferralDao(database: AppDatabase): ChildServicesReferralDao = database.childServicesReferralDao()

    @Provides
    fun provideInvestigationDao(database: AppDatabase): InvestigationDao = database.investigationDao()

    @Provides
    fun provideServicePlanDao(database: AppDatabase): ServicePlanDao = database.servicePlanDao()

    @Provides
    fun provideServicePlanGoalDao(database: AppDatabase): ServicePlanGoalDao = database.servicePlanGoalDao()

    @Provides
    fun provideVisitationScheduleDao(database: AppDatabase): VisitationScheduleDao = database.visitationScheduleDao()

    @Provides
    fun provideReferralDao(database: AppDatabase): ReferralDao = database.referralDao()

    @Provides
    fun provideAftercarePlanDao(database: AppDatabase): AftercarePlanDao = database.aftercarePlanDao()

    @Provides
    fun provideOrganizationPartnerDao(database: AppDatabase): OrganizationPartnerDao = database.organizationPartnerDao()

    @Provides
    fun provideServiceProviderDao(database: AppDatabase): ServiceProviderDao = database.serviceProviderDao()

    @Provides
    fun provideDonorFundingDao(database: AppDatabase): DonorFundingDao = database.donorFundingDao()

    @Provides
    fun provideBudgetAllocationDao(database: AppDatabase): BudgetAllocationDao = database.budgetAllocationDao()

    @Provides
    fun provideCountyDao(database: AppDatabase): CountyDao = database.countyDao()

    @Provides
    fun provideCountyOfficeDao(database: AppDatabase): CountyOfficeDao = database.countyOfficeDao()

    @Provides
    fun providePlacementDisruptionDao(database: AppDatabase): PlacementDisruptionDao = database.placementDisruptionDao()

    @Provides
    fun provideFosterFamilyTrainingDao(database: AppDatabase): FosterFamilyTrainingDao = database.fosterFamilyTrainingDao()

    @Provides
    fun provideReportGeneratedDao(database: AppDatabase): ReportGeneratedDao = database.reportGeneratedDao()

    @Provides
    fun provideEmergencyEventDao(database: AppDatabase): EmergencyEventDao = database.emergencyEventDao()

    @Provides
    fun provideGlobalDocumentStorageDao(database: AppDatabase): GlobalDocumentStorageDao = database.globalDocumentStorageDao()

    @Provides
    fun provideInterCountyTransferDao(database: AppDatabase): InterCountyTransferDao = database.interCountyTransferDao()

    @Provides
    fun provideWorkerLocationTrackingDao(database: AppDatabase): WorkerLocationTrackingDao = database.workerLocationTrackingDao()
}
