package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.FamilyDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for family data with local database and remote API integration.
 * 
 * This repository manages FamilyEntity objects, providing:
 * - Real-time observation via Flow from local Room database
 * - CRUD operations that sync with the backend API via a sync queue
 * - Offline-first architecture with automatic synchronization
 * 
 * @property familyDao Local database DAO for family operations.
 * @property syncQueueDao DAO for managing the local sync queue.
 * @property apiService Retrofit API service for backend communication.
 * @property authManager Manager for authentication tokens.
 */
@Singleton
class FamilyRepositoryImpl @Inject constructor(
    private val familyDao: FamilyDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) : FamilyRepository {

    override fun observeAll(): Flow<List<FamilyEntity>> {
        return familyDao.observeAll()
    }

    override suspend fun findById(id: Int): FamilyEntity? {
        return familyDao.findById(id)
    }

    override suspend fun insert(entity: FamilyEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            familyDao.insertWithSync(entity, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createFamilyProfile(authHeader, entity)
                }
            } catch (e: Exception) {
                // Ignore failure; sync queue will handle it
            }
            
            Result.success(entity.familyId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun update(entity: FamilyEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            familyDao.updateWithSync(entity, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateFamilyProfile(authHeader, entity.familyId, entity)
                }
            } catch (e: Exception) {
                // Ignore failure
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete with sync queue support
            familyDao.deleteByIdWithSync(id, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteFamilyProfile(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore failure
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchFromApi(token: String): Result<List<FamilyEntity>> {
        return try {
            val authHeader = "Bearer $token"
            val response = apiService.getFamilies(authHeader)
            
            if (response.isSuccessful && response.body() != null) {
                val families = response.body()!!
                
                // Update local database with fetched data
                for (family in families) {
                    val existing = familyDao.findById(family.familyId)
                    if (existing != null) {
                        familyDao.update(family)
                    } else {
                        familyDao.insert(family)
                    }
                }
                
                Result.success(families)
            } else {
                Result.failure(Exception("Failed to fetch families: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun count(): Int {
        return familyDao.count()
    }

    override fun searchByName(query: String): Flow<List<FamilyEntity>> {
        return familyDao.searchByName("%$query%")
    }

    // Simple insert/update/delete without API sync (for offline mode)
    suspend fun insertLocal(family: FamilyEntity): Long {
        return familyDao.insert(family)
    }

    suspend fun updateLocal(family: FamilyEntity) {
        familyDao.update(family)
    }

    suspend fun deleteLocal(id: Int) {
        familyDao.deleteById(id)
    }
}
