package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a risk assessment for a child.
 *
 * This entity tracks safety scores and risk indicators for children in care.
 *
 * @property assessmentId Unique identifier (auto-generated).
 * @property childId ID of the child being assessed.
 * @property assessmentDate Date when the assessment was conducted.
 * @property safetyScore Numerical safety score (0-100).
 * @property riskLevel Risk level (e.g., low, medium, high, critical).
 * @property maltreatmentRiskIndicators JSON string of risk indicators.
 * @property behavioralConcerns Description of behavioral concerns.
 * @property medicalHealthRisks Description of medical health risks.
 * @property educationalGaps Description of educational gaps.
 * @property assessmentBy User ID of the assessor.
 * @property notes Additional notes.
 * @property createdAt Date when the record was created.
 */
@Entity(tableName = "risk_assessments")
data class RiskAssessmentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "assessment_id") val assessmentId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "assessment_date") val assessmentDate: String,
    @ColumnInfo(name = "safety_score") val safetyScore: Int? = null,
    @ColumnInfo(name = "risk_level") val riskLevel: String? = "low",
    @ColumnInfo(name = "maltreatment_risk_indicators") val maltreatmentRiskIndicators: String? = null,
    @ColumnInfo(name = "behavioral_concerns") val behavioralConcerns: String? = null,
    @ColumnInfo(name = "medical_health_risks") val medicalHealthRisks: String? = null,
    @ColumnInfo(name = "educational_gaps") val educationalGaps: String? = null,
    @ColumnInfo(name = "assessment_by") val assessmentBy: Int? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)
