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
@Entity(tableName = "medical_records")
data class MedicalRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id") val recordId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "visit_date") val visitDate: String,
    @ColumnInfo(name = "doctor_name") val doctorName: String? = null,
    @ColumnInfo(name = "hospital_name") val hospitalName: String? = null,
    @ColumnInfo(name = "diagnosis") val diagnosis: String? = null,
    @ColumnInfo(name = "treatment") val treatment: String? = null,
    @ColumnInfo(name = "medications") val medications: String? = null,
    @ColumnInfo(name = "follow_up_date") val followUpDate: String? = null,
    @ColumnInfo(name = "is_immunization") val isImmunization: Boolean = false,
    @ColumnInfo(name = "immunization_type") val immunizationType: String? = null,
    @ColumnInfo(name = "medical_report_data", typeAffinity = ColumnInfo.BLOB) val medicalReportData: ByteArray? = null,
    @ColumnInfo(name = "medical_report_mime_type") val medicalReportMimeType: String? = null,
    @ColumnInfo(name = "medical_report_size") val medicalReportSize: Int? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null
)
