package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.DocumentsEntity
import com.adoptionapp.repository.DocumentsRepository
import kotlinx.coroutines.launch

class DocumentsViewModel(private val repository: DocumentsRepository) : ViewModel() {
    val allDocuments: LiveData<List<DocumentsEntity>> = repository.getAllDocuments()

    fun insert(document: DocumentsEntity) = viewModelScope.launch {
        repository.insert(document)
    }

    fun insertWithFile(document: DocumentsEntity, fileBytes: ByteArray?) = viewModelScope.launch {
        repository.insertWithFile(document, fileBytes)
    }
} 