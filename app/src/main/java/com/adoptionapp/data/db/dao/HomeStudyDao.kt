package com.adoptionapp.data.db.dao

import androidx.room.*
import com.adoptionapp.data.db.entities.HomeStudyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeStudyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HomeStudyEntity): Long

    @Update
    suspend fun update(entity: HomeStudyEntity)

    @Query("DELETE FROM home_studies WHERE home_study_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM home_studies WHERE family_id = :familyId ORDER BY started_at DESC")
    fun observeForFamily(familyId: Int): Flow<List<HomeStudyEntity>>

    @Query("SELECT COUNT(*) FROM home_studies")
    suspend fun count(): Int

    @Query("SELECT * FROM home_studies ORDER BY started_at DESC")
    fun observeAll(): Flow<List<HomeStudyEntity>>
}
