package com.adoptionapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null
)
