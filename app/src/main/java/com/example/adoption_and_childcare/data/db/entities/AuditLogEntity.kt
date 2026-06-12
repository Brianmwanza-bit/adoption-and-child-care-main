package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an audit log entry in the system.
 * 
 * This entity tracks all changes to data for security and compliance,
 * recording who changed what, when, and from where.
 * 
 * @property logId Unique identifier for the log entry (auto-generated).
 * @property tableName Name of the table that was modified.
 * @property recordId ID of the record that was modified.
 * @property action Action performed (e.g., Created, Updated, Deleted).
 * @property changedBy User ID of the person who made the change.
 * @property changedAt Timestamp when the change was made.
 * @property ipAddress IP address from which the change was made.
 * @property userAgent User agent string of the client.
 * @property oldData JSON string of the old data (before change).
 * @property newData JSON string of the new data (after change).
 */
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
