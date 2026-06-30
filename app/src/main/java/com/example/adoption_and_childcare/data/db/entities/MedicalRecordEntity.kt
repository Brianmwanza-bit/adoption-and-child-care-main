package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a medical record in the system.
 * 
 * This entity stores medical information about children, including
 * visits, diagnoses, treatments, and immunizations.
 * 
 * @property recordId Unique identifier for the medical record (auto-generated).
 * @property childId ID of the child the record belongs to.
 * @property visitDate Date of the medical visit.
 * @property doctorName Name of the attending doctor.
 * @property hospitalName Name of the hospital or clinic.
 * @property diagnosis Medical diagnosis.
 * @property treatment Treatment prescribed.
 * @property medications Medications prescribed.
 * @property followUpDate Date for follow-up appointment.
 * @property isImmunization Whether this record is for an immunization.
 * @property immunizationType Type of immunization.
 * @property medicalReportData Binary data of medical report file.
 * @property medicalReportMimeType MIME type of the medical report file.
 * @property medicalReportSize Size of the medical report file in bytes.
 * @property createdAt Timestamp when the record was created.
 * @property createdBy User ID of the creator.
 */
@Entity(tableName = MedicalRecordEntity.TABLE_NAME)
data class MedicalRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_RECORD_ID) val recordId: Int = 0,
    @ColumnInfo(name = COLUMN_CHILD_ID) val childId: Int,
    @ColumnInfo(name = COLUMN_VISIT_DATE) val visitDate: String,
    @ColumnInfo(name = COLUMN_DOCTOR_NAME) val doctorName: String? = null,
    @ColumnInfo(name = COLUMN_HOSPITAL_NAME) val hospitalName: String? = null,
    @ColumnInfo(name = COLUMN_DIAGNOSIS) val diagnosis: String? = null,
    @ColumnInfo(name = COLUMN_TREATMENT) val treatment: String? = null,
    @ColumnInfo(name = COLUMN_MEDICATIONS) val medications: String? = null,
    @ColumnInfo(name = COLUMN_FOLLOW_UP_DATE) val followUpDate: String? = null,
    @ColumnInfo(name = COLUMN_IS_IMMUNIZATION) val isImmunization: Boolean = false,
    @ColumnInfo(name = COLUMN_IMMUNIZATION_TYPE) val immunizationType: String? = null,
    @ColumnInfo(name = COLUMN_MEDICAL_REPORT_DATA, typeAffinity = ColumnInfo.BLOB) val medicalReportData: ByteArray? = null,
    @ColumnInfo(name = COLUMN_MEDICAL_REPORT_MIME_TYPE) val medicalReportMimeType: String? = null,
    @ColumnInfo(name = COLUMN_MEDICAL_REPORT_SIZE) val medicalReportSize: Int? = null,
    @ColumnInfo(name = COLUMN_CREATED_AT) val createdAt: String? = null,
    @ColumnInfo(name = COLUMN_CREATED_BY) val createdBy: Int? = null
) {
    /**
     * Compares this medical record entity with another object for equality.
     * 
     * @param other The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MedicalRecordEntity

        if (recordId != other.recordId) return false
        if (childId != other.childId) return false
        if (visitDate != other.visitDate) return false
        if (doctorName != other.doctorName) return false
        if (hospitalName != other.hospitalName) return false
        if (diagnosis != other.diagnosis) return false
        if (treatment != other.treatment) return false
        if (medications != other.medications) return false
        if (followUpDate != other.followUpDate) return false
        if (isImmunization != other.isImmunization) return false
        if (immunizationType != other.immunizationType) return false
        if (medicalReportData != null) {
            if (other.medicalReportData == null) return false
            if (!medicalReportData.contentEquals(other.medicalReportData)) return false
        } else if (other.medicalReportData != null) return false
        if (medicalReportMimeType != other.medicalReportMimeType) return false
        if (medicalReportSize != other.medicalReportSize) return false
        if (createdAt != other.createdAt) return false
        if (createdBy != other.createdBy) return false

        return true
    }

    /**
     * Generates a hash code for this medical record entity.
     * 
     * @return The hash code.
     */
    override fun hashCode(): Int {
        var result = recordId
        result = 31 * result + childId
        result = 31 * result + visitDate.hashCode()
        result = 31 * result + (doctorName?.hashCode() ?: 0)
        result = 31 * result + (hospitalName?.hashCode() ?: 0)
        result = 31 * result + (diagnosis?.hashCode() ?: 0)
        result = 31 * result + (treatment?.hashCode() ?: 0)
        result = 31 * result + (medications?.hashCode() ?: 0)
        result = 31 * result + (followUpDate?.hashCode() ?: 0)
        result = 31 * result + isImmunization.hashCode()
        result = 31 * result + (immunizationType?.hashCode() ?: 0)
        result = 31 * result + (medicalReportData?.contentHashCode() ?: 0)
        result = 31 * result + (medicalReportMimeType?.hashCode() ?: 0)
        result = 31 * result + (medicalReportSize ?: 0)
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + (createdBy ?: 0)
        return result
    }

    companion object {
        /** Name of the database table for medical records. */
        const val TABLE_NAME = "medical_records"
        
        const val COLUMN_RECORD_ID = "record_id"
        const val COLUMN_CHILD_ID = "child_id"
        const val COLUMN_VISIT_DATE = "visit_date"
        const val COLUMN_DOCTOR_NAME = "doctor_name"
        const val COLUMN_HOSPITAL_NAME = "hospital_name"
        const val COLUMN_DIAGNOSIS = "diagnosis"
        const val COLUMN_TREATMENT = "treatment"
        const val COLUMN_MEDICATIONS = "medications"
        const val COLUMN_FOLLOW_UP_DATE = "follow_up_date"
        const val COLUMN_IS_IMMUNIZATION = "is_immunization"
        const val COLUMN_IMMUNIZATION_TYPE = "immunization_type"
        const val COLUMN_MEDICAL_REPORT_DATA = "medical_report_data"
        const val COLUMN_MEDICAL_REPORT_MIME_TYPE = "medical_report_mime_type"
        const val COLUMN_MEDICAL_REPORT_SIZE = "medical_report_size"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_CREATED_BY = "created_by"
    }
}
