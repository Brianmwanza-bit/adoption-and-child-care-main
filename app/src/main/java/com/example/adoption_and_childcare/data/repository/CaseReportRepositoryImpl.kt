package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.CaseReportDao
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.entities.CaseReportEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for case report data with API integration.
 * Provides offline-first architecture with background sync via sync queue.
 */
@Singleton
class CaseReportRepositoryImpl @Inject constructor(
    private val caseReportDao: CaseReportDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<CaseReportEntity>> = caseReportDao.observeAll()
    
    suspend fun insert(report: CaseReportEntity, token: String): Result<Long> {
        return try {
            // Insert with sync queue support
            caseReportDao.insertWithSync(report, syncQueueDao)
            
            // Immediate sync attempt (optional)
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.createCaseReport(authHeader, report)
                }
            } catch (e: Exception) {
                // Ignore
            }
            Result.success(report.reportId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(report: CaseReportEntity, token: String): Result<Unit> {
        return try {
            // Update with sync queue support
            caseReportDao.updateWithSync(report, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.updateCaseReport(authHeader, report.reportId, report)
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
            caseReportDao.deleteByIdWithSync(id, syncQueueDao)
            
            try {
                val authHeader = authManager.getAuthHeader()
                if (authHeader != null) {
                    apiService.deleteCaseReport(authHeader, id)
                }
            } catch (e: Exception) {
                // Ignore
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
            
            val response = apiService.getCaseReports(authHeader)
            if (response.isSuccessful && response.body() != null) {
                val reports = response.body()!!.data
                for (report in reports) {
                    val existing = caseReportDao.findById(report.reportId)
                    if (existing != null) {
                        caseReportDao.update(report)
                    } else {
                        caseReportDao.insert(report)
                    }
                }
                Result.success(reports)
            } else {
                Result.failure(Exception("Failed to fetch case reports: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
