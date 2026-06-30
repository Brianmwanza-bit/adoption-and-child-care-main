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
@Entity(tableName = BackgroundCheckEntity.TABLE_NAME)
data class BackgroundCheckEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_CHECK_ID) val checkId: Int = 0,
    @ColumnInfo(name = COLUMN_USER_ID) val userId: Int,
    @ColumnInfo(name = COLUMN_STATUS) val status: String? = STATUS_PENDING,
    @ColumnInfo(name = COLUMN_RESULT) val result: String? = null,
    @ColumnInfo(name = COLUMN_REQUESTED_AT) val requestedAt: String? = null,
    @ColumnInfo(name = COLUMN_COMPLETED_AT) val completedAt: String? = null,
    @ColumnInfo(name = COLUMN_CERT_PATH) val clearanceCertificatePath: String? = null,
    @ColumnInfo(name = COLUMN_CERT_DATA, typeAffinity = ColumnInfo.BLOB) val clearanceCertificateData: ByteArray? = null,
    @ColumnInfo(name = COLUMN_MIME_TYPE) val clearanceMimeType: String? = null,
    @ColumnInfo(name = COLUMN_SIZE) val clearanceSize: Int? = null
) {
    /**
     * Compares this background check entity with another object for equality.
     * 
     * @param other The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BackgroundCheckEntity

        if (checkId != other.checkId) return false
        if (userId != other.userId) return false
        if (status != other.status) return false
        if (result != other.result) return false
        if (requestedAt != other.requestedAt) return false
        if (completedAt != other.completedAt) return false
        if (clearanceCertificatePath != other.clearanceCertificatePath) return false
        if (clearanceCertificateData != null) {
            if (other.clearanceCertificateData == null) return false
            if (!clearanceCertificateData.contentEquals(other.clearanceCertificateData)) return false
        } else if (other.clearanceCertificateData != null) return false
        if (clearanceMimeType != other.clearanceMimeType) return false
        if (clearanceSize != other.clearanceSize) return false

        return true
    }

    /**
     * Generates a hash code for this background check entity.
     * 
     * @return The hash code.
     */
    override fun hashCode(): Int {
        var result1 = checkId
        result1 = 31 * result1 + userId
        result1 = 31 * result1 + (status?.hashCode() ?: 0)
        result1 = 31 * result1 + (result?.hashCode() ?: 0)
        result1 = 31 * result1 + (requestedAt?.hashCode() ?: 0)
        result1 = 31 * result1 + (completedAt?.hashCode() ?: 0)
        result1 = 31 * result1 + (clearanceCertificatePath?.hashCode() ?: 0)
        result1 = 31 * result1 + (clearanceCertificateData?.contentHashCode() ?: 0)
        result1 = 31 * result1 + (clearanceMimeType?.hashCode() ?: 0)
        result1 = 31 * result1 + (clearanceSize ?: 0)
        return result1
    }

    companion object {
        /** Name of the database table for background checks. */
        const val TABLE_NAME = "background_checks"
        
        /** Default status for a new background check. */
        const val STATUS_PENDING = "Pending"
        
        const val COLUMN_CHECK_ID = "check_id"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_STATUS = "status"
        const val COLUMN_RESULT = "result"
        const val COLUMN_REQUESTED_AT = "requested_at"
        const val COLUMN_COMPLETED_AT = "completed_at"
        const val COLUMN_CERT_PATH = "clearance_certificate_path"
        const val COLUMN_CERT_DATA = "clearance_certificate_data"
        const val COLUMN_MIME_TYPE = "clearance_mime_type"
        const val COLUMN_SIZE = "clearance_size"
    }
}
