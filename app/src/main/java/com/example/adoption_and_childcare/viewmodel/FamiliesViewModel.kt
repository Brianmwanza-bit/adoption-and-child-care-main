package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import com.example.adoption_and_childcare.data.repository.FamilyRepository
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the families list and family-related operations.
 */
@HiltViewModel
class FamiliesViewModel @Inject constructor(
    private val familyRepository: FamilyRepository,
    private val authManager: AuthManager
) : ViewModel() {

    val families = familyRepository.observeAll()

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
            
            val result = familyRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertFamily(family: FamilyEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            familyRepository.insert(family, token)
        }
    }

    fun updateFamily(family: FamilyEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            familyRepository.update(family, token)
        }
    }

    fun deleteFamily(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            familyRepository.delete(id, token)
        }
    }

    fun search(query: String) = familyRepository.searchByName(query)
}
