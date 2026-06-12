package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface MoneyRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MoneyRecordEntity): Long

    @Transaction
    suspend fun insertWithSync(entity: MoneyRecordEntity, syncQueueDao: SyncQueueDao) {
        val id = insert(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "money_records",
                operation = "INSERT",
                recordId = id.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Update
    suspend fun update(entity: MoneyRecordEntity)

    @Transaction
    suspend fun updateWithSync(entity: MoneyRecordEntity, syncQueueDao: SyncQueueDao) {
        update(entity)
        val payload = Gson().toJson(entity)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "money_records",
                operation = "UPDATE",
                recordId = entity.moneyId.toString(),
                payload = payload,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("DELETE FROM money_records WHERE money_id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun deleteByIdWithSync(id: Int, syncQueueDao: SyncQueueDao) {
        deleteById(id)
        syncQueueDao.insert(
            SyncQueueEntity(
                tableName = "money_records",
                operation = "DELETE",
                recordId = id.toString(),
                payload = "{}",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    @Query("SELECT * FROM money_records WHERE child_id = :childId ORDER BY date DESC")
    fun observeForChild(childId: Int): Flow<List<MoneyRecordEntity>>

    @Query("SELECT COUNT(*) FROM money_records")
    suspend fun count(): Int

    @Query("SELECT * FROM money_records ORDER BY date DESC")
    fun observeAll(): Flow<List<MoneyRecordEntity>>

    @Query("SELECT * FROM money_records WHERE mpesa_receipt_no LIKE :q ORDER BY date DESC LIMIT :limit")
    suspend fun searchByReceipt(q: String, limit: Int = 5): List<MoneyRecordEntity>
}
