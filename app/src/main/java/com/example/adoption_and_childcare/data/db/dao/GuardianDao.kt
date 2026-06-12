package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.GuardianEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface GuardianDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GuardianEntity): Long

    @Transaction
    suspend fun insertWithSync(entity: GuardianEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "guardians",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Update
    suspend fun update(entity: GuardianEntity)

    @Transaction
    suspend fun updateWithSync(entity: GuardianEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "guardians",
                operation = "UPDATE",
                recordId = entity.guardianId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM guardians WHERE guardian_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "guardians",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM guardians ORDER BY first_name, last_name")
    fun observeAll(): Flow<List<GuardianEntity>>

    @Query("SELECT COUNT(*) FROM guardians")
    suspend fun count(): Int

    @Query("SELECT * FROM guardians")
    suspend fun getAll(): List<GuardianEntity>

    @Query("SELECT * FROM guardians WHERE guardian_id = :id")
    suspend fun findById(id: Int): GuardianEntity?

    @Query("SELECT child_id FROM guardians WHERE user_id = :userId")
    suspend fun getChildIdsByUserId(userId: Int): List<Int>
    
    @Query("""
        SELECT * FROM guardians 
        WHERE first_name LIKE :query 
           OR last_name LIKE :query 
           OR relationship LIKE :query
           OR phone LIKE :query
           OR email LIKE :query
        ORDER BY first_name, last_name
    """)
    suspend fun globalSearch(query: String): List<GuardianEntity>
}
