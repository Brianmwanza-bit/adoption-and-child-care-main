package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import kotlinx.coroutines.flow.Flow

import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson

@Dao
abstract class PlacementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: PlacementEntity): Long

    @Update
    abstract suspend fun update(entity: PlacementEntity)

    @Transaction
    open suspend fun insertWithSync(entity: PlacementEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "placements",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Transaction
    open suspend fun updateWithSync(entity: PlacementEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "placements",
                operation = "UPDATE",
                recordId = entity.placementId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM placements WHERE placement_id = :id")
    abstract suspend fun deleteById(id: Int)

    @Transaction
    open suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "placements",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM placements WHERE child_id = :childId ORDER BY start_date DESC")
    abstract fun observeForChild(childId: Int): Flow<List<PlacementEntity>>

    @Query("SELECT COUNT(*) FROM placements")
    abstract suspend fun count(): Int

    @Query("SELECT * FROM placements ORDER BY start_date DESC")
    abstract fun observeAll(): Flow<List<PlacementEntity>>

    @Query("SELECT * FROM placements WHERE placement_type LIKE :q ORDER BY start_date DESC LIMIT :limit")
    abstract suspend fun globalSearch(q: String, limit: Int = 5): List<PlacementEntity>
}
