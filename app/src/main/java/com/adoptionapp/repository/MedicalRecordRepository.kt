package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.data.dao.MedicalRecordDao
import com.adoptionapp.data.entity.MedicalRecord
import com.adoptionapp.network.MedicalRecordApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MedicalRecordRepository(
    private val medicalRecordDao: MedicalRecordDao,
    private val medicalRecordApi: MedicalRecordApi,
    private val syncManager: SyncManager
) {
    val allMedicalRecords: LiveData<List<MedicalRecord>> = medicalRecordDao.getAllMedicalRecords()

    suspend fun insert(medicalRecord: MedicalRecord) {
        withContext(Dispatchers.IO) {
            medicalRecordDao.insert(medicalRecord)
            syncManager.scheduleSyncMedicalRecords()
        }
    }

    suspend fun update(medicalRecord: MedicalRecord) {
        withContext(Dispatchers.IO) {
            medicalRecordDao.update(medicalRecord)
            syncManager.scheduleSyncMedicalRecords()
        }
    }

    suspend fun delete(medicalRecord: MedicalRecord) {
        withContext(Dispatchers.IO) {
            medicalRecordDao.delete(medicalRecord)
            syncManager.scheduleSyncMedicalRecords()
        }
    }

    suspend fun syncMedicalRecords() {
        withContext(Dispatchers.IO) {
            try {
                val remoteMedicalRecords = medicalRecordApi.getMedicalRecords()
                medicalRecordDao.replaceAll(remoteMedicalRecords)
            } catch (e: Exception) {
                // Handle network errors, keep local data
                e.printStackTrace()
            }
        }
    }

    suspend fun getMedicalRecordById(id: Int): MedicalRecord? {
        return withContext(Dispatchers.IO) {
            medicalRecordDao.getMedicalRecordById(id)
        }
    }

    suspend fun getMedicalRecordsByChildId(childId: Int): List<MedicalRecord> {
        return withContext(Dispatchers.IO) {
            medicalRecordDao.getMedicalRecordsByChildId(childId)
        }
    }
} 