package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.network.ApiService
import kotlinx.coroutines.flow.Flow

/**
 * Base repository interface providing common CRUD operations with API integration.
 * 
 * This interface defines the standard contract for all repositories, supporting
 * both local database operations and remote API synchronization.
 * 
 * @param T The entity type this repository manages.
 */
interface BaseRepository<T> {
    /** Observe all entities as a Flow for reactive UI updates. */
    fun observeAll(): Flow<List<T>>
    
    /** Find entity by ID. */
    suspend fun findById(id: Int): T?
    
    /** Insert entity and sync with API. */
    suspend fun insert(entity: T, token: String): Result<Long>
    
    /** Update entity and sync with API. */
    suspend fun update(entity: T, token: String): Result<Unit>
    
    /** Delete entity and sync with API. */
    suspend fun delete(id: Int, token: String): Result<Unit>
    
    /** Fetch all entities from remote API and update local cache. */
    suspend fun fetchFromApi(token: String): Result<List<T>>
    
    /** Count total entities. */
    suspend fun count(): Int
}
