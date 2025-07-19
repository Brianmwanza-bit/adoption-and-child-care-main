import androidx.room.*

@Dao
interface PlacementsDao {
    @Query("SELECT * FROM placements")
    suspend fun getAll(): List<PlacementsEntity>

    @Query("SELECT * FROM placements WHERE placement_id = :id")
    suspend fun getById(id: Int): PlacementsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(placement: PlacementsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(placements: List<PlacementsEntity>)

    @Update
    suspend fun update(placement: PlacementsEntity)

    @Delete
    suspend fun delete(placement: PlacementsEntity)

    @Query("DELETE FROM placements")
    suspend fun clearAll()
} 