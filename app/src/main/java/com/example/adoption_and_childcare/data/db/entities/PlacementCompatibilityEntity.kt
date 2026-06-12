package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a placement compatibility assessment.
 *
 * This entity tracks how well a child matches with a potential placement family.
 *
 * @property compatibilityId Unique identifier (auto-generated).
 * @property childId ID of the child.
 * @property familyId ID of the family.
 * @property compatibilityScore Overall compatibility score (0-100).
 * @property medicalNeedsSupport Score for medical needs support.
 * @property behavioralNeedsSupport Score for behavioral needs support.
 * @property educationalNeedsSupport Score for educational needs support.
 * @property emotionalSupportCapacity Score for emotional support capacity.
 * @property geographicPreferencesMatch Score for geographic preferences match.
 * @property religiousPreferenceMatch Score for religious preference match.
 * @property culturalFitScore Score for cultural fit.
 * @property specialConsiderations Any special considerations.
 * @property notes Additional notes.
 * @property assessmentDate Date of the assessment.
 * @property assessedBy User ID of the assessor.
 * @property lastReviewed Date when last reviewed.
 */
@Entity(tableName = "placement_compatibility")
data class PlacementCompatibilityEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "compatibility_id") val compatibilityId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "family_id") val familyId: Int,
    @ColumnInfo(name = "compatibility_score") val compatibilityScore: Double? = null,
    @ColumnInfo(name = "medical_needs_support") val medicalNeedsSupport: Double? = null,
    @ColumnInfo(name = "behavioral_needs_support") val behavioralNeedsSupport: Double? = null,
    @ColumnInfo(name = "educational_needs_support") val educationalNeedsSupport: Double? = null,
    @ColumnInfo(name = "emotional_support_capacity") val emotionalSupportCapacity: Double? = null,
    @ColumnInfo(name = "geographic_preferences_match") val geographicPreferencesMatch: Double? = null,
    @ColumnInfo(name = "religious_preference_match") val religiousPreferenceMatch: Double? = null,
    @ColumnInfo(name = "cultural_fit_score") val culturalFitScore: Double? = null,
    @ColumnInfo(name = "special_considerations") val specialConsiderations: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "assessment_date") val assessmentDate: String? = null,
    @ColumnInfo(name = "assessed_by") val assessedBy: Int? = null,
    @ColumnInfo(name = "last_reviewed") val lastReviewed: String? = null
)
