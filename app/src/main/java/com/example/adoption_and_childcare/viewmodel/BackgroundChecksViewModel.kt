package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import com.example.adoption_and_childcare.data.repository.BackgroundCheckRepositoryImpl
import com.example.adoption_and_childcare.data.repository.UserRepository
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing background checks and related operations.
 */
@HiltViewModel
class BackgroundChecksViewModel @Inject constructor(
    private val backgroundCheckRepository: BackgroundCheckRepositoryImpl,
    private val userRepository: UserRepository,
    private val authManager: AuthManager
) : ViewModel() {

    val backgroundChecks = backgroundCheckRepository.observeAll()
    val users = userRepository.observeAll()

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
            
            val result = backgroundCheckRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertCheck(check: BackgroundCheckEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            backgroundCheckRepository.insert(check, token)
        }
    }

    fun updateCheck(check: BackgroundCheckEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            backgroundCheckRepository.update(check, token)
        }
    }

    fun deleteCheck(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            backgroundCheckRepository.delete(id, token)
        }
    }
}
