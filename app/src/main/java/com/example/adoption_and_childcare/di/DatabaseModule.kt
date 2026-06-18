package com.example.adoption_and_childcare.di

import android.content.Context
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.AppDatabaseMinimal
import com.example.adoption_and_childcare.data.db.dao.ActionItemDao
import com.example.adoption_and_childcare.data.db.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the singleton instance of the main application database.
     *
     * @param context The application context.
     * @return The [AppDatabase] instance.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    /**
     * Provides the singleton instance of the minimal application database.
     *
     * @param context The application context.
     * @return The [AppDatabaseMinimal] instance.
     */
    @Provides
    @Singleton
    fun provideAppDatabaseMinimal(@ApplicationContext context: Context): AppDatabaseMinimal {
        return AppDatabaseMinimal.getInstance(context)
    }

    /**
     * Provides the DAO for interacting with users from the main database.
     *
     * @param database The [AppDatabase] instance.
     * @return The [UserDao] instance.
     */
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    /**
     * Provides the DAO for interacting with children.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ChildDao] instance.
     */
    @Provides
    fun provideChildDao(database: AppDatabase): ChildDao = database.childDao()

    /**
     * Provides the DAO for interacting with documents.
     *
     * @param database The [AppDatabase] instance.
     * @return The [DocumentDao] instance.
     */
    @Provides
    fun provideDocumentDao(database: AppDatabase): DocumentDao = database.documentDao()

    /**
     * Provides the DAO for interacting with placements.
     *
     * @param database The [AppDatabase] instance.
     * @return The [PlacementDao] instance.
     */
    @Provides
    fun providePlacementDao(database: AppDatabase): PlacementDao = database.placementDao()

    /**
     * Provides the DAO for interacting with families.
     *
     * @param database The [AppDatabase] instance.
     * @return The [FamilyDao] instance.
     */
    @Provides
    fun provideFamilyDao(database: AppDatabase): FamilyDao = database.familyDao()

    /**
     * Provides the DAO for interacting with adoption applications.
     *
     * @param database The [AppDatabase] instance.
     * @return The [AdoptionApplicationDao] instance.
     */
    @Provides
    fun provideAdoptionApplicationDao(database: AppDatabase): AdoptionApplicationDao = database.adoptionApplicationDao()

    /**
     * Provides the DAO for interacting with home studies.
     *
     * @param database The [AppDatabase] instance.
     * @return The [HomeStudyDao] instance.
     */
    @Provides
    fun provideHomeStudyDao(database: AppDatabase): HomeStudyDao = database.homeStudyDao()

    /**
     * Provides the DAO for interacting with case reports.
     *
     * @param database The [AppDatabase] instance.
     * @return The [CaseReportDao] instance.
     */
    @Provides
    fun provideCaseReportDao(database: AppDatabase): CaseReportDao = database.caseReportDao()

    /**
     * Provides the DAO for interacting with education records.
     *
     * @param database The [AppDatabase] instance.
     * @return The [EducationRecordDao] instance.
     */
    @Provides
    fun provideEducationRecordDao(database: AppDatabase): EducationRecordDao = database.educationRecordDao()

    /**
     * Provides the DAO for interacting with medical records.
     *
     * @param database The [AppDatabase] instance.
     * @return The [MedicalRecordDao] instance.
     */
    @Provides
    fun provideMedicalRecordDao(database: AppDatabase): MedicalRecordDao = database.medicalRecordDao()

    /**
     * Provides the DAO for interacting with money records.
     *
     * @param database The [AppDatabase] instance.
     * @return The [MoneyRecordDao] instance.
     */
    @Provides
    fun provideMoneyRecordDao(database: AppDatabase): MoneyRecordDao = database.moneyRecordDao()

    /**
     * Provides the DAO for interacting with audit logs.
     *
     * @param database The [AppDatabase] instance.
     * @return The [AuditLogDao] instance.
     */
    @Provides
    fun provideAuditLogDao(database: AppDatabase): AuditLogDao = database.auditLogDao()

    /**
     * Provides the DAO for interacting with court cases.
     *
     * @param database The [AppDatabase] instance.
     * @return The [CourtCaseDao] instance.
     */
    @Provides
    fun provideCourtCaseDao(database: AppDatabase): CourtCaseDao = database.courtCaseDao()

    /**
     * Provides the DAO for interacting with guardians.
     *
     * @param database The [AppDatabase] instance.
     * @return The [GuardianDao] instance.
     */
    @Provides
    fun provideGuardianDao(database: AppDatabase): GuardianDao = database.guardianDao()

    /**
     * Provides the DAO for interacting with permissions.
     *
     * @param database The [AppDatabase] instance.
     * @return The [PermissionDao] instance.
     */
    @Provides
    fun providePermissionDao(database: AppDatabase): PermissionDao = database.permissionDao()

    /**
     * Provides the DAO for interacting with user permissions.
     *
     * @param database The [AppDatabase] instance.
     * @return The [UserPermissionDao] instance.
     */
    @Provides
    fun provideUserPermissionDao(database: AppDatabase): UserPermissionDao = database.userPermissionDao()

    /**
     * Provides the DAO for interacting with notifications.
     *
     * @param database The [AppDatabase] instance.
     * @return The [NotificationDao] instance.
     */
    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao = database.notificationDao()

    /**
     * Provides the DAO for interacting with the sync queue.
     *
     * @param database The [AppDatabase] instance.
     * @return The [SyncQueueDao] instance.
     */
    @Provides
    fun provideSyncQueueDao(database: AppDatabase): SyncQueueDao = database.syncQueueDao()

    /**
     * Provides the DAO for interacting with SOS locations.
     *
     * @param database The [AppDatabase] instance.
     * @return The [SOSLocationDao] instance.
     */
    @Provides
    fun provideSOSLocationDao(database: AppDatabase): SOSLocationDao = database.sosLocationDao()

    /**
     * Provides the DAO for interacting with system settings.
     *
     * @param database The [AppDatabase] instance.
     * @return The [SystemSettingDao] instance.
     */
    @Provides
    fun provideSystemSettingDao(database: AppDatabase): SystemSettingDao = database.systemSettingDao()

    /**
     * Provides the DAO for interacting with background checks.
     *
     * @param database The [AppDatabase] instance.
     * @return The [BackgroundCheckDao] instance.
     */
    @Provides
    fun provideBackgroundCheckDao(database: AppDatabase): BackgroundCheckDao = database.backgroundCheckDao()

    /**
     * Provides the DAO for interacting with foster tasks.
     *
     * @param database The [AppDatabase] instance.
     * @return The [FosterTaskDao] instance.
     */
    @Provides
    fun provideFosterTaskDao(database: AppDatabase): FosterTaskDao = database.fosterTaskDao()

    /**
     * Provides the DAO for interacting with foster matches.
     *
     * @param database The [AppDatabase] instance.
     * @return The [FosterMatchDao] instance.
     */
    @Provides
    fun provideFosterMatchDao(database: AppDatabase): FosterMatchDao = database.fosterMatchDao()

    /**
     * Provides the DAO for interacting with general tasks.
     *
     * @param database The [AppDatabase] instance.
     * @return The [TaskDao] instance.
     */
    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    /**
     * Provides the DAO for interacting with action items.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ActionItemDao] instance.
     */
    @Provides
    fun provideActionItemDao(database: AppDatabase): ActionItemDao = database.actionItemDao()

    /**
     * Provides the DAO for interacting with dashboard metrics.
     *
     * @param database The [AppDatabase] instance.
     * @return The [DashboardMetricDao] instance.
     */
    @Provides
    fun provideDashboardMetricDao(database: AppDatabase): DashboardMetricDao = database.dashboardMetricDao()

    /**
     * Provides the DAO for interacting with dashboard preferences.
     *
     * @param database The [AppDatabase] instance.
     * @return The [DashboardPreferenceDao] instance.
     */
    @Provides
    fun provideDashboardPreferenceDao(database: AppDatabase): DashboardPreferenceDao = database.dashboardPreferenceDao()

    /**
     * Provides the DAO for interacting with critical dates.
     *
     * @param database The [AppDatabase] instance.
     * @return The [CriticalDateDao] instance.
     */
    @Provides
    fun provideCriticalDateDao(database: AppDatabase): CriticalDateDao = database.criticalDateDao()

    /**
     * Provides the DAO for interacting with worker messages.
     *
     * @param database The [AppDatabase] instance.
     * @return The [WorkerMessageDao] instance.
     */
    @Provides
    fun provideWorkerMessageDao(database: AppDatabase): WorkerMessageDao = database.workerMessageDao()

    /**
     * Provides the DAO for interacting with risk assessments.
     *
     * @param database The [AppDatabase] instance.
     * @return The [RiskAssessmentDao] instance.
     */
    @Provides
    fun provideRiskAssessmentDao(database: AppDatabase): RiskAssessmentDao = database.riskAssessmentDao()

    /**
     * Provides the DAO for interacting with permanency plans.
     *
     * @param database The [AppDatabase] instance.
     * @return The [PermanencyPlanDao] instance.
     */
    @Provides
    fun providePermanencyPlanDao(database: AppDatabase): PermanencyPlanDao = database.permanencyPlanDao()

    /**
     * Provides the DAO for interacting with caseload information.
     *
     * @param database The [AppDatabase] instance.
     * @return The [CaseloadDao] instance.
     */
    @Provides
    fun provideCaseloadDao(database: AppDatabase): CaseloadDao = database.caseloadDao()

    /**
     * Provides the DAO for interacting with case urgency flags.
     *
     * @param database The [AppDatabase] instance.
     * @return The [CaseUrgencyFlagDao] instance.
     */
    @Provides
    fun provideCaseUrgencyFlagDao(database: AppDatabase): CaseUrgencyFlagDao = database.caseUrgencyFlagDao()

    /**
     * Provides the DAO for interacting with case activities.
     *
     * @param database The [AppDatabase] instance.
     * @return The [CaseActivityDao] instance.
     */
    @Provides
    fun provideCaseActivityDao(database: AppDatabase): CaseActivityDao = database.caseActivityDao()

    /**
     * Provides the DAO for interacting with case deadlines.
     *
     * @param database The [AppDatabase] instance.
     * @return The [CaseDeadlineDao] instance.
     */
    @Provides
    fun provideCaseDeadlineDao(database: AppDatabase): CaseDeadlineDao = database.caseDeadlineDao()

    /**
     * Provides the DAO for interacting with case approvals.
     *
     * @param database The [AppDatabase] instance.
     * @return The [CaseApprovalDao] instance.
     */
    @Provides
    fun provideCaseApprovalDao(database: AppDatabase): CaseApprovalDao = database.caseApprovalDao()

    /**
     * Provides the DAO for interacting with placement compatibility.
     *
     * @param database The [AppDatabase] instance.
     * @return The [PlacementCompatibilityDao] instance.
     */
    @Provides
    fun providePlacementCompatibilityDao(database: AppDatabase): PlacementCompatibilityDao = database.placementCompatibilityDao()

    /**
     * Provides the DAO for interacting with workload tracking.
     *
     * @param database The [AppDatabase] instance.
     * @return The [WorkloadTrackingDao] instance.
     */
    @Provides
    fun provideWorkloadTrackingDao(database: AppDatabase): WorkloadTrackingDao = database.workloadTrackingDao()

    /**
     * Provides the DAO for child behavior assessments.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ChildBehaviorAssessmentDao] instance.
     */
    @Provides
    fun provideChildBehaviorAssessmentDao(database: AppDatabase): ChildBehaviorAssessmentDao = database.childBehaviorAssessmentDao()

    /**
     * Provides the DAO for child welfare incidents.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ChildWelfareIncidentDao] instance.
     */
    @Provides
    fun provideChildWelfareIncidentDao(database: AppDatabase): ChildWelfareIncidentDao = database.childWelfareIncidentDao()

    /**
     * Provides the DAO for vaccination records.
     *
     * @param database The [AppDatabase] instance.
     * @return The [VaccinationRecordDao] instance.
     */
    @Provides
    fun provideVaccinationRecordDao(database: AppDatabase): VaccinationRecordDao = database.vaccinationRecordDao()

    /**
     * Provides the DAO for sibling relationships.
     *
     * @param database The [AppDatabase] instance.
     * @return The [SiblingDao] instance.
     */
    @Provides
    fun provideSiblingDao(database: AppDatabase): SiblingDao = database.siblingDao()

    /**
     * Provides the DAO for consent records.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ConsentRecordDao] instance.
     */
    @Provides
    fun provideConsentRecordDao(database: AppDatabase): ConsentRecordDao = database.consentRecordDao()

    /**
     * Provides the DAO for child services referrals.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ChildServicesReferralDao] instance.
     */
    @Provides
    fun provideChildServicesReferralDao(database: AppDatabase): ChildServicesReferralDao = database.childServicesReferralDao()

    /**
     * Provides the DAO for investigations.
     *
     * @param database The [AppDatabase] instance.
     * @return The [InvestigationDao] instance.
     */
    @Provides
    fun provideInvestigationDao(database: AppDatabase): InvestigationDao = database.investigationDao()

    /**
     * Provides the DAO for service plans.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ServicePlanDao] instance.
     */
    @Provides
    fun provideServicePlanDao(database: AppDatabase): ServicePlanDao = database.servicePlanDao()

    /**
     * Provides the DAO for service plan goals.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ServicePlanGoalDao] instance.
     */
    @Provides
    fun provideServicePlanGoalDao(database: AppDatabase): ServicePlanGoalDao = database.servicePlanGoalDao()

    /**
     * Provides the DAO for visitation schedules.
     *
     * @param database The [AppDatabase] instance.
     * @return The [VisitationScheduleDao] instance.
     */
    @Provides
    fun provideVisitationScheduleDao(database: AppDatabase): VisitationScheduleDao = database.visitationScheduleDao()

    /**
     * Provides the DAO for general referrals.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ReferralDao] instance.
     */
    @Provides
    fun provideReferralDao(database: AppDatabase): ReferralDao = database.referralDao()

    /**
     * Provides the DAO for aftercare plans.
     *
     * @param database The [AppDatabase] instance.
     * @return The [AftercarePlanDao] instance.
     */
    @Provides
    fun provideAftercarePlanDao(database: AppDatabase): AftercarePlanDao = database.aftercarePlanDao()

    /**
     * Provides the DAO for organization partners.
     *
     * @param database The [AppDatabase] instance.
     * @return The [OrganizationPartnerDao] instance.
     */
    @Provides
    fun provideOrganizationPartnerDao(database: AppDatabase): OrganizationPartnerDao = database.organizationPartnerDao()

    /**
     * Provides the DAO for service providers.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ServiceProviderDao] instance.
     */
    @Provides
    fun provideServiceProviderDao(database: AppDatabase): ServiceProviderDao = database.serviceProviderDao()

    /**
     * Provides the DAO for donor funding.
     *
     * @param database The [AppDatabase] instance.
     * @return The [DonorFundingDao] instance.
     */
    @Provides
    fun provideDonorFundingDao(database: AppDatabase): DonorFundingDao = database.donorFundingDao()

    /**
     * Provides the DAO for budget allocations.
     *
     * @param database The [AppDatabase] instance.
     * @return The [BudgetAllocationDao] instance.
     */
    @Provides
    fun provideBudgetAllocationDao(database: AppDatabase): BudgetAllocationDao = database.budgetAllocationDao()

    /**
     * Provides the DAO for counties.
     *
     * @param database The [AppDatabase] instance.
     * @return The [CountyDao] instance.
     */
    @Provides
    fun provideCountyDao(database: AppDatabase): CountyDao = database.countyDao()

    /**
     * Provides the DAO for county offices.
     *
     * @param database The [AppDatabase] instance.
     * @return The [CountyOfficeDao] instance.
     */
    @Provides
    fun provideCountyOfficeDao(database: AppDatabase): CountyOfficeDao = database.countyOfficeDao()

    /**
     * Provides the DAO for placement disruptions.
     *
     * @param database The [AppDatabase] instance.
     * @return The [PlacementDisruptionDao] instance.
     */
    @Provides
    fun providePlacementDisruptionDao(database: AppDatabase): PlacementDisruptionDao = database.placementDisruptionDao()

    /**
     * Provides the DAO for foster family training.
     *
     * @param database The [AppDatabase] instance.
     * @return The [FosterFamilyTrainingDao] instance.
     */
    @Provides
    fun provideFosterFamilyTrainingDao(database: AppDatabase): FosterFamilyTrainingDao = database.fosterFamilyTrainingDao()

    /**
     * Provides the DAO for generated reports.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ReportGeneratedDao] instance.
     */
    @Provides
    fun provideReportGeneratedDao(database: AppDatabase): ReportGeneratedDao = database.reportGeneratedDao()

    /**
     * Provides the DAO for emergency events.
     *
     * @param database The [AppDatabase] instance.
     * @return The [EmergencyEventDao] instance.
     */
    @Provides
    fun provideEmergencyEventDao(database: AppDatabase): EmergencyEventDao = database.emergencyEventDao()

    /**
     * Provides the DAO for global document storage.
     *
     * @param database The [AppDatabase] instance.
     * @return The [GlobalDocumentStorageDao] instance.
     */
    @Provides
    fun provideGlobalDocumentStorageDao(database: AppDatabase): GlobalDocumentStorageDao = database.globalDocumentStorageDao()

    /**
     * Provides the DAO for inter-county transfers.
     *
     * @param database The [AppDatabase] instance.
     * @return The [InterCountyTransferDao] instance.
     */
    @Provides
    fun provideInterCountyTransferDao(database: AppDatabase): InterCountyTransferDao = database.interCountyTransferDao()

    /**
     * Provides the DAO for worker location tracking.
     *
     * @param database The [AppDatabase] instance.
     * @return The [WorkerLocationTrackingDao] instance.
     */
    @Provides
    fun provideWorkerLocationTrackingDao(database: AppDatabase): WorkerLocationTrackingDao = database.workerLocationTrackingDao()

    /**
     * Provides the DAO for family meetings.
     *
     * @param database The [AppDatabase] instance.
     * @return The [FamilyMeetingDao] instance.
     */
    @Provides
    fun provideFamilyMeetingDao(database: AppDatabase): FamilyMeetingDao = database.familyMeetingDao()

    /**
     * Provides the DAO for child development metrics.
     *
     * @param database The [AppDatabase] instance.
     * @return The [ChildDevelopmentMetricDao] instance.
     */
    @Provides
    fun provideChildDevelopmentMetricDao(database: AppDatabase): ChildDevelopmentMetricDao = database.childDevelopmentMetricDao()

    /**
     * Provides the DAO for staff resources.
     *
     * @param database The [AppDatabase] instance.
     * @return The [StaffResourceDao] instance.
     */
    @Provides
    fun provideStaffResourceDao(database: AppDatabase): StaffResourceDao = database.staffResourceDao()
}
