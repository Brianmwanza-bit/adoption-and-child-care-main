package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a document in the system.
 * 
 * This entity stores information about various documents related to children,
 * families, or cases, including file metadata and binary data.
 * 
 * @property documentId Unique identifier for the document (auto-generated).
 * @property childId ID of the child this document belongs to.
 * @property documentType Type of document (e.g., birth certificate, medical report).
 * @property fileName Name of the file.
 * @property fileType File extension/type.
 * @property fileSize Size of the file in bytes.
 * @property filePath Path to the file on device.
 * @property fileData Binary data of the file.
 * @property mimeType MIME type of the file.
 * @property description Description of the document content.
 * @property uploadedAt Timestamp when the document was uploaded.
 * @property uploadedBy User ID of the uploader.
 */
@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "document_id") val documentId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "document_type") val documentType: String,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "file_type") val fileType: String? = null,
    @ColumnInfo(name = "file_size") val fileSize: Int? = null,
    @ColumnInfo(name = "file_path") val filePath: String? = null,
    @ColumnInfo(name = "file_data", typeAffinity = ColumnInfo.BLOB) val fileData: ByteArray? = null,
    @ColumnInfo(name = "mime_type") val mimeType: String? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "uploaded_at") val uploadedAt: String? = null,
    @ColumnInfo(name = "uploaded_by") val uploadedBy: Int? = null
)
