package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "families")
data class FamilyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "family_id") val familyId: Int = 0,
    @ColumnInfo(name = "primary_contact_name") val primaryContactName: String,
    @ColumnInfo(name = "secondary_contact_name") val secondaryContactName: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "address") val address: String? = null,
    @ColumnInfo(name = "city") val city: String? = null,
    @ColumnInfo(name = "state") val state: String? = null,
    @ColumnInfo(name = "country") val country: String? = null,
    @ColumnInfo(name = "status") val status: String? = "Active",
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
