package com.example.adoption_and_childcare.data.sync

import android.util.Log
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.dao.*
import com.example.adoption_and_childcare.data.db.entities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.WriteBatch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles batch synchronization of all 62 tables to Firebase Firestore.
 *
 * @property syncQueueDao DAO for managing the local sync queue.
 * @property firestore Firebase Firestore instance for remote synchronization.
 * @property database Local Room database instance.
 */
@Singleton
class FirestoreSyncManager @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val firestore: FirebaseFirestore,
    private val database: AppDatabase
) {
    private val gson: Gson = Gson()
    private val mapType: Type = object : TypeToken<Map<String, Any>>() {}.type
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isSyncEnabled = true

    private val tableNames: List<String> = listOf(
        TABLE_USERS, TABLE_CHILDREN, TABLE_DOCUMENTS, TABLE_PLACEMENTS, TABLE_CASE_REPORTS,
        TABLE_COURT_CASES, TABLE_EDUCATION_RECORDS, TABLE_GUARDIANS, TABLE_MEDICAL_RECORDS,
        TABLE_MONEY_RECORDS, TABLE_PERMISSIONS, TABLE_USER_PERMISSIONS, TABLE_AUDIT_LOGS,
        TABLE_FAMILIES, TABLE_ADOPTION_APPLICATIONS, TABLE_HOME_STUDIES, TABLE_NOTIFICATIONS,
        TABLE_SOS_LOCATIONS, TABLE_BACKGROUND_CHECKS, TABLE_FOSTER_TASKS, TABLE_FOSTER_MATCHES,
        TABLE_SYSTEM_SETTINGS, TABLE_TASKS, TABLE_ACTION_ITEMS, TABLE_DASHBOARD_METRICS,
        TABLE_DASHBOARD_PREFERENCES, TABLE_CRITICAL_DATES, TABLE_WORKER_MESSAGES,
        TABLE_RISK_ASSESSMENTS, TABLE_PERMANENCY_PLANS, TABLE_CASELOAD, TABLE_CASE_URGENCY_FLAGS,
        TABLE_CASE_ACTIVITIES, TABLE_CASE_DEADLINES, TABLE_CASE_APPROVALS, TABLE_PLACEMENT_COMPATIBILITY,
        TABLE_WORKLOAD_TRACKING, TABLE_CHILD_BEHAVIOR_ASSESSMENTS, TABLE_CHILD_WELFARE_INCIDENTS,
        TABLE_VACCINATION_RECORDS, TABLE_SIBLINGS, TABLE_CONSENT_RECORDS, TABLE_CHILD_SERVICES_REFERRALS,
        TABLE_INVESTIGATIONS, TABLE_SERVICE_PLANS, TABLE_SERVICE_PLAN_GOALS, TABLE_VISITATION_SCHEDULES,
        TABLE_REFERRALS, TABLE_AFTERCARE_PLANS, TABLE_ORGANIZATION_PARTNERS, TABLE_SERVICE_PROVIDERS,
        TABLE_DONOR_FUNDING, TABLE_BUDGET_ALLOCATIONS, TABLE_COUNTIES, TABLE_COUNTY_OFFICES,
        TABLE_PLACEMENT_DISRUPTIONS, TABLE_FOSTER_FAMILY_TRAINING, TABLE_REPORTS_GENERATED,
        TABLE_EMERGENCY_EVENTS, TABLE_GLOBAL_DOCUMENT_STORAGE, TABLE_INTER_COUNTY_TRANSFERS,
        TABLE_WORKER_LOCATION_TRACKING
    )

    /**
     * Pushes all local changes to Firestore in batches.
     */
    suspend fun pushChangesInBatch() = withContext(Dispatchers.IO) {
        if (!isSyncEnabled) {
            Log.w(TAG, "Firestore sync is disabled due to previous permission or connectivity errors.")
            return@withContext
        }
        val pendingItems = syncQueueDao.getPending()
        if (pendingItems.isEmpty()) return@withContext

        pendingItems.chunked(BATCH_SIZE).forEach { itemBatch ->
            val batch: WriteBatch = firestore.batch()
            val syncedIds = mutableListOf<Int>()

            for (item in itemBatch) {
                try {
                    val docRef = firestore.collection(item.tableName).document(item.recordId)
                    when (item.operation) {
                        OP_INSERT, OP_UPDATE -> {
                            val data: Map<String, Any> = gson.fromJson(item.payload, mapType)
                            batch.set(docRef, data, SetOptions.merge())
                        }
                        OP_DELETE -> batch.delete(docRef)
                    }
                    syncedIds.add(item.id)
                } catch (e: Exception) {
                    Log.e(TAG, ERROR_PREPARING_ITEM + "${item.id}: ${e.message}")
                }
            }

            try {
                batch.commit().await()
                syncedIds.forEach { syncQueueDao.markSynced(it) }
                Log.d(TAG, SYNC_SUCCESS_PREFIX + syncedIds.size + SYNC_SUCCESS_SUFFIX)
            } catch (e: Exception) {
                handleSyncError(e)
            }
        }
    }

    private fun handleSyncError(e: Exception) {
        val message = e.message ?: ""
        if (message.contains("PERMISSION_DENIED")) {
            isSyncEnabled = false
            Log.e(TAG, "!! CRITICAL: Firestore API is not enabled or permission denied. " +
                    "Sync disabled until app restart. Enable it at: " +
                    "https://console.developers.google.com/apis/api/firestore.googleapis.com/overview")
        } else if (message.contains("UNAVAILABLE")) {
            Log.w(TAG, "Firestore service currently unavailable. Will retry later.")
        } else {
            Log.e(TAG, ERROR_BATCH_COMMIT + e.message)
        }
    }

    /**
     * Pulls updates from all 62 collections in Firestore and updates Room.
     * This is a one-time full sync. For real-time, use listeners.
     */
    suspend fun pullAllTables() = withContext(Dispatchers.IO) {
        if (!isSyncEnabled) return@withContext
        for (tableName in tableNames) {
            try {
                val snapshot = firestore.collection(tableName).get().await()
                for (doc in snapshot.documents) {
                    val data = doc.data ?: continue
                    val json = gson.toJson(data)
                    saveToRoom(tableName, json)
                }
            } catch (e: Exception) {
                handleSyncError(e)
                if (!isSyncEnabled) break
            }
        }
    }

    /**
     * Saves a JSON representation of a record to the appropriate Room table.
     *
     * @param tableName The name of the Firestore collection/Room table.
     * @param json The JSON representation of the entity.
     */
    private suspend fun saveToRoom(tableName: String, json: String) {
        try {
            when (tableName) {
                TABLE_USERS -> database.userDao().insert(gson.fromJson(json, UserEntity::class.java))
                TABLE_CHILDREN -> database.childDao().insert(gson.fromJson(json, ChildEntity::class.java))
                TABLE_FAMILIES -> database.familyDao().insert(gson.fromJson(json, FamilyEntity::class.java))
                TABLE_PLACEMENTS -> database.placementDao().insert(gson.fromJson(json, PlacementEntity::class.java))
                TABLE_CASE_REPORTS -> database.caseReportDao().insert(gson.fromJson(json, CaseReportEntity::class.java))
                TABLE_TASKS -> database.taskDao().insert(gson.fromJson(json, TaskEntity::class.java))
                TABLE_NOTIFICATIONS -> database.notificationDao().insertNotification(gson.fromJson(json, NotificationEntity::class.java))
                TABLE_AUDIT_LOGS -> database.auditLogDao().insert(gson.fromJson(json, AuditLogEntity::class.java))
                TABLE_BACKGROUND_CHECKS -> database.backgroundCheckDao().insert(gson.fromJson(json, BackgroundCheckEntity::class.java))
                TABLE_DASHBOARD_METRICS -> database.dashboardMetricDao().insert(gson.fromJson(json, DashboardMetricEntity::class.java))
                TABLE_COURT_CASES -> database.courtCaseDao().insert(gson.fromJson(json, CourtCaseEntity::class.java))
                TABLE_EDUCATION_RECORDS -> database.educationRecordDao().insert(gson.fromJson(json, EducationRecordEntity::class.java))
                TABLE_MEDICAL_RECORDS -> database.medicalRecordDao().insert(gson.fromJson(json, MedicalRecordEntity::class.java))
                TABLE_MONEY_RECORDS -> database.moneyRecordDao().insert(gson.fromJson(json, MoneyRecordEntity::class.java))
                TABLE_PERMISSIONS -> database.permissionDao().insert(gson.fromJson(json, PermissionEntity::class.java))
                TABLE_USER_PERMISSIONS -> database.userPermissionDao().insert(gson.fromJson(json, UserPermissionEntity::class.java))
                TABLE_GUARDIANS -> database.guardianDao().insert(gson.fromJson(json, GuardianEntity::class.java))
                TABLE_DOCUMENTS -> database.documentDao().insert(gson.fromJson(json, DocumentEntity::class.java))
                TABLE_FOSTER_TASKS -> database.fosterTaskDao().insert(gson.fromJson(json, FosterTaskEntity::class.java))
                TABLE_FOSTER_MATCHES -> database.fosterMatchDao().insert(gson.fromJson(json, FosterMatchEntity::class.java))
                TABLE_WORKER_MESSAGES -> database.workerMessageDao().insert(gson.fromJson(json, WorkerMessageEntity::class.java))
                TABLE_SOS_LOCATIONS -> database.sosLocationDao().insert(gson.fromJson(json, SOSLocationEntity::class.java))
                TABLE_INVESTIGATIONS -> database.investigationDao().insert(gson.fromJson(json, InvestigationEntity::class.java))
                TABLE_SERVICE_PLANS -> database.servicePlanDao().insert(gson.fromJson(json, ServicePlanEntity::class.java))
                TABLE_VISITATION_SCHEDULES -> database.visitationScheduleDao().insert(gson.fromJson(json, VisitationScheduleEntity::class.java))
                TABLE_REFERRALS -> database.referralDao().insert(gson.fromJson(json, ReferralEntity::class.java))
                TABLE_COUNTY_OFFICES -> database.countyOfficeDao().insert(gson.fromJson(json, CountyOfficeEntity::class.java))
                TABLE_ADOPTION_APPLICATIONS -> database.adoptionApplicationDao().insert(gson.fromJson(json, AdoptionApplicationEntity::class.java))
                TABLE_HOME_STUDIES -> database.homeStudyDao().insert(gson.fromJson(json, HomeStudyEntity::class.java))
                TABLE_SYSTEM_SETTINGS -> database.systemSettingDao().insert(gson.fromJson(json, SystemSettingEntity::class.java))
                TABLE_ACTION_ITEMS -> database.actionItemDao().insert(gson.fromJson(json, ActionItemEntity::class.java))
                TABLE_DASHBOARD_PREFERENCES -> database.dashboardPreferenceDao().insert(gson.fromJson(json, DashboardPreferenceEntity::class.java))
                TABLE_CRITICAL_DATES -> database.criticalDateDao().insert(gson.fromJson(json, CriticalDateEntity::class.java))
                TABLE_RISK_ASSESSMENTS -> database.riskAssessmentDao().insert(gson.fromJson(json, RiskAssessmentEntity::class.java))
                TABLE_PERMANENCY_PLANS -> database.permanencyPlanDao().insert(gson.fromJson(json, PermanencyPlanEntity::class.java))
                TABLE_CASELOAD -> database.caseloadDao().insert(gson.fromJson(json, CaseloadEntity::class.java))
                TABLE_CASE_URGENCY_FLAGS -> database.caseUrgencyFlagDao().insert(gson.fromJson(json, CaseUrgencyFlagEntity::class.java))
                TABLE_CASE_ACTIVITIES -> database.caseActivityDao().insert(gson.fromJson(json, CaseActivityEntity::class.java))
                TABLE_CASE_DEADLINES -> database.caseDeadlineDao().insert(gson.fromJson(json, CaseDeadlineEntity::class.java))
                TABLE_CASE_APPROVALS -> database.caseApprovalDao().insert(gson.fromJson(json, CaseApprovalEntity::class.java))
                TABLE_PLACEMENT_COMPATIBILITY -> database.placementCompatibilityDao().insert(gson.fromJson(json, PlacementCompatibilityEntity::class.java))
                TABLE_WORKLOAD_TRACKING -> database.workloadTrackingDao().insert(gson.fromJson(json, WorkloadTrackingEntity::class.java))
                TABLE_CHILD_BEHAVIOR_ASSESSMENTS -> database.childBehaviorAssessmentDao().insert(gson.fromJson(json, ChildBehaviorAssessmentEntity::class.java))
                TABLE_CHILD_WELFARE_INCIDENTS -> database.childWelfareIncidentDao().insert(gson.fromJson(json, ChildWelfareIncidentEntity::class.java))
                TABLE_VACCINATION_RECORDS -> database.vaccinationRecordDao().insert(gson.fromJson(json, VaccinationRecordEntity::class.java))
                TABLE_SIBLINGS -> database.siblingDao().insert(gson.fromJson(json, SiblingEntity::class.java))
                TABLE_CONSENT_RECORDS -> database.consentRecordDao().insert(gson.fromJson(json, ConsentRecordEntity::class.java))
                TABLE_CHILD_SERVICES_REFERRALS -> database.childServicesReferralDao().insert(gson.fromJson(json, ChildServicesReferralEntity::class.java))
                TABLE_SERVICE_PLAN_GOALS -> database.servicePlanGoalDao().insert(gson.fromJson(json, ServicePlanGoalEntity::class.java))
                TABLE_AFTERCARE_PLANS -> database.aftercarePlanDao().insert(gson.fromJson(json, AftercarePlanEntity::class.java))
                TABLE_ORGANIZATION_PARTNERS -> database.organizationPartnerDao().insert(gson.fromJson(json, OrganizationPartnerEntity::class.java))
                TABLE_SERVICE_PROVIDERS -> database.serviceProviderDao().insert(gson.fromJson(json, ServiceProviderEntity::class.java))
                TABLE_DONOR_FUNDING -> database.donorFundingDao().insert(gson.fromJson(json, DonorFundingEntity::class.java))
                TABLE_BUDGET_ALLOCATIONS -> database.budgetAllocationDao().insert(gson.fromJson(json, BudgetAllocationEntity::class.java))
                TABLE_COUNTIES -> database.countyDao().insert(gson.fromJson(json, CountyEntity::class.java))
                TABLE_PLACEMENT_DISRUPTIONS -> database.placementDisruptionDao().insert(gson.fromJson(json, PlacementDisruptionEntity::class.java))
                TABLE_FOSTER_FAMILY_TRAINING -> database.fosterFamilyTrainingDao().insert(gson.fromJson(json, FosterFamilyTrainingEntity::class.java))
                TABLE_REPORTS_GENERATED -> database.reportGeneratedDao().insert(gson.fromJson(json, ReportGeneratedEntity::class.java))
                TABLE_EMERGENCY_EVENTS -> database.emergencyEventDao().insert(gson.fromJson(json, EmergencyEventEntity::class.java))
                TABLE_GLOBAL_DOCUMENT_STORAGE -> database.globalDocumentStorageDao().insert(gson.fromJson(json, GlobalDocumentStorageEntity::class.java))
                TABLE_INTER_COUNTY_TRANSFERS -> database.interCountyTransferDao().insert(gson.fromJson(json, InterCountyTransferEntity::class.java))
                TABLE_WORKER_LOCATION_TRACKING -> database.workerLocationTrackingDao().insert(gson.fromJson(json, WorkerLocationTrackingEntity::class.java))
            }
        } catch (e: Exception) {
            Log.e(TAG, ERROR_SAVING_ROOM + tableName + TO_ROOM_ERROR + e.message)
        }
    }

    /**
     * Sets up real-time listeners for high-priority collections.
     */
    fun startRealtimeSync() {
        val priorityTables = listOf(TABLE_NOTIFICATIONS, TABLE_WORKER_MESSAGES, TABLE_TASKS, TABLE_CASE_URGENCY_FLAGS)
        
        priorityTables.forEach { tableName ->
            firestore.collection(tableName)
                .addSnapshotListener { snapshot, e ->
                    // snapshot: The QuerySnapshot from Firestore.
                    // e: The FirestoreException if any error occurred.
                    if (e != null) {
                        Log.e(TAG, ERROR_LISTEN_FAILED + tableName, e)
                        return@addSnapshotListener
                    }

                    snapshot?.documentChanges?.forEach { change ->
                        // change: The DocumentChange object representing a modified document.
                        val json = gson.toJson(change.document.data)
                        scope.launch {
                            saveToRoom(tableName, json)
                        }
                    }
                }
        }
    }

    companion object {
        private const val TAG: String = "FirestoreSync"
        private const val BATCH_SIZE: Int = 500

        private const val ERROR_PREPARING_ITEM: String = "Error preparing item "
        private const val SYNC_SUCCESS_PREFIX: String = "Successfully synced batch of "
        private const val SYNC_SUCCESS_SUFFIX: String = " items"
        private const val ERROR_BATCH_COMMIT: String = "Batch commit failed: "
        private const val ERROR_PULL_TABLE: String = "Failed to pull table "
        private const val ERROR_SAVING_ROOM: String = "Error saving "
        private const val TO_ROOM_ERROR: String = " to Room: "
        private const val ERROR_LISTEN_FAILED: String = "Listen failed for "

        /** Operation type for inserting a record. */
        const val OP_INSERT: String = "INSERT"
        /** Operation type for updating a record. */
        const val OP_UPDATE: String = "UPDATE"
        /** Operation type for deleting a record. */
        const val OP_DELETE: String = "DELETE"

        /** Table name constant for users. */
        const val TABLE_USERS: String = "users"
        /** Table name constant for children. */
        const val TABLE_CHILDREN: String = "children"
        /** Table name constant for documents. */
        const val TABLE_DOCUMENTS: String = "documents"
        /** Table name constant for placements. */
        const val TABLE_PLACEMENTS: String = "placements"
        /** Table name constant for case reports. */
        const val TABLE_CASE_REPORTS: String = "case_reports"
        /** Table name constant for court cases. */
        const val TABLE_COURT_CASES: String = "court_cases"
        /** Table name constant for education records. */
        const val TABLE_EDUCATION_RECORDS: String = "education_records"
        /** Table name constant for guardians. */
        const val TABLE_GUARDIANS: String = "guardians"
        /** Table name constant for medical records. */
        const val TABLE_MEDICAL_RECORDS: String = "medical_records"
        /** Table name constant for money records. */
        const val TABLE_MONEY_RECORDS: String = "money_records"
        /** Table name constant for permissions. */
        const val TABLE_PERMISSIONS: String = "permissions"
        /** Table name constant for user permissions. */
        const val TABLE_USER_PERMISSIONS: String = "user_permissions"
        /** Table name constant for audit logs. */
        const val TABLE_AUDIT_LOGS: String = "audit_logs"
        /** Table name constant for families. */
        const val TABLE_FAMILIES: String = "families"
        /** Table name constant for adoption applications. */
        const val TABLE_ADOPTION_APPLICATIONS: String = "adoption_applications"
        /** Table name constant for home studies. */
        const val TABLE_HOME_STUDIES: String = "home_studies"
        /** Table name constant for notifications. */
        const val TABLE_NOTIFICATIONS: String = "notifications"
        /** Table name constant for SOS locations. */
        const val TABLE_SOS_LOCATIONS: String = "sos_locations"
        /** Table name constant for background checks. */
        const val TABLE_BACKGROUND_CHECKS: String = "background_checks"
        /** Table name constant for foster tasks. */
        const val TABLE_FOSTER_TASKS: String = "foster_tasks"
        /** Table name constant for foster matches. */
        const val TABLE_FOSTER_MATCHES: String = "foster_matches"
        /** Table name constant for system settings. */
        const val TABLE_SYSTEM_SETTINGS: String = "system_settings"
        /** Table name constant for tasks. */
        const val TABLE_TASKS: String = "tasks"
        /** Table name constant for action items. */
        const val TABLE_ACTION_ITEMS: String = "action_items"
        /** Table name constant for dashboard metrics. */
        const val TABLE_DASHBOARD_METRICS: String = "dashboard_metrics"
        /** Table name constant for dashboard preferences. */
        const val TABLE_DASHBOARD_PREFERENCES: String = "dashboard_preferences"
        /** Table name constant for critical dates. */
        const val TABLE_CRITICAL_DATES: String = "critical_dates"
        /** Table name constant for worker messages. */
        const val TABLE_WORKER_MESSAGES: String = "worker_messages"
        /** Table name constant for risk assessments. */
        const val TABLE_RISK_ASSESSMENTS: String = "risk_assessments"
        /** Table name constant for permanency plans. */
        const val TABLE_PERMANENCY_PLANS: String = "permanency_plans"
        /** Table name constant for caseload. */
        const val TABLE_CASELOAD: String = "caseload"
        /** Table name constant for case urgency flags. */
        const val TABLE_CASE_URGENCY_FLAGS: String = "case_urgency_flags"
        /** Table name constant for case activities. */
        const val TABLE_CASE_ACTIVITIES: String = "case_activities"
        /** Table name constant for case deadlines. */
        const val TABLE_CASE_DEADLINES: String = "case_deadlines"
        /** Table name constant for case approvals. */
        const val TABLE_CASE_APPROVALS: String = "case_approvals"
        /** Table name constant for placement compatibility. */
        const val TABLE_PLACEMENT_COMPATIBILITY: String = "placement_compatibility"
        /** Table name constant for workload tracking. */
        const val TABLE_WORKLOAD_TRACKING: String = "workload_tracking"
        /** Table name constant for child behavior assessments. */
        const val TABLE_CHILD_BEHAVIOR_ASSESSMENTS: String = "child_behavior_assessments"
        /** Table name constant for child welfare incidents. */
        const val TABLE_CHILD_WELFARE_INCIDENTS: String = "child_welfare_incidents"
        /** Table name constant for vaccination records. */
        const val TABLE_VACCINATION_RECORDS: String = "vaccination_records"
        /** Table name constant for siblings. */
        const val TABLE_SIBLINGS: String = "siblings"
        /** Table name constant for consent records. */
        const val TABLE_CONSENT_RECORDS: String = "consent_records"
        /** Table name constant for child services referrals. */
        const val TABLE_CHILD_SERVICES_REFERRALS: String = "child_services_referrals"
        /** Table name constant for investigations. */
        const val TABLE_INVESTIGATIONS: String = "investigations"
        /** Table name constant for service plans. */
        const val TABLE_SERVICE_PLANS: String = "service_plans"
        /** Table name constant for service plan goals. */
        const val TABLE_SERVICE_PLAN_GOALS: String = "service_plan_goals"
        /** Table name constant for visitation schedules. */
        const val TABLE_VISITATION_SCHEDULES: String = "visitation_schedules"
        /** Table name constant for referrals. */
        const val TABLE_REFERRALS: String = "referrals"
        /** Table name constant for aftercare plans. */
        const val TABLE_AFTERCARE_PLANS: String = "aftercare_plans"
        /** Table name constant for organization partners. */
        const val TABLE_ORGANIZATION_PARTNERS: String = "organization_partners"
        /** Table name constant for service providers. */
        const val TABLE_SERVICE_PROVIDERS: String = "service_providers"
        /** Table name constant for donor funding. */
        const val TABLE_DONOR_FUNDING: String = "donor_funding"
        /** Table name constant for budget allocations. */
        const val TABLE_BUDGET_ALLOCATIONS: String = "budget_allocations"
        /** Table name constant for counties. */
        const val TABLE_COUNTIES: String = "counties"
        /** Table name constant for county offices. */
        const val TABLE_COUNTY_OFFICES: String = "county_offices"
        /** Table name constant for placement disruptions. */
        const val TABLE_PLACEMENT_DISRUPTIONS: String = "placement_disruptions"
        /** Table name constant for foster family training. */
        const val TABLE_FOSTER_FAMILY_TRAINING: String = "foster_family_training"
        /** Table name constant for reports generated. */
        const val TABLE_REPORTS_GENERATED: String = "reports_generated"
        /** Table name constant for emergency events. */
        const val TABLE_EMERGENCY_EVENTS: String = "emergency_events"
        /** Table name constant for global document storage. */
        const val TABLE_GLOBAL_DOCUMENT_STORAGE: String = "global_document_storage"
        /** Table name constant for inter-county transfers. */
        const val TABLE_INTER_COUNTY_TRANSFERS: String = "inter_county_transfers"
        /** Table name constant for worker location tracking. */
        const val TABLE_WORKER_LOCATION_TRACKING: String = "worker_location_tracking"
    }
}
