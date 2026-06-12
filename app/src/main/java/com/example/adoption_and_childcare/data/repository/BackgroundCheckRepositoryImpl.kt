package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.BackgroundCheckDao
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for background check data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class BackgroundCheckRepositoryImpl @Inject constructor(
    private val backgroundCheckDao: BackgroundCheckDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<BackgroundCheckEntity>> = backgroundCheckDao.observeAll()
    
    suspend fun insert(check: BackgroundCheckEntity, token: String): Result<Long> {
        return try {
            val localId = backgroundCheckDao.insert(check)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createBackgroundCheck(authHeader, check)
                    if (!response.isSuccessful) {
                        println("Failed to sync background check with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for background check insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(check: BackgroundCheckEntity, token: String): Result<Unit> {
        return try {
            backgroundCheckDao.update(check)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updateBackgroundCheck(authHeader, check.checkId, check)
                    if (!response.isSuccessful) {
                        println("Failed to sync background check update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for background check update: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            backgroundCheckDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteBackgroundCheck(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for background check delete: ${e.message}")
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
