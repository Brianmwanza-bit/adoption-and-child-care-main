import androidx.room.*

@Dao
interface BackgroundChecksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(check: BackgroundChecksEntity)

    @Update
    suspend fun update(check: BackgroundChecksEntity)

    @Delete
    suspend fun delete(check: BackgroundChecksEntity)

    @Query("SELECT * FROM background_checks WHERE user_id = :userId")
    suspend fun getByUserId(userId: Int): List<BackgroundChecksEntity>

    @Query("SELECT * FROM background_checks")
    suspend fun getAll(): List<BackgroundChecksEntity>
} 