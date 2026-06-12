package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a case report in the system.
 * 
 * This entity stores reports written by case workers about children's
 * progress, incidents, or other case-related information.
 * 
 * @property reportId Unique identifier for the report (auto-generated).
 * @property childId ID of the child the report is about.
 * @property userId User ID of the report author.
 * @property reportDate Date of the report.
 * @property reportTitle Title of the report.
 * @property reportType Type of report (e.g., Quarterly, Incident).
 * @property content Detailed content of the report.
 * @property isConfidential Whether the report is confidential.
 * @property reportData Binary data of attached report file.
 * @property reportMimeType MIME type of the report file.
 * @property reportSize Size of the report file in bytes.
 * @property createdAt Timestamp when the report was created.
 * @property updatedAt Timestamp when the report was last updated.
 */
@Entity(tableName = "case_reports")
data class CaseReportEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "report_id") val reportId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "report_date") val reportDate: String,
    @ColumnInfo(name = "report_title") val reportTitle: String,
    @ColumnInfo(name = "report_type") val reportType: String? = null,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "is_confidential") val isConfidential: Boolean = false,
    @ColumnInfo(name = "report_data", typeAffinity = ColumnInfo.BLOB) val reportData: ByteArray? = null,
    @ColumnInfo(name = "report_mime_type") val reportMimeType: String? = null,
    @ColumnInfo(name = "report_size") val reportSize: Int? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
