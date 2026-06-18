package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user in the adoption and child care system.
 * 
 * This entity stores user account information including authentication credentials,
 * personal details, contact information, and role-based access control data.
 * Passwords are stored as hashes for security.
 * 
 * @property userId Unique identifier for the user (auto-generated).
 * @property username Unique username for login.
 * @property passwordHash Hashed password for authentication (never store plain text).
 * @property role User role determining permissions (e.g., admin, social_worker, case_worker).
 * @property email User's email address.
 * @property phone User's phone number.
 * @property nationalIdNo National ID number for identification.
 * @property idNumber Alternative ID number.
 * @property county County where the user operates.
 * @property subCounty Sub-county where the user operates.
 * @property photo Profile photo file path.
 * @property photoUrl URL to profile photo.
 * @property photoData Binary data of profile photo.
 * @property photoMimeType MIME type of profile photo.
 * @property photoSize Size of profile photo in bytes.
 * @property latitude User's last known latitude.
 * @property longitude User's last known longitude.
 * @property createdAt Timestamp when the user was created.
 * @property updatedAt Timestamp when the user was last updated.
 * @property lastLogin Timestamp of the last login.
 * @property isActive Whether the user account is active.
 */
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
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    
    // Sync Metadata
    @ColumnInfo(name = "remote_id") val remoteId: String? = null,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "PENDING", // PENDING, SYNCED, ERROR
    @ColumnInfo(name = "last_synced_at") val lastSyncedAt: Long? = null
)
