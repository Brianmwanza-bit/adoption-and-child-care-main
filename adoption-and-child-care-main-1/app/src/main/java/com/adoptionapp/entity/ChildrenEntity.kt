package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChildrenEntity(
    @PrimaryKey(autoGenerate = true)
    val child_id: Int = 0,
    val name: String,
    val age: Int,
    val photoBlob: ByteArray?
) 