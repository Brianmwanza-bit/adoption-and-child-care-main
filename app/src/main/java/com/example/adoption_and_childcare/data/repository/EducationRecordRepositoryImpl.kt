package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.EducationRecordDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for education record data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class EducationRecordRepositoryImpl @Inject constructor(
    private val educationRecordDao: EducationRecordDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<EducationRecordEntity>> = educationRecordDao.observeAll()
    
    suspend fun insert(record: EducationRecordEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            educationRecordDao.insertWithSync(record, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createEducationRecord(authHeader, record)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(record.recordId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(record: EducationRecordEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            educationRecordDao.updateWithSync(record, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateEducationRecord(authHeader, record.recordId, record)
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
            educationRecordDao.deleteByIdWithSync(id, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteEducationRecord(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): EducationRecordEntity? {
        return educationRecordDao.findById(id)
    }
    
    suspend fun count(): Int = educationRecordDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<EducationRecordEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getAllEducationRecords(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch education records: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
