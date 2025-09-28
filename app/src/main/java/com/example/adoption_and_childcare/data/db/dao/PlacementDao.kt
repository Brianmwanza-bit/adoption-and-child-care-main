package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlacementEntity): Long

    @Update
    suspend fun update(entity: PlacementEntity)

    @Query("DELETE FROM placements WHERE placement_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM placements WHERE child_id = :childId ORDER BY start_date DESC")
    fun observeForChild(childId: Int): Flow<List<PlacementEntity>>

    @Query("SELECT COUNT(*) FROM placements")
    suspend fun count(): Int

    @Query("SELECT * FROM placements ORDER BY start_date DESC")
    fun observeAll(): Flow<List<PlacementEntity>>
}
