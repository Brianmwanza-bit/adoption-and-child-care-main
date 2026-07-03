package com.example.adoption_and_childcare.data.repository

import android.content.Context
import com.example.adoption_and_childcare.data.db.dao.AdoptionApplicationDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AdoptionApplicationRepository that handles data sync between local DB and remote API.
 */
@Singleton
class AdoptionApplicationRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val applicationDao: AdoptionApplicationDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) : BaseSyncRepository(appContext), AdoptionApplicationRepository {

    /**
     * Observes all adoption applications from the local database.
     */
    override fun observeAll(): Flow<List<AdoptionApplicationEntity>> = applicationDao.observeAll()

    /**
     * Observes adoption applications for a specific family.
     */
    override fun observeForFamily(familyId: Int): Flow<List<AdoptionApplicationEntity>> = 
        applicationDao.observeForFamily(familyId)

    /**
     * Finds an adoption application by its local ID.
     */
    override suspend fun findById(id: Int): AdoptionApplicationEntity? = applicationDao.findById(id)

    /**
     * Inserts a new adoption application and attempts to sync it with the remote API.
     */
    override suspend fun insert(application: AdoptionApplicationEntity, token: String): Long {
        // Insert with sync queue support
        applicationDao.insertWithSync(application, syncQueueDao)
        
        // Immediate background sync attempt
        try {
            val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
            if (authHeader != null) {
                apiService.createAdoptionApplication(authHeader, application)
            }
        } catch (e: Exception) {
            android.util.Log.e("AdoptionRepo", "Failed to sync insert: ${e.message}")
        }
        
        scheduleSync()
        return application.applicationId.toLong()
    }

    /**
     * Updates an existing adoption application and attempts to sync it with the remote API.
     */
    override suspend fun update(application: AdoptionApplicationEntity, token: String) {
        // Update with sync queue support
        applicationDao.updateWithSync(application, syncQueueDao)
        
        // Immediate background sync attempt
        try {
            val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
            if (authHeader != null) {
                apiService.updateAdoptionApplication(authHeader, application.applicationId, application) 
            }
        } catch (e: Exception) {
            android.util.Log.e("AdoptionRepo", "Failed to sync update: ${e.message}")
        }
        
        scheduleSync()
    }

    /**
     * Deletes an adoption application by its local ID.
     */
    override suspend fun delete(id: Int, token: String) {
        deleteById(id)
    }

    /**
     * Returns the total count of adoption applications in the local database.
     */
    override suspend fun count(): Int = applicationDao.count()

    /**
     * Internal method to handle deletion with sync logic.
     */
    override suspend fun deleteById(id: Int) {
        // Delete with sync queue support
        applicationDao.deleteByIdWithSync(id, syncQueueDao)
        
        // Immediate background sync attempt
        try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader != null) {
                apiService.deleteAdoptionApplication(authHeader, id)
            }
        } catch (e: Exception) {
            android.util.Log.e("AdoptionRepo", "Failed to sync delete: ${e.message}")
        }
        
        scheduleSync()
    }

    /**
     * Fetches all adoption applications from the remote API and updates the local database.
     */
    override suspend fun fetchFromApi(token: String): Result<List<AdoptionApplicationEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
            if (authHeader == null) return Result.failure(Exception("No auth token"))

            val response = apiService.getAdoptionApplications(authHeader)
            
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val list = body.data
                for (item in list) {
                    val existing = applicationDao.findById(item.applicationId)
                    if (existing != null) {
                        applicationDao.update(item)
                    } else {
                        applicationDao.insert(item)
                    }
                }
                Result.success(list)
            } else {
                Result.failure(Exception("Failed to fetch: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
