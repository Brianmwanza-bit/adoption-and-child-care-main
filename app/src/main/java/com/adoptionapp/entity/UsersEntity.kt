package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UsersEntity(
    @PrimaryKey(autoGenerate = true)
    val user_id: Int = 0,
    val username: String,
    val password: String,
    val role: String,
    val email: String? = null,
    val full_name: String? = null,
    val phone: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val is_active: Boolean = true
)
