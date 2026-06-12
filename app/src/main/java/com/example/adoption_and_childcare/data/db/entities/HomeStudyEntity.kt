package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a home study evaluation in the system.
 * 
 * This entity tracks home study assessments conducted for families
 * interested in adoption or foster care.
 * 
 * @property homeStudyId Unique identifier for the home study (auto-generated).
 * @property familyId ID of the family being evaluated.
 * @property startedAt Date when the home study began.
 * @property completedAt Date when the home study was completed.
 * @property result Result of the home study (e.g., Approved, Under Review).
 * @property notes Additional notes about the home study.
 * @property socialWorkerId User ID of the social worker conducting the study.
 * @property updatedAt Timestamp when the record was last updated.
 */
@Entity(tableName = "home_studies")
data class HomeStudyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "home_study_id") val homeStudyId: Int = 0,
    @ColumnInfo(name = "family_id") val familyId: Int,
    @ColumnInfo(name = "started_at") val startedAt: String? = null,
    @ColumnInfo(name = "completed_at") val completedAt: String? = null,
    @ColumnInfo(name = "result") val result: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "social_worker_id") val socialWorkerId: Int? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
