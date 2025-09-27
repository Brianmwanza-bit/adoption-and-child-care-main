package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val user_id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val role: String,
    val created_at: String,
    val updated_at: String,
    val profile_photo: String? = null // URI as String
)
