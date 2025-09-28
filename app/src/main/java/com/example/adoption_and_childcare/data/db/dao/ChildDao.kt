package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(child: ChildEntity): Long

    @Update
    suspend fun update(child: ChildEntity)

    @Query("DELETE FROM children WHERE child_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM children ORDER BY last_name, first_name")
    fun observeAll(): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE child_id = :id LIMIT 1")
    suspend fun findById(id: Int): ChildEntity?

    @Query("SELECT * FROM children WHERE first_name LIKE :q OR last_name LIKE :q ORDER BY last_name, first_name")
    fun searchByName(q: String): Flow<List<ChildEntity>>

    @Query("SELECT COUNT(*) FROM children")
    suspend fun count(): Int
}
