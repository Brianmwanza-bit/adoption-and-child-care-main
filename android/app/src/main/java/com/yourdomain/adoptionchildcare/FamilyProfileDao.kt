import androidx.room.*

@Dao
interface FamilyProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: FamilyProfileEntity)

    @Update
    suspend fun update(profile: FamilyProfileEntity)

    @Delete
    suspend fun delete(profile: FamilyProfileEntity)

    @Query("SELECT * FROM family_profile WHERE user_id = :userId")
    suspend fun getByUserId(userId: Int): FamilyProfileEntity?

    @Query("SELECT * FROM family_profile")
    suspend fun getAll(): List<FamilyProfileEntity>
} 