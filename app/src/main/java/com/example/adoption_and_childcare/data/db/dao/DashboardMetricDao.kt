package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.DashboardMetricEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for dashboard metrics database operations.
 * 
 * This class provides methods for CRUD operations on dashboard metrics,
 * including sync-aware methods that automatically queue changes for
 * offline-first synchronization.
 */
@Dao
interface DashboardMetricDao {
    /**
     * Observes all dashboard metrics as a Flow.
     * @return Flow of list of all dashboard metrics
     */
    @Query("SELECT * FROM dashboard_metrics")
    fun observeAll(): Flow<List<DashboardMetricEntity>>

    /**
     * Gets all dashboard metrics.
     * @return List of all dashboard metrics
     */
    @Query("SELECT * FROM dashboard_metrics")
    suspend fun getAll(): List<DashboardMetricEntity>

    /**
     * Gets a dashboard metric by ID.
     * @param id The metric ID
     * @return The dashboard metric entity or null if not found
     */
    @Query("SELECT * FROM dashboard_metrics WHERE metric_id = :id")
    suspend fun getById(id: Int): DashboardMetricEntity?

    /**
     * Finds a dashboard metric by its name.
     * @param name The metric name to search for
     * @return The dashboard metric entity or null if not found
     */
    @Query("SELECT * FROM dashboard_metrics WHERE metric_name = :name")
    suspend fun findByMetricName(name: String): DashboardMetricEntity?

    /**
     * Inserts a dashboard metric.
     * @param metric The dashboard metric entity to insert
     * @return The row ID of the inserted metric
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metric: DashboardMetricEntity): Long

    /**
     * Inserts a dashboard metric and queues it for sync.
     * @param metric The dashboard metric entity to insert
     * @param syncQueueDao The sync queue DAO for queuing the operation
     */
    @Transaction
    suspend fun insertWithSync(metric: DashboardMetricEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(metric)
        val payload = Gson().toJson(metric)
        syncQueueDao.insert(SyncQueueEntity(tableName = "dashboard_metrics", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    /**
     * Inserts multiple dashboard metrics.
     * @param metrics List of dashboard metric entities to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(metrics: List<DashboardMetricEntity>)

    /**
     * Updates a dashboard metric.
     * @param metric The dashboard metric entity with updated values
     */
    @Update
    suspend fun update(metric: DashboardMetricEntity)

    /**
     * Deletes a dashboard metric.
     * @param metric The dashboard metric entity to delete
     */
    @Delete
    suspend fun delete(metric: DashboardMetricEntity)

    /**
     * Deletes a dashboard metric by ID.
     * @param id The metric ID to delete
     */
    @Query("DELETE FROM dashboard_metrics WHERE metric_id = :id")
    suspend fun deleteById(id: Int)
}
