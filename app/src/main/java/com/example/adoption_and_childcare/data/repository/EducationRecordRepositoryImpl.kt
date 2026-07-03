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
    /**
     * Observes all education records from the local database.
     */
    fun observeAll(): Flow<List<EducationRecordEntity>> = educationRecordDao.observeAll()
    
    /**
     * Inserts an education record and attempts to sync it.
     * @param record The record to insert.
     * @param token Authentication token (fallback).
     */
    suspend fun insert(record: EducationRecordEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            educationRecordDao.insertWithSync(record, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.createEducationRecord(authHeader, record)
                }
            } catch (e: Exception) {
                android.util.Log.w("EducationRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(record.recordId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates an education record and attempts to sync it.
     * @param record The record to update.
     * @param token Authentication token (fallback).
     */
    suspend fun update(record: EducationRecordEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            educationRecordDao.updateWithSync(record, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.updateEducationRecord(authHeader, record.recordId, record)
                }
            } catch (e: Exception) {
                android.util.Log.w("EducationRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Deletes an education record and attempts to sync it.
     * @param id The record ID.
     * @param token Authentication token (fallback).
     */
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete with sync queue support
            educationRecordDao.deleteByIdWithSync(id, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.deleteEducationRecord(authHeader, id)
                }
            } catch (e: Exception) {
                android.util.Log.w("EducationRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Finds an education record by ID.
     */
    suspend fun findById(id: Int): EducationRecordEntity? {
        return educationRecordDao.findById(id)
    }
    
    /**
     * Returns the total count of education records.
     */
    suspend fun count(): Int = educationRecordDao.getAll().size
    
    /**
     * Fetches all education records from the API.
     * @param token Authentication token.
     */
    suspend fun fetchFromApi(token: String): Result<List<EducationRecordEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getAllEducationRecords(authHeader)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception("Failed to fetch education records: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
