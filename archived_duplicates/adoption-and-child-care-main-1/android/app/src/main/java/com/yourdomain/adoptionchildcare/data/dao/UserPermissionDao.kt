package com.yourdomain.adoptionchildcare.data.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.entities.UserPermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPermissionDao {
    @Query("SELECT * FROM user_permissions WHERE user_id = :userId")
    fun observeForUser(userId: Int): Flow<List<UserPermissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: UserPermissionEntity)
}
