import androidx.room.*

@Dao
interface CaseReportsDao {
    @Query("SELECT * FROM case_reports")
    suspend fun getAll(): List<CaseReportsEntity>

    @Query("SELECT * FROM case_reports WHERE report_id = :id")
    suspend fun getById(id: Int): CaseReportsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: CaseReportsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<CaseReportsEntity>)

    @Update
    suspend fun update(report: CaseReportsEntity)

    @Delete
    suspend fun delete(report: CaseReportsEntity)

    @Query("DELETE FROM case_reports")
    suspend fun clearAll()
} 