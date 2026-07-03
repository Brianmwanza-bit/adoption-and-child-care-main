package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user-related data operations.
 * 
 * This interface defines the contract for accessing and manipulating user data
 * using the local Room database.
 */
interface UserRepository {
    /**
     * Observes all users in the local database.
     *
     * @return A Flow emitting the list of all UserEntity objects.
     */
    fun observeAll(): Flow<List<UserEntity>>

    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for.
     * @return The UserEntity if found, null otherwise.
     */
    suspend fun findByEmail(email: String): UserEntity?

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return The UserEntity if found, null otherwise.
     */
    suspend fun findByUsername(username: String): UserEntity?

    /**
     * Inserts a new user into the local database.
     *
     * @param user The user entity to insert.
     */
    suspend fun insert(user: UserEntity)

    /**
     * Updates an existing user in the local database.
     *
     * @param user The user entity with updated information.
     */
    suspend fun update(user: UserEntity)

    /**
     * Deletes a user by their unique identifier.
     *
     * @param userId The ID of the user to delete.
     */
    suspend fun deleteById(userId: Int)
    
    /**
     * Authenticates a user with the remote API and syncs the local session.
     *
     * @param username The username or email for login.
     * @param password The plain-text password.
     * @return A Result containing the authenticated UserEntity or an error.
     */
    suspend fun login(username: String, password: String): Result<UserEntity>

    /**
     * Registers a new user on the remote server and persists them locally.
     *
     * @param user The user details for registration.
     * @param password The plain-text password.
     * @return A Result containing the registered UserEntity or an error.
     */
    suspend fun register(user: UserEntity, password: String): Result<UserEntity>

    /**
     * Fetches all users from the remote API and updates local database.
     *
     * @return A Result containing the list of users or an error.
     */
    suspend fun fetchUsersFromApi(): Result<List<UserEntity>>
}
