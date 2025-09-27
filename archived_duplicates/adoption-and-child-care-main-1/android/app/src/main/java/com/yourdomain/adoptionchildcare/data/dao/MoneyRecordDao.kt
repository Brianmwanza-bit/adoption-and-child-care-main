package com.yourdomain.adoptionchildcare.data.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.entities.MoneyRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoneyRecordDao {
    @Query("SELECT * FROM money_records WHERE child_id = :childId ORDER BY date DESC")
    fun observeForChild(childId: Int): Flow<List<MoneyRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: MoneyRecordEntity)
}
