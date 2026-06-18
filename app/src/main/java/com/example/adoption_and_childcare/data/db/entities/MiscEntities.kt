package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_meetings")
data class FamilyMeetingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "meeting_id") val meetingId: Int = 0,
    @ColumnInfo(name = "family_id") val familyId: Int,
    @ColumnInfo(name = "meeting_date") val meetingDate: String,
    @ColumnInfo(name = "topic") val topic: String? = null,
    @ColumnInfo(name = "outcome") val outcome: String? = null,
    @ColumnInfo(name = "caseworker_id") val caseworkerId: Int? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "child_development_metrics")
data class ChildDevelopmentMetricEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "metric_id") val metricId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "measurement_date") val measurementDate: String,
    @ColumnInfo(name = "weight_kg") val weightKg: Double? = null,
    @ColumnInfo(name = "height_cm") val heightCm: Double? = null,
    @ColumnInfo(name = "head_circumference_cm") val headCircumferenceCm: Double? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "staff_resources")
data class StaffResourceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "resource_id") val resourceId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "resource_name") val resourceName: String,
    @ColumnInfo(name = "resource_type") val resourceType: String? = null,
    @ColumnInfo(name = "serial_number") val serialNumber: String? = null,
    @ColumnInfo(name = "assigned_date") val assignedDate: String? = null,
    @ColumnInfo(name = "status") val status: String? = "active",
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
