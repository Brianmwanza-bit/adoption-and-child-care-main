import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FosterMatchesRepository(private val dao: FosterMatchesDao) {
    suspend fun insert(match: FosterMatchesEntity) = withContext(Dispatchers.IO) { dao.insert(match) }
    suspend fun update(match: FosterMatchesEntity) = withContext(Dispatchers.IO) { dao.update(match) }
    suspend fun delete(match: FosterMatchesEntity) = withContext(Dispatchers.IO) { dao.delete(match) }
    suspend fun getByFamilyId(familyId: Int) = withContext(Dispatchers.IO) { dao.getByFamilyId(familyId) }
    suspend fun getAll() = withContext(Dispatchers.IO) { dao.getAll() }
} 