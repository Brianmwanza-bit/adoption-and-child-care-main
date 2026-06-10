package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
)
