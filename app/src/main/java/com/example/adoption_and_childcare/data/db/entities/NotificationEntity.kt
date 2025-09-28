package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notification_id") val notification_id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "is_read") val is_read: Boolean = false,
    @ColumnInfo(name = "created_at") val created_at: String? = null,
    @ColumnInfo(name = "user_id") val user_id: Int? = null
)
