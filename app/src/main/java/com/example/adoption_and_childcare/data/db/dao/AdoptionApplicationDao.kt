package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import kotlinx.coroutines.flow.Flow

import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson

@Dao
abstract class AdoptionApplicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: AdoptionApplicationEntity): Long

    @Update
    abstract suspend fun update(entity: AdoptionApplicationEntity)

    @Transaction
    open suspend fun insertWithSync(entity: AdoptionApplicationEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity.copy(applicationId = id.toInt()))
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "adoption_applications",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Transaction
    open suspend fun updateWithSync(entity: AdoptionApplicationEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "adoption_applications",
                operation = "UPDATE",
                recordId = entity.applicationId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM adoption_applications WHERE application_id = :id")
    abstract suspend fun deleteById(id: Int)

    @Transaction
    open suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "adoption_applications",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM adoption_applications ORDER BY submitted_at DESC")
    abstract fun observeAll(): Flow<List<AdoptionApplicationEntity>>

    @Query("SELECT * FROM adoption_applications WHERE family_id = :familyId ORDER BY submitted_at DESC")
    abstract fun observeForFamily(familyId: Int): Flow<List<AdoptionApplicationEntity>>

    @Query("SELECT * FROM adoption_applications WHERE application_id = :id LIMIT 1")
    abstract suspend fun findById(id: Int): AdoptionApplicationEntity?

    @Query("SELECT COUNT(*) FROM adoption_applications")
    abstract suspend fun count(): Int

    @Query("SELECT * FROM adoption_applications WHERE sync_status = 'PENDING'")
    abstract suspend fun getPendingSync(): List<AdoptionApplicationEntity>

    @Query("UPDATE adoption_applications SET sync_status = :status, remote_id = :remoteId, last_synced_at = :timestamp WHERE application_id = :localId")
    abstract suspend fun updateSyncStatus(localId: Int, status: String, remoteId: String?, timestamp: Long)

    @Query("SELECT * FROM adoption_applications WHERE status LIKE :q OR notes LIKE :q ORDER BY submitted_at DESC LIMIT :limit")
    abstract suspend fun globalSearch(q: String, limit: Int = 5): List<AdoptionApplicationEntity>
}
