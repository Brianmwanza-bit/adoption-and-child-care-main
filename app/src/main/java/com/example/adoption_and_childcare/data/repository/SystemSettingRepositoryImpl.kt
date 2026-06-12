package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.SystemSettingDao
import com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for system settings data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class SystemSettingRepositoryImpl @Inject constructor(
    private val systemSettingDao: SystemSettingDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<SystemSettingEntity>> = systemSettingDao.observeAll()
    
    suspend fun insert(setting: SystemSettingEntity, token: String): Result<Long> {
        return try {
            val localId = systemSettingDao.insert(setting)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createSystemSetting(authHeader, setting)
                    if (!response.isSuccessful) {
                        println("Failed to sync system setting with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for system setting insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(setting: SystemSettingEntity, token: String): Result<Unit> {
        return try {
            systemSettingDao.update(setting)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updateSystemSetting(authHeader, setting.settingId, setting)
                    if (!response.isSuccessful) {
                        println("Failed to sync system setting update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for system setting update: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            systemSettingDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteSystemSetting(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for system setting delete: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
     suspend fun findById(id: Int): SystemSettingEntity? {
        return systemSettingDao.findById(id)
    }
    
    suspend fun count(): Int = systemSettingDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<SystemSettingEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getSystemSettings(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch system settings: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
