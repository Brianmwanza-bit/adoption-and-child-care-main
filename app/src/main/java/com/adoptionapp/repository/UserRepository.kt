package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.data.dao.UserDao
import com.adoptionapp.data.entity.User
import com.adoptionapp.network.UserApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val userApi: UserApi,
    private val syncManager: SyncManager
) {
    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) {
        withContext(Dispatchers.IO) {
            userDao.insert(user)
            syncManager.scheduleSyncUsers()
        }
    }

    suspend fun update(user: User) {
        withContext(Dispatchers.IO) {
            userDao.update(user)
            syncManager.scheduleSyncUsers()
        }
    }

    suspend fun delete(user: User) {
        withContext(Dispatchers.IO) {
            userDao.delete(user)
            syncManager.scheduleSyncUsers()
        }
    }

    suspend fun syncUsers() {
        withContext(Dispatchers.IO) {
            try {
                val remoteUsers = userApi.getUsers()
                userDao.replaceAll(remoteUsers)
            } catch (e: Exception) {
                // Handle network errors, keep local data
                e.printStackTrace()
            }
        }
    }

    suspend fun getUserById(id: Int): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(id)
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }
    }
} 