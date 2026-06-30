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
@Entity(tableName = EducationRecordEntity.TABLE_NAME)
data class EducationRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_RECORD_ID) val recordId: Int = 0,
    @ColumnInfo(name = COLUMN_CHILD_ID) val childId: Int,
    @ColumnInfo(name = COLUMN_SCHOOL_NAME) val schoolName: String,
    @ColumnInfo(name = COLUMN_GRADE) val grade: String? = null,
    @ColumnInfo(name = COLUMN_ENROLLMENT_DATE) val enrollmentDate: String? = null,
    @ColumnInfo(name = COLUMN_EXIT_DATE) val exitDate: String? = null,
    @ColumnInfo(name = COLUMN_PERFORMANCE) val performance: String? = null,
    @ColumnInfo(name = COLUMN_SPECIAL_NEEDS) val specialNeeds: String? = null,
    @ColumnInfo(name = COLUMN_TEACHER_CONTACT) val teacherContact: String? = null,
    @ColumnInfo(name = COLUMN_REPORT_CARD_DATA, typeAffinity = ColumnInfo.BLOB) val reportCardData: ByteArray? = null,
    @ColumnInfo(name = COLUMN_REPORT_CARD_MIME_TYPE) val reportCardMimeType: String? = null,
    @ColumnInfo(name = COLUMN_REPORT_CARD_SIZE) val reportCardSize: Int? = null,
    @ColumnInfo(name = COLUMN_CREATED_AT) val createdAt: String? = null,
    @ColumnInfo(name = COLUMN_UPDATED_AT) val updatedAt: String? = null
) {
    /**
     * Compares this education record entity with another object for equality.
     * 
     * @param other The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EducationRecordEntity

        if (recordId != other.recordId) return false
        if (childId != other.childId) return false
        if (schoolName != other.schoolName) return false
        if (grade != other.grade) return false
        if (enrollmentDate != other.enrollmentDate) return false
        if (exitDate != other.exitDate) return false
        if (performance != other.performance) return false
        if (specialNeeds != other.specialNeeds) return false
        if (teacherContact != other.teacherContact) return false
        if (reportCardData != null) {
            if (other.reportCardData == null) return false
            if (!reportCardData.contentEquals(other.reportCardData)) return false
        } else if (other.reportCardData != null) return false
        if (reportCardMimeType != other.reportCardMimeType) return false
        if (reportCardSize != other.reportCardSize) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    /**
     * Generates a hash code for this education record entity.
     * 
     * @return The hash code.
     */
    override fun hashCode(): Int {
        var result = recordId
        result = 31 * result + childId
        result = 31 * result + schoolName.hashCode()
        result = 31 * result + (grade?.hashCode() ?: 0)
        result = 31 * result + (enrollmentDate?.hashCode() ?: 0)
        result = 31 * result + (exitDate?.hashCode() ?: 0)
        result = 31 * result + (performance?.hashCode() ?: 0)
        result = 31 * result + (specialNeeds?.hashCode() ?: 0)
        result = 31 * result + (teacherContact?.hashCode() ?: 0)
        result = 31 * result + (reportCardData?.contentHashCode() ?: 0)
        result = 31 * result + (reportCardMimeType?.hashCode() ?: 0)
        result = 31 * result + (reportCardSize ?: 0)
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        return result
    }

    companion object {
        /** Name of the database table for education records. */
        const val TABLE_NAME = "education_records"
        
        const val COLUMN_RECORD_ID = "record_id"
        const val COLUMN_CHILD_ID = "child_id"
        const val COLUMN_SCHOOL_NAME = "school_name"
        const val COLUMN_GRADE = "grade"
        const val COLUMN_ENROLLMENT_DATE = "enrollment_date"
        const val COLUMN_EXIT_DATE = "exit_date"
        const val COLUMN_PERFORMANCE = "performance"
        const val COLUMN_SPECIAL_NEEDS = "special_needs"
        const val COLUMN_TEACHER_CONTACT = "teacher_contact"
        const val COLUMN_REPORT_CARD_DATA = "report_card_data"
        const val COLUMN_REPORT_CARD_MIME_TYPE = "report_card_mime_type"
        const val COLUMN_REPORT_CARD_SIZE = "report_card_size"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_UPDATED_AT = "updated_at"
    }
}
