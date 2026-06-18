package com.example.adoption_and_childcare.data.repository

import android.content.Context
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.dao.SyncQueueDao
import com.example.adoption_and_childcare.data.db.dao.UserDao
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.network.LoginRequest
import com.example.adoption_and_childcare.network.RegisterRequest
import com.example.adoption_and_childcare.utils.AuthManager
import com.example.adoption_and_childcare.utils.Security
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the UserRepository interface.
 *
 * This class provides concrete implementations for user data operations,
 * integrating local database access with remote API authentication.
 *
 * @property userDao The Data Access Object for user-related database operations.
 * @property syncQueueDao The DAO for managing the synchronization queue.
 * @property apiService The Retrofit service for API communication.
 * @property sessionManager The manager for user session and authentication tokens.
 * @property authManager The utility for handling authentication state.
 * @property context The application context.
 */
@Singleton
public class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val authManager: AuthManager,
    @param:ApplicationContext private val context: Context
) : BaseSyncRepository(context), UserRepository {

    /**
     * Observes all users in the local database.
     *
     * @return A Flow emitting the list of all UserEntity objects.
     */
    override fun observeAll(): Flow<List<UserEntity>> = userDao.observeAll()

    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for.
     * @return The UserEntity if found, null otherwise.
     */
    override suspend fun findByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.findByEmail(email)
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return The UserEntity if found, null otherwise.
     */
    override suspend fun findByUsername(username: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.findByUsername(username)
    }

    /**
     * Inserts a new user into the local database.
     *
     * @param user The user entity to insert.
     */
    override suspend fun insert(user: UserEntity): Unit = withContext(Dispatchers.IO) {
        userDao.insert(user)
    }

    /**
     * Updates an existing user in the local database.
     *
     * @param user The user entity with updated information.
     */
    override suspend fun update(user: UserEntity): Unit = withContext(Dispatchers.IO) {
        userDao.update(user)
    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param userId The ID of the user to delete.
     */
    override suspend fun deleteById(userId: Int): Unit = withContext(Dispatchers.IO) {
        userDao.deleteById(userId)
    }

    /**
     * Registers a new user both locally and remotely.
     *
     * @param user The user details for registration.
     * @param password The plain-text password.
     * @return A Result containing the registered UserEntity or an error.
     */
    override suspend fun register(user: UserEntity, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            // 1. SAVE TO ROOM FIRST (Primary DB)
            val hashedPassword = Security.hashPassword(password)
            val userToSave = user.copy(
                passwordHash = hashedPassword,
                syncStatus = "PENDING",
                lastSyncedAt = 0
            )

            // This also adds to SyncQueue
            userDao.insertWithSync(userToSave, syncQueueDao)
            val savedUser = userDao.findByUsername(user.username) ?: userToSave

            // 2. Save session locally so user can enter the app immediately
            sessionManager.saveSession(savedUser)

            // 3. Attempt remote registration in background
            try {
                val registerRequest = RegisterRequest(
                    username = user.username,
                    email = user.email ?: "",
                    password = password,
                    phone = user.phone ?: "",
                    idNumber = user.idNumber ?: "",
                    nationalIdNo = user.nationalIdNo,
                    role = user.role,
                    county = user.county,
                    subCounty = user.subCounty
                )

                val response = apiService.register(registerRequest)
                val body = response.body()
                if (response.isSuccessful && body?.success == true) {
                    val token = body.token

                    // Update local user with server data
                    val syncedUser = savedUser.copy(
                        syncStatus = "SYNCED",
                        lastSyncedAt = System.currentTimeMillis() / 1000
                    )
                    userDao.update(syncedUser)
                    if (token != null) {
                        sessionManager.saveAuthToken(token)
                    }
                }
            } catch (_: Exception) {
                // Ignore API failure here; SyncQueue will handle it
            }

            scheduleSync()
            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param username The username or email for login.
     * @param password The plain-text password.
     * @return A Result containing the authenticated UserEntity or an error.
     */
    override suspend fun login(username: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            // 1. Check local DB first (Offline mode support)
            val localUser = userDao.findByUsername(username) ?: userDao.findByEmail(username)
            val hashedPassword = Security.hashPassword(password)

            if (localUser != null && localUser.passwordHash == hashedPassword) {
                sessionManager.saveSession(localUser)

                // Try to sync/refresh token in background
                try {
                    val loginRequest = LoginRequest(email = username, password = password)
                    val response = apiService.login(loginRequest)
                    val body = response.body()
                    if (response.isSuccessful && body?.success == true) {
                        val token = body.token
                        if (token != null) {
                            sessionManager.saveAuthToken(token)
                        }
                    }
                } catch (_: Exception) { /* Ignore */ }

                scheduleSync()
                return@withContext Result.success(localUser)
            }

            // 2. If not found locally, try remote login
            val loginRequest = LoginRequest(email = username, password = password)
            val response = apiService.login(loginRequest)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val token = body.token
                val user = body.user

                if (user != null) {
                    // Save locally
                    val userToSave = user.copy(
                        passwordHash = hashedPassword,
                        syncStatus = "SYNCED",
                        lastSyncedAt = System.currentTimeMillis() / 1000
                    )

                    userDao.insert(userToSave)
                    val finalUser = userDao.findByUsername(user.username) ?: userToSave

                    sessionManager.saveSession(finalUser)
                    if (token != null) {
                        sessionManager.saveAuthToken(token)
                    }

                    scheduleSync()
                    Result.success(finalUser)
                } else {
                    Result.failure(Exception("User data not found in response"))
                }
            } else {
                val errorMsg = body?.error?.message ?: context.getString(R.string.error_invalid_credentials)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches the full list of users from the remote API.
     *
     * @return A Result containing the list of users or an error.
     */
    override suspend fun fetchUsersFromApi(): Result<List<UserEntity>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = authManager.getAuthHeader() ?: return@withContext Result.failure(Exception("Not authenticated"))

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
