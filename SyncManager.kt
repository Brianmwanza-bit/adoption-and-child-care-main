import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncManager {
    fun scheduleChildrenSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<ChildrenSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "children_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleUsersSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<UsersSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "users_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleGuardiansSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<GuardiansSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "guardians_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleCourtCasesSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<CourtCasesSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "court_cases_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun schedulePlacementsSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<PlacementsSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "placements_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleMedicalRecordsSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<MedicalRecordsSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "medical_records_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleCaseReportsSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<CaseReportsSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "case_reports_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleMoneyRecordsSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<MoneyRecordsSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "money_records_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleEducationRecordsSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<EducationRecordsSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "education_records_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleDocumentsSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<DocumentsSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "documents_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleAuditLogsSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<AuditLogsSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "audit_logs_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun schedulePermissionsSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<PermissionsSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "permissions_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleUserPermissionsSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<UserPermissionsSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "user_permissions_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleFamilyProfileSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<FamilyProfileSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "family_profile_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleFosterTasksSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<FosterTasksSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "foster_tasks_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleFosterMatchesSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<FosterMatchesSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "foster_matches_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun scheduleBackgroundChecksSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<BackgroundChecksSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "background_checks_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    // Add similar methods for other entities/tables as needed
} 