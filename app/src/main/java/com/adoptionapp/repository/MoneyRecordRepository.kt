package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.data.dao.MoneyRecordDao
import com.adoptionapp.data.entity.MoneyRecord
import com.adoptionapp.network.MoneyRecordApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoneyRecordRepository(
    private val moneyRecordDao: MoneyRecordDao,
    private val moneyRecordApi: MoneyRecordApi,
    private val syncManager: SyncManager
) {
    val allMoneyRecords: LiveData<List<MoneyRecord>> = moneyRecordDao.getAllMoneyRecords()

    suspend fun insert(moneyRecord: MoneyRecord) {
        withContext(Dispatchers.IO) {
            moneyRecordDao.insert(moneyRecord)
            syncManager.scheduleSyncMoneyRecords()
        }
    }

    suspend fun update(moneyRecord: MoneyRecord) {
        withContext(Dispatchers.IO) {
            moneyRecordDao.update(moneyRecord)
            syncManager.scheduleSyncMoneyRecords()
        }
    }

    suspend fun delete(moneyRecord: MoneyRecord) {
        withContext(Dispatchers.IO) {
            moneyRecordDao.delete(moneyRecord)
            syncManager.scheduleSyncMoneyRecords()
        }
    }

    suspend fun syncMoneyRecords() {
        withContext(Dispatchers.IO) {
            try {
                val remoteMoneyRecords = moneyRecordApi.getMoneyRecords()
                moneyRecordDao.replaceAll(remoteMoneyRecords)
            } catch (e: Exception) {
                // Handle network errors, keep local data
                e.printStackTrace()
            }
        }
    }

    suspend fun getMoneyRecordById(id: Int): MoneyRecord? {
        return withContext(Dispatchers.IO) {
            moneyRecordDao.getMoneyRecordById(id)
        }
    }

    suspend fun getMoneyRecordsByChildId(childId: Int): List<MoneyRecord> {
        return withContext(Dispatchers.IO) {
            moneyRecordDao.getMoneyRecordsByChildId(childId)
        }
    }
} 