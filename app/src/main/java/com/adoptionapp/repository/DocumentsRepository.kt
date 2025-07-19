package com.adoptionapp.repository

import androidx.lifecycle.LiveData
import com.adoptionapp.DocumentsDao
import com.adoptionapp.DocumentsEntity
 
class DocumentsRepository(private val dao: DocumentsDao) {
    fun getAllDocuments(): LiveData<List<DocumentsEntity>> = dao.getAllDocuments()
    suspend fun insert(document: DocumentsEntity) = dao.insert(document)
} 