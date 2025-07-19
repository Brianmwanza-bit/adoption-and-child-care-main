import androidx.room.*

@Dao
interface CourtCasesDao {
    @Query("SELECT * FROM court_cases")
    suspend fun getAll(): List<CourtCasesEntity>

    @Query("SELECT * FROM court_cases WHERE case_id = :id")
    suspend fun getById(id: Int): CourtCasesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(case: CourtCasesEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cases: List<CourtCasesEntity>)

    @Update
    suspend fun update(case: CourtCasesEntity)

    @Delete
    suspend fun delete(case: CourtCasesEntity)

    @Query("DELETE FROM court_cases")
    suspend fun clearAll()
} 