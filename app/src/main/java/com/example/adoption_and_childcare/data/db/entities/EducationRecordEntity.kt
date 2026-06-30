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
public data class EducationRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_RECORD_ID) public val recordId: Int = 0,
    @ColumnInfo(name = COLUMN_CHILD_ID) public val childId: Int,
    @ColumnInfo(name = COLUMN_SCHOOL_NAME) public val schoolName: String,
    @ColumnInfo(name = COLUMN_GRADE) public val grade: String? = null,
    @ColumnInfo(name = COLUMN_ENROLLMENT_DATE) public val enrollmentDate: String? = null,
    @ColumnInfo(name = COLUMN_EXIT_DATE) public val exitDate: String? = null,
    @ColumnInfo(name = COLUMN_PERFORMANCE) public val performance: String? = null,
    @ColumnInfo(name = COLUMN_SPECIAL_NEEDS) public val specialNeeds: String? = null,
    @ColumnInfo(name = COLUMN_TEACHER_CONTACT) public val teacherContact: String? = null,
    @ColumnInfo(name = COLUMN_REPORT_CARD_DATA, typeAffinity = ColumnInfo.BLOB) public val reportCardData: ByteArray? = null,
    @ColumnInfo(name = COLUMN_REPORT_CARD_MIME_TYPE) public val reportCardMimeType: String? = null,
    @ColumnInfo(name = COLUMN_REPORT_CARD_SIZE) public val reportCardSize: Int? = null,
    @ColumnInfo(name = COLUMN_CREATED_AT) public val createdAt: String? = null,
    @ColumnInfo(name = COLUMN_UPDATED_AT) public val updatedAt: String? = null
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

    public companion object {
        /** Name of the database table for education records. */
        @Suppress("HardcodedStringLiteral")
        public const val TABLE_NAME: String = "education_records"
        
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_RECORD_ID: String = "record_id"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_CHILD_ID: String = "child_id"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_SCHOOL_NAME: String = "school_name"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_GRADE: String = "grade"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_ENROLLMENT_DATE: String = "enrollment_date"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_EXIT_DATE: String = "exit_date"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_PERFORMANCE: String = "performance"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_SPECIAL_NEEDS: String = "special_needs"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_TEACHER_CONTACT: String = "teacher_contact"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_REPORT_CARD_DATA: String = "report_card_data"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_REPORT_CARD_MIME_TYPE: String = "report_card_mime_type"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_REPORT_CARD_SIZE: String = "report_card_size"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_CREATED_AT: String = "created_at"
        @Suppress("HardcodedStringLiteral")
        public const val COLUMN_UPDATED_AT: String = "updated_at"
    }
}
