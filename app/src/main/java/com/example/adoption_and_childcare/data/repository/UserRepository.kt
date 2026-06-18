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
    fun observeAll(): Flow<List<UserEntity>>
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun findByUsername(username: String): UserEntity?
    suspend fun insert(user: UserEntity)
    suspend fun update(user: UserEntity)
    suspend fun deleteById(userId: Int)
    
    /**
     * Authenticates a user with the remote API and syncs the local session.
     */
    suspend fun login(username: String, password: String): Result<UserEntity>

    /**
     * Registers a new user on the remote server and persists them locally.
     */
    suspend fun register(user: UserEntity, password: String): Result<UserEntity>

    /**
     * Fetches all users from the remote API and updates local database.
     */
    suspend fun fetchUsersFromApi(): Result<List<UserEntity>>
}

