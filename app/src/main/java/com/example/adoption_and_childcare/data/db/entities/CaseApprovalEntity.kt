package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an approval request on a case.
 *
 * This entity tracks the approval pipeline for case actions.
 *
 * @property approvalId Unique identifier (auto-generated).
 * @property caseId ID of the case this approval belongs to.
 * @property approvalType Type of approval (home_study, placement_authorization, document_verification, legal_review, supervisor_sign_off, family_approval, child_readiness).
 * @property status Current status (pending, approved, rejected, needs_revision, escalated).
 * @property submittedBy User ID who submitted the request.
 * @property reviewedBy User ID who reviewed the request.
 * @property submissionComments Comments from submission.
 * @property reviewComments Comments from review.
 * @property revisionRequestedOn Date when revision was requested.
 * @property submittedDate Date when the request was submitted.
 * @property reviewedDate Date when the request was reviewed.
 * @property requiredApproval Whether this approval is required.
 */
@Entity(tableName = "case_approvals")
data class CaseApprovalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "approval_id") val approvalId: Int = 0,
    @ColumnInfo(name = "case_id") val caseId: Int,
    @ColumnInfo(name = "approval_type") val approvalType: String,
    @ColumnInfo(name = "status") val status: String? = "pending",
    @ColumnInfo(name = "submitted_by") val submittedBy: Int? = null,
    @ColumnInfo(name = "reviewed_by") val reviewedBy: Int? = null,
    @ColumnInfo(name = "submission_comments") val submissionComments: String? = null,
    @ColumnInfo(name = "review_comments") val reviewComments: String? = null,
    @ColumnInfo(name = "revision_requested_on") val revisionRequestedOn: String? = null,
    @ColumnInfo(name = "submitted_date") val submittedDate: String? = null,
    @ColumnInfo(name = "reviewed_date") val reviewedDate: String? = null,
    @ColumnInfo(name = "required_approval") val requiredApproval: Boolean = true
)
