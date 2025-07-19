import androidx.room.*

@Dao
interface FosterTasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: FosterTasksEntity)

    @Update
    suspend fun update(task: FosterTasksEntity)

    @Delete
    suspend fun delete(task: FosterTasksEntity)

    @Query("SELECT * FROM foster_tasks WHERE family_id = :familyId")
    suspend fun getByFamilyId(familyId: Int): List<FosterTasksEntity>

    @Query("SELECT * FROM foster_tasks")
    suspend fun getAll(): List<FosterTasksEntity>
} 