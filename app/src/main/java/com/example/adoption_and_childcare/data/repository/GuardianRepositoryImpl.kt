package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.GuardianDao
import com.example.adoption_and_childcare.data.db.entities.GuardianEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for guardian data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class GuardianRepositoryImpl @Inject constructor(
    private val guardianDao: GuardianDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<GuardianEntity>> = guardianDao.observeAll()
    
    suspend fun insert(guardian: GuardianEntity, token: String): Result<Long> {
        return try {
            val localId = guardianDao.insert(guardian)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createGuardian(authHeader, guardian)
                    if (!response.isSuccessful) {
                        println("Failed to sync guardian with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for guardian insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(guardian: GuardianEntity, token: String): Result<Unit> {
        return try {
            guardianDao.update(guardian)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updateGuardian(authHeader, guardian.guardianId, guardian)
                    if (!response.isSuccessful) {
                        println("Failed to sync guardian update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for guardian update: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            guardianDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteGuardian(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for guardian delete: ${e.message}")
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
