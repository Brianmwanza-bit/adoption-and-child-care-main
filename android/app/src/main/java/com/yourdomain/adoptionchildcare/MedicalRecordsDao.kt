import androidx.room.*

@Dao
interface MedicalRecordsDao {
    @Query("SELECT * FROM medical_records")
    suspend fun getAll(): List<MedicalRecordsEntity>

    @Query("SELECT * FROM medical_records WHERE record_id = :id")
    suspend fun getById(id: Int): MedicalRecordsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: MedicalRecordsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<MedicalRecordsEntity>)

    @Update
    suspend fun update(record: MedicalRecordsEntity)

    @Delete
    suspend fun delete(record: MedicalRecordsEntity)

    @Query("DELETE FROM medical_records")
    suspend fun clearAll()
} 