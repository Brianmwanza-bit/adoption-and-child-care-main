package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.PlacementDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for placement data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class PlacementRepositoryImpl @Inject constructor(
    private val placementDao: PlacementDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<PlacementEntity>> = placementDao.observeAll()
    
    suspend fun insert(placement: PlacementEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            placementDao.insertWithSync(placement, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createPlacement(authHeader, placement)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(placement.placementId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(placement: PlacementEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            placementDao.updateWithSync(placement, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updatePlacement(authHeader, placement.placementId, placement)
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
            placementDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deletePlacement(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchFromApi(token: String): Result<List<PlacementEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getPlacements(authHeader)
            
            if (response.isSuccessful && response.body() != null) {
                val placements = response.body()!!
                for (placement in placements) {
                    val existing = placementDao.findById(placement.placementId)
                    if (existing != null) {
                        placementDao.update(placement)
                    } else {
                        placementDao.insert(placement)
                    }
                }
                Result.success(placements)
            } else {
                Result.failure(Exception("Failed to fetch placements: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun count(): Int {
        return placementDao.count()
    }
}
