package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.ChildDao
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for child data with local database and remote API integration.
 * 
 * This repository manages ChildEntity objects, providing:
 * - Real-time observation via Flow from local Room database
 * - CRUD operations that sync with the backend API
 * - Offline-first architecture with automatic synchronization
 * 
 * @property childDao Local database DAO for child operations.
 * @property apiService Retrofit API service for backend communication.
 */
@Singleton
class ChildRepositoryImpl @Inject constructor(
    private val childDao: ChildDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) : ChildRepository {

    override fun observeAll(): Flow<List<ChildEntity>> {
        return childDao.observeAll()
    }

    override suspend fun findById(id: Int): ChildEntity? {
        return childDao.findById(id)
    }

    override suspend fun insert(child: ChildEntity, token: String): Result<Long> {
        return try {
            // Insert locally first
            val localId = childDao.insert(child)
            
            // Sync with API in background using AuthManager
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createChild(authHeader, child)
                    if (!response.isSuccessful) {
                        println("Failed to sync child with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for child insert: ${e.message}")
            }
            
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun update(child: ChildEntity, token: String): Result<Unit> {
        return try {
            // Update locally first
            childDao.update(child)
            
            // Sync with API in background using AuthManager
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updateChild(authHeader, child.childId, child)
                    if (!response.isSuccessful) {
                        println("Failed to sync child update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for child update: ${e.message}")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete locally first
            childDao.deleteById(id)
            
            // Sync with API in background using AuthManager
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.deleteChild(authHeader, id)
                    if (!response.isSuccessful) {
                        println("Failed to sync child deletion with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for child deletion: ${e.message}")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchFromApi(token: String): Result<List<ChildEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getChildren(authHeader)
            
            if (response.isSuccessful && response.body() != null) {
                val children = response.body()!!
                
                // Update local database with fetched data
                for (child in children) {
                    val existing = childDao.findById(child.childId)
                    if (existing != null) {
                        childDao.update(child)
                    } else {
                        childDao.insert(child)
                    }
                }
                
                Result.success(children)
            } else {
                Result.failure(Exception("Failed to fetch children: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun count(): Int {
        return childDao.count()
    }

    override fun searchByName(query: String): Flow<List<ChildEntity>> {
        return childDao.searchByName("%$query%")
    }

    // Simple insert/update/delete without API sync (for offline mode)
    suspend fun insertLocal(child: ChildEntity): Long {
        return childDao.insert(child)
    }

    suspend fun updateLocal(child: ChildEntity) {
        childDao.update(child)
    }

    suspend fun deleteLocal(id: Int) {
        childDao.deleteById(id)
    }
}
