package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a foster care task in the system.
 * 
 * This entity tracks tasks assigned to case workers for foster care families.
 * 
 * @property taskId Unique identifier for the task (auto-generated).
 * @property familyId ID of the family the task is for.
 * @property caseWorkerId User ID of the assigned case worker.
 * @property description Description of the task.
 * @property status Current status (e.g., Pending, Urgent, Completed).
 * @property createdAt Date when the task was created.
 * @property dueDate Date when the task is due.
 * @property completedAt Date when the task was completed.
 */
@Entity(tableName = "foster_tasks")
data class FosterTaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id") val taskId: Int = 0,
    @ColumnInfo(name = "family_id") val familyId: Int,
    @ColumnInfo(name = "case_worker_id") val caseWorkerId: Int? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "status") val status: String? = "Pending",
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "due_date") val dueDate: String? = null,
    @ColumnInfo(name = "completed_at") val completedAt: String? = null
)