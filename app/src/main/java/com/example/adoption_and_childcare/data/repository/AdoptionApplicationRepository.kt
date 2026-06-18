package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import kotlinx.coroutines.flow.Flow

interface AdoptionApplicationRepository {
    fun observeAll(): Flow<List<AdoptionApplicationEntity>>
    fun observeForFamily(familyId: Int): Flow<List<AdoptionApplicationEntity>>
    suspend fun findById(id: Int): AdoptionApplicationEntity?
    suspend fun insert(application: AdoptionApplicationEntity, token: String): Long
    suspend fun update(application: AdoptionApplicationEntity, token: String)
    suspend fun delete(id: Int, token: String)
    suspend fun deleteById(id: Int)
    suspend fun fetchFromApi(token: String): Result<List<AdoptionApplicationEntity>>
    suspend fun count(): Int
}
