package com.example.adoption_and_childcare

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.adoption_and_childcare.data.sync.FirestoreSyncManager
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.AppDatabaseMinimal
import com.example.adoption_and_childcare.data.db.DatabaseInitializer
import com.example.adoption_and_childcare.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Main application class for the Adoption and Child Care app.
 *
 * This class handles global initialization, dependency injection setup,
 * and database seeding.
 */
@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    /** Factory for Hilt-injected WorkManager workers. */
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /** Manager for Firestore synchronization operations. */
    @Inject
    lateinit var firestoreSyncManager: dagger.Lazy<FirestoreSyncManager>
    
    /** The main application database instance. */
    @Inject
    lateinit var appDatabase: dagger.Lazy<AppDatabase>
    
    /** The minimal application database instance for lightweight tasks. */
    @Inject
    lateinit var appDatabaseMinimal: dagger.Lazy<AppDatabaseMinimal>

    /**
     * Application-level coroutine scope for background tasks.
     */
    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        
        // 1. Initialize all heavy components in the background to avoid ANR during startup
        applicationScope.launch {
            val isPlayServicesAvailable = DatabaseInitializer.isGooglePlayServicesAvailable(this@MyApp)
            
            if (isPlayServicesAvailable) {
                // Link and start real-time Firestore listeners (Cloud DB)
                DatabaseInitializer.checkCloudDatabaseStatus()
                firestoreSyncManager.get().startRealtimeSync()
            } else {
                Log.w("MyApp", "Google Play Services unavailable. Firestore sync disabled.")
            }

            // 2. Initialize Local Databases with Mock Data
            // Seed main AppDatabase
            DatabaseInitializer.initializeDatabase(appDatabase.get())
            
            // Seed AppDatabaseMinimal
            DatabaseInitializer.initializeMinimalDatabase(appDatabaseMinimal.get())
        }
    }
}
