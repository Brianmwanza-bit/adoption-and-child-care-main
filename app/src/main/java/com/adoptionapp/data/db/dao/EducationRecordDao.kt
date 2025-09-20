package com.adoptionapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adoptionapp.data.db.entities.EducationRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EducationRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EducationRecordEntity): Long

    @Update
    suspend fun update(entity: EducationRecordEntity)

    @Query("SELECT * FROM education_records WHERE child_id = :childId ORDER BY enrollment_date DESC")
    fun observeForChild(childId: Int): Flow<List<EducationRecordEntity>>

    @Query("SELECT COUNT(*) FROM education_records")
    suspend fun count(): Int

    @Query("SELECT * FROM education_records ORDER BY enrollment_date DESC")
    fun observeAll(): Flow<List<EducationRecordEntity>>
}
