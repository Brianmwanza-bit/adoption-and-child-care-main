package com.yourdomain.adoptionchildcare.data.db.dao

import androidx.room.*
import com.yourdomain.adoptionchildcare.data.db.entities.AuditLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AuditLogEntity): Long

    @Update
    suspend fun update(entity: AuditLogEntity)

    @Query("DELETE FROM audit_logs WHERE log_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM audit_logs ORDER BY changed_at DESC")
    fun observeAll(): Flow<List<AuditLogEntity>>

    @Query("SELECT COUNT(*) FROM audit_logs")
    suspend fun count(): Int
}
