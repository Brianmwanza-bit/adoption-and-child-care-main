package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for background check-related database operations.
 * 
 * This interface provides methods for CRUD operations on background checks,
 * including sync-aware methods that automatically queue changes for
 * offline-first synchronization.
 */
@Dao
interface BackgroundCheckDao {
    /**
     * Returns an observable flow of all background checks.
     * 
     * @return A Flow containing all background checks.
     */
    @Query("SELECT * FROM ${BackgroundCheckEntity.TABLE_NAME}")
    fun observeAll(): Flow<List<BackgroundCheckEntity>>

    /**
     * Retrieves all background checks from the database.
     * 
     * @return A list of all background checks.
     */
    @Query("SELECT * FROM ${BackgroundCheckEntity.TABLE_NAME}")
    suspend fun getAll(): List<BackgroundCheckEntity>

    /**
     * Finds a background check by its unique identifier.
     * 
     * @param id The ID of the background check.
     * @return The background check if found, null otherwise.
     */
    @Query("SELECT * FROM ${BackgroundCheckEntity.TABLE_NAME} WHERE check_id = :id")
    suspend fun getById(id: Int): BackgroundCheckEntity?

    /**
     * Retrieves background checks for a specific user.
     * 
     * @param userId The ID of the user.
     * @return A list of background checks.
     */
    @Query("SELECT * FROM ${BackgroundCheckEntity.TABLE_NAME} WHERE user_id = :userId")
    suspend fun getByUserId(userId: Int): List<BackgroundCheckEntity>

    /**
     * Inserts a background check into the database.
     * 
     * @param backgroundCheck The background check to insert.
     * @return The row ID of the newly inserted record.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(backgroundCheck: BackgroundCheckEntity): Long

    /**
     * Inserts a background check and queues it for remote synchronization.
     * 
     * @param backgroundCheck The background check to insert.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
    @Transaction
    suspend fun insertWithSync(backgroundCheck: BackgroundCheckEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(backgroundCheck)
        val payload = Gson().toJson(backgroundCheck)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = BackgroundCheckEntity.TABLE_NAME,
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Inserts multiple background checks into the database.
     * 
     * @param backgroundChecks The list of background checks to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(backgroundChecks: List<BackgroundCheckEntity>)

    /**
     * Updates a background check in the database.
     * 
     * @param backgroundCheck The background check to update.
     */
    @Update
    suspend fun update(backgroundCheck: BackgroundCheckEntity)

    /**
     * Updates a background check and queues it for remote synchronization.
     * 
     * @param backgroundCheck The background check to update.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
    @Transaction
    suspend fun updateWithSync(backgroundCheck: BackgroundCheckEntity, syncQueueDao: SyncQueueDao) {
        update(backgroundCheck)
        val payload = Gson().toJson(backgroundCheck)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = BackgroundCheckEntity.TABLE_NAME,
                operation = "UPDATE",
                recordId = backgroundCheck.checkId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Deletes a background check from the database.
     * 
     * @param backgroundCheck The background check to delete.
     */
    @Delete
    suspend fun delete(backgroundCheck: BackgroundCheckEntity)

    /**
     * Deletes a background check by its unique identifier.
     * 
     * @param id The ID of the background check to delete.
     */
    @Query("DELETE FROM ${BackgroundCheckEntity.TABLE_NAME} WHERE check_id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Deletes a background check and queues the deletion for remote synchronization.
     * 
     * @param id The ID of the background check to delete.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = BackgroundCheckEntity.TABLE_NAME,
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }
    
    /**
     * Performs a global search across background checks.
     * 
     * @param query The search query.
     * @return A list of matching background checks.
     */
    @Query("""
        SELECT * FROM ${BackgroundCheckEntity.TABLE_NAME} 
        WHERE status LIKE :query
           OR result LIKE :query
           OR requested_at LIKE :query
           OR completed_at LIKE :query
        ORDER BY requested_at DESC
    """)
    suspend fun globalSearch(query: String): List<BackgroundCheckEntity>
}
