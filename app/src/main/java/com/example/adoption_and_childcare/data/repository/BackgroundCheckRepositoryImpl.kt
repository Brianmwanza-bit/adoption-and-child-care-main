package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.BackgroundCheckDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for background check data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class BackgroundCheckRepositoryImpl @Inject constructor(
    private val backgroundCheckDao: BackgroundCheckDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<BackgroundCheckEntity>> = backgroundCheckDao.observeAll()
    
    suspend fun insert(check: BackgroundCheckEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            backgroundCheckDao.insertWithSync(check, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createBackgroundCheck(authHeader, check)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(check.checkId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(check: BackgroundCheckEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            backgroundCheckDao.updateWithSync(check, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateBackgroundCheck(authHeader, check.checkId, check)
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
            backgroundCheckDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteBackgroundCheck(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): BackgroundCheckEntity? = backgroundCheckDao.getById(id)
    suspend fun count(): Int = backgroundCheckDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<BackgroundCheckEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            // TODO: Implement API endpoint for background checks
            // val response = apiService.getAllBackgroundChecks(authHeader)
            Result.failure(Exception("API endpoint not implemented yet"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
