import androidx.room.*

@Dao
interface UsersDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UsersEntity>

    @Query("SELECT * FROM users WHERE user_id = :id")
    suspend fun getById(id: Int): UsersEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UsersEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UsersEntity>)

    @Update
    suspend fun update(user: UsersEntity)

    @Delete
    suspend fun delete(user: UsersEntity)

    @Query("DELETE FROM users")
    suspend fun clearAll()
} 