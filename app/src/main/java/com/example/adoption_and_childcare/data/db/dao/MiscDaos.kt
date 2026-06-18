package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyMeetingDao {
    @Query("SELECT * FROM family_meetings ORDER BY meeting_date DESC")
    fun observeAll(): Flow<List<FamilyMeetingEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FamilyMeetingEntity): Long
    @Update
    suspend fun update(entity: FamilyMeetingEntity)
    @Delete
    suspend fun delete(entity: FamilyMeetingEntity)
}

@Dao
interface ChildDevelopmentMetricDao {
    @Query("SELECT * FROM child_development_metrics ORDER BY measurement_date DESC")
    fun observeAll(): Flow<List<ChildDevelopmentMetricEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChildDevelopmentMetricEntity): Long
    @Update
    suspend fun update(entity: ChildDevelopmentMetricEntity)
    @Delete
    suspend fun delete(entity: ChildDevelopmentMetricEntity)
}

@Dao
interface StaffResourceDao {
    @Query("SELECT * FROM staff_resources ORDER BY assigned_date DESC")
    fun observeAll(): Flow<List<StaffResourceEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: StaffResourceEntity): Long
    @Update
    suspend fun update(entity: StaffResourceEntity)
    @Delete
    suspend fun delete(entity: StaffResourceEntity)
}
