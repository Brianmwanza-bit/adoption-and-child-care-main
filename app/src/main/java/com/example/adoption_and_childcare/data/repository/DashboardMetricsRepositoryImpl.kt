package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.DashboardMetricDao
import com.example.adoption_and_childcare.data.db.entities.DashboardMetricEntity
import com.example.adoption_and_childcare.network.AnalyticsSummary
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for dashboard metrics with API integration.
 * Provides real-time metrics from both local database and remote API.
 *
 * @property dashboardMetricDao DAO for local database operations
 * @property apiService Retrofit API service for backend communication
 * @property authManager Authentication manager for handling auth tokens
 */
@Singleton
class DashboardMetricsRepositoryImpl @Inject constructor(
    private val dashboardMetricDao: DashboardMetricDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    companion object {
        // Metric name constants
        private const val METRIC_TOTAL_CHILDREN = "total_children"
        private const val METRIC_TOTAL_FAMILIES = "total_families"
        private const val METRIC_ACTIVE_PLACEMENTS = "active_placements"
        private const val METRIC_PENDING_APPLICATIONS = "pending_applications"
        
        // Metric label constants
        private const val LABEL_TOTAL_CHILDREN = "Total Children"
        private const val LABEL_TOTAL_FAMILIES = "Total Families"
        private const val LABEL_ACTIVE_PLACEMENTS = "Active Placements"
        private const val LABEL_PENDING_APPLICATIONS = "Pending Applications"
    }
    /**
     * Observes all dashboard metrics as a Flow.
     * @return Flow of list of all dashboard metrics
     */
    fun observeAll(): Flow<List<DashboardMetricEntity>> = dashboardMetricDao.observeAll()
    
    /**
     * Inserts a dashboard metric into the local database.
     * @param metric The dashboard metric entity to insert
     * @param token Authentication token (currently unused, auth is handled by AuthManager)
     * @return Result containing the row ID or failure
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun insert(metric: DashboardMetricEntity, token: String): Result<Long> {
        return try {
            val localId = dashboardMetricDao.insert(metric)
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates a dashboard metric in the local database.
     * @param metric The dashboard metric entity with updated values
     * @param token Authentication token (currently unused, auth is handled by AuthManager)
     * @return Result containing Unit or failure
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun update(metric: DashboardMetricEntity, token: String): Result<Unit> {
        return try {
            dashboardMetricDao.update(metric)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Deletes a dashboard metric from the local database by ID.
     * @param id The metric ID to delete
     * @param token Authentication token (currently unused, auth is handled by AuthManager)
     * @return Result containing Unit or failure
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            dashboardMetricDao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Finds a dashboard metric by ID.
     * @param id The metric ID to search for
     * @return The dashboard metric entity or null if not found
     */
    suspend fun findById(id: Int): DashboardMetricEntity? {
        return dashboardMetricDao.getById(id)
    }
    
    /**
     * Counts the total number of dashboard metrics.
     * @return The count of metrics
     */
    suspend fun count(): Int = dashboardMetricDao.getAll().size
    
    /**
     * Fetches analytics summary from the API.
     * @param token Authentication token (currently unused, auth is handled by AuthManager)
     * @return Result containing the analytics summary or failure
     */
    @Suppress("UNUSED_PARAMETER")
    private suspend fun fetchAnalyticsSummary(token: String): Result<AnalyticsSummary> {
        return try {
            val authHeader = authManager.getAuthHeader()
                ?: return Result.failure(Exception("Not authenticated. Please log in."))
            
            val response = apiService.getAnalyticsSummary(authHeader)
            if (response.isSuccessful) {
                val body = response.body() ?: return Result.failure(Exception("Failed to fetch analytics summary: Empty response"))
                Result.success(body)
            } else {
                Result.failure(Exception("Failed to fetch analytics summary: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetches dashboard metrics from API and updates local database.
     * @param token Authentication token (currently unused, auth is handled by AuthManager)
     * @return Result containing list of dashboard metrics or failure
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun fetchFromApi(token: String): Result<List<DashboardMetricEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
                ?: return Result.failure(Exception("Not authenticated. Please log in."))
            
            // Dashboard metrics are fetched via analytics summary
            val summaryResult = fetchAnalyticsSummary(token)
            if (summaryResult.isSuccess) {
                val summary = summaryResult.getOrNull() ?: return Result.failure(Exception("Failed to fetch analytics summary"))
                // Convert to DashboardMetricEntity list
                val metrics = listOf(
                    DashboardMetricEntity(
                        metricName = METRIC_TOTAL_CHILDREN,
                        metricValue = summary.totalChildren.toDouble(),
                        metricLabel = LABEL_TOTAL_CHILDREN,
                        lastUpdated = System.currentTimeMillis()
                    ),
                    DashboardMetricEntity(
                        metricName = METRIC_TOTAL_FAMILIES,
                        metricValue = summary.totalFamilies.toDouble(),
                        metricLabel = LABEL_TOTAL_FAMILIES,
                        lastUpdated = System.currentTimeMillis()
                    ),
                    DashboardMetricEntity(
                        metricName = METRIC_ACTIVE_PLACEMENTS,
                        metricValue = summary.activePlacements.toDouble(),
                        metricLabel = LABEL_ACTIVE_PLACEMENTS,
                        lastUpdated = System.currentTimeMillis()
                    ),
                    DashboardMetricEntity(
                        metricName = METRIC_PENDING_APPLICATIONS,
                        metricValue = summary.pendingApplications.toDouble(),
                        metricLabel = LABEL_PENDING_APPLICATIONS,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
                
                // Update local database
                for (metric in metrics) {
                    val existing = dashboardMetricDao.findByMetricName(metric.metricName)
                    if (existing != null) {
                        dashboardMetricDao.update(metric)
                    } else {
                        dashboardMetricDao.insert(metric)
                    }
                }
                
                Result.success(metrics)
            } else {
                Result.failure(Exception("Failed to fetch dashboard metrics"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
