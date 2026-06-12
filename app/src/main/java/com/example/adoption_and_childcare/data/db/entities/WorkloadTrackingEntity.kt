package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a caseworker's workload tracking entry.
 *
 * This entity tracks daily workload metrics for caseworkers.
 *
 * @property workloadId Unique identifier (auto-generated).
 * @property caseworkerId User ID of the caseworker.
 * @property trackingDate Date of the tracking entry.
 * @property totalActiveCases Total number of active cases.
 * @property casesWithUrgentFlags Number of cases with urgent flags.
 * @property overdueTasksCount Number of overdue tasks.
 * @property scheduledActivitiesToday Number of activities scheduled for today.
 * @property completedActivities Number of completed activities.
 * @property documentsProcessed Number of documents processed.
 * @property approvalsPending Number of pending approvals.
 * @property timeLoggedHours Total hours logged.
 * @property notes Additional notes.
 * @property createdAt Date when the record was created.
 */
@Entity(tableName = "workload_tracking")
data class WorkloadTrackingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "workload_id") val workloadId: Int = 0,
    @ColumnInfo(name = "caseworker_id") val caseworkerId: Int,
    @ColumnInfo(name = "tracking_date") val trackingDate: String,
    @ColumnInfo(name = "total_active_cases") val totalActiveCases: Int = 0,
    @ColumnInfo(name = "cases_with_urgent_flags") val casesWithUrgentFlags: Int = 0,
    @ColumnInfo(name = "overdue_tasks_count") val overdueTasksCount: Int = 0,
    @ColumnInfo(name = "scheduled_activities_today") val scheduledActivitiesToday: Int = 0,
    @ColumnInfo(name = "completed_activities") val completedActivities: Int = 0,
    @ColumnInfo(name = "documents_processed") val documentsProcessed: Int = 0,
    @ColumnInfo(name = "approvals_pending") val approvalsPending: Int = 0,
    @ColumnInfo(name = "time_logged_hours") val timeLoggedHours: Double = 0.0,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
