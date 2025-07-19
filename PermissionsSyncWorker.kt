import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.room.Room

class PermissionsSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val token = TokenManager.getToken(applicationContext)
        if (token.isNullOrEmpty()) return Result.failure()
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-db"
        ).build()
        val dao = db.permissionsDao()
        try {
            val remotePermissions = RetrofitClient.apiService.getPermissions("Bearer $token")
            dao.clearAll()
            dao.insertAll(remotePermissions)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
} 