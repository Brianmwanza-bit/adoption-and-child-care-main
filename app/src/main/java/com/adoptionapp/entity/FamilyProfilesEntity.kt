package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FamilyProfilesEntity(
    @PrimaryKey(autoGenerate = true)
    val family_id: Int = 0,
    val name: String,
    val contact: String,
    val address: String
) 