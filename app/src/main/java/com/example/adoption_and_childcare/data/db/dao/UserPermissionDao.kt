package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity
import kotlinx.coroutines.flow.Flow

import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson

@Dao
interface UserPermissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UserPermissionEntity): Long

    @Transaction
    suspend fun insertWithSync(entity: UserPermissionEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "user_permissions",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Update
    suspend fun update(entity: UserPermissionEntity)

    @Transaction
    suspend fun updateWithSync(entity: UserPermissionEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "user_permissions",
                operation = "UPDATE",
                recordId = entity.id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM user_permissions WHERE user_id = :userId AND permission_id = :permissionId")
    suspend fun deleteByUserAndPermission(userId: Int, permissionId: Int)

    @Query("DELETE FROM user_permissions WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "user_permissions",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM user_permissions WHERE id = :id")
    suspend fun findById(id: Int): UserPermissionEntity?

    @Query("SELECT * FROM user_permissions")
    suspend fun getAll(): List<UserPermissionEntity>

    @Query("SELECT * FROM user_permissions ORDER BY user_id")
    fun observeAll(): Flow<List<UserPermissionEntity>>

    @Query("SELECT COUNT(*) FROM user_permissions")
    suspend fun count(): Int
}
