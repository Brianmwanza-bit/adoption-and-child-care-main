package com.example.adoption_and_childcare.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.adoption_and_childcare.data.db.dao.ChildDao
import com.example.adoption_and_childcare.data.db.dao.FamilyDao
import com.example.adoption_and_childcare.data.db.dao.AdoptionApplicationDao
import com.example.adoption_and_childcare.network.RetrofitClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val childDao: ChildDao,
    private val familyDao: FamilyDao,
    private val applicationDao: AdoptionApplicationDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            syncChildren()
            syncFamilies()
            syncApplications()
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed: ${e.message}")
            Result.retry()
        }
    }

    private suspend fun syncChildren() {
        val pending = childDao.getPendingSync()
        if (pending.isNotEmpty()) {
            val response = RetrofitClient.syncApiService.syncChildrenBatch(pending)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val now = System.currentTimeMillis()
                    body.synced_local_ids.forEach { localId ->
                        val remoteId = body.remote_ids?.get(localId)
                        childDao.updateSyncStatus(localId, "SYNCED", remoteId, now)
                    }
                }
            }
        }
    }

    private suspend fun syncFamilies() {
        val pending = familyDao.getPendingSync()
        if (pending.isNotEmpty()) {
            val response = RetrofitClient.syncApiService.syncFamiliesBatch(pending)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val now = System.currentTimeMillis()
                    body.synced_local_ids.forEach { localId ->
                        val remoteId = body.remote_ids?.get(localId)
                        familyDao.updateSyncStatus(localId, "SYNCED", remoteId, now)
                    }
                }
            }
        }
    }

    private suspend fun syncApplications() {
        val pending = applicationDao.getPendingSync()
        if (pending.isNotEmpty()) {
            val response = RetrofitClient.syncApiService.syncApplicationsBatch(pending)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val now = System.currentTimeMillis()
                    body.synced_local_ids.forEach { localId ->
                        val remoteId = body.remote_ids?.get(localId)
                        applicationDao.updateSyncStatus(localId, "SYNCED", remoteId, now)
                    }
                }
            }
        }
    }
}
