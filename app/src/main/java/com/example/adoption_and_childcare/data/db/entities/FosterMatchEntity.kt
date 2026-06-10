package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foster_matches")
data class FosterMatchEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "match_id") val matchId: Int = 0,
    @ColumnInfo(name = "family_id") val familyId: Int,
    @ColumnInfo(name = "case_worker_id") val caseWorkerId: Int? = null,
    @ColumnInfo(name = "child_id") val childId: Int? = null,
    @ColumnInfo(name = "status") val status: String? = "Pending",
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "matched_at") val matchedAt: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null
)