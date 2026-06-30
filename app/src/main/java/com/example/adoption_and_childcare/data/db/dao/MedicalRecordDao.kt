package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MedicalRecordEntity): Long

    @Transaction
    suspend fun insertWithSync(entity: MedicalRecordEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "medical_records",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Update
    suspend fun update(entity: MedicalRecordEntity)

    @Transaction
    suspend fun updateWithSync(entity: MedicalRecordEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "medical_records",
                operation = "UPDATE",
                recordId = entity.recordId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM medical_records WHERE record_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "medical_records",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM medical_records WHERE child_id = :childId ORDER BY visit_date DESC")
    fun observeForChild(childId: Int): Flow<List<MedicalRecordEntity>>

    @Query("SELECT * FROM medical_records WHERE child_id = :childId ORDER BY visit_date DESC")
    suspend fun getForChild(childId: Int): List<MedicalRecordEntity>

    @Query("SELECT COUNT(*) FROM medical_records")
    suspend fun count(): Int

    @Query("SELECT * FROM medical_records")
    suspend fun getAll(): List<MedicalRecordEntity>

    @Query("SELECT * FROM medical_records WHERE record_id = :id")
    suspend fun findById(id: Int): MedicalRecordEntity?

    @Query("SELECT * FROM medical_records WHERE record_id = :id")
    fun observeById(id: Int): Flow<MedicalRecordEntity?>

    @Query("SELECT * FROM medical_records ORDER BY visit_date DESC")
    fun observeAll(): Flow<List<MedicalRecordEntity>>
    
    @Query("""
        SELECT * FROM medical_records 
        WHERE diagnosis LIKE :query 
           OR hospital_name LIKE :query 
           OR treatment LIKE :query
           OR visit_date LIKE :query
           OR medications LIKE :query
        ORDER BY visit_date DESC
    """)
    suspend fun globalSearch(query: String): List<MedicalRecordEntity>
}
