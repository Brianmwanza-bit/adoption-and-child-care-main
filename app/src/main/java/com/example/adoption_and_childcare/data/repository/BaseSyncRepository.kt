package com.example.adoption_and_childcare.data.repository

import android.content.Context
import androidx.work.*
import com.example.adoption_and_childcare.data.sync.SyncWorker
import java.util.concurrent.TimeUnit

/**
 * Base repository class for synchronization operations.
 * 
 * This abstract class provides common functionality for scheduling background
 * synchronization work using Android WorkManager, with network constraints
 * and exponential backoff retry logic.
 * 
 * @property context Application context for WorkManager access.
 */
abstract class BaseSyncRepository(private val context: Context) {
    
    protected fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "BackgroundSync",
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )
    }
}
