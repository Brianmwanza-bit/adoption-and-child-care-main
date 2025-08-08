package com.yourdomain.adoptionchildcare.repository

import com.yourdomain.adoptionchildcare.UsersDao
import com.yourdomain.adoptionchildcare.UsersEntity
import com.yourdomain.adoptionchildcare.RetrofitClient
import com.yourdomain.adoptionchildcare.ErrorHandler
import com.yourdomain.adoptionchildcare.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository for user management operations
 * Implements repository pattern with local database and remote API
 */
class UserRepository(
    private val userDao: UsersDao,
    private val apiService: com.yourdomain.adoptionchildcare.ApiService = RetrofitClient.apiService
) {
    
    /**
     * Get all users from local database
     */
    suspend fun getAllUsers(): List<UsersEntity> = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("SELECT", "users")
            userDao.getAllUsers()
        } catch (e: Exception) {
            Logger.logError("UserRepository", e, "Failed to get all users from database")
            emptyList()
        }
    }

    /**
     * Get user by ID from local database
     */
    suspend fun getUserById(id: Int): UsersEntity? = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("SELECT", "users", id.toString())
            userDao.getUserById(id)
        } catch (e: Exception) {
            Logger.logError("UserRepository", e, "Failed to get user by ID: $id")
            null
        }
    }

    /**
     * Insert user into local database
     */
    suspend fun insertUser(user: UsersEntity): Long = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("INSERT", "users", user.id.toString())
            userDao.insertUser(user)
        } catch (e: Exception) {
            Logger.logError("UserRepository", e, "Failed to insert user: ${user.id}")
            -1
        }
    }

    /**
     * Update user in local database
     */
    suspend fun updateUser(user: UsersEntity): Int = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("UPDATE", "users", user.id.toString())
            userDao.updateUser(user)
        } catch (e: Exception) {
            Logger.logError("UserRepository", e, "Failed to update user: ${user.id}")
            0
        }
    }

    /**
     * Delete user from local database
     */
    suspend fun deleteUser(user: UsersEntity): Int = withContext(Dispatchers.IO) {
        try {
            Logger.logDatabaseOperation("DELETE", "users", user.id.toString())
            userDao.deleteUser(user)
        } catch (e: Exception) {
            Logger.logError("UserRepository", e, "Failed to delete user: ${user.id}")
            0
        }
    }

    /**
     * Sync users with remote API
     */
    suspend fun syncUsers(token: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Logger.logSync("SYNC", "users", true)
            val users = apiService.getUsers("Bearer $token")
            users.forEach { user ->
                insertUser(user)
            }
            Logger.logSync("SYNC", "users", true, "Synced ${users.size} users")
            true
        } catch (e: Exception) {
            Logger.logSync("SYNC", "users", false, e.message)
            ErrorHandler.handleException(e)
            false
        }
    }

    /**
     * Create user via API
     */
    suspend fun createUserRemote(token: String, user: UsersEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            Logger.logApiRequest("POST", "/users")
            val response = apiService.createUser("Bearer $token", user)
            if (response.isSuccessful) {
                insertUser(user)
                Logger.logApiResponse("/users", response.code(), 0)
                true
            } else {
                Logger.logApiResponse("/users", response.code(), 0)
                false
            }
        } catch (e: Exception) {
            Logger.logError("UserRepository", e, "Failed to create user remotely")
            ErrorHandler.handleException(e)
            false
        }
    }

    /**
     * Update user via API
     */
    suspend fun updateUserRemote(token: String, user: UsersEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            Logger.logApiRequest("PUT", "/users/${user.id}")
            val response = apiService.updateUser("Bearer $token", user.id, user)
            if (response.isSuccessful) {
                updateUser(user)
                Logger.logApiResponse("/users/${user.id}", response.code(), 0)
                true
            } else {
                Logger.logApiResponse("/users/${user.id}", response.code(), 0)
                false
            }
        } catch (e: Exception) {
            Logger.logError("UserRepository", e, "Failed to update user remotely")
            ErrorHandler.handleException(e)
            false
        }
    }

    /**
     * Delete user via API
     */
    suspend fun deleteUserRemote(token: String, userId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            Logger.logApiRequest("DELETE", "/users/$userId")
            val response = apiService.deleteUser("Bearer $token", userId)
            if (response.isSuccessful) {
                val user = getUserById(userId)
                user?.let { deleteUser(it) }
                Logger.logApiResponse("/users/$userId", response.code(), 0)
                true
            } else {
                Logger.logApiResponse("/users/$userId", response.code(), 0)
                false
            }
        } catch (e: Exception) {
            Logger.logError("UserRepository", e, "Failed to delete user remotely")
            ErrorHandler.handleException(e)
            false
        }
    }

    /**
     * Authenticate user
     */
    suspend fun authenticateUser(username: String, password: String): UsersEntity? = withContext(Dispatchers.IO) {
        try {
            // For now, check local database
            // TODO: Implement proper authentication with API
            val users = getAllUsers()
            users.find { it.username == username && it.password == password }
        } catch (e: Exception) {
            Logger.logError("UserRepository", e, "Failed to authenticate user: $username")
            null
        }
    }
}
