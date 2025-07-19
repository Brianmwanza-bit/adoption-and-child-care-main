package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.ChildrenEntity
import com.adoptionapp.data.dao.ChildrenDao
import com.adoptionapp.network.ChildApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChildRepository(
    private val childDao: ChildrenDao,
    private val childApi: ChildApi,
    private val syncManager: SyncManager
) {
    val allChildren: LiveData<List<ChildrenEntity>> = childDao.getAllLive()

    suspend fun insert(child: ChildrenEntity) {
        withContext(Dispatchers.IO) {
            childDao.insert(child)
            syncManager.scheduleSyncChildren()
        }
    }

    suspend fun update(child: ChildrenEntity) {
        withContext(Dispatchers.IO) {
            childDao.update(child)
            syncManager.scheduleSyncChildren()
        }
    }

    suspend fun delete(child: ChildrenEntity) {
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

    suspend fun getChildById(id: Int): ChildrenEntity? {
        return withContext(Dispatchers.IO) {
            childDao.getById(id)
        }
    }
} 