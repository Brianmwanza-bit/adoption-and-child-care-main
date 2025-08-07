import androidx.room.*

@Dao
interface EducationRecordsDao {
    @Query("SELECT * FROM education_records")
    suspend fun getAll(): List<EducationRecordsEntity>

    @Query("SELECT * FROM education_records WHERE record_id = :id")
    suspend fun getById(id: Int): EducationRecordsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: EducationRecordsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<EducationRecordsEntity>)

    @Update
    suspend fun update(record: EducationRecordsEntity)

    @Delete
    suspend fun delete(record: EducationRecordsEntity)

    @Query("DELETE FROM education_records")
    suspend fun clearAll()
} 
 