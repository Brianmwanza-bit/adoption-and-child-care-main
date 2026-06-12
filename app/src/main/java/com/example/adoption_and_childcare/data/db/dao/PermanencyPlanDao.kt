package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.PermanencyPlanEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface PermanencyPlanDao {
    @Query("SELECT * FROM permanency_plans")
    fun observeAll(): Flow<List<PermanencyPlanEntity>>

    @Query("SELECT * FROM permanency_plans")
    suspend fun getAll(): List<PermanencyPlanEntity>

    @Query("SELECT * FROM permanency_plans WHERE plan_id = :id")
    suspend fun getById(id: Int): PermanencyPlanEntity?

    @Query("SELECT * FROM permanency_plans WHERE child_id = :childId")
    suspend fun getByChildId(childId: Int): List<PermanencyPlanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: PermanencyPlanEntity): Long

    @Transaction
    suspend fun insertWithSync(plan: PermanencyPlanEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(plan)
        val payload = Gson().toJson(plan)
        syncQueueDao.insert(SyncQueueEntity(tableName = "permanency_plans", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<PermanencyPlanEntity>)

    @Update
    suspend fun update(plan: PermanencyPlanEntity)

    @Transaction
    suspend fun updateWithSync(plan: PermanencyPlanEntity, syncQueueDao: SyncQueueDao) {
        update(plan)
        val payload = Gson().toJson(plan)
        syncQueueDao.insert(SyncQueueEntity(tableName = "permanency_plans", operation = "UPDATE", recordId = plan.planId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(plan: PermanencyPlanEntity)

    @Query("DELETE FROM permanency_plans WHERE plan_id = :id")
    suspend fun deleteById(id: Int)
}
