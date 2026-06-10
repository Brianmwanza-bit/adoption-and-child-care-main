package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id") val userId: Int = 0,
    @ColumnInfo(name = "username") val username: String,
    // Store hashes, not plain text
    @ColumnInfo(name = "password_hash") val passwordHash: String,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "national_id_no") val nationalIdNo: String? = null,
    @ColumnInfo(name = "id_number") val idNumber: String? = null,
    @ColumnInfo(name = "county") val county: String? = null,
    @ColumnInfo(name = "sub_county") val subCounty: String? = null,
    @ColumnInfo(name = "photo") val photo: String? = null,
    @ColumnInfo(name = "photo_url") val photoUrl: String? = null,
    @ColumnInfo(name = "photo_data", typeAffinity = ColumnInfo.BLOB) val photoData: ByteArray? = null,
    @ColumnInfo(name = "photo_mime_type") val photoMimeType: String? = null,
    @ColumnInfo(name = "photo_size") val photoSize: Int? = null,
    @ColumnInfo(name = "latitude") val latitude: Double? = null,
    @ColumnInfo(name = "longitude") val longitude: Double? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null,
    @ColumnInfo(name = "last_login") val lastLogin: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)
