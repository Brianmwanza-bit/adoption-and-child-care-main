package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.HomeStudyDao
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for home study data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class HomeStudyRepositoryImpl @Inject constructor(
    private val homeStudyDao: HomeStudyDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    // Local database operations
    fun observeAll(): Flow<List<HomeStudyEntity>> = homeStudyDao.observeAll()
    
    suspend fun insert(study: HomeStudyEntity, token: String): Result<Long> {
        return try {
            val localId = homeStudyDao.insert(study)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createHomeStudy(authHeader, study)
                    if (!response.isSuccessful) {
                        println("Failed to sync home study with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for home study insert: ${e.message}")
            }
            
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(study: HomeStudyEntity, token: String): Result<Unit> {
        return try {
            homeStudyDao.update(study)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updateHomeStudy(authHeader, study.homeStudyId, study)
                    if (!response.isSuccessful) {
                        println("Failed to sync home study update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for home study update: ${e.message}")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            homeStudyDao.deleteById(id)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteHomeStudy(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for home study delete: ${e.message}")
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
