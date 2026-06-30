package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a sync queue entry for offline-first synchronization.
 * 
 * This entity tracks local changes that need to be synchronized with the remote server.
 * 
 * @property id Unique identifier for the sync entry (auto-generated).
 * @property tableName Name of the table that was modified.
 * @property operation Type of operation (INSERT, UPDATE, DELETE).
 * @property recordId ID of the record that was modified.
 * @property payload JSON payload of the changed data.
 * @property createdAt Timestamp when the change was made.
 * @property synced Whether the change has been synced (0 = pending, 1 = synced).
 * @property retryCount Number of retry attempts for failed syncs.
 */
@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "table_name") val tableName: String,
    val operation: String, // 'INSERT', 'UPDATE', 'DELETE'
    @ColumnInfo(name = "record_id") val recordId: String,
    val payload: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    val synced: Int = 0,
    @ColumnInfo(name = "retry_count") val retryCount: Int = 0
) {
    companion object {
        const val TABLE_NAME = "sync_queue"
        const val OP_INSERT = "INSERT"
        const val OP_UPDATE = "UPDATE"
        const val OP_DELETE = "DELETE"
    }
}
