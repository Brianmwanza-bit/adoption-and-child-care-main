package com.example.adoption_and_childcare.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.adoption_and_childcare.data.db.converters.Converters
import com.example.adoption_and_childcare.data.db.dao.*
import com.example.adoption_and_childcare.data.db.entities.*

/**
 * Minimal Room database for the application.
 * 
 * Note: Updated to include all 66 entities as per project requirements.
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
        FamilyMeetingEntity::class,
        ChildDevelopmentMetricEntity::class,
        StaffResourceEntity::class,
    ],
    version = 21,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabaseMinimal : RoomDatabase(), BaseAppDatabase {
    /** @return The DAO for managing users. */
    abstract override fun userDao(): UserDao
    /** @return The DAO for managing children. */
    abstract override fun childDao(): ChildDao
    /** @return The DAO for managing documents. */
    abstract override fun documentDao(): DocumentDao
    /** @return The DAO for managing placements. */
    abstract override fun placementDao(): PlacementDao
    /** @return The DAO for managing families. */
    abstract override fun familyDao(): FamilyDao
    /** @return The DAO for managing adoption applications. */
    abstract override fun adoptionApplicationDao(): AdoptionApplicationDao
    /** @return The DAO for managing home studies. */
    abstract override fun homeStudyDao(): HomeStudyDao
    /** @return The DAO for managing case reports. */
    abstract override fun caseReportDao(): CaseReportDao
    /** @return The DAO for managing education records. */
    abstract override fun educationRecordDao(): EducationRecordDao
    /** @return The DAO for managing medical records. */
    abstract override fun medicalRecordDao(): MedicalRecordDao
    /** @return The DAO for managing money records. */
    abstract override fun moneyRecordDao(): MoneyRecordDao
    /** @return The DAO for managing audit logs. */
    abstract override fun auditLogDao(): AuditLogDao
    /** @return The DAO for managing court cases. */
    abstract override fun courtCaseDao(): CourtCaseDao
    /** @return The DAO for managing guardians. */
    abstract override fun guardianDao(): GuardianDao
    /** @return The DAO for managing permissions. */
    abstract override fun permissionDao(): PermissionDao
    /** @return The DAO for managing user permissions. */
    abstract override fun userPermissionDao(): UserPermissionDao
    /** @return The DAO for managing notifications. */
    abstract override fun notificationDao(): NotificationDao
    /** @return The DAO for managing sync queues. */
    abstract override fun syncQueueDao(): SyncQueueDao
    /** @return The DAO for managing SOS locations. */
    abstract override fun sosLocationDao(): SOSLocationDao
    /** @return The DAO for managing background checks. */
    abstract override fun backgroundCheckDao(): BackgroundCheckDao
    /** @return The DAO for managing foster tasks. */
    abstract override fun fosterTaskDao(): FosterTaskDao
    /** @return The DAO for managing foster matches. */
    abstract override fun fosterMatchDao(): FosterMatchDao
    /** @return The DAO for managing system settings. */
    abstract override fun systemSettingDao(): SystemSettingDao
    /** @return The DAO for managing tasks. */
    abstract override fun taskDao(): TaskDao
    /** @return The DAO for managing action items. */
    abstract override fun actionItemDao(): ActionItemDao
    /** @return The DAO for managing dashboard metrics. */
    abstract override fun dashboardMetricDao(): DashboardMetricDao
    /** @return The DAO for managing dashboard preferences. */
    abstract override fun dashboardPreferenceDao(): DashboardPreferenceDao
    /** @return The DAO for managing critical dates. */
    abstract override fun criticalDateDao(): CriticalDateDao
    /** @return The DAO for managing worker messages. */
    abstract override fun workerMessageDao(): WorkerMessageDao
    /** @return The DAO for managing risk assessments. */
    abstract override fun riskAssessmentDao(): RiskAssessmentDao
    /** @return The DAO for managing permanency plans. */
    abstract override fun permanencyPlanDao(): PermanencyPlanDao
    /** @return The DAO for managing caseloads. */
    abstract override fun caseloadDao(): CaseloadDao
    /** @return The DAO for managing case urgency flags. */
    abstract override fun caseUrgencyFlagDao(): CaseUrgencyFlagDao
    /** @return The DAO for managing case activities. */
    abstract override fun caseActivityDao(): CaseActivityDao
    /** @return The DAO for managing case deadlines. */
    abstract override fun caseDeadlineDao(): CaseDeadlineDao
    /** @return The DAO for managing case approvals. */
    abstract override fun caseApprovalDao(): CaseApprovalDao
    /** @return The DAO for managing placement compatibilities. */
    abstract override fun placementCompatibilityDao(): PlacementCompatibilityDao
    /** @return The DAO for managing workload tracking. */
    abstract override fun workloadTrackingDao(): WorkloadTrackingDao
    /** @return The DAO for managing child behavior assessments. */
    abstract override fun childBehaviorAssessmentDao(): ChildBehaviorAssessmentDao
    /** @return The DAO for managing child welfare incidents. */
    abstract override fun childWelfareIncidentDao(): ChildWelfareIncidentDao
    /** @return The DAO for managing vaccination records. */
    abstract override fun vaccinationRecordDao(): VaccinationRecordDao
    /** @return The DAO for managing siblings. */
    abstract override fun siblingDao(): SiblingDao
    /** @return The DAO for managing consent records. */
    abstract override fun consentRecordDao(): ConsentRecordDao
    /** @return The DAO for managing child services referrals. */
    abstract override fun childServicesReferralDao(): ChildServicesReferralDao
    /** @return The DAO for managing investigations. */
    abstract override fun investigationDao(): InvestigationDao
    /** @return The DAO for managing service plans. */
    abstract override fun servicePlanDao(): ServicePlanDao
    /** @return The DAO for managing service plan goals. */
    abstract override fun servicePlanGoalDao(): ServicePlanGoalDao
    /** @return The DAO for managing visitation schedules. */
    abstract override fun visitationScheduleDao(): VisitationScheduleDao
    /** @return The DAO for managing referrals. */
    abstract override fun referralDao(): ReferralDao
    /** @return The DAO for managing aftercare plans. */
    abstract override fun aftercarePlanDao(): AftercarePlanDao
    /** @return The DAO for managing organization partners. */
    abstract override fun organizationPartnerDao(): OrganizationPartnerDao
    /** @return The DAO for managing service providers. */
    abstract override fun serviceProviderDao(): ServiceProviderDao
    /** @return The DAO for managing donor fundings. */
    abstract override fun donorFundingDao(): DonorFundingDao
    /** @return The DAO for managing budget allocations. */
    abstract override fun budgetAllocationDao(): BudgetAllocationDao
    /** @return The DAO for managing counties. */
    abstract override fun countyDao(): CountyDao
    /** @return The DAO for managing county offices. */
    abstract override fun countyOfficeDao(): CountyOfficeDao
    /** @return The DAO for managing placement disruptions. */
    abstract override fun placementDisruptionDao(): PlacementDisruptionDao
    /** @return The DAO for managing foster family trainings. */
    abstract override fun fosterFamilyTrainingDao(): FosterFamilyTrainingDao
    /** @return The DAO for managing report generations. */
    abstract override fun reportGeneratedDao(): ReportGeneratedDao
    /** @return The DAO for managing emergency events. */
    abstract override fun emergencyEventDao(): EmergencyEventDao
    /** @return The DAO for managing global document storage. */
    abstract override fun globalDocumentStorageDao(): GlobalDocumentStorageDao
    /** @return The DAO for managing inter-county transfers. */
    abstract override fun interCountyTransferDao(): InterCountyTransferDao
    /** @return The DAO for managing worker location tracking. */
    abstract override fun workerLocationTrackingDao(): WorkerLocationTrackingDao
    /** @return The DAO for managing family meetings. */
    abstract override fun familyMeetingDao(): FamilyMeetingDao
    /** @return The DAO for managing child development metrics. */
    abstract override fun childDevelopmentMetricDao(): ChildDevelopmentMetricDao
    /** @return The DAO for managing staff resources. */
    abstract override fun staffResourceDao(): StaffResourceDao

    companion object {
        @Volatile private var INSTANCE: AppDatabaseMinimal? = null

        /**
         * Gets the singleton instance of the database.
         * 
         * @param context The application context.
         * @return The singleton database instance.
         */
        fun getInstance(context: Context): AppDatabaseMinimal = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabaseMinimal::class.java,
                "adoption_childcare_minimal.db"
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                .also { INSTANCE = it }
        }
    }
}
