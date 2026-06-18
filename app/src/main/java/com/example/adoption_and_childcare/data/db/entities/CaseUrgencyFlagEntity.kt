package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an urgency flag on a case.
 *
 * This entity tracks critical/high/normal flags on cases requiring attention.
 *
 * @property flagId Unique identifier (auto-generated).
 * @property caseId ID of the flagged case.
 * @property flagType Type of flag (critical, high, normal).
 * @property reason Reason for the flag.
 * @property riskLevel Risk level (High, Medium, Normal).
 * @property description Detailed description.
 * @property createdAt Date when the flag was raised.
 * @property createdBy User ID who raised the flag.
 * @property resolvedAt Date when the flag was resolved.
 * @property resolvedBy User ID who resolved the flag.
 */
@Entity(tableName = "case_urgency_flags")
data class CaseUrgencyFlagEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "flag_id") val flagId: Int = 0,
    @ColumnInfo(name = "case_id") val caseId: Int,
    @ColumnInfo(name = "flag_type") val flagType: String,
    @ColumnInfo(name = "reason") val reason: String? = null,
    @ColumnInfo(name = "risk_level") val riskLevel: String? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null,
    @ColumnInfo(name = "resolved_at") val resolvedAt: String? = null,
    @ColumnInfo(name = "resolved_by") val resolvedBy: Int? = null
)
