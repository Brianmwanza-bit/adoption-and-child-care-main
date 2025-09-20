package com.adoptionapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_studies")
data class HomeStudyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "home_study_id") val homeStudyId: Int = 0,
    @ColumnInfo(name = "family_id") val familyId: Int,
    @ColumnInfo(name = "started_at") val startedAt: String? = null,
    @ColumnInfo(name = "completed_at") val completedAt: String? = null,
    @ColumnInfo(name = "result") val result: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "social_worker_id") val socialWorkerId: Int? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
