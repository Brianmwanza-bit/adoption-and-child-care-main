package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.MedicalRecordDao
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for medical record data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class MedicalRecordRepositoryImpl @Inject constructor(
    private val medicalRecordDao: MedicalRecordDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<MedicalRecordEntity>> = medicalRecordDao.observeAll()
    
    suspend fun insert(record: MedicalRecordEntity, token: String): Result<Long> {
        return try {
            val localId = medicalRecordDao.insert(record)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createMedicalRecord(authHeader, record)
                    if (!response.isSuccessful) {
                        println("Failed to sync medical record with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for medical record insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(record: MedicalRecordEntity, token: String): Result<Unit> {
        return try {
            medicalRecordDao.update(record)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updateMedicalRecord(authHeader, record.recordId, record)
                    if (!response.isSuccessful) {
                        println("Failed to sync medical record update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for medical record update: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            medicalRecordDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteMedicalRecord(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for medical record delete: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): MedicalRecordEntity? {
        return medicalRecordDao.findById(id)
    }
    
    suspend fun count(): Int = medicalRecordDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<MedicalRecordEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getAllMedicalRecords(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch medical records: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
