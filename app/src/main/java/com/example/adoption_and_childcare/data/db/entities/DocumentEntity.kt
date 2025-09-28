package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "document_id") val documentId: Int = 0,
    @ColumnInfo(name = "child_id") val childId: Int,
    @ColumnInfo(name = "document_type") val documentType: String,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "file_type") val fileType: String? = null,
    @ColumnInfo(name = "file_size") val fileSize: Int? = null,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "uploaded_at") val uploadedAt: String? = null,
    @ColumnInfo(name = "uploaded_by") val uploadedBy: Int? = null
)
