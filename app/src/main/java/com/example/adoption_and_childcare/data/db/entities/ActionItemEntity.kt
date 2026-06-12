package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an action item in the system.
 *
 * This entity tracks actionable items with priorities and due dates.
 *
 * @property actionId Unique identifier for the action item (auto-generated).
 * @property title Title of the action item.
 * @property priority Priority level (e.g., urgent, high, normal).
 * @property dueDate Date when the action item is due.
 * @property assigneeId User ID of the person assigned.
 * @property relatedCaseId ID of the related case.
 * @property status Current status (e.g., pending, completed).
 * @property createdAt Date when the action item was created.
 */
@Entity(tableName = "action_items")
data class ActionItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "action_id") val actionId: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "priority") val priority: String? = "normal",
    @ColumnInfo(name = "due_date") val dueDate: String? = null,
    @ColumnInfo(name = "assignee_id") val assigneeId: Int? = null,
    @ColumnInfo(name = "related_case_id") val relatedCaseId: Int? = null,
    @ColumnInfo(name = "status") val status: String? = "pending",
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
