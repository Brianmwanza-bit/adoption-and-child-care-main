package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.ActionItemEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for action item-related database operations.
 */
@Dao
interface ActionItemDao {
    @Query("SELECT * FROM action_items")
    fun observeAll(): Flow<List<ActionItemEntity>>

    @Query("SELECT * FROM action_items")
    suspend fun getAll(): List<ActionItemEntity>

    @Query("SELECT * FROM action_items WHERE action_id = :id")
    suspend fun getById(id: Int): ActionItemEntity?

    @Query("SELECT * FROM action_items WHERE assignee_id = :userId")
    suspend fun getByAssignee(userId: Int): List<ActionItemEntity>

    @Query("SELECT * FROM action_items WHERE status != 'completed'")
    suspend fun getPending(): List<ActionItemEntity>

    @Query("SELECT COUNT(*) FROM action_items")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(actionItem: ActionItemEntity): Long

    @Transaction
    suspend fun insertWithSync(actionItem: ActionItemEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(actionItem)
        val payload = Gson().toJson(actionItem)
        syncQueueDao.insert(SyncQueueEntity(tableName = "action_items", operation = "INSERT", recordId = id.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ActionItemEntity>)

    @Update
    suspend fun update(actionItem: ActionItemEntity)

    @Transaction
    suspend fun updateWithSync(actionItem: ActionItemEntity, syncQueueDao: SyncQueueDao) {
        update(actionItem)
        val payload = Gson().toJson(actionItem)
        syncQueueDao.insert(SyncQueueEntity(tableName = "action_items", operation = "UPDATE", recordId = actionItem.actionId.toString(), payload = payload, createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(actionItem: ActionItemEntity)

    @Query("DELETE FROM action_items WHERE action_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(SyncQueueEntity(tableName = "action_items", operation = "DELETE", recordId = id.toString(), payload = "{}", createdAt = System.currentTimeMillis()))
    }
}
