package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a child in the adoption and child care system.
 * 
 * This entity stores comprehensive information about children under care,
 * including personal details, placement status, case information, and sync metadata
 * for offline-first functionality.
 * 
 * @property childId Unique identifier for the child (auto-generated).
 * @property caseNumber Unique case number assigned to the child.
 * @property firstName Child's first name.
 * @property middleName Child's middle name.
 * @property lastName Child's last name.
 * @property gender Child's gender.
 * @property dateOfBirth Child's date of birth.
 * @property birthCertificateNo Birth certificate number.
 * @property nationality Child's nationality.
 * @property photoUrl URL to child's photo.
 * @property photoData Binary data of child's photo.
 * @property photoMimeType MIME type of child's photo.
 * @property photoSize Size of child's photo in bytes.
 * @property currentCounty County where the child is currently located.
 * @property county County of origin.
 * @property isEmancipated Whether the child is legally emancipated.
 * @property emancipationDate Date of emancipation.
 * @property emancipationReason Reason for emancipation.
 * @property currentStatus Current status of the child (e.g., Active, Placed).
 * @property createdAt Timestamp when the record was created.
 * @property updatedAt Timestamp when the record was last updated.
 * @property createdBy User ID of the creator.
 * @property assignedCaseWorker User ID of the assigned case worker.
 * @property remoteId Remote server ID for synchronization.
 * @property syncStatus Synchronization status (PENDING, SYNCED, ERROR).
 * @property lastSyncedAt Timestamp of last synchronization.
 */
@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "child_id") val childId: Int = 0,
    @ColumnInfo(name = "case_number") val caseNumber: String? = null,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "middle_name") val middleName: String? = null,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "gender") val gender: String? = null,
    @ColumnInfo(name = "date_of_birth") val dateOfBirth: String? = null,
    @ColumnInfo(name = "birth_certificate_no") val birthCertificateNo: String? = null,
    @ColumnInfo(name = "nationality") val nationality: String? = null,
    @ColumnInfo(name = "photo_url") val photoUrl: String? = null,
    @ColumnInfo(name = "photo_data", typeAffinity = ColumnInfo.BLOB) val photoData: ByteArray? = null,
    @ColumnInfo(name = "photo_mime_type") val photoMimeType: String? = null,
    @ColumnInfo(name = "photo_size") val photoSize: Int? = null,
    @ColumnInfo(name = "current_county") val currentCounty: String? = null,
    @ColumnInfo(name = "county") val county: String? = null,
    @ColumnInfo(name = "is_emancipated") val isEmancipated: Boolean = false,
    @ColumnInfo(name = "emancipation_date") val emancipationDate: String? = null,
    @ColumnInfo(name = "emancipation_reason") val emancipationReason: String? = null,
    @ColumnInfo(name = "current_status") val currentStatus: String? = "Active",
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null,
    @ColumnInfo(name = "assigned_case_worker") val assignedCaseWorker: Int? = null,
    
    // Sync Metadata
    @ColumnInfo(name = "remote_id") val remoteId: String? = null,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "PENDING", // PENDING, SYNCED, ERROR
    @ColumnInfo(name = "last_synced_at") val lastSyncedAt: Long? = null
)
