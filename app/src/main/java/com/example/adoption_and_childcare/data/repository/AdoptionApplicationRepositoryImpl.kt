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

@Singleton
class AdoptionApplicationRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val applicationDao: AdoptionApplicationDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) : BaseSyncRepository(appContext), AdoptionApplicationRepository {

    override fun observeAll(): Flow<List<AdoptionApplicationEntity>> = applicationDao.observeAll()

    override fun observeForFamily(familyId: Int): Flow<List<AdoptionApplicationEntity>> = 
        applicationDao.observeForFamily(familyId)

    override suspend fun findById(id: Int): AdoptionApplicationEntity? = applicationDao.findById(id)

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
            // Ignore
        }
        
        scheduleSync()
        return application.applicationId.toLong()
    }

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
            // Ignore
        }
        
        scheduleSync()
    }

    override suspend fun delete(id: Int, token: String) {
        deleteById(id)
    }

    override suspend fun count(): Int = applicationDao.count()

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
            // Ignore
        }
        
        scheduleSync()
    }

    override suspend fun fetchFromApi(token: String): Result<List<AdoptionApplicationEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader() ?: "Bearer $token"
            val response = apiService.getAdoptionApplications(authHeader)
            
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val list = body
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
