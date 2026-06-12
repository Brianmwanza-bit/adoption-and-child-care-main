package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an adoption application in the system.
 * 
 * This entity tracks the adoption application process, linking families to children
 * and tracking the application status through various stages.
 * 
 * @property applicationId Unique identifier for the application (auto-generated).
 * @property applicationNumber Unique application number.
 * @property familyId ID of the family applying for adoption.
 * @property childId ID of the child being applied for.
 * @property submittedAt Timestamp when the application was submitted.
 * @property status Current status (e.g., Pending, Under Review, Approved).
 * @property notes Additional notes about the application.
 * @property assignedSocialWorker User ID of the assigned social worker.
 * @property updatedAt Timestamp when the record was last updated.
 * @property remoteId Remote server ID for synchronization.
 * @property syncStatus Synchronization status (PENDING, SYNCED, ERROR).
 * @property lastSyncedAt Timestamp of last synchronization.
 */
@Entity(tableName = "adoption_applications")
data class AdoptionApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "application_id") val applicationId: Int = 0,
    @ColumnInfo(name = "application_number") val applicationNumber: String? = null,
    @ColumnInfo(name = "family_id") val familyId: Int,
    @ColumnInfo(name = "child_id") val childId: Int? = null,
    @ColumnInfo(name = "submitted_at") val submittedAt: String? = null,
    @ColumnInfo(name = "status") val status: String? = "Pending",
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "assigned_social_worker") val assignedSocialWorker: Int? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null,

    // Sync Metadata
    @ColumnInfo(name = "remote_id") val remoteId: String? = null,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "PENDING", // PENDING, SYNCED, ERROR
    @ColumnInfo(name = "last_synced_at") val lastSyncedAt: Long? = null
)
