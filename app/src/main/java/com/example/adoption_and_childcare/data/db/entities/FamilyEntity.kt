package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a family in the adoption and child care system.
 * 
 * This entity stores information about families interested in adoption or foster care,
 * including contact details, location, and sync metadata for offline-first functionality.
 * 
 * @property familyId Unique identifier for the family (auto-generated).
 * @property primaryContactName Name of the primary contact person.
 * @property secondaryContactName Name of the secondary contact person.
 * @property email Family's email address.
 * @property phone Family's phone number.
 * @property nationalIdNo National ID number of the primary contact.
 * @property address Physical address.
 * @property city City of residence.
 * @property county County of residence.
 * @property state State/region of residence.
 * @property country Country of residence.
 * @property status Current status of the family (e.g., Active, Inactive).
 * @property createdAt Timestamp when the record was created.
 * @property updatedAt Timestamp when the record was last updated.
 * @property remoteId Remote server ID for synchronization.
 * @property syncStatus Synchronization status (PENDING, SYNCED, ERROR).
 * @property lastSyncedAt Timestamp of last synchronization.
 */
@Entity(tableName = "families")
data class FamilyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "family_id") val familyId: Int = 0,
    @ColumnInfo(name = "primary_contact_name") val primaryContactName: String,
    @ColumnInfo(name = "secondary_contact_name") val secondaryContactName: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "national_id_no") val nationalIdNo: String? = null,
    @ColumnInfo(name = "address") val address: String? = null,
    @ColumnInfo(name = "city") val city: String? = null,
    @ColumnInfo(name = "county") val county: String? = null,
    @ColumnInfo(name = "state") val state: String? = null,
    @ColumnInfo(name = "country") val country: String? = null,
    @ColumnInfo(name = "status") val status: String? = "Active",
    
    // Extensive Details
    @ColumnInfo(name = "license_number") val licenseNumber: String? = null,
    @ColumnInfo(name = "license_issue_date") val licenseIssueDate: String? = null,
    @ColumnInfo(name = "license_expiration_date") val licenseExpirationDate: String? = null,
    @ColumnInfo(name = "license_status") val licenseStatus: String? = null,
    @ColumnInfo(name = "sub_county") val subCounty: String? = null,
    @ColumnInfo(name = "latitude") val latitude: Double? = null,
    @ColumnInfo(name = "longitude") val longitude: Double? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null,

    // Sync Metadata
    @ColumnInfo(name = "remote_id") val remoteId: String? = null,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "PENDING", // PENDING, SYNCED, ERROR
    @ColumnInfo(name = "last_synced_at") val lastSyncedAt: Long? = null
)
