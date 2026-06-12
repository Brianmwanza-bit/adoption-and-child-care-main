package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
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

    @Transaction
    suspend fun insertWithSync(backgroundCheck: BackgroundCheckEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(backgroundCheck)
        val payload = Gson().toJson(backgroundCheck)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "background_checks",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(backgroundChecks: List<BackgroundCheckEntity>)

    @Update
    suspend fun update(backgroundCheck: BackgroundCheckEntity)

    @Transaction
    suspend fun updateWithSync(backgroundCheck: BackgroundCheckEntity, syncQueueDao: SyncQueueDao) {
        update(backgroundCheck)
        val payload = Gson().toJson(backgroundCheck)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "background_checks",
                operation = "UPDATE",
                recordId = backgroundCheck.checkId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Delete
    suspend fun delete(backgroundCheck: BackgroundCheckEntity)

    @Query("DELETE FROM background_checks WHERE check_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "background_checks",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
