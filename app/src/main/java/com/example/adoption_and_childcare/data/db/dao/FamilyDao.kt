package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import kotlinx.coroutines.flow.Flow

import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson

/**
 * Data Access Object for family-related database operations.
 * 
 * This class provides methods for CRUD operations on family records,
 * including sync-aware methods that automatically queue changes for
 * offline-first synchronization.
 */
@Dao
abstract class FamilyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: FamilyEntity): Long

    @Update
    abstract suspend fun update(entity: FamilyEntity)

    @Transaction
    open suspend fun insertWithSync(entity: FamilyEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "families",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Transaction
    open suspend fun updateWithSync(entity: FamilyEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "families",
                operation = "UPDATE",
                recordId = entity.familyId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM families WHERE family_id = :id")
    abstract suspend fun deleteById(id: Int)

    @Transaction
    open suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "families",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM families ORDER BY created_at DESC")
    abstract fun observeAll(): Flow<List<FamilyEntity>>

    @Query("SELECT * FROM families WHERE family_id = :id LIMIT 1")
    abstract suspend fun findById(id: Int): FamilyEntity?

    @Query("SELECT * FROM families WHERE primary_contact_name LIKE :query OR secondary_contact_name LIKE :query ORDER BY primary_contact_name")
    abstract fun searchByName(query: String): Flow<List<FamilyEntity>>

    @Query("SELECT * FROM families WHERE primary_contact_name LIKE :q OR secondary_contact_name LIKE :q OR phone LIKE :q OR national_id_no LIKE :q OR county LIKE :q ORDER BY primary_contact_name LIMIT :limit")
    abstract suspend fun globalSearch(q: String, limit: Int = 5): List<FamilyEntity>

    @Query("SELECT COUNT(*) FROM families")
    abstract suspend fun count(): Int

    @Query("SELECT * FROM families WHERE sync_status = 'PENDING'")
    abstract suspend fun getPendingSync(): List<FamilyEntity>

    @Query("UPDATE families SET sync_status = :status, remote_id = :remoteId, last_synced_at = :timestamp WHERE family_id = :localId")
    abstract suspend fun updateSyncStatus(localId: Int, status: String, remoteId: String?, timestamp: Long)
}
