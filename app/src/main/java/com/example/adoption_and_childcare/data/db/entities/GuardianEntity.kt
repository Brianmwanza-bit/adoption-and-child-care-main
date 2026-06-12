package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a guardian in the system.
 * 
 * This entity stores information about legal guardians of children,
 * including contact details, relationship, and verification status.
 * 
 * @property guardianId Unique identifier for the guardian (auto-generated).
 * @property childId ID of the child this guardian is responsible for.
 * @property firstName Guardian's first name.
 * @property lastName Guardian's last name.
 * @property relationship Relationship to the child (e.g., Parent, Relative).
 * @property phone Guardian's phone number.
 * @property email Guardian's email address.
 * @property address Guardian's physical address.
 * @property isPrimary Whether this is the primary guardian.
 * @property legalDocPath Path to legal documentation.
 * @property legalDocData Binary data of legal documentation.
 * @property legalDocMimeType MIME type of the legal document.
 * @property legalDocSize Size of the legal document in bytes.
 * @property verificationStatus Verification status (e.g., Pending, Verified).
 * @property userId User ID linked to this guardian for RBAC.
 * @property createdAt Timestamp when the record was created.
 * @property updatedAt Timestamp when the record was last updated.
 */
@Entity(tableName = "guardians")
data class GuardianEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "guardian_id") val guardianId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "relationship") val relationship: String,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "address") val address: String? = null,
    @ColumnInfo(name = "is_primary") val isPrimary: Boolean = false,
    @ColumnInfo(name = "legal_doc_path") val legalDocPath: String? = null,
    @ColumnInfo(name = "legal_doc_data", typeAffinity = ColumnInfo.BLOB) val legalDocData: ByteArray? = null,
    @ColumnInfo(name = "legal_doc_mime_type") val legalDocMimeType: String? = null,
    @ColumnInfo(name = "legal_doc_size") val legalDocSize: Int? = null,
    @ColumnInfo(name = "verification_status") val verificationStatus: String? = "Pending",
    @ColumnInfo(name = "user_id") val userId: Int? = null, // Links guardian to user account for RBAC
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)