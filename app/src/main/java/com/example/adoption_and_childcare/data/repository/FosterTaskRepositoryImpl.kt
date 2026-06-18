package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.FosterTaskDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for foster task data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class FosterTaskRepositoryImpl @Inject constructor(
    private val fosterTaskDao: FosterTaskDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<FosterTaskEntity>> = fosterTaskDao.observeAll()
    
    suspend fun insert(task: FosterTaskEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            fosterTaskDao.insertWithSync(task, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createFosterTask(authHeader, task)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(task.taskId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(task: FosterTaskEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            fosterTaskDao.updateWithSync(task, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateFosterTask(authHeader, task.taskId, task)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete with sync queue support
            fosterTaskDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteFosterTask(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): FosterTaskEntity? {
        return fosterTaskDao.findById(id)
    }
    
    suspend fun count(): Int = fosterTaskDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<FosterTaskEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getFosterTasks(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch foster tasks: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
