package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.data.dao.EducationRecordDao
import com.adoptionapp.data.entity.EducationRecord
import com.adoptionapp.network.EducationRecordApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EducationRecordRepository(
    private val educationRecordDao: EducationRecordDao,
    private val educationRecordApi: EducationRecordApi,
    private val syncManager: SyncManager
) {
    val allEducationRecords: LiveData<List<EducationRecord>> = educationRecordDao.getAllEducationRecords()

    suspend fun insert(educationRecord: EducationRecord) {
        withContext(Dispatchers.IO) {
            educationRecordDao.insert(educationRecord)
            syncManager.scheduleSyncEducationRecords()
        }
    }

    suspend fun update(educationRecord: EducationRecord) {
        withContext(Dispatchers.IO) {
            educationRecordDao.update(educationRecord)
            syncManager.scheduleSyncEducationRecords()
        }
    }

    suspend fun delete(educationRecord: EducationRecord) {
        withContext(Dispatchers.IO) {
            educationRecordDao.delete(educationRecord)
            syncManager.scheduleSyncEducationRecords()
        }
    }

    suspend fun syncEducationRecords() {
        withContext(Dispatchers.IO) {
            try {
                val remoteEducationRecords = educationRecordApi.getEducationRecords()
                educationRecordDao.replaceAll(remoteEducationRecords)
            } catch (e: Exception) {
                // Handle network errors, keep local data
                e.printStackTrace()
            }
        }
    }

    suspend fun getEducationRecordById(id: Int): EducationRecord? {
        return withContext(Dispatchers.IO) {
            educationRecordDao.getEducationRecordById(id)
        }
    }

    suspend fun getEducationRecordsByChildId(childId: Int): List<EducationRecord> {
        return withContext(Dispatchers.IO) {
            educationRecordDao.getEducationRecordsByChildId(childId)
        }
    }
} 