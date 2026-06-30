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
    /**
     * Inserts a child record into the database.
     * 
     * @param child The child record to insert.
     * @return The row ID of the newly inserted record.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(child: ChildEntity): Long

    /**
     * Updates a child record in the database.
     * 
     * @param child The child record to update.
     */
    @Update
    abstract suspend fun update(child: ChildEntity)

    /**
     * Inserts a child record and queues it for remote synchronization.
     * 
     * @param child The child record to insert.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
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

    /**
     * Updates a child record and queues it for remote synchronization.
     * 
     * @param child The child record to update.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
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

    /**
     * Deletes a child record by its unique identifier.
     * 
     * @param id The ID of the child record to delete.
     */
    @Query("DELETE FROM children WHERE child_id = :id")
    abstract suspend fun deleteById(id: Int)
    
    /**
     * Deletes a child record and queues the deletion for remote synchronization.
     * 
     * @param id The ID of the child record to delete.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
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

    /**
     * Returns an observable flow of all child records.
     * 
     * @return A Flow containing all child records.
     */
    @Query("SELECT * FROM children ORDER BY last_name, first_name")
    abstract fun observeAll(): Flow<List<ChildEntity>>

    /**
     * Finds a child record by its unique identifier.
     * 
     * @param id The ID of the child record.
     * @return The child record if found, null otherwise.
     */
    @Query("SELECT * FROM children WHERE child_id = :id LIMIT 1")
    abstract suspend fun findById(id: Int): ChildEntity?

    /**
     * Searches for child records by name.
     * 
     * @param query The search query.
     * @return A Flow containing matching child records.
     */
    @Query("SELECT * FROM children WHERE first_name LIKE :query OR last_name LIKE :query ORDER BY last_name, first_name")
    abstract fun searchByName(query: String): Flow<List<ChildEntity>>

    /**
     * Performs a global search across child records.
     * 
     * @param q The search query.
     * @param limit The maximum number of results to return.
     * @return A list of matching child records.
     */
    @Query("SELECT * FROM children WHERE first_name LIKE :q OR last_name LIKE :q OR birth_certificate_no LIKE :q OR county LIKE :q ORDER BY last_name, first_name LIMIT :limit")
    abstract suspend fun globalSearch(q: String, limit: Int = 5): List<ChildEntity>

    /**
     * Counts the total number of child records.
     * 
     * @return The count of child records.
     */
    @Query("SELECT COUNT(*) FROM children")
    abstract suspend fun count(): Int

    /**
     * Retrieves all child records that are pending synchronization.
     * 
     * @return A list of child records pending sync.
     */
    @Query("SELECT * FROM children WHERE sync_status = 'PENDING'")
    abstract suspend fun getPendingSync(): List<ChildEntity>

    /**
     * Updates the synchronization status of a child record.
     * 
     * @param localId The local ID of the child record.
     * @param status The new sync status.
     * @param remoteId The remote ID (e.g., Firestore ID).
     * @param timestamp The timestamp of the sync.
     */
    @Query("UPDATE children SET sync_status = :status, remote_id = :remoteId, last_synced_at = :timestamp WHERE child_id = :localId")
    abstract suspend fun updateSyncStatus(localId: Int, status: String, remoteId: String?, timestamp: Long)
}
