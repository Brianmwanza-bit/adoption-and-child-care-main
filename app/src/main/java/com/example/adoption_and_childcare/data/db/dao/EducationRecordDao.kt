package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for education record-related database operations.
 * 
 * This interface provides methods for CRUD operations on education records,
 * including sync-aware methods that automatically queue changes for
 * offline-first synchronization.
 */
@Dao
interface EducationRecordDao {
    /**
     * Inserts an education record into the database.
     * 
     * @param entity The education record to insert.
     * @return The row ID of the newly inserted record.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EducationRecordEntity): Long

    /**
     * Inserts an education record and queues it for remote synchronization.
     * 
     * @param entity The education record to insert.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
    @Transaction
    suspend fun insertWithSync(entity: EducationRecordEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "education_records",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Updates an education record in the database.
     * 
     * @param entity The education record to update.
     */
    @Update
    suspend fun update(entity: EducationRecordEntity)

    /**
     * Updates an education record and queues it for remote synchronization.
     * 
     * @param entity The education record to update.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
    @Transaction
    suspend fun updateWithSync(entity: EducationRecordEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "education_records",
                operation = "UPDATE",
                recordId = entity.recordId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Deletes an education record by its unique identifier.
     * 
     * @param id The ID of the education record to delete.
     */
    @Query("DELETE FROM education_records WHERE record_id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Deletes an education record and queues the deletion for remote synchronization.
     * 
     * @param id The ID of the education record to delete.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "education_records",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Returns an observable flow of education records for a specific child.
     * 
     * @param childId The ID of the child.
     * @return A Flow containing the list of education records.
     */
    @Query("SELECT * FROM education_records WHERE child_id = :childId ORDER BY enrollment_date DESC")
    fun observeForChild(childId: Int): Flow<List<EducationRecordEntity>>

    /**
     * Retrieves education records for a specific child.
     * 
     * @param childId The ID of the child.
     * @return A list of education records.
     */
    @Query("SELECT * FROM education_records WHERE child_id = :childId ORDER BY enrollment_date DESC")
    suspend fun getForChild(childId: Int): List<EducationRecordEntity>

    /**
     * Counts the total number of education records.
     * 
     * @return The count of education records.
     */
    @Query("SELECT COUNT(*) FROM education_records")
    suspend fun count(): Int

    /**
     * Retrieves all education records from the database.
     * 
     * @return A list of all education records.
     */
    @Query("SELECT * FROM education_records")
    suspend fun getAll(): List<EducationRecordEntity>

    /**
     * Finds an education record by its unique identifier.
     * 
     * @param id The ID of the education record.
     * @return The education record if found, null otherwise.
     */
    @Query("SELECT * FROM education_records WHERE record_id = :id")
    suspend fun findById(id: Int): EducationRecordEntity?

    /**
     * Returns an observable flow of all education records.
     * 
     * @return A Flow containing all education records.
     */
    @Query("SELECT * FROM education_records ORDER BY enrollment_date DESC")
    fun observeAll(): Flow<List<EducationRecordEntity>>
    
    /**
     * Performs a global search across education records.
     * 
     * @param query The search query.
     * @return A list of matching education records.
     */
    @Query("""
        SELECT * FROM education_records 
        WHERE school_name LIKE :query 
           OR grade LIKE :query 
           OR performance LIKE :query
           OR special_needs LIKE :query
           OR teacher_contact LIKE :query
        ORDER BY enrollment_date DESC
    """)
    suspend fun globalSearch(query: String): List<EducationRecordEntity>
}
