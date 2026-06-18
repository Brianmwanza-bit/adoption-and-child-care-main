package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.data.repository.DocumentRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the documents list and document-related operations.
 */
@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val documentRepository: DocumentRepositoryImpl,
    private val childRepository: ChildRepository,
    private val authManager: AuthManager
) : ViewModel() {

    val documents = documentRepository.observeAll()
    val children = childRepository.observeAll()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        refreshFromApi()
    }

    fun refreshFromApi() {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: return@launch
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = documentRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertDocument(document: DocumentEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            documentRepository.insert(document, token)
        }
    }

    fun updateDocument(document: DocumentEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            documentRepository.update(document, token)
        }
    }

    fun deleteDocument(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            documentRepository.delete(id, token)
        }
    }
}
