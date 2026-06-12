package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.RiskAssessmentEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface RiskAssessmentDao {
    @Query("SELECT * FROM risk_assessments")
    fun observeAll(): Flow<List<RiskAssessmentEntity>>

    @Query("SELECT * FROM risk_assessments")
    suspend fun getAll(): List<RiskAssessmentEntity>

    @Query("SELECT * FROM risk_assessments WHERE assessment_id = :id")
    suspend fun getById(id: Int): RiskAssessmentEntity?

    @Query("SELECT * FROM risk_assessments WHERE child_id = :childId ORDER BY assessment_date DESC")
    suspend fun getByChildId(childId: Int): List<RiskAssessmentEntity>

    @Query("SELECT * FROM risk_assessments WHERE child_id = :childId ORDER BY assessment_date DESC LIMIT 1")
    suspend fun getLatestForChild(childId: Int): RiskAssessmentEntity?

    @Query("SELECT * FROM risk_assessments WHERE risk_level = :level")
    suspend fun getByRiskLevel(level: String): List<RiskAssessmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(assessment: RiskAssessmentEntity): Long

    @Transaction
    suspend fun insertWithSync(assessment: RiskAssessmentEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(assessment)
        val payload = Gson().toJson(assessment)
        syncQueueDao.insert(SyncQueueEntity(tableName = "risk_assessments", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assessments: List<RiskAssessmentEntity>)

    @Update
    suspend fun update(assessment: RiskAssessmentEntity)

    @Transaction
    suspend fun updateWithSync(assessment: RiskAssessmentEntity, syncQueueDao: SyncQueueDao) {
        update(assessment)
        val payload = Gson().toJson(assessment)
        syncQueueDao.insert(SyncQueueEntity(tableName = "risk_assessments", operation = "UPDATE", recordId = assessment.assessmentId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(assessment: RiskAssessmentEntity)

    @Query("DELETE FROM risk_assessments WHERE assessment_id = :id")
    suspend fun deleteById(id: Int)
}
