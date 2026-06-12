package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a task in the system.
 *
 * This entity tracks tasks assigned to users across various modules.
 *
 * @property taskId Unique identifier for the task (auto-generated).
 * @property title Title of the task.
 * @property description Detailed description of the task.
 * @property priority Priority level (e.g., critical, high, normal).
 * @property status Current status (e.g., pending, in_progress, completed).
 * @property dueDate Date when the task is due.
 * @property assignedTo User ID of the assignee.
 * @property createdBy User ID who created the task.
 * @property relatedEntityType Type of related entity (e.g., child, family).
 * @property relatedEntityId ID of the related entity.
 * @property createdAt Date when the task was created.
 * @property updatedAt Date when the task was last updated.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id") val taskId: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "priority") val priority: String? = "normal",
    @ColumnInfo(name = "status") val status: String? = "pending",
    @ColumnInfo(name = "due_date") val dueDate: String? = null,
    @ColumnInfo(name = "assigned_to") val assignedTo: Int? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null,
    @ColumnInfo(name = "related_entity_type") val relatedEntityType: String? = null,
    @ColumnInfo(name = "related_entity_id") val relatedEntityId: Int? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
