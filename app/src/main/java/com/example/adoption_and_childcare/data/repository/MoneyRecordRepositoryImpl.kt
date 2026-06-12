package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.MoneyRecordDao
import com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for money/finance record data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class MoneyRecordRepositoryImpl @Inject constructor(
    private val moneyRecordDao: MoneyRecordDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<MoneyRecordEntity>> = moneyRecordDao.observeAll()
    
    suspend fun insert(record: MoneyRecordEntity, token: String): Result<Long> {
        return try {
            val localId = moneyRecordDao.insert(record)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createMoneyRecord(authHeader, record)
                    if (!response.isSuccessful) {
                        println("Failed to sync money record with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for money record insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(record: MoneyRecordEntity, token: String): Result<Unit> {
        return try {
            moneyRecordDao.update(record)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updateMoneyRecord(authHeader, record.moneyId, record)
                    if (!response.isSuccessful) {
                        println("Failed to sync money record update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for money record update: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            moneyRecordDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteMoneyRecord(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for money record delete: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): MoneyRecordEntity? {
        return moneyRecordDao.findById(id)
    }
    
    suspend fun count(): Int = moneyRecordDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<MoneyRecordEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getAllMoneyRecords(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch money records: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
