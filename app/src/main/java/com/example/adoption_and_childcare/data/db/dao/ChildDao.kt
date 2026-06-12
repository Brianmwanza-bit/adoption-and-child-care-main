package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import kotlinx.coroutines.flow.Flow

import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson

/**
 * Data Access Object for child-related database operations.
 * 
 * This class provides methods for CRUD operations on child records,
 * including sync-aware methods that automatically queue changes for
 * offline-first synchronization.
 */
@Dao
abstract class ChildDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(child: ChildEntity): Long

    @Update
    abstract suspend fun update(child: ChildEntity)

    @Transaction
    open suspend fun insertWithSync(child: ChildEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(child)
        val payload = Gson().toJson(child)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "children",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Transaction
    open suspend fun updateWithSync(child: ChildEntity, syncQueueDao: SyncQueueDao) {
        update(child)
        val payload = Gson().toJson(child)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "children",
                operation = "UPDATE",
                recordId = child.childId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM children WHERE child_id = :id")
    abstract suspend fun deleteById(id: Int)
    
    @Transaction
    open suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "children",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM children ORDER BY last_name, first_name")
    abstract fun observeAll(): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE child_id = :id LIMIT 1")
    abstract suspend fun findById(id: Int): ChildEntity?

    @Query("SELECT * FROM children WHERE first_name LIKE :query OR last_name LIKE :query ORDER BY last_name, first_name")
    abstract fun searchByName(query: String): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE first_name LIKE :q OR last_name LIKE :q OR birth_certificate_no LIKE :q OR county LIKE :q ORDER BY last_name, first_name LIMIT :limit")
    abstract suspend fun globalSearch(q: String, limit: Int = 5): List<ChildEntity>

    @Query("SELECT COUNT(*) FROM children")
    abstract suspend fun count(): Int

    @Query("SELECT * FROM children WHERE sync_status = 'PENDING'")
    abstract suspend fun getPendingSync(): List<ChildEntity>

    @Query("UPDATE children SET sync_status = :status, remote_id = :remoteId, last_synced_at = :timestamp WHERE child_id = :localId")
    abstract suspend fun updateSyncStatus(localId: Int, status: String, remoteId: String?, timestamp: Long)
}
