package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.CaseDeadlineEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDeadlineDao {
    @Query("SELECT * FROM case_deadlines")
    fun observeAll(): Flow<List<CaseDeadlineEntity>>

    @Query("SELECT * FROM case_deadlines")
    suspend fun getAll(): List<CaseDeadlineEntity>

    @Query("SELECT * FROM case_deadlines WHERE deadline_id = :id")
    suspend fun getById(id: Int): CaseDeadlineEntity?

    @Query("SELECT * FROM case_deadlines WHERE case_id = :caseId ORDER BY due_date ASC")
    suspend fun getByCaseId(caseId: Int): List<CaseDeadlineEntity>

    @Query("SELECT * FROM case_deadlines WHERE status = 'overdue'")
    suspend fun getOverdue(): List<CaseDeadlineEntity>

    @Query("SELECT * FROM case_deadlines WHERE status = 'pending' ORDER BY due_date ASC")
    suspend fun getPending(): List<CaseDeadlineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deadline: CaseDeadlineEntity): Long

    @Transaction
    suspend fun insertWithSync(deadline: CaseDeadlineEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(deadline)
        val payload = Gson().toJson(deadline)
        syncQueueDao.insert(SyncQueueEntity(tableName = "case_deadlines", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(deadlines: List<CaseDeadlineEntity>)

    @Update
    suspend fun update(deadline: CaseDeadlineEntity)

    @Transaction
    suspend fun updateWithSync(deadline: CaseDeadlineEntity, syncQueueDao: SyncQueueDao) {
        update(deadline)
        val payload = Gson().toJson(deadline)
        syncQueueDao.insert(SyncQueueEntity(tableName = "case_deadlines", operation = "UPDATE", recordId = deadline.deadlineId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(deadline: CaseDeadlineEntity)

    @Query("DELETE FROM case_deadlines WHERE deadline_id = :id")
    suspend fun deleteById(id: Int)
}
