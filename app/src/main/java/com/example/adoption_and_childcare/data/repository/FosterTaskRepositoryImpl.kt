package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.FosterTaskDao
import com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for foster task data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class FosterTaskRepositoryImpl @Inject constructor(
    private val fosterTaskDao: FosterTaskDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<FosterTaskEntity>> = fosterTaskDao.observeAll()
    
    suspend fun insert(task: FosterTaskEntity, token: String): Result<Long> {
        return try {
            val localId = fosterTaskDao.insert(task)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createFosterTask(authHeader, task)
                    if (!response.isSuccessful) {
                        println("Failed to sync foster task with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for foster task insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(task: FosterTaskEntity, token: String): Result<Unit> {
        return try {
            fosterTaskDao.update(task)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updateFosterTask(authHeader, task.taskId, task)
                    if (!response.isSuccessful) {
                        println("Failed to sync foster task update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for foster task update: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            fosterTaskDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteFosterTask(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for foster task delete: ${e.message}")
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
