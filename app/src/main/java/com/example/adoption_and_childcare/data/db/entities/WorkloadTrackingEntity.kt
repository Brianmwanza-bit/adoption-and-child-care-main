package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a caseworker's workload tracking entry.
 *
 * This entity tracks daily workload metrics for caseworkers.
 */
@Entity(tableName = "workload_tracking")
data class WorkloadTrackingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "workload_id") val workloadId: Int = 0,
    @ColumnInfo(name = "caseworker_id") val caseworkerId: Int,
    @ColumnInfo(name = "tracking_date") val trackingDate: String,
    @ColumnInfo(name = "total_active_cases") val totalActiveCases: Int = 0,
    @ColumnInfo(name = "tasks_pending") val tasksPending: Int = 0,
    @ColumnInfo(name = "approvals_pending") val approvalsPending: Int = 0,
    @ColumnInfo(name = "deadlines_overdue") val deadlinesOverdue: Int = 0,
    @ColumnInfo(name = "cases_with_urgent_flags") val casesWithUrgentFlags: Int = 0,
    @ColumnInfo(name = "time_logged_hours") val timeLoggedHours: Double = 0.0,
    @ColumnInfo(name = "overdue_tasks_count") val overdueTasksCount: Int = 0,
    @ColumnInfo(name = "home_visits_scheduled") val homeVisitsScheduled: Int = 0,
    @ColumnInfo(name = "home_visits_completed") val homeVisitsCompleted: Int = 0,
    @ColumnInfo(name = "reports_submitted") val reportsSubmitted: Int = 0,
    @ColumnInfo(name = "productivity_score") val productivityScore: Double = 0.0,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
