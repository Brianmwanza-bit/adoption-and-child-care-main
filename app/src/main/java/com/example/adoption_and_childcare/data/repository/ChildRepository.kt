package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for child-related data operations.
 * 
 * This interface defines the contract for accessing and manipulating child data,
 * supporting Flow-based reactive data streams and API synchronization.
 */
interface ChildRepository : BaseRepository<ChildEntity> {
    /** Search for children by name pattern. */
    fun searchByName(query: String): Flow<List<ChildEntity>>
    
    /** Insert entity and sync with API. */
    override suspend fun insert(entity: ChildEntity, token: String): Result<Long>
    
    /** Update entity and sync with API. */
    override suspend fun update(entity: ChildEntity, token: String): Result<Unit>
    
    /** Delete entity and sync with API. */
    override suspend fun delete(id: Int, token: String): Result<Unit>
    
    /** Fetch all entities from remote API and update local cache. */
    override suspend fun fetchFromApi(token: String): Result<List<ChildEntity>>
}
