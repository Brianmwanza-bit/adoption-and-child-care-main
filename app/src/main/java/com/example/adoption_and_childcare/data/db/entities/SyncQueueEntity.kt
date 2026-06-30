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
public data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) public val id: Int = 0,
    @ColumnInfo(name = COLUMN_TABLE_NAME) public val tableName: String,
    public val operation: String, // 'INSERT', 'UPDATE', 'DELETE'
    @ColumnInfo(name = COLUMN_RECORD_ID) public val recordId: String,
    public val payload: String,
    @ColumnInfo(name = COLUMN_CREATED_AT) public val createdAt: Long,
    public val synced: Int = 0,
    @ColumnInfo(name = COLUMN_RETRY_COUNT) public val retryCount: Int = 0
) {
    public companion object {
        /** Name of the database table for sync queue. */
        @Suppress("HardcodedStringLiteral")
        public const val TABLE_NAME: String = "sync_queue"
        
        /** Column name for the table name. */
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_TABLE_NAME: String = "table_name"
        
        /** Column name for the record ID. */
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_RECORD_ID: String = "record_id"
        
        /** Column name for the creation timestamp. */
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_CREATED_AT: String = "created_at"
        
        /** Column name for the retry count. */
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_RETRY_COUNT: String = "retry_count"
    }
}
