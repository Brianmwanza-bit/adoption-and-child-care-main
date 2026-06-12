package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Transaction
    suspend fun insertWithSync(user: UserEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(user)
        val payload = Gson().toJson(user)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "users",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Update
    suspend fun update(user: UserEntity)

    @Transaction
    suspend fun updateWithSync(user: UserEntity, syncQueueDao: SyncQueueDao) {
        update(user)
        val payload = Gson().toJson(user)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "users",
                operation = "UPDATE",
                recordId = user.userId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM users WHERE user_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "users",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM users ORDER BY user_id DESC")
    fun observeAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE user_id = :id LIMIT 1")
    suspend fun findById(id: Int): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Query("SELECT * FROM users WHERE username LIKE :q OR email LIKE :q OR phone LIKE :q OR national_id_no LIKE :q OR county LIKE :q ORDER BY username LIMIT :limit")
    suspend fun globalSearch(q: String, limit: Int = 5): List<UserEntity>
}
