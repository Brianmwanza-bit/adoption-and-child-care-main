package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.CaseApprovalEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseApprovalDao {
    @Query("SELECT * FROM case_approvals")
    fun observeAll(): Flow<List<CaseApprovalEntity>>

    @Query("SELECT * FROM case_approvals")
    suspend fun getAll(): List<CaseApprovalEntity>

    @Query("SELECT * FROM case_approvals WHERE approval_id = :id")
    suspend fun getById(id: Int): CaseApprovalEntity?

    @Query("SELECT * FROM case_approvals WHERE case_id = :caseId")
    suspend fun getByCaseId(caseId: Int): List<CaseApprovalEntity>

    @Query("SELECT * FROM case_approvals WHERE status = 'pending'")
    suspend fun getPending(): List<CaseApprovalEntity>

    @Query("SELECT * FROM case_approvals WHERE reviewed_by = :userId")
    suspend fun getByReviewer(userId: Int): List<CaseApprovalEntity>

    @Query("SELECT * FROM case_approvals WHERE submitted_by = :userId")
    suspend fun getBySubmitter(userId: Int): List<CaseApprovalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(approval: CaseApprovalEntity): Long

    @Transaction
    suspend fun insertWithSync(approval: CaseApprovalEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(approval)
        val payload = Gson().toJson(approval)
        syncQueueDao.insert(SyncQueueEntity(tableName = "case_approvals", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(approvals: List<CaseApprovalEntity>)

    @Update
    suspend fun update(approval: CaseApprovalEntity)

    @Transaction
    suspend fun updateWithSync(approval: CaseApprovalEntity, syncQueueDao: SyncQueueDao) {
        update(approval)
        val payload = Gson().toJson(approval)
        syncQueueDao.insert(SyncQueueEntity(tableName = "case_approvals", operation = "UPDATE", recordId = approval.approvalId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(approval: CaseApprovalEntity)

    @Query("DELETE FROM case_approvals WHERE approval_id = :id")
    suspend fun deleteById(id: Int)
}
