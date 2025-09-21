package com.yourdomain.adoptionchildcare.data.db.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.db.entities.MedicalRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MedicalRecordEntity): Long

    @Update
    suspend fun update(entity: MedicalRecordEntity)

    @Query("SELECT * FROM medical_records WHERE child_id = :childId ORDER BY visit_date DESC")
    fun observeForChild(childId: Int): Flow<List<MedicalRecordEntity>>

    @Query("SELECT COUNT(*) FROM medical_records")
    suspend fun count(): Int

    @Query("SELECT * FROM medical_records ORDER BY visit_date DESC")
    fun observeAll(): Flow<List<MedicalRecordEntity>>
}
