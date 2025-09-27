package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "case_reports")
data class CaseReportEntity(
    @PrimaryKey(autoGenerate = true)
    val report_id: Int = 0,
    val child_id: Int,
    val user_id: Int,
    val report_date: String,
    val report_title: String,
    val report_type: String?,
    val content: String,
    val is_confidential: Boolean = false,
    val created_at: String?,
    val updated_at: String?
)
