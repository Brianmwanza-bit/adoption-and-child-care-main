package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.WorkloadTrackingEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkloadTrackingDao {
    @Query("SELECT * FROM workload_tracking")
    fun observeAll(): Flow<List<WorkloadTrackingEntity>>

    @Query("SELECT * FROM workload_tracking")
    suspend fun getAll(): List<WorkloadTrackingEntity>

    @Query("SELECT * FROM workload_tracking WHERE workload_id = :id")
    suspend fun getById(id: Int): WorkloadTrackingEntity?

    @Query("SELECT * FROM workload_tracking WHERE caseworker_id = :workerId ORDER BY tracking_date DESC")
    suspend fun getByCaseworkerId(workerId: Int): List<WorkloadTrackingEntity>

    @Query("SELECT * FROM workload_tracking WHERE caseworker_id = :workerId ORDER BY tracking_date DESC LIMIT 1")
    suspend fun getLatestForWorker(workerId: Int): WorkloadTrackingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workload: WorkloadTrackingEntity): Long

    @Transaction
    suspend fun insertWithSync(workload: WorkloadTrackingEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(workload)
        val payload = Gson().toJson(workload)
        syncQueueDao.insert(SyncQueueEntity(tableName = "workload_tracking", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(workloads: List<WorkloadTrackingEntity>)

    @Update
    suspend fun update(workload: WorkloadTrackingEntity)

    @Delete
    suspend fun delete(workload: WorkloadTrackingEntity)
}
