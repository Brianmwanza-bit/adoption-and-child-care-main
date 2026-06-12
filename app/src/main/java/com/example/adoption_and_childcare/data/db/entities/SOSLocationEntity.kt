package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an SOS location tracking entry.
 * 
 * This entity stores GPS coordinates captured during SOS emergency events
 * for tracking and response purposes.
 * 
 * @property id Unique identifier for the location entry (auto-generated).
 * @property sosEventId ID of the SOS event this location belongs to.
 * @property latitude GPS latitude coordinate.
 * @property longitude GPS longitude coordinate.
 * @property accuracy GPS accuracy in meters.
 * @property timestamp Timestamp when the location was captured.
 */
@Entity(tableName = "sos_locations")
data class SOSLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "sos_event_id") val sosEventId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?,
    val timestamp: Long
)
