package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.MedicalRecordDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for medical record data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class MedicalRecordRepositoryImpl @Inject constructor(
    private val medicalRecordDao: MedicalRecordDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    /**
     * Observes all medical records from the local database.
     */
    fun observeAll(): Flow<List<MedicalRecordEntity>> = medicalRecordDao.observeAll()
    
    /**
     * Inserts a medical record and attempts to sync it.
     * @param record The record to insert.
     * @param token Authentication token (fallback).
     */
    suspend fun insert(record: MedicalRecordEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            medicalRecordDao.insertWithSync(record, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.createMedicalRecord(authHeader, record)
                }
            } catch (e: Exception) {
                android.util.Log.w("MedicalRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(record.recordId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates a medical record and attempts to sync it.
     * @param record The record to update.
     * @param token Authentication token (fallback).
     */
    suspend fun update(record: MedicalRecordEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            medicalRecordDao.updateWithSync(record, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.updateMedicalRecord(authHeader, record.recordId, record)
                }
            } catch (e: Exception) {
                android.util.Log.w("MedicalRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Deletes a medical record and attempts to sync it.
     * @param id The record ID.
     * @param token Authentication token (fallback).
     */
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete with sync queue support
            medicalRecordDao.deleteByIdWithSync(id, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.deleteMedicalRecord(authHeader, id)
                }
            } catch (e: Exception) {
                android.util.Log.w("MedicalRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Finds a medical record by ID.
     */
    suspend fun findById(id: Int): MedicalRecordEntity? {
        return medicalRecordDao.findById(id)
    }
    
    /**
     * Returns the total count of medical records.
     */
    suspend fun count(): Int = medicalRecordDao.getAll().size
    
    /**
     * Fetches all medical records from the API.
     * @param token Authentication token.
     */
    suspend fun fetchFromApi(token: String): Result<List<MedicalRecordEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getAllMedicalRecords(authHeader)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception("Failed to fetch medical records: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
