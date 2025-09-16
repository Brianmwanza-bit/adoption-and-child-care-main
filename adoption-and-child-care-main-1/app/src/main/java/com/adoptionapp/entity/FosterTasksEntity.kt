package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FosterTasksEntity(
    @PrimaryKey(autoGenerate = true)
    val task_id: Int = 0,
    val title: String,
    val description: String?,
    val due_date: String?,
    val status: String
) 