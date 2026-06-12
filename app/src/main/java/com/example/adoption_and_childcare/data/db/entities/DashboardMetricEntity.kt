package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a dashboard metric in the system.
 *
 * This entity stores calculated metrics displayed on the dashboard.
 *
 * @property metricId Unique identifier for the metric (auto-generated).
 * @property metricName Name of the metric.
 * @property metricValue Current value of the metric.
 * @property previousValue Previous value for trend calculation.
 * @property trendPercentage Percentage change from previous value.
 * @property calculatedDate Date when the metric was calculated.
 * @property dateRangeDays Number of days in the calculation range.
 * @property createdAt Date when the metric record was created.
 */
@Entity(tableName = "dashboard_metrics")
data class DashboardMetricEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "metric_id") val metricId: Int = 0,
    @ColumnInfo(name = "metric_name") val metricName: String,
    @ColumnInfo(name = "metric_value") val metricValue: Double = 0.0,
    @ColumnInfo(name = "metric_label") val metricLabel: String? = null,
    @ColumnInfo(name = "previous_value") val previousValue: Double? = null,
    @ColumnInfo(name = "trend_percentage") val trendPercentage: Double? = null,
    @ColumnInfo(name = "calculated_date") val calculatedDate: String? = null,
    @ColumnInfo(name = "date_range_days") val dateRangeDays: Int? = 30,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
