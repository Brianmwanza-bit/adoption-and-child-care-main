package com.yourdomain.adoptionchildcare.data.db.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.db.entities.UserPermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPermissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UserPermissionEntity): Long

    @Update
    suspend fun update(entity: UserPermissionEntity)

    @Query("DELETE FROM user_permissions WHERE user_id = :userId AND permission_id = :permissionId")
    suspend fun deleteByUserAndPermission(userId: Int, permissionId: Int)

    @Query("SELECT * FROM user_permissions ORDER BY user_id")
    fun observeAll(): Flow<List<UserPermissionEntity>>

    @Query("SELECT COUNT(*) FROM user_permissions")
    suspend fun count(): Int
}
