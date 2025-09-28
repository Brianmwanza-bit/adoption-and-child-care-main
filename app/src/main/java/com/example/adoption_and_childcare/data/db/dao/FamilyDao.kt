package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FamilyEntity): Long

    @Update
    suspend fun update(entity: FamilyEntity)

    @Query("DELETE FROM families WHERE family_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM families ORDER BY created_at DESC")
    fun observeAll(): Flow<List<FamilyEntity>>

    @Query("SELECT * FROM families WHERE family_id = :id LIMIT 1")
    suspend fun findById(id: Int): FamilyEntity?

    @Query("SELECT * FROM families WHERE primary_contact_name LIKE :q OR secondary_contact_name LIKE :q ORDER BY primary_contact_name")
    fun searchByName(q: String): Flow<List<FamilyEntity>>

    @Query("SELECT COUNT(*) FROM families")
    suspend fun count(): Int
}
