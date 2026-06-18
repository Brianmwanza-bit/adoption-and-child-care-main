package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.PermissionDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.PermissionEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for permission data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class PermissionRepositoryImpl @Inject constructor(
    private val permissionDao: PermissionDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<PermissionEntity>> = permissionDao.observeAll()
    
    suspend fun insert(permission: PermissionEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            permissionDao.insertWithSync(permission, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createPermission(authHeader, permission)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(permission.permissionId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(permission: PermissionEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            permissionDao.updateWithSync(permission, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updatePermission(authHeader, permission.permissionId, permission)
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
            permissionDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deletePermission(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): PermissionEntity? {
        return permissionDao.findById(id)
    }
    
    suspend fun count(): Int = permissionDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<PermissionEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getPermissions(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch permissions: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
