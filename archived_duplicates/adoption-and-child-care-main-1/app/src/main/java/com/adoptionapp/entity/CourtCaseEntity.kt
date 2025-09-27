package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "court_cases")
data class CourtCaseEntity(
    @PrimaryKey(autoGenerate = true)
    val case_id: Int = 0,
    val child_id: Int,
    val case_number: String,
    val court_name: String?,
    val judge_name: String?,
    val case_status: String,
    val hearing_date: String?,
    val notes: String?
)
