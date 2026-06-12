package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an activity on a case.
 *
 * This entity tracks all case-related activities such as home visits,
 * contact calls, approvals, reviews, and legal actions.
 *
 * @property activityId Unique identifier (auto-generated).
 * @property caseId ID of the case this activity belongs to.
 * @property activityType Type of activity (home_visit, contact_call, approval, review, legal_action, placement_change, document_update, other).
 * @property activityDate Date of the activity.
 * @property activityTime Time of the activity.
 * @property title Title of the activity.
 * @property notes Notes about the activity.
 * @property caseworkerId User ID of the caseworker.
 * @property location Location where the activity took place.
 * @property durationMinutes Duration of the activity in minutes.
 * @property outcome Outcome of the activity.
 * @property createdAt Date when the record was created.
 * @property updatedAt Date when the record was last updated.
 */
@Entity(tableName = "case_activities")
data class CaseActivityEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "activity_id") val activityId: Int = 0,
    @ColumnInfo(name = "case_id") val caseId: Int,
    @ColumnInfo(name = "activity_type") val activityType: String,
    @ColumnInfo(name = "activity_date") val activityDate: String? = null,
    @ColumnInfo(name = "activity_time") val activityTime: String? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "caseworker_id") val caseworkerId: Int? = null,
    @ColumnInfo(name = "location") val location: String? = null,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int? = null,
    @ColumnInfo(name = "outcome") val outcome: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
