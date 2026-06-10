package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "background_checks")
data class BackgroundCheckEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "check_id") val checkId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "status") val status: String? = "Pending",
    @ColumnInfo(name = "result") val result: String? = null,
    @ColumnInfo(name = "requested_at") val requestedAt: String? = null,
    @ColumnInfo(name = "completed_at") val completedAt: String? = null,
    @ColumnInfo(name = "clearance_certificate_path") val clearanceCertificatePath: String? = null,
    @ColumnInfo(name = "clearance_certificate_data", typeAffinity = ColumnInfo.BLOB) val clearanceCertificateData: ByteArray? = null,
    @ColumnInfo(name = "clearance_mime_type") val clearanceMimeType: String? = null,
    @ColumnInfo(name = "clearance_size") val clearanceSize: Int? = null
)