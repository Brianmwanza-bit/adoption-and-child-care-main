import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FamilyProfileRepository(private val dao: FamilyProfileDao) {
    suspend fun insert(profile: FamilyProfileEntity) = withContext(Dispatchers.IO) { dao.insert(profile) }
    suspend fun update(profile: FamilyProfileEntity) = withContext(Dispatchers.IO) { dao.update(profile) }
    suspend fun delete(profile: FamilyProfileEntity) = withContext(Dispatchers.IO) { dao.delete(profile) }
    suspend fun getByUserId(userId: Int) = withContext(Dispatchers.IO) { dao.getByUserId(userId) }
    suspend fun getAll() = withContext(Dispatchers.IO) { dao.getAll() }
} 