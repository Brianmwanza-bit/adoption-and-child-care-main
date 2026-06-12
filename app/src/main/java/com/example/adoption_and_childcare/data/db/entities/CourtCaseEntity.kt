package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a court case in the system.
 * 
 * This entity tracks legal proceedings related to adoption cases,
 * including court dates, judges, and case outcomes.
 * 
 * @property caseId Unique identifier for the court case (auto-generated).
 * @property childId ID of the child involved in the case.
 * @property caseNumber Case number assigned by the court.
 * @property courtName Name of the court.
 * @property judgeName Name of the presiding judge.
 * @property caseType Type of case (e.g., Adoption, Custody).
 * @property filingDate Date when the case was filed.
 * @property hearingDate Date of the hearing.
 * @property outcome Outcome of the case.
 * @property status Current status (e.g., Pending, Closed).
 * @property nextHearingDate Date of the next hearing.
 * @property createdAt Timestamp when the record was created.
 * @property updatedAt Timestamp when the record was last updated.
 */
@Entity(tableName = "court_cases")
data class CourtCaseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "case_id") val caseId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "case_number") val caseNumber: String? = null,
    @ColumnInfo(name = "court_name") val courtName: String? = null,
    @ColumnInfo(name = "judge_name") val judgeName: String? = null,
    @ColumnInfo(name = "case_type") val caseType: String? = null,
    @ColumnInfo(name = "filing_date") val filingDate: String? = null,
    @ColumnInfo(name = "hearing_date") val hearingDate: String? = null,
    @ColumnInfo(name = "outcome") val outcome: String? = null,
    @ColumnInfo(name = "status") val status: String? = "Pending",
    @ColumnInfo(name = "next_hearing_date") val nextHearingDate: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
