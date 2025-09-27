package com.yourdomain.adoptionchildcare.data.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.entities.EducationRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EducationRecordDao {
    @Query("SELECT * FROM education_records WHERE child_id = :childId ORDER BY enrollment_date DESC")
    fun observeForChild(childId: Int): Flow<List<EducationRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: EducationRecordEntity)
}
