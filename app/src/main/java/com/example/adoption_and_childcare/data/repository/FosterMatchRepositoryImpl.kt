package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.FosterMatchDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for foster match data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class FosterMatchRepositoryImpl @Inject constructor(
    private val fosterMatchDao: FosterMatchDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<FosterMatchEntity>> = fosterMatchDao.observeAll()
    
    suspend fun insert(match: FosterMatchEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            fosterMatchDao.insertWithSync(match, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createFosterMatch(authHeader, match)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(match.matchId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(match: FosterMatchEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            fosterMatchDao.updateWithSync(match, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateFosterMatch(authHeader, match.matchId, match)
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
            fosterMatchDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteFosterMatch(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): FosterMatchEntity? {
        return fosterMatchDao.findById(id)
    }
    
    suspend fun count(): Int = fosterMatchDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<FosterMatchEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getFosterMatches(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch foster matches: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
