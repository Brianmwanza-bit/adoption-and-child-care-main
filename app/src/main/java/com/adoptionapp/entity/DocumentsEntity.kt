package com.adoptionapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DocumentsEntity(
    @PrimaryKey(autoGenerate = true)
    val document_id: Int = 0,
    val document_type: String,
    val description: String?,
    val fileBlob: ByteArray?
) 