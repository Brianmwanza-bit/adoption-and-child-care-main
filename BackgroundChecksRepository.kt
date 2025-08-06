import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackgroundChecksRepository(
    private val dao: BackgroundChecksDao,
    private val api: ApiService,
    private val tokenProvider: () -> String
) {
    suspend fun insert(check: BackgroundChecksEntity) = withContext(Dispatchers.IO) {
        dao.insert(check)
        api.createBackgroundCheck("Bearer ${tokenProvider()}", check)
    }
    suspend fun update(check: BackgroundChecksEntity) = withContext(Dispatchers.IO) {
        dao.update(check)
        api.updateBackgroundCheck("Bearer ${tokenProvider()}", check.check_id, check)
    }
    suspend fun delete(check: BackgroundChecksEntity) = withContext(Dispatchers.IO) {
        dao.delete(check)
        api.deleteBackgroundCheck("Bearer ${tokenProvider()}", check.check_id)
    }
    suspend fun getByUserId(userId: Int): List<BackgroundChecksEntity> = withContext(Dispatchers.IO) {
        val remote = api.getBackgroundChecks("Bearer ${tokenProvider()}")
        remote.filter { it.user_id == userId }
    }
    suspend fun getAll(): List<BackgroundChecksEntity> = withContext(Dispatchers.IO) {
        val remote = api.getBackgroundChecks("Bearer ${tokenProvider()}")
        remote
    }
} 