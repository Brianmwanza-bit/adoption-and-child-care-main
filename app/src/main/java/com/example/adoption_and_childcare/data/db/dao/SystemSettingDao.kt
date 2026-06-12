package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemSettingDao {
    @Query("SELECT * FROM system_settings")
    fun observeAll(): Flow<List<SystemSettingEntity>>

    @Query("SELECT * FROM system_settings")
    suspend fun getAll(): List<SystemSettingEntity>

    @Query("SELECT * FROM system_settings WHERE setting_id = :id")
    suspend fun findById(id: Int): SystemSettingEntity?

    @Query("SELECT * FROM system_settings WHERE setting_key = :key")
    suspend fun getByKey(key: String): SystemSettingEntity?

    @Query("SELECT * FROM system_settings WHERE category = :category")
    suspend fun getByCategory(category: String): List<SystemSettingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(systemSetting: SystemSettingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(systemSettings: List<SystemSettingEntity>)

    @Update
    suspend fun update(systemSetting: SystemSettingEntity)

    @Delete
    suspend fun delete(systemSetting: SystemSettingEntity)

    @Query("DELETE FROM system_settings WHERE setting_id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM system_settings WHERE setting_key = :key")
    suspend fun deleteByKey(key: String)
}