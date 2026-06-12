package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface CourtCaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CourtCaseEntity): Long

    @Transaction
    suspend fun insertWithSync(entity: CourtCaseEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "court_cases",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Update
    suspend fun update(entity: CourtCaseEntity)

    @Transaction
    suspend fun updateWithSync(entity: CourtCaseEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "court_cases",
                operation = "UPDATE",
                recordId = entity.caseId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM court_cases WHERE case_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "court_cases",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM court_cases ORDER BY filing_date DESC")
    fun observeAll(): Flow<List<CourtCaseEntity>>

    @Query("SELECT * FROM court_cases WHERE hearing_date >= DATE('now') ORDER BY hearing_date ASC")
    fun observeUpcoming(): Flow<List<CourtCaseEntity>>

    @Query("SELECT COUNT(*) FROM court_cases")
    suspend fun count(): Int
}
