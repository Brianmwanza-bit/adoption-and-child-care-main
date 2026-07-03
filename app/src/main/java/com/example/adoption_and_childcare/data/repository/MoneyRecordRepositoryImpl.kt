package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.MoneyRecordDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for money/finance record data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class MoneyRecordRepositoryImpl @Inject constructor(
    private val moneyRecordDao: MoneyRecordDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    /**
     * Observes all money records from the local database.
     */
    fun observeAll(): Flow<List<MoneyRecordEntity>> = moneyRecordDao.observeAll()
    
    /**
     * Inserts a money record and attempts to sync it.
     * @param record The record to insert.
     * @param token Authentication token (fallback).
     */
    suspend fun insert(record: MoneyRecordEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            moneyRecordDao.insertWithSync(record, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.createMoneyRecord(authHeader, record)
                }
            } catch (e: Exception) {
                android.util.Log.w("MoneyRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(record.moneyId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Updates a money record and attempts to sync it.
     * @param record The record to update.
     * @param token Authentication token (fallback).
     */
    suspend fun update(record: MoneyRecordEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            moneyRecordDao.updateWithSync(record, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.updateMoneyRecord(authHeader, record.moneyId, record)
                }
            } catch (e: Exception) {
                android.util.Log.w("MoneyRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Deletes a money record and attempts to sync it.
     * @param id The record ID.
     * @param token Authentication token (fallback).
     */
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            // Delete with sync queue support
            moneyRecordDao.deleteByIdWithSync(id, syncQueueDao)
            
            // Immediate sync attempt
            try {
                val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
                if (authHeader != null) {
                    apiService.deleteMoneyRecord(authHeader, id)
                }
            } catch (e: Exception) {
                android.util.Log.w("MoneyRepo", "Immediate sync failed: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Finds a money record by ID.
     */
    suspend fun findById(id: Int): MoneyRecordEntity? {
        return moneyRecordDao.findById(id)
    }
    
    /**
     * Returns the total count of money records.
     */
    suspend fun count(): Int = moneyRecordDao.getAll().size
    
    /**
     * Fetches all money records from the API.
     * @param token Authentication token.
     */
    suspend fun fetchFromApi(token: String): Result<List<MoneyRecordEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader() ?: if (token.isNotEmpty()) "Bearer $token" else null
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getAllMoneyRecords(authHeader)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception("Failed to fetch money records: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
