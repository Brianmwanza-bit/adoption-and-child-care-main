package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface FosterTaskDao {
    @Query("SELECT * FROM foster_tasks")
    fun observeAll(): Flow<List<FosterTaskEntity>>

    @Query("SELECT * FROM foster_tasks")
    suspend fun getAll(): List<FosterTaskEntity>

    @Query("SELECT * FROM foster_tasks WHERE task_id = :id")
    suspend fun findById(id: Int): FosterTaskEntity?

    @Query("SELECT * FROM foster_tasks WHERE family_id = :familyId")
    suspend fun getByFamilyId(familyId: Int): List<FosterTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fosterTask: FosterTaskEntity): Long

    @Transaction
    suspend fun insertWithSync(fosterTask: FosterTaskEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(fosterTask)
        val payload = Gson().toJson(fosterTask)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "foster_tasks",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fosterTasks: List<FosterTaskEntity>)

    @Update
    suspend fun update(fosterTask: FosterTaskEntity)

    @Transaction
    suspend fun updateWithSync(fosterTask: FosterTaskEntity, syncQueueDao: SyncQueueDao) {
        update(fosterTask)
        val payload = Gson().toJson(fosterTask)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "foster_tasks",
                operation = "UPDATE",
                recordId = fosterTask.taskId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Delete
    suspend fun delete(fosterTask: FosterTaskEntity)

    @Query("DELETE FROM foster_tasks WHERE task_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "foster_tasks",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }
    
    @Query("""
        SELECT * FROM foster_tasks 
        WHERE description LIKE :query 
           OR status LIKE :query
           OR due_date LIKE :query
           OR created_at LIKE :query
        ORDER BY created_at DESC
    """)
    suspend fun globalSearch(query: String): List<com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity>
}
