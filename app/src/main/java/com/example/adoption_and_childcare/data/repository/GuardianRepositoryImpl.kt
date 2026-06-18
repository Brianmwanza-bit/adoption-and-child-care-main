package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.GuardianDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.GuardianEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for guardian data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class GuardianRepositoryImpl @Inject constructor(
    private val guardianDao: GuardianDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<GuardianEntity>> = guardianDao.observeAll()
    
    suspend fun insert(guardian: GuardianEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            guardianDao.insertWithSync(guardian, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createGuardian(authHeader, guardian)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(guardian.guardianId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(guardian: GuardianEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            guardianDao.updateWithSync(guardian, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateGuardian(authHeader, guardian.guardianId, guardian)
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
            guardianDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteGuardian(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): GuardianEntity? {
        return guardianDao.findById(id)
    }
    
    suspend fun count(): Int = guardianDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<GuardianEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getGuardians(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch guardians: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
