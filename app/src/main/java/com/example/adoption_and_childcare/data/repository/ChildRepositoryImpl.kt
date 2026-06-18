package com.example.adoption_and_childcare.data.repository

import android.content.Context
import com.example.adoption_and_childcare.data.db.dao.ChildDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for child data with local database and remote API integration.
 * 
 * This repository manages ChildEntity objects, providing:
 * - Real-time observation via Flow from local Room database
 * - CRUD operations that sync with the backend API via a sync queue
 * - Offline-first architecture with automatic synchronization
 * 
 * @property childDao Local database DAO for child operations.
 * @property syncQueueDao DAO for managing the local sync queue.
 * @property apiService Retrofit API service for backend communication.
 * @property authManager Manager for authentication tokens.
 */
@Singleton
class ChildRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val childDao: ChildDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) : BaseSyncRepository(context), ChildRepository {

    override fun observeAll(): Flow<List<ChildEntity>> {
        return childDao.observeAll()
    }

    override suspend fun findById(id: Int): ChildEntity? {
        return childDao.findById(id)
    }

    override suspend fun insert(child: ChildEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            childDao.insertWithSync(child, syncQueueDao)
            
            // Immediate sync attempt (optional, SyncWorker will also handle it)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createChild(authHeader, child)
                }
            } catch (e: Exception) {
                // Ignore failure here; the record is in the sync queue
            }
            
            scheduleSync()
            Result.success(child.childId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun update(child: ChildEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            childDao.updateWithSync(child, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateChild(authHeader, child.childId, child)
                }
            } catch (e: Exception) {
                // Ignore failure
            }
            
            scheduleSync()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete with sync queue support
            childDao.deleteByIdWithSync(id, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteChild(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore failure
            }
            
            scheduleSync()
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
            
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val children = body
                
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
