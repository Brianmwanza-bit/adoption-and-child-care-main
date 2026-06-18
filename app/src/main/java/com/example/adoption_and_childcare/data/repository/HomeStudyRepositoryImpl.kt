package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.HomeStudyDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for home study data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class HomeStudyRepositoryImpl @Inject constructor(
    private val homeStudyDao: HomeStudyDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    // Local database operations
    fun observeAll(): Flow<List<HomeStudyEntity>> = homeStudyDao.observeAll()
    
    suspend fun insert(study: HomeStudyEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            homeStudyDao.insertWithSync(study, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createHomeStudy(authHeader, study)
                }
            } catch (e: Exception) {
                // Ignore failure; sync queue will handle it
            }
            
            Result.success(study.homeStudyId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(study: HomeStudyEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            homeStudyDao.updateWithSync(study, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateHomeStudy(authHeader, study.homeStudyId, study)
                }
            } catch (e: Exception) {
                // Ignore failure
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete with sync queue support
            homeStudyDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteHomeStudy(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore failure
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): HomeStudyEntity? {
        return homeStudyDao.findById(id)
    }
    
    suspend fun count(): Int = homeStudyDao.getAll().size
    
    // API operations
    suspend fun fetchFromApi(token: String): Result<List<HomeStudyEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getAllHomeStudies(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch home studies: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun insertWithSync(study: HomeStudyEntity, token: String): Result<Long> {
        return insert(study, token)
    }
}
