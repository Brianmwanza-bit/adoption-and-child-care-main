package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guardians")
data class GuardianEntity(
    @PrimaryKey(autoGenerate = true)
    val guardian_id: Int = 0,
    val child_id: Int,
    val name: String,
    val relationship: String,
    val contact: String,
    val address: String?
)
