package com.yourdomain.adoptionchildcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "case_reports")
data class CaseReportEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "report_id") val reportId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "report_date") val reportDate: String,
    @ColumnInfo(name = "report_title") val reportTitle: String,
    @ColumnInfo(name = "report_type") val reportType: String? = null,
    @ColumnInfo(name = "adoption_status") val adoptionStatus: String? = null,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "is_confidential") val isConfidential: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
