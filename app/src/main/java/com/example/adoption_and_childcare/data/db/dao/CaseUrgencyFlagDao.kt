package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.CaseUrgencyFlagEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseUrgencyFlagDao {
    @Query("SELECT * FROM case_urgency_flags")
    fun observeAll(): Flow<List<CaseUrgencyFlagEntity>>

    @Query("SELECT * FROM case_urgency_flags")
    suspend fun getAll(): List<CaseUrgencyFlagEntity>

    @Query("SELECT * FROM case_urgency_flags WHERE flag_id = :id")
    suspend fun getById(id: Int): CaseUrgencyFlagEntity?

    @Query("SELECT * FROM case_urgency_flags WHERE case_id = :caseId")
    suspend fun getByCaseId(caseId: Int): List<CaseUrgencyFlagEntity>

    @Query("SELECT * FROM case_urgency_flags WHERE resolved_at IS NULL")
    suspend fun getActive(): List<CaseUrgencyFlagEntity>

    @Query("SELECT * FROM case_urgency_flags WHERE resolved_at IS NULL AND flag_type = 'critical'")
    suspend fun getCriticalActive(): List<CaseUrgencyFlagEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flag: CaseUrgencyFlagEntity): Long

    @Transaction
    suspend fun insertWithSync(flag: CaseUrgencyFlagEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(flag)
        val payload = Gson().toJson(flag)
        syncQueueDao.insert(SyncQueueEntity(tableName = "case_urgency_flags", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(flags: List<CaseUrgencyFlagEntity>)

    @Update
    suspend fun update(flag: CaseUrgencyFlagEntity)

    @Transaction
    suspend fun updateWithSync(flag: CaseUrgencyFlagEntity, syncQueueDao: SyncQueueDao) {
        update(flag)
        val payload = Gson().toJson(flag)
        syncQueueDao.insert(SyncQueueEntity(tableName = "case_urgency_flags", operation = "UPDATE", recordId = flag.flagId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(flag: CaseUrgencyFlagEntity)

    @Query("DELETE FROM case_urgency_flags WHERE flag_id = :id")
    suspend fun deleteById(id: Int)
}
