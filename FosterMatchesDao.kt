import androidx.room.*

@Dao
interface FosterMatchesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(match: FosterMatchesEntity)

    @Update
    suspend fun update(match: FosterMatchesEntity)

    @Delete
    suspend fun delete(match: FosterMatchesEntity)

    @Query("SELECT * FROM foster_matches WHERE family_id = :familyId")
    suspend fun getByFamilyId(familyId: Int): List<FosterMatchesEntity>

    @Query("SELECT * FROM foster_matches")
    suspend fun getAll(): List<FosterMatchesEntity>
} 