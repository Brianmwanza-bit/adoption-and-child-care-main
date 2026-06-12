package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.CriticalDateEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface CriticalDateDao {
    @Query("SELECT * FROM critical_dates")
    fun observeAll(): Flow<List<CriticalDateEntity>>

    @Query("SELECT * FROM critical_dates")
    suspend fun getAll(): List<CriticalDateEntity>

    @Query("SELECT * FROM critical_dates WHERE date_id = :id")
    suspend fun getById(id: Int): CriticalDateEntity?

    @Query("SELECT * FROM critical_dates WHERE child_id = :childId")
    suspend fun getByChildId(childId: Int): List<CriticalDateEntity>

    @Query("SELECT * FROM critical_dates WHERE is_completed = 0 ORDER BY event_date ASC")
    suspend fun getUpcoming(): List<CriticalDateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(criticalDate: CriticalDateEntity): Long

    @Transaction
    suspend fun insertWithSync(criticalDate: CriticalDateEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(criticalDate)
        val payload = Gson().toJson(criticalDate)
        syncQueueDao.insert(SyncQueueEntity(tableName = "critical_dates", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dates: List<CriticalDateEntity>)

    @Update
    suspend fun update(criticalDate: CriticalDateEntity)

    @Transaction
    suspend fun updateWithSync(criticalDate: CriticalDateEntity, syncQueueDao: SyncQueueDao) {
        update(criticalDate)
        val payload = Gson().toJson(criticalDate)
        syncQueueDao.insert(SyncQueueEntity(tableName = "critical_dates", operation = "UPDATE", recordId = criticalDate.dateId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(criticalDate: CriticalDateEntity)

    @Query("DELETE FROM critical_dates WHERE date_id = :id")
    suspend fun deleteById(id: Int)
}
