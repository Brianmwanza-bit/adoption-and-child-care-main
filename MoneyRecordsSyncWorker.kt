import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.room.Room

class MoneyRecordsSyncWorker(
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
        val dao = db.moneyRecordsDao()
        try {
            val remoteRecords = RetrofitClient.apiService.getMoneyRecords("Bearer $token")
            dao.clearAll()
            dao.insertAll(remoteRecords)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
} 