package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourtCaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CourtCaseEntity): Long

    @Update
    suspend fun update(entity: CourtCaseEntity)

    @Query("DELETE FROM court_cases WHERE case_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM court_cases ORDER BY filing_date DESC")
    fun observeAll(): Flow<List<CourtCaseEntity>>

    @Query("SELECT COUNT(*) FROM court_cases")
    suspend fun count(): Int
}
