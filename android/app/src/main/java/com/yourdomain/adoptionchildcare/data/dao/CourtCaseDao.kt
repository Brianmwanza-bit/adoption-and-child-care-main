package com.yourdomain.adoptionchildcare.data.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.entities.CourtCaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourtCaseDao {
    @Query("SELECT * FROM court_cases WHERE child_id = :childId ORDER BY filing_date DESC")
    fun observeForChild(childId: Int): Flow<List<CourtCaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: CourtCaseEntity)
}
