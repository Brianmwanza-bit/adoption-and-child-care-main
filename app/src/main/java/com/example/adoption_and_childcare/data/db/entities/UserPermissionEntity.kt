package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user-permission mapping in the system.
 * 
 * This entity links users to permissions for role-based access control.
 * Uses a composite primary key of userId and permissionId.
 * 
 * @property userId ID of the user.
 * @property permissionId ID of the permission.
 * @property grantedAt Timestamp when the permission was granted.
 * @property grantedBy User ID of the admin who granted the permission.
 */
@Entity(
    tableName = "user_permissions",
    indices = [
        androidx.room.Index(value = ["user_id"]),
        androidx.room.Index(value = ["permission_id"])
    ]
)
data class UserPermissionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "permission_id") val permissionId: Int,
    @ColumnInfo(name = "granted_at") val grantedAt: String? = null,
    @ColumnInfo(name = "granted_by") val grantedBy: Int? = null
)
