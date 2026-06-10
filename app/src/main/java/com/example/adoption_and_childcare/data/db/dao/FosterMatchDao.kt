package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FosterMatchDao {
    @Query("SELECT * FROM foster_matches")
    fun observeAll(): Flow<List<FosterMatchEntity>>

    @Query("SELECT * FROM foster_matches")
    suspend fun getAll(): List<FosterMatchEntity>

    @Query("SELECT * FROM foster_matches WHERE match_id = :id")
    suspend fun getById(id: Int): FosterMatchEntity?

    @Query("SELECT * FROM foster_matches WHERE family_id = :familyId")
    suspend fun getByFamilyId(familyId: Int): List<FosterMatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fosterMatch: FosterMatchEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fosterMatches: List<FosterMatchEntity>)

    @Update
    suspend fun update(fosterMatch: FosterMatchEntity)

    @Delete
    suspend fun delete(fosterMatch: FosterMatchEntity)

    @Query("DELETE FROM foster_matches WHERE match_id = :id")
    suspend fun deleteById(id: Int)
}