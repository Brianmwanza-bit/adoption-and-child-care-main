package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.TaskEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks")
    suspend fun getAll(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE task_id = :id")
    suspend fun getById(id: Int): TaskEntity?

    @Query("SELECT * FROM tasks WHERE assigned_to = :userId")
    suspend fun getByAssignee(userId: Int): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE status = :status")
    suspend fun getByStatus(status: String): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Transaction
    suspend fun insertWithSync(task: TaskEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(task)
        val payload = Gson().toJson(task)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "tasks",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Update
    suspend fun update(task: TaskEntity)

    @Transaction
    suspend fun updateWithSync(task: TaskEntity, syncQueueDao: SyncQueueDao) {
        update(task)
        val payload = Gson().toJson(task)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "tasks",
                operation = "UPDATE",
                recordId = task.taskId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE task_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "tasks",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }
    
    @Query("""
        SELECT * FROM tasks 
        WHERE title LIKE :query 
           OR description LIKE :query
           OR priority LIKE :query
           OR status LIKE :query
           OR due_date LIKE :query
        ORDER BY created_at DESC
    """)
    suspend fun globalSearch(query: String): List<com.example.adoption_and_childcare.data.db.entities.TaskEntity>
}
