package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.data.dao.UserDao
import com.adoptionapp.data.entity.User
import com.adoptionapp.network.UserApi
import com.adoptionapp.sync.SyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context
import com.adoptionapp.TokenManager
import com.adoptionapp.ApiService
import retrofit2.Response
import com.adoptionapp.data.model.LoginRequest
import com.adoptionapp.data.model.LoginResponse
import com.adoptionapp.data.model.RegisterRequest
import com.adoptionapp.data.model.RegisterResponse

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
                // Propagate error to ViewModel
                throw e
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

    suspend fun login(context: Context, username: String, password: String): Response<LoginResponse> {
        return withContext(Dispatchers.IO) {
            val response = (userApi as? ApiService)?.login(LoginRequest(username, password))
            if (response != null && response.isSuccessful) {
                val token = response.body()?.token
                if (!token.isNullOrEmpty()) {
                    TokenManager.saveToken(context, token)
                }
            }
            response!!
        }
    }

    suspend fun register(context: Context, username: String, password: String, email: String, role: String): Response<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            val response = (userApi as? ApiService)?.register(RegisterRequest(username, password, email, role))
            response!!
        }
    }
} 