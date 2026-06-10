package com.example.adoption_and_childcare.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker class for handling periodic background synchronization.
 * Leverages [SyncManager] to perform the actual data transfer between local and remote sources.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("SyncWorker", "Starting background sync...")
            val result = syncManager.sync()
            when (result) {
                is SyncResult.Success -> {
                    Log.d("SyncWorker", "Sync successful: Pushed ${result.pushed}, Pulled ${result.pulled}")
                    Result.success()
                }
                is SyncResult.Error -> {
                    Log.e("SyncWorker", "Sync failed with error: ${result.message}")
                    Result.retry()
                }
                is SyncResult.Offline -> {
                    Log.d("SyncWorker", "Sync skipped: Device is offline")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Unexpected sync exception: ${e.message}")
            Result.retry()
        }
    }
}
