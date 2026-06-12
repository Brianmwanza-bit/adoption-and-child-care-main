package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a permission in the system.
 * 
 * This entity stores permissions that can be granted to users
 * for role-based access control.
 * 
 * @property permissionId Unique identifier for the permission (auto-generated).
 * @property name Name of the permission (e.g., read_children, write_reports).
 * @property description Description of what the permission allows.
 * @property category Category the permission belongs to.
 */
@Entity(tableName = "permissions")
data class PermissionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "permission_id") val permissionId: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "category") val category: String? = null
)
