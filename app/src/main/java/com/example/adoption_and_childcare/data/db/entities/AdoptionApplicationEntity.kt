package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
