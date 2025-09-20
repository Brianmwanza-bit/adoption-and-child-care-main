package com.adoptionapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id") val userId: Int = 0,
    @ColumnInfo(name = "username") val username: String,
    // Store hashes, not plain text
    @ColumnInfo(name = "password_hash") val passwordHash: String,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "last_login") val lastLogin: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)
