package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.CaseloadEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseloadDao {
    @Query("SELECT * FROM caseload")
    fun observeAll(): Flow<List<CaseloadEntity>>

    @Query("SELECT * FROM caseload")
    suspend fun getAll(): List<CaseloadEntity>

    @Query("SELECT * FROM caseload WHERE caseload_id = :id")
    suspend fun getById(id: Int): CaseloadEntity?

    @Query("SELECT * FROM caseload WHERE worker_id = :workerId ORDER BY date DESC")
    suspend fun getByWorkerId(workerId: Int): List<CaseloadEntity>

    @Query("SELECT * FROM caseload WHERE worker_id = :workerId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestForWorker(workerId: Int): CaseloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(caseload: CaseloadEntity): Long

    @Transaction
    suspend fun insertWithSync(caseload: CaseloadEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(caseload)
        val payload = Gson().toJson(caseload)
        syncQueueDao.insert(SyncQueueEntity(tableName = "caseload", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(caseloads: List<CaseloadEntity>)

    @Update
    suspend fun update(caseload: CaseloadEntity)

    @Delete
    suspend fun delete(caseload: CaseloadEntity)
}
