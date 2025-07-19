package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BackgroundChecksEntity(
    @PrimaryKey(autoGenerate = true)
    val check_id: Int = 0,
    val person_name: String,
    val type: String,
    val status: String,
    val result: String?
) 