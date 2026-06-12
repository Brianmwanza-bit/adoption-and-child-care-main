package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a caseworker's caseload snapshot.
 *
 * This entity tracks daily caseload metrics for each worker.
 *
 * @property caseloadId Unique identifier (auto-generated).
 * @property workerId User ID of the caseworker.
 * @property date Date of the snapshot.
 * @property activeCases Number of active cases.
 * @property pendingReviews Number of pending reviews.
 * @property overdueTasks Number of overdue tasks.
 * @property capacityPercentage Capacity percentage (0-100).
 * @property createdAt Date when the record was created.
 */
@Entity(tableName = "caseload")
data class CaseloadEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "caseload_id") val caseloadId: Int = 0,
    @ColumnInfo(name = "worker_id") val workerId: Int,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "active_cases") val activeCases: Int = 0,
    @ColumnInfo(name = "pending_reviews") val pendingReviews: Int = 0,
    @ColumnInfo(name = "overdue_tasks") val overdueTasks: Int = 0,
    @ColumnInfo(name = "capacity_percentage") val capacityPercentage: Double = 0.0,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
