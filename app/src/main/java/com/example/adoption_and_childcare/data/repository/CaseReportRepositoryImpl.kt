package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.CaseReportDao
import com.example.adoption_and_childcare.data.db.entities.CaseReportEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for case report data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class CaseReportRepositoryImpl @Inject constructor(
    private val caseReportDao: CaseReportDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<CaseReportEntity>> = caseReportDao.observeAll()
    
    suspend fun insert(report: CaseReportEntity, token: String): Result<Long> {
        return try {
            val localId = caseReportDao.insert(report)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    // Case reports synced via push/pull sync endpoint
                    println("Case report inserted locally, will sync via push endpoint")
                }
            } catch (e: Exception) {
                println("API sync failed for case report insert: ${e.message}")
            }
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(report: CaseReportEntity, token: String): Result<Unit> {
        return try {
            caseReportDao.update(report)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    println("Case report updated locally, will sync via push endpoint")
                }
            } catch (e: Exception) {
                println("API sync failed for case report update: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            caseReportDao.deleteById(id)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    println("Case report deleted locally, will sync via push endpoint")
                }
            } catch (e: Exception) {
                println("API sync failed for case report delete: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): CaseReportEntity? {
        return caseReportDao.findById(id)
    }
    suspend fun count(): Int = caseReportDao.count()
    
    suspend fun fetchFromApi(token: String): Result<List<CaseReportEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            // Case reports fetched via pull sync endpoint
            println("Case reports will be fetched via pull sync endpoint")
            // TODO: Implement API endpoint
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
