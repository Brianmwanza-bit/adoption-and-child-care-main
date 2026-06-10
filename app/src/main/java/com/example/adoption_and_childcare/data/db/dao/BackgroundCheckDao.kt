package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackgroundCheckDao {
    @Query("SELECT * FROM background_checks")
    fun observeAll(): Flow<List<BackgroundCheckEntity>>

    @Query("SELECT * FROM background_checks")
    suspend fun getAll(): List<BackgroundCheckEntity>

    @Query("SELECT * FROM background_checks WHERE check_id = :id")
    suspend fun getById(id: Int): BackgroundCheckEntity?

    @Query("SELECT * FROM background_checks WHERE user_id = :userId")
    suspend fun getByUserId(userId: Int): List<BackgroundCheckEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(backgroundCheck: BackgroundCheckEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(backgroundChecks: List<BackgroundCheckEntity>)

    @Update
    suspend fun update(backgroundCheck: BackgroundCheckEntity)

    @Delete
    suspend fun delete(backgroundCheck: BackgroundCheckEntity)

    @Query("DELETE FROM background_checks WHERE check_id = :id")
    suspend fun deleteById(id: Int)
}