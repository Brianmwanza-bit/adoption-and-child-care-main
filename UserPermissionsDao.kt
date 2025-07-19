import androidx.room.*

@Dao
interface UserPermissionsDao {
    @Query("SELECT * FROM user_permissions")
    suspend fun getAll(): List<UserPermissionsEntity>

    @Query("SELECT * FROM user_permissions WHERE user_id = :userId AND permission_id = :permissionId")
    suspend fun getById(userId: Int, permissionId: Int): UserPermissionsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userPermission: UserPermissionsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userPermissions: List<UserPermissionsEntity>)

    @Update
    suspend fun update(userPermission: UserPermissionsEntity)

    @Delete
    suspend fun delete(userPermission: UserPermissionsEntity)

    @Query("DELETE FROM user_permissions")
    suspend fun clearAll()
} 