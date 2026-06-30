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
@Entity(tableName = SyncQueueEntity.TABLE_NAME)
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = COLUMN_TABLE_NAME) val tableName: String,
    val operation: String, // 'INSERT', 'UPDATE', 'DELETE'
    @ColumnInfo(name = COLUMN_RECORD_ID) val recordId: String,
    val payload: String,
    @ColumnInfo(name = COLUMN_CREATED_AT) val createdAt: Long,
    val synced: Int = 0,
    @ColumnInfo(name = COLUMN_RETRY_COUNT) val retryCount: Int = 0
) {
    companion object {
        /** Name of the database table for sync queue. */
        const val TABLE_NAME = "sync_queue"
        
        /** Column name for the table name. */
        const val COLUMN_TABLE_NAME = "table_name"
        
        /** Column name for the record ID. */
        const val COLUMN_RECORD_ID = "record_id"
        
        /** Column name for the creation timestamp. */
        const val COLUMN_CREATED_AT = "created_at"
        
        /** Column name for the retry count. */
        const val COLUMN_RETRY_COUNT = "retry_count"
    }
}
