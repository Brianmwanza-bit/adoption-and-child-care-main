package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import com.example.adoption_and_childcare.data.repository.AdoptionApplicationRepository
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing adoption applications and related operations.
 */
@HiltViewModel
class AdoptionApplicationsViewModel @Inject constructor(
    private val adoptionApplicationRepository: AdoptionApplicationRepository,
    private val childRepository: ChildRepository,
    private val authManager: AuthManager
) : ViewModel() {

    val applications = adoptionApplicationRepository.observeAll()
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
            
            val result = adoptionApplicationRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertApplication(application: AdoptionApplicationEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            adoptionApplicationRepository.insert(application, token)
        }
    }

    fun updateApplication(application: AdoptionApplicationEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            adoptionApplicationRepository.update(application, token)
        }
    }

    fun deleteApplication(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            adoptionApplicationRepository.delete(id, token)
        }
    }
}
