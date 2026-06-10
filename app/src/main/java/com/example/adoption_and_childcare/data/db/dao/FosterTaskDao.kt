package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FosterTaskDao {
    @Query("SELECT * FROM foster_tasks")
    fun observeAll(): Flow<List<FosterTaskEntity>>

    @Query("SELECT * FROM foster_tasks")
    suspend fun getAll(): List<FosterTaskEntity>

    @Query("SELECT * FROM foster_tasks WHERE task_id = :id")
    suspend fun getById(id: Int): FosterTaskEntity?

    @Query("SELECT * FROM foster_tasks WHERE family_id = :familyId")
    suspend fun getByFamilyId(familyId: Int): List<FosterTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fosterTask: FosterTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fosterTasks: List<FosterTaskEntity>)

    @Update
    suspend fun update(fosterTask: FosterTaskEntity)

    @Delete
    suspend fun delete(fosterTask: FosterTaskEntity)

    @Query("DELETE FROM foster_tasks WHERE task_id = :id")
    suspend fun deleteById(id: Int)
}