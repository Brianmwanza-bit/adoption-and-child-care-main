package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a child placement in the system.
 * 
 * This entity tracks where children are placed, whether in foster homes,
 * institutions, or other care arrangements.
 * 
 * @property placementId Unique identifier for the placement (auto-generated).
 * @property childId ID of the child being placed.
 * @property destinationFamilyId ID of the family where the child is placed.
 * @property placementType Type of placement (e.g., Foster Home, Institution).
 * @property startDate Start date of the placement.
 * @property endDate End date of the placement.
 * @property organization Name of the organization handling the placement.
 * @property placementAddress Address of the placement location.
 * @property contactPerson Name of the contact person.
 * @property contactPhone Phone number of the contact person.
 * @property contactEmail Email of the contact person.
 * @property notes Additional notes about the placement.
 * @property isCurrent Whether this is the current active placement.
 * @property createdAt Timestamp when the record was created.
 * @property createdBy User ID of the creator.
 */
@Entity(tableName = "placements")
data class PlacementEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "placement_id") val placementId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "destination_family_id") val destinationFamilyId: Int? = null,
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
