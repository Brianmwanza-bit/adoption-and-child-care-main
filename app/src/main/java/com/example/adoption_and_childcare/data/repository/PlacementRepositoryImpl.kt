package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.PlacementDao
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for placement data with local database and remote API integration.
 */
@Singleton
class PlacementRepositoryImpl @Inject constructor(
    private val placementDao: PlacementDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {

    fun observeAll(): Flow<List<PlacementEntity>> {
        return placementDao.observeAll()
    }

    suspend fun findById(id: Int): PlacementEntity? {
        return placementDao.findById(id)
    }

    suspend fun insert(entity: PlacementEntity, token: String): Result<Long> {
        return try {
            val localId = placementDao.insert(entity)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createPlacement(authHeader, entity)
                    if (!response.isSuccessful) {
                        println("Failed to sync placement with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for placement insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun update(entity: PlacementEntity, token: String): Result<Unit> {
        return try {
            placementDao.update(entity)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updatePlacement(authHeader, entity.placementId, entity)
                    if (!response.isSuccessful) {
                        println("Failed to sync placement update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for placement update: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            placementDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.deletePlacement(authHeader, id)
                    if (!response.isSuccessful) {
                        println("Failed to sync placement deletion with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for placement deletion: ${e.message}")
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
