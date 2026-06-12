package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a message between caseworkers.
 *
 * This entity tracks inter-worker communication linked to cases.
 *
 * @property messageId Unique identifier (auto-generated).
 * @property senderId User ID of the message sender.
 * @property recipientId User ID of the message recipient.
 * @property caseId ID of the related case.
 * @property title Subject of the message.
 * @property content Body of the message.
 * @property isRead Whether the recipient has read the message.
 * @property readAt Date when the message was read.
 * @property createdAt Date when the message was sent.
 */
@Entity(tableName = "worker_messages")
data class WorkerMessageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_id") val messageId: Int = 0,
    @ColumnInfo(name = "sender_id") val senderId: Int,
    @ColumnInfo(name = "recipient_id") val recipientId: Int,
    @ColumnInfo(name = "case_id") val caseId: Int? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String? = null,
    @ColumnInfo(name = "is_read") val isRead: Boolean = false,
    @ColumnInfo(name = "read_at") val readAt: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
