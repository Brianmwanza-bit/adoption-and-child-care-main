package com.adoptionapp.data.db.dao

import androidx.room.*
import com.adoptionapp.data.db.entities.MoneyRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoneyRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MoneyRecordEntity): Long

    @Update
    suspend fun update(entity: MoneyRecordEntity)

    @Query("DELETE FROM money_records WHERE money_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM money_records WHERE child_id = :childId ORDER BY date DESC")
    fun observeForChild(childId: Int): Flow<List<MoneyRecordEntity>>

    @Query("SELECT COUNT(*) FROM money_records")
    suspend fun count(): Int

    @Query("SELECT * FROM money_records ORDER BY date DESC")
    fun observeAll(): Flow<List<MoneyRecordEntity>>
}
