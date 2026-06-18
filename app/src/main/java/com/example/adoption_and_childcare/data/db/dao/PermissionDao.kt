package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.PermissionEntity
import kotlinx.coroutines.flow.Flow

import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson

@Dao
interface PermissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PermissionEntity): Long

    @Transaction
    suspend fun insertWithSync(entity: PermissionEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "permissions",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Update
    suspend fun update(entity: PermissionEntity)

    @Transaction
    suspend fun updateWithSync(entity: PermissionEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "permissions",
                operation = "UPDATE",
                recordId = entity.permissionId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM permissions WHERE permission_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "permissions",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM permissions ORDER BY name")
    fun observeAll(): Flow<List<PermissionEntity>>

    @Query("SELECT COUNT(*) FROM permissions")
    suspend fun count(): Int

    @Query("SELECT * FROM permissions")
    suspend fun getAll(): List<PermissionEntity>

    @Query("SELECT * FROM permissions WHERE permission_id = :id")
    suspend fun findById(id: Int): PermissionEntity?
}
