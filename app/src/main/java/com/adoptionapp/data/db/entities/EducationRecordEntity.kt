package com.adoptionapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "education_records")
data class EducationRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id") val recordId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "school_name") val schoolName: String,
    @ColumnInfo(name = "grade") val grade: String? = null,
    @ColumnInfo(name = "enrollment_date") val enrollmentDate: String? = null,
    @ColumnInfo(name = "exit_date") val exitDate: String? = null,
    @ColumnInfo(name = "performance") val performance: String? = null,
    @ColumnInfo(name = "special_needs") val specialNeeds: String? = null,
    @ColumnInfo(name = "teacher_contact") val teacherContact: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
