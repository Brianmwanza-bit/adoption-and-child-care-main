package com.yourdomain.adoptionchildcare.data.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.entities.PlacementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacementDao {
    @Query("SELECT * FROM placements WHERE child_id = :childId ORDER BY start_date DESC")
    fun observeForChild(childId: Int): Flow<List<PlacementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: PlacementEntity)
}
