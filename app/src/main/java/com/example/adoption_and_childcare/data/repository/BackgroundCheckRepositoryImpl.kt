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
    /**
     * Observes all background checks from the local database.
     */
    fun observeAll(): Flow<List<BackgroundCheckEntity>> = backgroundCheckDao.observeAll()
    
    /**
     * Inserts a background check and attempts to sync it.
     * @param check The check to insert.
     * @param token Authentication token (fallback).
     */
    suspend fun insert(check: BackgroundCheckEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            backgroundCheckDao.insertWithSync(check, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.createBackgroundCheck(authHeader, check)
                }
            } catch (e: Exception) {
                android.util.Log.w("BackgroundCheckRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(check.checkId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates a background check and attempts to sync it.
     * @param check The check to update.
     * @param token Authentication token (fallback).
     */
    suspend fun update(check: BackgroundCheckEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            backgroundCheckDao.updateWithSync(check, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.updateBackgroundCheck(authHeader, check.checkId, check)
                }
            } catch (e: Exception) {
                android.util.Log.w("BackgroundCheckRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Deletes a background check and attempts to sync it.
     * @param id The check ID.
     * @param token Authentication token (fallback).
     */
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete with sync queue support
            backgroundCheckDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.deleteBackgroundCheck(authHeader, id)
                }
            } catch (e: Exception) {
                android.util.Log.w("BackgroundCheckRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Finds a background check by ID.
     */
    suspend fun findById(id: Int): BackgroundCheckEntity? = backgroundCheckDao.getById(id)
    
    /**
     * Returns the total count of background checks.
     */
    suspend fun count(): Int = backgroundCheckDao.getAll().size
    
    /**
     * Fetches all background checks from the API.
     * @param token Authentication token.
     */
    suspend fun fetchFromApi(token: String): Result<List<BackgroundCheckEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getBackgroundChecks(authHeader)
            if (response.isSuccessful && response.body() != null) {
                val checks = response.body()!!.data
                for (check in checks) {
                    val existing = backgroundCheckDao.getById(check.checkId)
                    if (existing != null) {
                        backgroundCheckDao.update(check)
                    } else {
                        backgroundCheckDao.insert(check)
                    }
                }
                Result.success(checks)
            } else {
                Result.failure(Exception("Failed to fetch background checks: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
