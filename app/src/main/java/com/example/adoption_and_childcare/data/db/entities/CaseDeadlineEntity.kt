package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a deadline on a case.
 *
 * This entity tracks deadlines for various case actions with priorities and statuses.
 *
 * @property deadlineId Unique identifier (auto-generated).
 * @property caseId ID of the case this deadline belongs to.
 * @property deadlineType Type of deadline (home_study_review, placement_decision, legal_review, court_hearing, documentation_renewal, family_contact_required, health_checkup, school_enrollment, background_check_renewal, other).
 * @property dueDate Due date for the deadline.
 * @property title Title of the deadline.
 * @property description Detailed description.
 * @property status Current status (pending, completed, overdue, extended, waived).
 * @property priority Priority level (critical, high, normal).
 * @property responsibleParty Person or role responsible.
 * @property extensionDate Date if the deadline was extended.
 * @property extensionReason Reason for the extension.
 * @property completionNotes Notes upon completion.
 * @property createdAt Date when the record was created.
 * @property completedAt Date when the deadline was completed.
 */
@Entity(tableName = "case_deadlines")
data class CaseDeadlineEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "deadline_id") val deadlineId: Int = 0,
    @ColumnInfo(name = "case_id") val caseId: Int,
    @ColumnInfo(name = "deadline_type") val deadlineType: String,
    @ColumnInfo(name = "due_date") val dueDate: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "status") val status: String? = "pending",
    @ColumnInfo(name = "priority") val priority: String? = "normal",
    @ColumnInfo(name = "responsible_party") val responsibleParty: String? = null,
    @ColumnInfo(name = "extension_date") val extensionDate: String? = null,
    @ColumnInfo(name = "extension_reason") val extensionReason: String? = null,
    @ColumnInfo(name = "completion_notes") val completionNotes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "completed_at") val completedAt: String? = null
)
