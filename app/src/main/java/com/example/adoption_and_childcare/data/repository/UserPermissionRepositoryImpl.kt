package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.UserPermissionDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for user permission data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class UserPermissionRepositoryImpl @Inject constructor(
    private val userPermissionDao: UserPermissionDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    /**
     * Observes all user permissions from the local database.
     */
    fun observeAll(): Flow<List<UserPermissionEntity>> = userPermissionDao.observeAll()
    
    /**
     * Inserts a user permission and attempts to sync it.
     * @param userPermission The permission to insert.
     * @param token Authentication token (fallback).
     */
    suspend fun insert(userPermission: UserPermissionEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            userPermissionDao.insertWithSync(userPermission, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.createUserPermission(authHeader, userPermission)
                }
            } catch (e: Exception) {
                android.util.Log.w("UserPermissionRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(userPermission.id.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Deletes a user permission and attempts to sync it.
     * @param id The permission ID.
     * @param token Authentication token (fallback).
     */
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete with sync queue support
            userPermissionDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.deleteUserPermission(authHeader, id)
                }
            } catch (e: Exception) {
                android.util.Log.w("UserPermissionRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Finds a user permission by ID.
     */
    suspend fun findById(id: Int): UserPermissionEntity? {
        return userPermissionDao.findById(id)
    }
    
    /**
     * Returns the total count of user permissions.
     */
    suspend fun count(): Int = userPermissionDao.getAll().size
    
    /**
     * Fetches all user permissions from the API.
     * @param token Authentication token.
     */
    suspend fun fetchFromApi(token: String): Result<List<UserPermissionEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getUserPermissions(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch user permissions: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
