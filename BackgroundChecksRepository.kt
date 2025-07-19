import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackgroundChecksRepository(private val dao: BackgroundChecksDao) {
    suspend fun insert(check: BackgroundChecksEntity) = withContext(Dispatchers.IO) { dao.insert(check) }
    suspend fun update(check: BackgroundChecksEntity) = withContext(Dispatchers.IO) { dao.update(check) }
    suspend fun delete(check: BackgroundChecksEntity) = withContext(Dispatchers.IO) { dao.delete(check) }
    suspend fun getByUserId(userId: Int) = withContext(Dispatchers.IO) { dao.getByUserId(userId) }
    suspend fun getAll() = withContext(Dispatchers.IO) { dao.getAll() }
} 