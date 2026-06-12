package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.PermissionDao
import com.example.adoption_and_childcare.data.db.entities.PermissionEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for permission data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class PermissionRepositoryImpl @Inject constructor(
    private val permissionDao: PermissionDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<PermissionEntity>> = permissionDao.observeAll()
    
    suspend fun insert(permission: PermissionEntity, token: String): Result<Long> {
        return try {
            val localId = permissionDao.insert(permission)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createPermission(authHeader, permission)
                    if (!response.isSuccessful) {
                        println("Failed to sync permission with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for permission insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(permission: PermissionEntity, token: String): Result<Unit> {
        return try {
            permissionDao.update(permission)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updatePermission(authHeader, permission.permissionId, permission)
                    if (!response.isSuccessful) {
                        println("Failed to sync permission update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for permission update: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            permissionDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deletePermission(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for permission delete: ${e.message}")
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
