package com.example.adoption_and_childcare.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.edit
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.network.SyncPushRequestItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
 * @property apiService Service for making network requests.
 * @property context Application context.
 * @property sessionManager Manager for user sessions and authentication tokens.
 */
class SyncManager(
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val context: Context,
    private val sessionManager: SessionManager
) {
    /**
     * Performs a full synchronization (Push and Pull).
     *
     * @return [SyncResult] representing the outcome of the operation.
     */
    suspend fun sync(): SyncResult = withContext(Dispatchers.IO) {
        if (!isOnline()) return@withContext SyncResult.Offline

        val token = "$AUTH_PREFIX${sessionManager.getAuthToken()}"
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastSyncedAt = prefs.getLong(KEY_LAST_SYNCED, 0L)

        try {
            // 1. PUSH
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
                    return@withContext SyncResult.Error("$ERROR_PUSH_FAILED ${pushResponse.message()}")
                }
            }

            // 2. PULL
            val pullResponse = apiService.pullSync(token, lastSyncedAt)
            var pulledCount = 0
            if (pullResponse.isSuccessful) {
                val data = pullResponse.body()
                if (data != null) {
                    val db = AppDatabase.getInstance(context)
                    // Upsert logic
                    data.children.forEach { db.childDao().insert(it) }
                    data.families.forEach { db.familyDao().insert(it) }
                    data.placements.forEach { db.placementDao().insert(it) }
                    data.medical_records.forEach { db.medicalRecordDao().insert(it) }
                    data.education_records.forEach { db.educationRecordDao().insert(it) }
                    data.money_records.forEach { db.moneyRecordDao().insert(it) }
                    data.documents.forEach { db.documentDao().insert(it) }
                    data.case_reports.forEach { db.caseReportDao().insert(it) }
                    data.court_cases.forEach { db.courtCaseDao().insert(it) }
                    data.guardians.forEach { db.guardianDao().insert(it) }
                    data.adoption_applications.forEach { db.adoptionApplicationDao().insert(it) }
                    data.home_studies.forEach { db.homeStudyDao().insert(it) }
                    data.audit_logs.forEach { db.auditLogDao().insert(it) }
                    data.notifications.forEach { db.notificationDao().insertNotification(it) }
                    
                    pulledCount = data.children.size + data.families.size + data.placements.size + 
                                  data.medical_records.size + data.education_records.size + 
                                  data.money_records.size + data.documents.size + 
                                  data.case_reports.size + data.court_cases.size + 
                                  data.guardians.size + data.adoption_applications.size +
                                  data.home_studies.size + data.audit_logs.size +
                                  data.notifications.size
                }
            } else {
                return@withContext SyncResult.Error("$ERROR_PULL_FAILED ${pullResponse.message()}")
            }

            // 3. Save timestamp
            prefs.edit {
                putLong(KEY_LAST_SYNCED, System.currentTimeMillis() / 1000)
            }

            SyncResult.Success(pushedCount, pulledCount)
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        private const val PREFS_NAME = "sync_prefs"
        private const val KEY_LAST_SYNCED = "last_synced_at"
        private const val AUTH_PREFIX = "Bearer "
        private const val ERROR_PUSH_FAILED = "Push failed:"
        private const val ERROR_PULL_FAILED = "Pull failed:"
        private const val ERROR_UNKNOWN = "Unknown sync error"
    }
}
