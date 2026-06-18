package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.UserDao
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.network.LoginRequest
import com.example.adoption_and_childcare.network.RegisterRequest
import com.example.adoption_and_childcare.utils.AuthManager
import com.example.adoption_and_childcare.utils.Security
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the UserRepository interface.
 * 
 * This class provides concrete implementations for user data operations,
 * integrating local database access with remote API authentication.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val authManager: AuthManager
) : UserRepository {
    override suspend fun findByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.findByEmail(email)
    }

    override suspend fun findByUsername(username: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.findByUsername(username)
    }

    override suspend fun insert(user: UserEntity): Unit {
        withContext(Dispatchers.IO) {
            userDao.insert(user)
        }
    }

    override suspend fun update(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.update(user)
    }

    override suspend fun deleteById(userId: Int) = withContext(Dispatchers.IO) {
        userDao.deleteById(userId)
    }

    override suspend fun register(user: UserEntity, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            // Check if user already exists locally
            val existingUser = userDao.findByEmail(user.email ?: "") ?: userDao.findByUsername(user.username)
            if (existingUser != null) {
                return@withContext Result.failure(Exception("User already exists"))
            }

            // Hash password and save locally
            val userWithHash = user.copy(passwordHash = Security.hashPassword(password))
            
            // Save locally and get generated ID
            val savedId = userDao.insert(userWithHash)
            val userWithId = userWithHash.copy(userId = savedId.toInt())
            
            // Save session and a dummy local token
            sessionManager.saveSession(userWithId)
            sessionManager.saveAuthToken("local_token_${userWithId.userId}")
            
            Result.success(userWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(username: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            // Authenticate locally using Room DB
            val localUser = userDao.findByUsername(username) ?: userDao.findByEmail(username)
            val hashedPassword = Security.hashPassword(password)
            
            if (localUser != null && localUser.passwordHash == hashedPassword) {
                sessionManager.saveSession(localUser)
                // Generate a dummy local token
                sessionManager.saveAuthToken("local_token_${localUser.userId}")
                Result.success(localUser)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchUsersFromApi(): Result<List<UserEntity>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val response = apiService.getUsers(authHeader)
            if (response.isSuccessful) {
                val users = response.body() ?: emptyList()
                users.forEach { user ->
                    userDao.insert(user)
                }
                Result.success(users)
            } else {
                Result.failure(Exception("Failed to fetch users: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
