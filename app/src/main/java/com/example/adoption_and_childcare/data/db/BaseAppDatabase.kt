package com.example.adoption_and_childcare.data.db

import com.example.adoption_and_childcare.data.db.dao.*

/**
 * Base interface for the application database.
 * Defines accessors for all DAOs used in the system.
 */
interface BaseAppDatabase {
    /** @return The DAO for managing users. */
    fun userDao(): UserDao
    /** @return The DAO for managing children. */
    fun childDao(): ChildDao
    /** @return The DAO for managing documents. */
    fun documentDao(): DocumentDao
    /** @return The DAO for managing placements. */
    fun placementDao(): PlacementDao
    /** @return The DAO for managing families. */
    fun familyDao(): FamilyDao
    /** @return The DAO for managing adoption applications. */
    fun adoptionApplicationDao(): AdoptionApplicationDao
    /** @return The DAO for managing home studies. */
    fun homeStudyDao(): HomeStudyDao
    /** @return The DAO for managing case reports. */
    fun caseReportDao(): CaseReportDao
    /** @return The DAO for managing education records. */
    fun educationRecordDao(): EducationRecordDao
    /** @return The DAO for managing medical records. */
    fun medicalRecordDao(): MedicalRecordDao
    /** @return The DAO for managing money records. */
    fun moneyRecordDao(): MoneyRecordDao
    /** @return The DAO for managing audit logs. */
    fun auditLogDao(): AuditLogDao
    /** @return The DAO for managing court cases. */
    fun courtCaseDao(): CourtCaseDao
    /** @return The DAO for managing guardians. */
    fun guardianDao(): GuardianDao
    /** @return The DAO for managing permissions. */
    fun permissionDao(): PermissionDao
    /** @return The DAO for managing user permissions. */
    fun userPermissionDao(): UserPermissionDao
    /** @return The DAO for managing notifications. */
    fun notificationDao(): NotificationDao
    /** @return The DAO for managing sync queues. */
    fun syncQueueDao(): SyncQueueDao
    /** @return The DAO for managing SOS locations. */
    fun sosLocationDao(): SOSLocationDao
    /** @return The DAO for managing background checks. */
    fun backgroundCheckDao(): BackgroundCheckDao
    /** @return The DAO for managing foster tasks. */
    fun fosterTaskDao(): FosterTaskDao
    /** @return The DAO for managing foster matches. */
    fun fosterMatchDao(): FosterMatchDao
    /** @return The DAO for managing system settings. */
    fun systemSettingDao(): SystemSettingDao
    /** @return The DAO for managing tasks. */
    fun taskDao(): TaskDao
    /** @return The DAO for managing action items. */
    fun actionItemDao(): ActionItemDao
    /** @return The DAO for managing dashboard metrics. */
    fun dashboardMetricDao(): DashboardMetricDao
    /** @return The DAO for managing dashboard preferences. */
    fun dashboardPreferenceDao(): DashboardPreferenceDao
    /** @return The DAO for managing critical dates. */
    fun criticalDateDao(): CriticalDateDao
    /** @return The DAO for managing worker messages. */
    fun workerMessageDao(): WorkerMessageDao
    /** @return The DAO for managing risk assessments. */
    fun riskAssessmentDao(): RiskAssessmentDao
    /** @return The DAO for managing permanency plans. */
    fun permanencyPlanDao(): PermanencyPlanDao
    /** @return The DAO for managing caseloads. */
    fun caseloadDao(): CaseloadDao
    /** @return The DAO for managing case urgency flags. */
    fun caseUrgencyFlagDao(): CaseUrgencyFlagDao
    /** @return The DAO for managing case activities. */
    fun caseActivityDao(): CaseActivityDao
    /** @return The DAO for managing case deadlines. */
    fun caseDeadlineDao(): CaseDeadlineDao
    /** @return The DAO for managing case approvals. */
    fun caseApprovalDao(): CaseApprovalDao
    /** @return The DAO for managing placement compatibilities. */
    fun placementCompatibilityDao(): PlacementCompatibilityDao
    /** @return The DAO for managing workload tracking. */
    fun workloadTrackingDao(): WorkloadTrackingDao
    /** @return The DAO for managing child behavior assessments. */
    fun childBehaviorAssessmentDao(): ChildBehaviorAssessmentDao
    /** @return The DAO for managing child welfare incidents. */
    fun childWelfareIncidentDao(): ChildWelfareIncidentDao
    /** @return The DAO for managing vaccination records. */
    fun vaccinationRecordDao(): VaccinationRecordDao
    /** @return The DAO for managing siblings. */
    fun siblingDao(): SiblingDao
    /** @return The DAO for managing consent records. */
    fun consentRecordDao(): ConsentRecordDao
    /** @return The DAO for managing child services referrals. */
    fun childServicesReferralDao(): ChildServicesReferralDao
    /** @return The DAO for managing investigations. */
    fun investigationDao(): InvestigationDao
    /** @return The DAO for managing service plans. */
    fun servicePlanDao(): ServicePlanDao
    /** @return The DAO for managing service plan goals. */
    fun servicePlanGoalDao(): ServicePlanGoalDao
    /** @return The DAO for managing visitation schedules. */
    fun visitationScheduleDao(): VisitationScheduleDao
    /** @return The DAO for managing referrals. */
    fun referralDao(): ReferralDao
    /** @return The DAO for managing aftercare plans. */
    fun aftercarePlanDao(): AftercarePlanDao
    /** @return The DAO for managing organization partners. */
    fun organizationPartnerDao(): OrganizationPartnerDao
    /** @return The DAO for managing service providers. */
    fun serviceProviderDao(): ServiceProviderDao
    /** @return The DAO for managing donor fundings. */
    fun donorFundingDao(): DonorFundingDao
    /** @return The DAO for managing budget allocations. */
    fun budgetAllocationDao(): BudgetAllocationDao
    /** @return The DAO for managing counties. */
    fun countyDao(): CountyDao
    /** @return The DAO for managing county offices. */
    fun countyOfficeDao(): CountyOfficeDao
    /** @return The DAO for managing placement disruptions. */
    fun placementDisruptionDao(): PlacementDisruptionDao
    /** @return The DAO for managing foster family trainings. */
    fun fosterFamilyTrainingDao(): FosterFamilyTrainingDao
    /** @return The DAO for managing report generations. */
    fun reportGeneratedDao(): ReportGeneratedDao
    /** @return The DAO for managing emergency events. */
    fun emergencyEventDao(): EmergencyEventDao
    /** @return The DAO for managing global document storage. */
    fun globalDocumentStorageDao(): GlobalDocumentStorageDao
    /** @return The DAO for managing inter-county transfers. */
    fun interCountyTransferDao(): InterCountyTransferDao
    /** @return The DAO for managing worker location tracking. */
    fun workerLocationTrackingDao(): WorkerLocationTrackingDao
    /** @return The DAO for managing family meetings. */
    fun familyMeetingDao(): FamilyMeetingDao
    /** @return The DAO for managing child development metrics. */
    fun childDevelopmentMetricDao(): ChildDevelopmentMetricDao
    /** @return The DAO for managing staff resources. */
    fun staffResourceDao(): StaffResourceDao
}
