package com.example.adoption_and_childcare.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.edit
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.network.RetrofitClient
import com.example.adoption_and_childcare.network.SyncPushRequestItem
import com.example.adoption_and_childcare.network.SyncPullResponse
import com.yourdomain.adoptionchildcare.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.room.withTransaction

/**
 * Represents the result of a synchronization operation.
 */
sealed class SyncResult {
    /**
     * Indicates a successful synchronization.
     * @property pushed Number of records sent to the server.
     * @property pulled Number of records received from the server.
     */
    data class Success(val pushed: Int, val pulled: Int) : SyncResult()

    /**
     * Indicates a synchronization failure.
     * @property message The error message.
     */
    data class Error(val message: String) : SyncResult()

    /**
     * Indicates the device is offline and cannot sync.
     */
    object Offline : SyncResult()
}

/**
 * Manages data synchronization between the local database and the remote API.
 *
 * @property syncQueueDao DAO for managing the local sync queue.
 * @property context Application context.
 * @property sessionManager Manager for user sessions and authentication tokens.
 */
class SyncManager(
    private val syncQueueDao: SyncQueueDao,
    private val context: Context,
    private val sessionManager: SessionManager
) {
    /**
     * Performs a full synchronization (Push and Pull) across all database tables.
     *
     * @return [SyncResult] representing the outcome of the operation.
     */
    suspend fun sync(): SyncResult = withContext(Dispatchers.IO) {
        if (!isOnline()) return@withContext SyncResult.Offline

        // Always get a fresh ApiService in case the URL has changed in settings
        val apiService = RetrofitClient.getDynamicApiService(context)
        
        val token = context.getString(R.string.sync_auth_prefix) + sessionManager.getAuthToken()
        val prefsName = context.getString(R.string.sync_prefs_name)
        val keyLastSynced = context.getString(R.string.sync_key_last_synced)
        
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val lastSyncedAt = prefs.getLong(keyLastSynced, 0L)

        try {
            // 1. PUSH - Send local changes to server
            val pending = syncQueueDao.getPending()
            var pushedCount = 0
            if (pending.isNotEmpty()) {
                val pushItems = pending.map {
                    SyncPushRequestItem(it.tableName, it.operation, it.recordId, it.payload)
                }
                val pushResponse = apiService.pushSync(token, pushItems)
                if (pushResponse.isSuccessful) {
                    pending.forEach { syncQueueDao.markSynced(it.id) }
                    pushedCount = pending.size
                } else {
                    return@withContext SyncResult.Error(
                        context.getString(R.string.sync_error_push_failed, pushResponse.message())
                    )
                }
            }

            // 2. PULL - Fetch updates from server
            val pullResponse = apiService.pullSync(token, lastSyncedAt)
            var pulledCount = 0
            if (pullResponse.isSuccessful) {
                val data: SyncPullResponse? = pullResponse.body()
                if (data != null) {
                    val db = AppDatabase.getInstance(context)
                    
                    db.withTransaction {
                        // Upsert logic for ALL tables using for loops to allow suspend calls
                        for (item in data.children) db.childDao().insert(item)
                        for (item in data.families) db.familyDao().insert(item)
                        for (item in data.placements) db.placementDao().insert(item)
                        for (item in data.medicalRecords) db.medicalRecordDao().insert(item)
                        for (item in data.educationRecords) db.educationRecordDao().insert(item)
                        for (item in data.moneyRecords) db.moneyRecordDao().insert(item)
                        for (item in data.documents) db.documentDao().insert(item)
                        for (item in data.caseReports) db.caseReportDao().insert(item)
                        for (item in data.courtCases) db.courtCaseDao().insert(item)
                        for (item in data.guardians) db.guardianDao().insert(item)
                        for (item in data.adoptionApplications) db.adoptionApplicationDao().insert(item)
                        for (item in data.homeStudies) db.homeStudyDao().insert(item)
                        for (item in data.auditLogs) db.auditLogDao().insert(item)
                        for (item in data.notifications) db.notificationDao().insertNotification(item)
                        
                        // Additional tables for full system sync
                        for (item in data.backgroundChecks) db.backgroundCheckDao().insert(item)
                        for (item in data.fosterTasks) db.fosterTaskDao().insert(item)
                        for (item in data.fosterMatches) db.fosterMatchDao().insert(item)
                        for (item in data.systemSettings) db.systemSettingDao().insert(item)
                        for (item in data.sosLocations) db.sosLocationDao().insert(item)
                        for (item in data.permissions) db.permissionDao().insert(item)
                        for (item in data.userPermissions) db.userPermissionDao().insert(item)
                        for (item in data.users) db.userDao().insert(item)
                        
                        // Dashboard and Case Management tables
                        for (item in data.tasks) db.taskDao().insert(item)
                        for (item in data.actionItems) db.actionItemDao().insert(item)
                        for (item in data.dashboardMetrics) db.dashboardMetricDao().insert(item)
                        for (item in data.dashboardPreferences) db.dashboardPreferenceDao().insert(item)
                        for (item in data.criticalDates) db.criticalDateDao().insert(item)
                        for (item in data.workerMessages) db.workerMessageDao().insert(item)
                        for (item in data.riskAssessments) db.riskAssessmentDao().insert(item)
                        for (item in data.permanencyPlans) db.permanencyPlanDao().insert(item)
                        for (item in data.caseload) db.caseloadDao().insert(item)
                        for (item in data.caseUrgencyFlags) db.caseUrgencyFlagDao().insert(item)
                        for (item in data.caseActivities) db.caseActivityDao().insert(item)
                        for (item in data.caseDeadlines) db.caseDeadlineDao().insert(item)
                        for (item in data.caseApprovals) db.caseApprovalDao().insert(item)
                        for (item in data.placementCompatibility) db.placementCompatibilityDao().insert(item)
                        for (item in data.workloadTracking) db.workloadTrackingDao().insert(item)
                    }
                    
                    pulledCount = data.children.size + data.families.size + data.placements.size + 
                                  data.medicalRecords.size + data.educationRecords.size + 
                                  data.moneyRecords.size + data.documents.size + 
                                  data.caseReports.size + data.courtCases.size + 
                                  data.guardians.size + data.adoptionApplications.size +
                                  data.homeStudies.size + data.auditLogs.size +
                                  data.notifications.size + data.backgroundChecks.size +
                                  data.fosterTasks.size + data.fosterMatches.size +
                                  data.systemSettings.size +
                                  data.sosLocations.size +
                                  data.permissions.size +
                                  data.userPermissions.size +
                                  data.users.size +
                                  data.tasks.size +
                                  data.actionItems.size +
                                  data.dashboardMetrics.size +
                                  data.dashboardPreferences.size +
                                  data.criticalDates.size +
                                  data.workerMessages.size +
                                  data.riskAssessments.size +
                                  data.permanencyPlans.size +
                                  data.caseload.size +
                                  data.caseUrgencyFlags.size +
                                  data.caseActivities.size +
                                  data.caseDeadlines.size +
                                  data.caseApprovals.size +
                                  data.placementCompatibility.size +
                                  data.workloadTracking.size
                }
            } else {
                return@withContext SyncResult.Error(
                    context.getString(R.string.sync_error_pull_failed, pullResponse.message())
                )
            }

            // 3. Save timestamp
            prefs.edit {
                putLong(keyLastSynced, System.currentTimeMillis() / 1000)
            }

            SyncResult.Success(pushedCount, pulledCount)
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: context.getString(R.string.sync_error_unknown))
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
