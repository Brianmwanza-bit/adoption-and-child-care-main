package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface EducationRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EducationRecordEntity): Long

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

    @Update
    suspend fun update(entity: EducationRecordEntity)

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

    @Query("DELETE FROM education_records WHERE record_id = :id")
    suspend fun deleteById(id: Int)

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

    @Query("SELECT * FROM education_records WHERE child_id = :childId ORDER BY enrollment_date DESC")
    fun observeForChild(childId: Int): Flow<List<EducationRecordEntity>>

    @Query("SELECT COUNT(*) FROM education_records")
    suspend fun count(): Int

    @Query("SELECT * FROM education_records ORDER BY enrollment_date DESC")
    fun observeAll(): Flow<List<EducationRecordEntity>>
}
