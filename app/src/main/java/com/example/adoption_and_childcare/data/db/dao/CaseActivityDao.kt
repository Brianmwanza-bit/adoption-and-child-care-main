package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.CaseActivityEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseActivityDao {
    @Query("SELECT * FROM case_activities")
    fun observeAll(): Flow<List<CaseActivityEntity>>

    @Query("SELECT * FROM case_activities")
    suspend fun getAll(): List<CaseActivityEntity>

    @Query("SELECT * FROM case_activities WHERE activity_id = :id")
    suspend fun getById(id: Int): CaseActivityEntity?

    @Query("SELECT * FROM case_activities WHERE case_id = :caseId ORDER BY activity_date DESC, activity_time DESC")
    suspend fun getByCaseId(caseId: Int): List<CaseActivityEntity>

    @Query("SELECT * FROM case_activities WHERE caseworker_id = :workerId ORDER BY activity_date DESC")
    suspend fun getByCaseworkerId(workerId: Int): List<CaseActivityEntity>

    @Query("SELECT * FROM case_activities WHERE activity_type = :type")
    suspend fun getByType(type: String): List<CaseActivityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: CaseActivityEntity): Long

    @Transaction
    suspend fun insertWithSync(activity: CaseActivityEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(activity)
        val payload = Gson().toJson(activity)
        syncQueueDao.insert(SyncQueueEntity(tableName = "case_activities", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<CaseActivityEntity>)

    @Update
    suspend fun update(activity: CaseActivityEntity)

    @Transaction
    suspend fun updateWithSync(activity: CaseActivityEntity, syncQueueDao: SyncQueueDao) {
        update(activity)
        val payload = Gson().toJson(activity)
        syncQueueDao.insert(SyncQueueEntity(tableName = "case_activities", operation = "UPDATE", recordId = activity.activityId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(activity: CaseActivityEntity)

    @Query("DELETE FROM case_activities WHERE activity_id = :id")
    suspend fun deleteById(id: Int)
}
