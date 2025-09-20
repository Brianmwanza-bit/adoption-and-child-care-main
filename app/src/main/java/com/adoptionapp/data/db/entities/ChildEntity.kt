package com.adoptionapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "child_id") val childId: Int = 0,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "middle_name") val middleName: String? = null,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "gender") val gender: String? = null,
    @ColumnInfo(name = "date_of_birth") val dateOfBirth: String? = null,
    @ColumnInfo(name = "birth_certificate_no") val birthCertificateNo: String? = null,
    @ColumnInfo(name = "nationality") val nationality: String? = null,
    @ColumnInfo(name = "photo_url") val photoUrl: String? = null,
    @ColumnInfo(name = "is_emancipated") val isEmancipated: Boolean = false,
    @ColumnInfo(name = "emancipation_date") val emancipationDate: String? = null,
    @ColumnInfo(name = "emancipation_reason") val emancipationReason: String? = null,
    @ColumnInfo(name = "current_status") val currentStatus: String? = "Active",
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null
)
