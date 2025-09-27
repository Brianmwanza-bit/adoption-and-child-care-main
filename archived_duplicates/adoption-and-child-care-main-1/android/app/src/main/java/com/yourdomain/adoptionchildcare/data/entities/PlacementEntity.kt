package com.yourdomain.adoptionchildcare.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placements")
data class PlacementEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "placement_id") val placementId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "placement_type") val placementType: String?,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String?,
    @ColumnInfo(name = "organization") val organization: String?,
    @ColumnInfo(name = "placement_address") val placementAddress: String?,
    @ColumnInfo(name = "contact_person") val contactPerson: String?,
    @ColumnInfo(name = "contact_phone") val contactPhone: String?,
    @ColumnInfo(name = "contact_email") val contactEmail: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "is_current") val isCurrent: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int?
)
