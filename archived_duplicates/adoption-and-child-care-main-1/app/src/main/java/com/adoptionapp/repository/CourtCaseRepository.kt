package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.data.dao.CourtCaseDao
import com.adoptionapp.data.entity.CourtCase
import com.adoptionapp.network.CourtCaseApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourtCaseRepository(
    private val courtCaseDao: CourtCaseDao,
    private val courtCaseApi: CourtCaseApi,
    private val syncManager: SyncManager
) {
    val allCourtCases: LiveData<List<CourtCase>> = courtCaseDao.getAllCourtCases()

    suspend fun insert(courtCase: CourtCase) {
        withContext(Dispatchers.IO) {
            courtCaseDao.insert(courtCase)
            syncManager.scheduleSyncCourtCases()
        }
    }

    suspend fun update(courtCase: CourtCase) {
        withContext(Dispatchers.IO) {
            courtCaseDao.update(courtCase)
            syncManager.scheduleSyncCourtCases()
        }
    }

    suspend fun delete(courtCase: CourtCase) {
        withContext(Dispatchers.IO) {
            courtCaseDao.delete(courtCase)
            syncManager.scheduleSyncCourtCases()
        }
    }

    suspend fun syncCourtCases() {
        withContext(Dispatchers.IO) {
            try {
                val remoteCourtCases = courtCaseApi.getCourtCases()
                courtCaseDao.replaceAll(remoteCourtCases)
            } catch (e: Exception) {
                // Handle network errors, keep local data
                e.printStackTrace()
            }
        }
    }

    suspend fun getCourtCaseById(id: Int): CourtCase? {
        return withContext(Dispatchers.IO) {
            courtCaseDao.getCourtCaseById(id)
        }
    }

    suspend fun getCourtCasesByChildId(childId: Int): List<CourtCase> {
        return withContext(Dispatchers.IO) {
            courtCaseDao.getCourtCasesByChildId(childId)
        }
    }
} 