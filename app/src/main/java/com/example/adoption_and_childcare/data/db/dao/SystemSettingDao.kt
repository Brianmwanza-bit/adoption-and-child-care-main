package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for system settings.
 * 
 * Provides methods for persistent storage of application-wide configurations,
 * including sync-aware operations for offline-first architecture.
 */
@Dao
abstract class SystemSettingDao {
    @Query("SELECT * FROM system_settings")
    abstract fun observeAll(): Flow<List<SystemSettingEntity>>

    @Query("SELECT * FROM system_settings")
    abstract suspend fun getAll(): List<SystemSettingEntity>

    @Query("SELECT * FROM system_settings WHERE setting_id = :id")
    abstract suspend fun findById(id: Int): SystemSettingEntity?

    @Query("SELECT * FROM system_settings WHERE setting_key = :key")
    abstract suspend fun getByKey(key: String): SystemSettingEntity?

    @Query("SELECT * FROM system_settings WHERE category = :category")
    abstract suspend fun getByCategory(category: String): List<SystemSettingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(systemSetting: SystemSettingEntity): Long

    @Update
    abstract suspend fun update(systemSetting: SystemSettingEntity)

    /**
     * Inserts a setting and queues it for remote synchronization.
     */
    @Transaction
    open suspend fun insertWithSync(entity: SystemSettingEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "system_settings",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Updates a setting and queues it for remote synchronization.
     */
    @Transaction
    open suspend fun updateWithSync(entity: SystemSettingEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "system_settings",
                operation = "UPDATE",
                recordId = entity.settingId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM system_settings WHERE setting_id = :id")
    abstract suspend fun deleteById(id: Int)

    /**
     * Deletes a setting and queues the deletion for remote synchronization.
     */
    @Transaction
    open suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "system_settings",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Delete
    abstract suspend fun delete(systemSetting: SystemSettingEntity)

    @Query("DELETE FROM system_settings WHERE setting_key = :key")
    abstract suspend fun deleteByKey(key: String)
}
