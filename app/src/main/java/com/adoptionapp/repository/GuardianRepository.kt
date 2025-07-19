package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.data.dao.GuardianDao
import com.adoptionapp.data.entity.Guardian
import com.adoptionapp.network.GuardianApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GuardianRepository(
    private val guardianDao: GuardianDao,
    private val guardianApi: GuardianApi,
    private val syncManager: SyncManager
) {
    val allGuardians: LiveData<List<Guardian>> = guardianDao.getAllGuardians()

    suspend fun insert(guardian: Guardian) {
        withContext(Dispatchers.IO) {
            guardianDao.insert(guardian)
            syncManager.scheduleSyncGuardians()
        }
    }

    suspend fun update(guardian: Guardian) {
        withContext(Dispatchers.IO) {
            guardianDao.update(guardian)
            syncManager.scheduleSyncGuardians()
        }
    }

    suspend fun delete(guardian: Guardian) {
        withContext(Dispatchers.IO) {
            guardianDao.delete(guardian)
            syncManager.scheduleSyncGuardians()
        }
    }

    suspend fun syncGuardians() {
        withContext(Dispatchers.IO) {
            try {
                val remoteGuardians = guardianApi.getGuardians()
                guardianDao.replaceAll(remoteGuardians)
            } catch (e: Exception) {
                // Handle network errors, keep local data
                e.printStackTrace()
            }
        }
    }

    suspend fun getGuardianById(id: Int): Guardian? {
        return withContext(Dispatchers.IO) {
            guardianDao.getGuardianById(id)
        }
    }
} 