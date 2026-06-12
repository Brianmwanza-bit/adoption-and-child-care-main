package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.UserDao
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.network.AuthResponse
import com.example.adoption_and_childcare.network.LoginRequest
import com.example.adoption_and_childcare.network.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the UserRepository interface.
 * 
 * This class provides concrete implementations for user data operations,
 * using a local DAO for database access and a remote API service
 * for server communication.
 * 
 * @property userDao DAO for local user database operations.
 * @property apiService Retrofit API service for remote user operations.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService
) : UserRepository {
    override suspend fun findByEmail(email: String): UserEntity? = userDao.findByEmail(email)
    override suspend fun findByUsername(username: String): UserEntity? = userDao.findByUsername(username)
    override suspend fun insert(user: UserEntity) { userDao.insert(user) }

    override suspend fun loginRemote(request: LoginRequest): AuthResponse? {
        val response = apiService.login(request)
        if (response.isSuccessful) {
            val body = response.body()
            body?.user?.let { userDao.insert(it) }
            return body
        }
        return null
    }

    override suspend fun registerRemote(request: RegisterRequest): AuthResponse? {
        val response = apiService.register(request)
        if (response.isSuccessful) {
            val body = response.body()
            body?.user?.let { userDao.insert(it) }
            return body
        }
        return null
    }
}

