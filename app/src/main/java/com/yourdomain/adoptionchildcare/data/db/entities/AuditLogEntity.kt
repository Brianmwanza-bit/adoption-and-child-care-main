package com.yourdomain.adoptionchildcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "log_id") val logId: Int = 0,
    @ColumnInfo(name = "table_name") val tableName: String,
    @ColumnInfo(name = "record_id") val recordId: Int,
    @ColumnInfo(name = "action") val action: String,
    @ColumnInfo(name = "changed_by") val changedBy: Int? = null,
    @ColumnInfo(name = "changed_at") val changedAt: String? = null,
    @ColumnInfo(name = "ip_address") val ipAddress: String? = null,
    @ColumnInfo(name = "user_agent") val userAgent: String? = null,
    @ColumnInfo(name = "old_data") val oldData: String? = null,
    @ColumnInfo(name = "new_data") val newData: String? = null
)
