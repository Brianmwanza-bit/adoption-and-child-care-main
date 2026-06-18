package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investigations")
data class InvestigationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "investigation_id") val investigationId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "case_number") val caseNumber: String? = null,
    @ColumnInfo(name = "investigation_type") val investigationType: String? = null,
    @ColumnInfo(name = "opened_date") val openedDate: String,
    @ColumnInfo(name = "closed_date") val closedDate: String? = null,
    @ColumnInfo(name = "status") val status: String? = "open",
    @ColumnInfo(name = "allegation") val allegation: String? = null,
    @ColumnInfo(name = "findings") val findings: String? = null,
    @ColumnInfo(name = "recommendations") val recommendations: String? = null,
    @ColumnInfo(name = "investigator_id") val investigatorId: Int? = null,
    @ColumnInfo(name = "supervisor_id") val supervisorId: Int? = null,
    @ColumnInfo(name = "report_file") val reportFile: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "service_plans")
data class ServicePlanEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "plan_id") val planId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "plan_name") val planName: String,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String? = null,
    @ColumnInfo(name = "status") val status: String? = "active",
    @ColumnInfo(name = "goals_summary") val goalsSummary: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null,
    @ColumnInfo(name = "approved_by") val approvedBy: Int? = null,
    @ColumnInfo(name = "approval_date") val approvalDate: String? = null,
    @ColumnInfo(name = "review_date") val reviewDate: String? = null,
    @ColumnInfo(name = "next_review_date") val nextReviewDate: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "service_plan_goals")
data class ServicePlanGoalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "goal_id") val goalId: Int = 0,
    @ColumnInfo(name = "plan_id") val planId: Int,
    @ColumnInfo(name = "goal_description") val goalDescription: String,
    @ColumnInfo(name = "target_date") val targetDate: String? = null,
    @ColumnInfo(name = "status") val status: String? = "not_started",
    @ColumnInfo(name = "completion_date") val completionDate: String? = null,
    @ColumnInfo(name = "completion_notes") val completionNotes: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "visitation_schedules")
data class VisitationScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "visitation_id") val visitationId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "visitor_name") val visitorName: String,
    @ColumnInfo(name = "visitor_relationship") val visitorRelationship: String? = null,
    @ColumnInfo(name = "visitation_date") val visitationDate: String,
    @ColumnInfo(name = "start_time") val startTime: String? = null,
    @ColumnInfo(name = "end_time") val endTime: String? = null,
    @ColumnInfo(name = "location") val location: String? = null,
    @ColumnInfo(name = "supervised_by") val supervisedBy: Int? = null,
    @ColumnInfo(name = "status") val status: String? = "scheduled",
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "referrals")
data class ReferralEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "referral_id") val referralId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int? = null,
    @ColumnInfo(name = "referral_type") val referralType: String? = null,
    @ColumnInfo(name = "referred_to") val referredTo: String? = null,
    @ColumnInfo(name = "reason") val reason: String? = null,
    @ColumnInfo(name = "referral_date") val referralDate: String? = null,
    @ColumnInfo(name = "outcome") val outcome: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "aftercare_plans")
data class AftercarePlanEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "aftercare_id") val aftercareId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "plan_name") val planName: String,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String? = null,
    @ColumnInfo(name = "support_services") val supportServices: String? = null,
    @ColumnInfo(name = "housing_arrangement") val housingArrangement: String? = null,
    @ColumnInfo(name = "education_employment") val educationEmployment: String? = null,
    @ColumnInfo(name = "financial_support") val financialSupport: String? = null,
    @ColumnInfo(name = "mentorship_assigned") val mentorshipAssigned: String? = null,
    @ColumnInfo(name = "caseworker_id") val caseworkerId: Int? = null,
    @ColumnInfo(name = "status") val status: String? = "active",
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
