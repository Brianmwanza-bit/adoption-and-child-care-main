package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permissions")
data class PermissionEntity(
    @PrimaryKey(autoGenerate = true)
    val permission_id: Int = 0,
    val name: String,
    val description: String?,
    val category: String?
)
