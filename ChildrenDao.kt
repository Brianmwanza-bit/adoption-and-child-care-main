import androidx.room.*

@Dao
interface ChildrenDao {
    @Query("SELECT * FROM children")
    suspend fun getAll(): List<ChildrenEntity>

    @Query("SELECT * FROM children WHERE child_id = :id")
    suspend fun getById(id: Int): ChildrenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(children: ChildrenEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(children: List<ChildrenEntity>)

    @Update
    suspend fun update(children: ChildrenEntity)

    @Delete
    suspend fun delete(children: ChildrenEntity)

    @Query("DELETE FROM children")
    suspend fun clearAll()
} 