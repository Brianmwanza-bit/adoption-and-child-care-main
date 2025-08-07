import androidx.room.*

@Dao
interface PermissionsDao {
    @Query("SELECT * FROM permissions")
    suspend fun getAll(): List<PermissionsEntity>

    @Query("SELECT * FROM permissions WHERE permission_id = :id")
    suspend fun getById(id: Int): PermissionsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(permission: PermissionsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(permissions: List<PermissionsEntity>)

    @Update
    suspend fun update(permission: PermissionsEntity)

    @Delete
    suspend fun delete(permission: PermissionsEntity)

    @Query("DELETE FROM permissions")
    suspend fun clearAll()
} 