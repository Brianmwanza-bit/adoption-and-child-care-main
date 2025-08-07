import androidx.room.*

@Dao
interface MoneyRecordsDao {
    @Query("SELECT * FROM money_records")
    suspend fun getAll(): List<MoneyRecordsEntity>

    @Query("SELECT * FROM money_records WHERE money_id = :id")
    suspend fun getById(id: Int): MoneyRecordsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: MoneyRecordsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<MoneyRecordsEntity>)

    @Update
    suspend fun update(record: MoneyRecordsEntity)

    @Delete
    suspend fun delete(record: MoneyRecordsEntity)

    @Query("DELETE FROM money_records")
    suspend fun clearAll()
} 