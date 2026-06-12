package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a critical date for a child's case.
 *
 * This entity tracks important dates such as court hearings,
 * review deadlines, and medical appointments.
 *
 * @property dateId Unique identifier (auto-generated).
 * @property childId ID of the child this date belongs to.
 * @property dateType Type of date (e.g., court_hearing, review_deadline).
 * @property eventDate The date of the event.
 * @property isCompleted Whether the event has been completed.
 * @property completedDate Date when the event was completed.
 * @property notes Additional notes about the date.
 * @property createdAt Date when the record was created.
 */
@Entity(tableName = "critical_dates")
data class CriticalDateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "date_id") val dateId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "date_type") val dateType: String,
    @ColumnInfo(name = "event_date") val eventDate: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "completed_date") val completedDate: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
