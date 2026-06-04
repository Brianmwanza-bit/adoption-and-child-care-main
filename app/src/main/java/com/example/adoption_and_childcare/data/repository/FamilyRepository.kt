package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import kotlinx.coroutines.flow.Flow

interface FamilyRepository {
    fun observeAll(): Flow<List<FamilyEntity>>
    suspend fun findById(id: Int): FamilyEntity?
    suspend fun insert(family: FamilyEntity): Long
    suspend fun update(family: FamilyEntity)
    suspend fun deleteById(id: Int)
    fun searchByName(query: String): Flow<List<FamilyEntity>>
}
