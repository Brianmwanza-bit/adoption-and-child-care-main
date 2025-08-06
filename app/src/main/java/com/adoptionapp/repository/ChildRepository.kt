package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.ChildrenEntity
import com.adoptionapp.data.dao.ChildrenDao
import com.adoptionapp.network.ChildApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.adoptionapp.RetrofitClient

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
                // Propagate error to ViewModel
                throw e
            }
        }
    }

    suspend fun getChildById(id: Int): ChildrenEntity? {
        return withContext(Dispatchers.IO) {
            childDao.getById(id)
        }
    }

    suspend fun insertWithPhoto(child: ChildrenEntity, photo: ByteArray?) {
        withContext(Dispatchers.IO) {
            childDao.insert(child)
            if (photo != null) {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), photo)
                val body = MultipartBody.Part.createFormData("photo", "photo.png", requestFile)
                try {
                    val token = "Bearer " + (/* get token from storage or context */ "")
                    RetrofitClient.apiService.uploadChildPhoto(token, child.child_id, body)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            syncManager.scheduleSyncChildren()
        }
    }
} 