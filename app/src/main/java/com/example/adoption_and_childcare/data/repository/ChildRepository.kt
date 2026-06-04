package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import kotlinx.coroutines.flow.Flow

interface ChildRepository {
    fun observeAll(): Flow<List<ChildEntity>>
    suspend fun findById(id: Int): ChildEntity?
    suspend fun insert(child: ChildEntity): Long
    suspend fun update(child: ChildEntity)
    suspend fun deleteById(id: Int)
    fun searchByName(query: String): Flow<List<ChildEntity>>
}
