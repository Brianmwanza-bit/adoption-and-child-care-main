import androidx.room.*

@Dao
interface GuardiansDao {
    @Query("SELECT * FROM guardians")
    suspend fun getAll(): List<GuardiansEntity>

    @Query("SELECT * FROM guardians WHERE guardian_id = :id")
    suspend fun getById(id: Int): GuardiansEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(guardian: GuardiansEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(guardians: List<GuardiansEntity>)

    @Update
    suspend fun update(guardian: GuardiansEntity)

    @Delete
    suspend fun delete(guardian: GuardiansEntity)

    @Query("DELETE FROM guardians")
    suspend fun clearAll()
} 