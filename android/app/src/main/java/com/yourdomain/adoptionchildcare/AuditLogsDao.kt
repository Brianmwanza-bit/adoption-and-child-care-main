import androidx.room.*

@Dao
interface AuditLogsDao {
    @Query("SELECT * FROM audit_logs")
    suspend fun getAll(): List<AuditLogsEntity>

    @Query("SELECT * FROM audit_logs WHERE log_id = :id")
    suspend fun getById(id: Int): AuditLogsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AuditLogsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<AuditLogsEntity>)

    @Update
    suspend fun update(log: AuditLogsEntity)

    @Delete
    suspend fun delete(log: AuditLogsEntity)

    @Query("DELETE FROM audit_logs")
    suspend fun clearAll()
} 