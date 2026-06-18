package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.SystemSettingDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for system settings data with dual-sync architecture.
 * Room DB is the primary storage. API is secondary with live sync + batch processing via SyncQueue.
 */
@Singleton
class SystemSettingRepositoryImpl @Inject constructor(
    private val systemSettingDao: SystemSettingDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    /**
     * Observes all system settings from Room.
     */
    fun observeAll(): Flow<List<SystemSettingEntity>> = systemSettingDao.observeAll()
    
    /**
     * Inserts a setting locally and attempts immediate sync.
     */
    suspend fun insert(setting: SystemSettingEntity, token: String): Result<Long> {
        return try {
            systemSettingDao.insertWithSync(setting, syncQueueDao)
            val authHeader = authManager.getAuthHeader()
            if (authHeader != null) {
                try {
                    val response = apiService.createSystemSetting(authHeader, setting)
                    if (response.isSuccessful) {
                        // Mark as synced in queue if immediate sync succeeds
                        val lastQueueItem = syncQueueDao.getPending().lastOrNull { it.tableName == "system_settings" }
                        if (lastQueueItem != null) syncQueueDao.markSynced(lastQueueItem.id)
                    }
                } catch (e: Exception) {
                    println("Immediate live sync failed, will be retried in batch: ${e.message}")
                }
            }
            Result.success(0L) // Return dummy ID as it's not strictly used for UI flow here
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates a setting locally and attempts immediate sync.
     */
    suspend fun update(setting: SystemSettingEntity, token: String): Result<Unit> {
        return try {
            systemSettingDao.updateWithSync(setting, syncQueueDao)
            val authHeader = authManager.getAuthHeader()
            if (authHeader != null) {
                try {
                    val response = apiService.updateSystemSetting(authHeader, setting.settingId, setting)
                    if (response.isSuccessful) {
                        val lastQueueItem = syncQueueDao.getPending().lastOrNull { it.tableName == "system_settings" }
                        if (lastQueueItem != null) syncQueueDao.markSynced(lastQueueItem.id)
                    }
                } catch (e: Exception) {
                    println("Immediate live update failed, will be retried in batch: ${e.message}")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Deletes a setting locally and attempts immediate sync.
     */
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            systemSettingDao.deleteByIdWithSync(id, syncQueueDao)
            val authHeader = authManager.getAuthHeader()
            if (authHeader != null) {
                try {
                    val response = apiService.deleteSystemSetting(authHeader, id)
                    if (response.isSuccessful) {
                        val lastQueueItem = syncQueueDao.getPending().lastOrNull { it.tableName == "system_settings" }
                        if (lastQueueItem != null) syncQueueDao.markSynced(lastQueueItem.id)
                    }
                } catch (e: Exception) {
                    println("Immediate live delete failed: ${e.message}")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): SystemSettingEntity? = systemSettingDao.findById(id)
    
    suspend fun count(): Int = systemSettingDao.getAll().size
    
    /**
     * Fetches fresh settings from API and updates local Room storage.
     */
    suspend fun fetchFromApi(token: String): Result<List<SystemSettingEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader() ?: return Result.failure(Exception("Not authenticated"))
            val response = apiService.getSystemSettings(authHeader)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                data.forEach { systemSettingDao.insert(it) }
                Result.success(data)
            } else {
                Result.failure(Exception("API error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
