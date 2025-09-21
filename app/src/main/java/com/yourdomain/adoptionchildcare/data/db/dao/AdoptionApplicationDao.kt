package com.yourdomain.adoptionchildcare.data.db.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.db.entities.AdoptionApplicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdoptionApplicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AdoptionApplicationEntity): Long

    @Update
    suspend fun update(entity: AdoptionApplicationEntity)

    @Query("DELETE FROM adoption_applications WHERE application_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM adoption_applications ORDER BY submitted_at DESC")
    fun observeAll(): Flow<List<AdoptionApplicationEntity>>

    @Query("SELECT * FROM adoption_applications WHERE family_id = :familyId ORDER BY submitted_at DESC")
    fun observeForFamily(familyId: Int): Flow<List<AdoptionApplicationEntity>>

    @Query("SELECT * FROM adoption_applications WHERE application_id = :id LIMIT 1")
    suspend fun findById(id: Int): AdoptionApplicationEntity?

    @Query("SELECT COUNT(*) FROM adoption_applications")
    suspend fun count(): Int
}
