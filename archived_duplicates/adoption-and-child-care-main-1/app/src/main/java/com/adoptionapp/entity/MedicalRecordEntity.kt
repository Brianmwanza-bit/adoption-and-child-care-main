package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_records")
data class MedicalRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val record_id: Int = 0,
    val child_id: Int,
    val visit_date: String,
    val doctor_name: String?,
    val hospital_name: String?,
    val diagnosis: String?,
    val treatment: String?,
    val medications: String?,
    val follow_up_date: String?,
    val is_immunization: Boolean = false
)
