package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.DashboardPreferenceEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardPreferenceDao {
    @Query("SELECT * FROM dashboard_preferences")
    fun observeAll(): Flow<List<DashboardPreferenceEntity>>

    @Query("SELECT * FROM dashboard_preferences WHERE user_id = :userId")
    suspend fun getByUserId(userId: Int): DashboardPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preference: DashboardPreferenceEntity): Long

    @Transaction
    suspend fun insertWithSync(preference: DashboardPreferenceEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(preference)
        val payload = Gson().toJson(preference)
        syncQueueDao.insert(SyncQueueEntity(tableName = "dashboard_preferences", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(preferences: List<DashboardPreferenceEntity>)

    @Update
    suspend fun update(preference: DashboardPreferenceEntity)

    @Transaction
    suspend fun updateWithSync(preference: DashboardPreferenceEntity, syncQueueDao: SyncQueueDao) {
        update(preference)
        val payload = Gson().toJson(preference)
        syncQueueDao.insert(SyncQueueEntity(tableName = "dashboard_preferences", operation = "UPDATE", recordId = preference.preferenceId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(preference: DashboardPreferenceEntity)
}
