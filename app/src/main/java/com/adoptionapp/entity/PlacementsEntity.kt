package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlacementsEntity(
    @PrimaryKey(autoGenerate = true)
    val placement_id: Int = 0,
    val child_id: Int,
    val family_id: Int,
    val status: String
) 