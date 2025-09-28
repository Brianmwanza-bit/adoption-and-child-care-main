package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "user_permissions", primaryKeys = ["user_id", "permission_id"])
data class UserPermissionEntity(
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "permission_id") val permissionId: Int,
    @ColumnInfo(name = "granted_at") val grantedAt: String? = null,
    @ColumnInfo(name = "granted_by") val grantedBy: Int? = null
)
