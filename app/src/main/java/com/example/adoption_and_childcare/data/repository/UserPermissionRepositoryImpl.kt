package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.UserPermissionDao
import com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for user permission data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class UserPermissionRepositoryImpl @Inject constructor(
    private val userPermissionDao: UserPermissionDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<UserPermissionEntity>> = userPermissionDao.observeAll()
    
    suspend fun insert(userPermission: UserPermissionEntity, token: String): Result<Long> {
        return try {
            val localId = userPermissionDao.insert(userPermission)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createUserPermission(authHeader, userPermission)
                    if (!response.isSuccessful) {
                        println("Failed to sync user permission with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for user permission insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            userPermissionDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteUserPermission(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for user permission delete: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): UserPermissionEntity? {
        return userPermissionDao.findById(id)
    }
    
    suspend fun count(): Int = userPermissionDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<UserPermissionEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getUserPermissions(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user permissions: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
