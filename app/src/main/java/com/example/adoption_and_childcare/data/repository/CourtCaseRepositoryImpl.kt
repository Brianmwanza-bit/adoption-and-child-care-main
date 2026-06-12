package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.CourtCaseDao
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for court case data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class CourtCaseRepositoryImpl @Inject constructor(
    private val courtCaseDao: CourtCaseDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<CourtCaseEntity>> = courtCaseDao.observeAll()
    
    suspend fun insert(courtCase: CourtCaseEntity, token: String): Result<Long> {
        return try {
            val localId = courtCaseDao.insert(courtCase)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.createCourtCase(authHeader, courtCase)
                    if (!response.isSuccessful) {
                        println("Failed to sync court case with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for court case insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(courtCase: CourtCaseEntity, token: String): Result<Unit> {
        return try {
            courtCaseDao.update(courtCase)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    val response = apiService.updateCourtCase(authHeader, courtCase.caseId, courtCase)
                    if (!response.isSuccessful) {
                        println("Failed to sync court case update with API: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                println("API sync failed for court case update: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            courtCaseDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteCourtCase(authHeader, id)
                }
            } catch (e: Exception) {
                println("API sync failed for court case delete: ${e.message}")
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
            
            // TODO: Implement API endpoint for court cases
            // val response = apiService.getAllCourtCases(authHeader)
            Result.failure(Exception("API endpoint not implemented yet"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
