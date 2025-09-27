package com.yourdomain.adoptionchildcare.data.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.entities.MedicalRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalRecordDao {
    @Query("SELECT * FROM medical_records WHERE child_id = :childId ORDER BY visit_date DESC")
    fun observeForChild(childId: Int): Flow<List<MedicalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: MedicalRecordEntity)
}
