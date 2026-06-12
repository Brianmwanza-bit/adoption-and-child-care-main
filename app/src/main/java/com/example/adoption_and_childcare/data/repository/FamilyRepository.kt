package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for family-related data operations.
 * 
 * This interface defines the contract for accessing and manipulating family data,
 * supporting Flow-based reactive data streams and API synchronization.
 */
interface FamilyRepository : BaseRepository<FamilyEntity> {
    /** Search for families by name pattern. */
    fun searchByName(query: String): Flow<List<FamilyEntity>>
    
    /** Insert entity and sync with API. */
    override suspend fun insert(entity: FamilyEntity, token: String): Result<Long>
    
    /** Update entity and sync with API. */
    override suspend fun update(entity: FamilyEntity, token: String): Result<Unit>
    
    /** Delete entity and sync with API. */
    override suspend fun delete(id: Int, token: String): Result<Unit>
    
    /** Fetch all entities from remote API and update local cache. */
    override suspend fun fetchFromApi(token: String): Result<List<FamilyEntity>>
}
