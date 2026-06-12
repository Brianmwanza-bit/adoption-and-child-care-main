package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a background check in the system.
 * 
 * This entity tracks background checks conducted on users for safety
 * and compliance purposes.
 * 
 * @property checkId Unique identifier for the background check (auto-generated).
 * @property userId ID of the user being checked.
 * @property status Current status (e.g., Pending, Complete).
 * @property result Result of the background check.
 * @property requestedAt Date when the check was requested.
 * @property completedAt Date when the check was completed.
 * @property clearanceCertificatePath Path to the clearance certificate.
 * @property clearanceCertificateData Binary data of the clearance certificate.
 * @property clearanceMimeType MIME type of the certificate.
 * @property clearanceSize Size of the certificate in bytes.
 */
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