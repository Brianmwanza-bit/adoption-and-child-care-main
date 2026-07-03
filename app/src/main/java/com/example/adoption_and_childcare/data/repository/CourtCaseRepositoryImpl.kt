package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.CourtCaseDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for court case data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class CourtCaseRepositoryImpl @Inject constructor(
    private val courtCaseDao: CourtCaseDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<CourtCaseEntity>> = courtCaseDao.observeAll()
    
    suspend fun insert(courtCase: CourtCaseEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            courtCaseDao.insertWithSync(courtCase, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createCourtCase(authHeader, courtCase)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(courtCase.caseId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(courtCase: CourtCaseEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            courtCaseDao.updateWithSync(courtCase, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateCourtCase(authHeader, courtCase.caseId, courtCase)
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
            courtCaseDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteCourtCase(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): CourtCaseEntity? {
        return courtCaseDao.findById(id)
    }
    suspend fun count(): Int = courtCaseDao.count()
    
    suspend fun fetchFromApi(token: String): Result<List<CourtCaseEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getCourtCases(authHeader)
            if (response.isSuccessful && response.body() != null) {
                val cases = response.body()!!.data
                for (courtCase in cases) {
                    val existing = courtCaseDao.findById(courtCase.caseId)
                    if (existing != null) {
                        courtCaseDao.update(courtCase)
                    } else {
                        courtCaseDao.insert(courtCase)
                    }
                }
                Result.success(cases)
            } else {
                Result.failure(Exception("Failed to fetch court cases: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
