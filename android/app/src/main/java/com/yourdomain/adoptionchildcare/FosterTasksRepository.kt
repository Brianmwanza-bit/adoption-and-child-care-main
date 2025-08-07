import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FosterTasksRepository(private val dao: FosterTasksDao) {
    suspend fun insert(task: FosterTasksEntity) = withContext(Dispatchers.IO) { dao.insert(task) }
    suspend fun update(task: FosterTasksEntity) = withContext(Dispatchers.IO) { dao.update(task) }
    suspend fun delete(task: FosterTasksEntity) = withContext(Dispatchers.IO) { dao.delete(task) }
    suspend fun getByFamilyId(familyId: Int) = withContext(Dispatchers.IO) { dao.getByFamilyId(familyId) }
    suspend fun getAll() = withContext(Dispatchers.IO) { dao.getAll() }
} 