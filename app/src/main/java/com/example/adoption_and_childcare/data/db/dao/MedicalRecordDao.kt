package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for medical record-related database operations.
 * 
 * This interface provides methods for CRUD operations on medical records,
 * including sync-aware methods that automatically queue changes for
 * offline-first synchronization.
 */
@Dao
interface MedicalRecordDao {
    /**
     * Inserts a medical record into the database.
     * 
     * @param entity The medical record to insert.
     * @return The row ID of the newly inserted record.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MedicalRecordEntity): Long

    /**
     * Inserts a medical record and queues it for remote synchronization.
     * 
     * @param entity The medical record to insert.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
    @Transaction
    suspend fun insertWithSync(entity: MedicalRecordEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = MedicalRecordEntity.TABLE_NAME,
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Updates a medical record in the database.
     * 
     * @param entity The medical record to update.
     */
    @Update
    suspend fun update(entity: MedicalRecordEntity)

    /**
     * Updates a medical record and queues it for remote synchronization.
     * 
     * @param entity The medical record to update.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
    @Transaction
    suspend fun updateWithSync(entity: MedicalRecordEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = MedicalRecordEntity.TABLE_NAME,
                operation = "UPDATE",
                recordId = entity.recordId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Deletes a medical record by its unique identifier.
     * 
     * @param id The ID of the medical record to delete.
     */
    @Query("DELETE FROM ${MedicalRecordEntity.TABLE_NAME} WHERE record_id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Deletes a medical record and queues the deletion for remote synchronization.
     * 
     * @param id The ID of the medical record to delete.
     * @param syncQueueDao The DAO for managing the sync queue.
     */
    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = MedicalRecordEntity.TABLE_NAME,
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Returns an observable flow of medical records for a specific child.
     * 
     * @param childId The ID of the child.
     * @return A Flow containing the list of medical records.
     */
    @Query("SELECT * FROM ${MedicalRecordEntity.TABLE_NAME} WHERE child_id = :childId ORDER BY visit_date DESC")
    fun observeForChild(childId: Int): Flow<List<MedicalRecordEntity>>

    /**
     * Retrieves medical records for a specific child.
     * 
     * @param childId The ID of the child.
     * @return A list of medical records.
     */
    @Query("SELECT * FROM ${MedicalRecordEntity.TABLE_NAME} WHERE child_id = :childId ORDER BY visit_date DESC")
    suspend fun getForChild(childId: Int): List<MedicalRecordEntity>

    /**
     * Counts the total number of medical records.
     * 
     * @return The count of medical records.
     */
    @Query("SELECT COUNT(*) FROM ${MedicalRecordEntity.TABLE_NAME}")
    suspend fun count(): Int

    /**
     * Retrieves all medical records from the database.
     * 
     * @return A list of all medical records.
     */
    @Query("SELECT * FROM ${MedicalRecordEntity.TABLE_NAME}")
    suspend fun getAll(): List<MedicalRecordEntity>

    /**
     * Finds a medical record by its unique identifier.
     * 
     * @param id The ID of the medical record.
     * @return The medical record if found, null otherwise.
     */
    @Query("SELECT * FROM ${MedicalRecordEntity.TABLE_NAME} WHERE record_id = :id")
    suspend fun findById(id: Int): MedicalRecordEntity?

    /**
     * Returns an observable flow of a specific medical record.
     * 
     * @param id The ID of the medical record.
     * @return A Flow containing the medical record.
     */
    @Query("SELECT * FROM ${MedicalRecordEntity.TABLE_NAME} WHERE record_id = :id")
    fun observeById(id: Int): Flow<MedicalRecordEntity?>

    /**
     * Returns an observable flow of all medical records.
     * 
     * @return A Flow containing all medical records.
     */
    @Query("SELECT * FROM ${MedicalRecordEntity.TABLE_NAME} ORDER BY visit_date DESC")
    fun observeAll(): Flow<List<MedicalRecordEntity>>
    
    /**
     * Performs a global search across medical records.
     * 
     * @param query The search query.
     * @return A list of matching medical records.
     */
    @Query("""
        SELECT * FROM ${MedicalRecordEntity.TABLE_NAME}
        WHERE diagnosis LIKE :query 
           OR hospital_name LIKE :query 
           OR treatment LIKE :query
           OR visit_date LIKE :query
           OR medications LIKE :query
        ORDER BY visit_date DESC
    """)
    suspend fun globalSearch(query: String): List<MedicalRecordEntity>
}
