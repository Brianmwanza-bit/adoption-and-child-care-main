package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sos_locations")
data class SOSLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "sos_event_id") val sosEventId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?,
    val timestamp: Long
)
