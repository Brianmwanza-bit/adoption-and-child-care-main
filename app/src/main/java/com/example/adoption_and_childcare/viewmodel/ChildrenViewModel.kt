package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the children list and child-related operations.
 */
@HiltViewModel
class ChildrenViewModel @Inject constructor(
    private val childRepository: ChildRepository,
    private val authManager: AuthManager
) : ViewModel() {

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
            
            val result = childRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertChild(child: ChildEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            childRepository.insert(child, token)
        }
    }

    fun updateChild(child: ChildEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            childRepository.update(child, token)
        }
    }

    fun deleteChild(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            childRepository.delete(id, token)
        }
    }

    fun search(query: String) = childRepository.searchByName(query)
}
