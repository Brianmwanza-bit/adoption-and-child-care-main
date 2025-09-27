package com.yourdomain.adoptionchildcare.data.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.entities.PermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PermissionDao {
    @Query("SELECT * FROM permissions ORDER BY name")
    fun observeAll(): Flow<List<PermissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: PermissionEntity)
}
