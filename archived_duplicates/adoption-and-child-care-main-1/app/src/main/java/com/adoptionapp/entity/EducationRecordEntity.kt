package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "education_records")
data class EducationRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val record_id: Int = 0,
    val child_id: Int,
    val school_name: String,
    val grade: String?,
    val enrollment_date: String?,
    val exit_date: String?,
    val performance: String?,
    val special_needs: String?,
    val teacher_contact: String?,
    val created_at: String?,
    val updated_at: String?
)
