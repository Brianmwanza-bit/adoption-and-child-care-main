package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an education record in the system.
 * 
 * This entity stores educational information about children, including
 * school enrollment, grades, and academic performance.
 * 
 * @property recordId Unique identifier for the education record (auto-generated).
 * @property childId ID of the child the record belongs to.
 * @property schoolName Name of the school.
 * @property grade Grade level.
 * @property enrollmentDate Date of enrollment.
 * @property exitDate Date when the child left the school.
 * @property performance Academic performance notes.
 * @property specialNeeds Special needs or accommodations.
 * @property teacherContact Contact information for the teacher.
 * @property reportCardData Binary data of report card file.
 * @property reportCardMimeType MIME type of the report card file.
 * @property reportCardSize Size of the report card file in bytes.
 * @property createdAt Timestamp when the record was created.
 * @property updatedAt Timestamp when the record was last updated.
 */
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
    @ColumnInfo(name = "report_card_data", typeAffinity = ColumnInfo.BLOB) val reportCardData: ByteArray? = null,
    @ColumnInfo(name = "report_card_mime_type") val reportCardMimeType: String? = null,
    @ColumnInfo(name = "report_card_size") val reportCardSize: Int? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
) {
    companion object {
        const val TABLE_NAME = "education_records"
    }
}

