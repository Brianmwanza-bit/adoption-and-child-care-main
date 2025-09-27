package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_permissions")
data class UserPermissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val user_id: Int,
    val permission_id: Int
)
