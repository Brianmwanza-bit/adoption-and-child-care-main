package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.PermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PermissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PermissionEntity): Long

    @Update
    suspend fun update(entity: PermissionEntity)

    @Query("DELETE FROM permissions WHERE permission_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM permissions ORDER BY name")
    fun observeAll(): Flow<List<PermissionEntity>>

    @Query("SELECT COUNT(*) FROM permissions")
    suspend fun count(): Int
}
