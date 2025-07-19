package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.data.dao.CaseReportDao
import com.adoptionapp.data.entity.CaseReport
import com.adoptionapp.network.CaseReportApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CaseReportRepository(
    private val caseReportDao: CaseReportDao,
    private val caseReportApi: CaseReportApi,
    private val syncManager: SyncManager
) {
    val allCaseReports: LiveData<List<CaseReport>> = caseReportDao.getAllCaseReports()

    suspend fun insert(caseReport: CaseReport) {
        withContext(Dispatchers.IO) {
            caseReportDao.insert(caseReport)
            syncManager.scheduleSyncCaseReports()
        }
    }

    suspend fun update(caseReport: CaseReport) {
        withContext(Dispatchers.IO) {
            caseReportDao.update(caseReport)
            syncManager.scheduleSyncCaseReports()
        }
    }

    suspend fun delete(caseReport: CaseReport) {
        withContext(Dispatchers.IO) {
            caseReportDao.delete(caseReport)
            syncManager.scheduleSyncCaseReports()
        }
    }

    suspend fun syncCaseReports() {
        withContext(Dispatchers.IO) {
            try {
                val remoteCaseReports = caseReportApi.getCaseReports()
                caseReportDao.replaceAll(remoteCaseReports)
            } catch (e: Exception) {
                // Handle network errors, keep local data
                e.printStackTrace()
            }
        }
    }

    suspend fun getCaseReportById(id: Int): CaseReport? {
        return withContext(Dispatchers.IO) {
            caseReportDao.getCaseReportById(id)
        }
    }

    suspend fun getCaseReportsByChildId(childId: Int): List<CaseReport> {
        return withContext(Dispatchers.IO) {
            caseReportDao.getCaseReportsByChildId(childId)
        }
    }
} 