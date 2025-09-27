package com.yourdomain.adoptionchildcare.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "child_id") val childId: Int = 0,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "middle_name") val middleName: String?,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "gender") val gender: String?,
    @ColumnInfo(name = "date_of_birth") val dateOfBirth: String?,
    @ColumnInfo(name = "birth_certificate_no") val birthCertificateNo: String?,
    @ColumnInfo(name = "nationality") val nationality: String?,
    @ColumnInfo(name = "photo_url") val photoUrl: String?,
    @ColumnInfo(name = "is_emancipated") val isEmancipated: Boolean = false,
    @ColumnInfo(name = "emancipation_date") val emancipationDate: String?,
    @ColumnInfo(name = "emancipation_reason") val emancipationReason: String?,
    @ColumnInfo(name = "current_status") val currentStatus: String? = "Active",
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int?
)
