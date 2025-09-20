package com.adoptionapp.data.db.dao

import androidx.room.*
import com.adoptionapp.data.db.entities.GuardianEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GuardianDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GuardianEntity): Long

    @Update
    suspend fun update(entity: GuardianEntity)

    @Query("DELETE FROM guardians WHERE guardian_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM guardians ORDER BY first_name, last_name")
    fun observeAll(): Flow<List<GuardianEntity>>

    @Query("SELECT COUNT(*) FROM guardians")
    suspend fun count(): Int
}
