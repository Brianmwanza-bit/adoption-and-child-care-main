package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a permanency plan for a child.
 *
 * This entity tracks the long-term placement goals for children in care.
 *
 * @property planId Unique identifier (auto-generated).
 * @property childId ID of the child this plan is for.
 * @property planNumber Plan identifier number.
 * @property primaryGoal Primary permanency goal.
 * @property secondaryGoal Secondary permanency goal.
 * @property tertiaryGoal Tertiary permanency goal.
 * @property startDate Date when the plan starts.
 * @property reviewDate Date for the next review.
 * @property completionDate Date when the plan is completed.
 * @property status Current status of the plan.
 * @property concurrentPlanning Whether concurrent planning is enabled.
 * @property notes Additional notes.
 * @property createdAt Date when the record was created.
 */
@Entity(tableName = "permanency_plans")
data class PermanencyPlanEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "plan_id") val planId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "plan_number") val planNumber: String? = null,
    @ColumnInfo(name = "primary_goal") val primaryGoal: String? = null,
    @ColumnInfo(name = "secondary_goal") val secondaryGoal: String? = null,
    @ColumnInfo(name = "tertiary_goal") val tertiaryGoal: String? = null,
    @ColumnInfo(name = "start_date") val startDate: String? = null,
    @ColumnInfo(name = "review_date") val reviewDate: String? = null,
    @ColumnInfo(name = "completion_date") val completionDate: String? = null,
    @ColumnInfo(name = "status") val status: String? = "draft",
    @ColumnInfo(name = "concurrent_planning") val concurrentPlanning: Boolean = false,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
