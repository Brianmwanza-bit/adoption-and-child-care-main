package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.data.dao.ChildDao
import com.adoptionapp.data.entity.Child
import com.adoptionapp.network.ChildApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChildRepository(
    private val childDao: ChildDao,
    private val childApi: ChildApi,
    private val syncManager: SyncManager
) {
    val allChildren: LiveData<List<Child>> = childDao.getAllChildren()

    suspend fun insert(child: Child) {
        withContext(Dispatchers.IO) {
            childDao.insert(child)
            syncManager.scheduleSyncChildren()
        }
    }

    suspend fun update(child: Child) {
        withContext(Dispatchers.IO) {
            childDao.update(child)
            syncManager.scheduleSyncChildren()
        }
    }

    suspend fun delete(child: Child) {
        withContext(Dispatchers.IO) {
            childDao.delete(child)
            syncManager.scheduleSyncChildren()
        }
    }

    suspend fun syncChildren() {
        withContext(Dispatchers.IO) {
            try {
                val remoteChildren = childApi.getChildren()
                childDao.replaceAll(remoteChildren)
            } catch (e: Exception) {
                // Handle network errors, keep local data
                e.printStackTrace()
            }
        }
    }

    suspend fun getChildById(id: Int): Child? {
        return withContext(Dispatchers.IO) {
            childDao.getChildById(id)
        }
    }
} 