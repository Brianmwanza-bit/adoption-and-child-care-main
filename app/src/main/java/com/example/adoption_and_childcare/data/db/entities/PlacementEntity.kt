package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placements")
data class PlacementEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "placement_id") val placementId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "placement_type") val placementType: String? = null,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String? = null,
    @ColumnInfo(name = "organization") val organization: String? = null,
    @ColumnInfo(name = "placement_address") val placementAddress: String? = null,
    @ColumnInfo(name = "contact_person") val contactPerson: String? = null,
    @ColumnInfo(name = "contact_phone") val contactPhone: String? = null,
    @ColumnInfo(name = "contact_email") val contactEmail: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "is_current") val isCurrent: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Int? = null
)
