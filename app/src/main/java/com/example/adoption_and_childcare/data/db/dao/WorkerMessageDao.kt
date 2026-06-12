package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.WorkerMessageEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerMessageDao {
    @Query("SELECT * FROM worker_messages")
    fun observeAll(): Flow<List<WorkerMessageEntity>>

    @Query("SELECT * FROM worker_messages")
    suspend fun getAll(): List<WorkerMessageEntity>

    @Query("SELECT * FROM worker_messages WHERE message_id = :id")
    suspend fun getById(id: Int): WorkerMessageEntity?

    @Query("SELECT * FROM worker_messages WHERE recipient_id = :userId ORDER BY created_at DESC")
    suspend fun getInbox(userId: Int): List<WorkerMessageEntity>

    @Query("SELECT * FROM worker_messages WHERE sender_id = :userId ORDER BY created_at DESC")
    suspend fun getSent(userId: Int): List<WorkerMessageEntity>

    @Query("SELECT * FROM worker_messages WHERE recipient_id = :userId AND is_read = 0")
    suspend fun getUnread(userId: Int): List<WorkerMessageEntity>

    @Query("SELECT COUNT(*) FROM worker_messages WHERE recipient_id = :userId AND is_read = 0")
    suspend fun getUnreadCount(userId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: WorkerMessageEntity): Long

    @Transaction
    suspend fun insertWithSync(message: WorkerMessageEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(message)
        val payload = Gson().toJson(message)
        syncQueueDao.insert(SyncQueueEntity(tableName = "worker_messages", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<WorkerMessageEntity>)

    @Update
    suspend fun update(message: WorkerMessageEntity)

    @Transaction
    suspend fun updateWithSync(message: WorkerMessageEntity, syncQueueDao: SyncQueueDao) {
        update(message)
        val payload = Gson().toJson(message)
        syncQueueDao.insert(SyncQueueEntity(tableName = "worker_messages", operation = "UPDATE", recordId = message.messageId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(message: WorkerMessageEntity)
}
