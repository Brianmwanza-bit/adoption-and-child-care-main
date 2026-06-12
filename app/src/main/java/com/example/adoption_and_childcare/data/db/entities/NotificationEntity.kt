package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a notification in the system.
 * 
 * This entity stores notifications for users, including alerts, reminders,
 * and system messages.
 * 
 * @property notificationId Unique identifier for the notification (auto-generated).
 * @property userId ID of the user receiving the notification.
 * @property title Title of the notification.
 * @property message Detailed message content.
 * @property isRead Whether the notification has been read.
 * @property createdAt Timestamp when the notification was created.
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notification_id") val notificationId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "is_read") val isRead: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
