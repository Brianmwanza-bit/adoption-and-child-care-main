package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {
    @Query("SELECT COUNT(*) FROM sync_queue WHERE synced = 0")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT * FROM sync_queue WHERE synced = 0 ORDER BY created_at ASC")
    fun getPending(): List<SyncQueueEntity>

    @Query("UPDATE sync_queue SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)

    @Query("UPDATE sync_queue SET retry_count = retry_count + 1 WHERE id = :id")
    suspend fun incrementRetry(id: Int)

    @Query("DELETE FROM sync_queue WHERE synced = 1 AND created_at < :timestamp")
    suspend fun deleteSynced(timestamp: Long)

    @Insert
    suspend fun insert(syncQueue: SyncQueueEntity)
}
