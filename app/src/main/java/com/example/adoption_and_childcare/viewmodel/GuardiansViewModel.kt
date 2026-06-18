package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.GuardianEntity
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.data.repository.GuardianRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing guardians and related operations.
 */
@HiltViewModel
class GuardiansViewModel @Inject constructor(
    private val guardianRepository: GuardianRepositoryImpl,
    private val childRepository: ChildRepository,
    private val authManager: AuthManager
) : ViewModel() {

    val guardians = guardianRepository.observeAll()
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
            
            val result = guardianRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertGuardian(guardian: GuardianEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            guardianRepository.insert(guardian, token)
        }
    }

    fun updateGuardian(guardian: GuardianEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            guardianRepository.update(guardian, token)
        }
    }

    fun deleteGuardian(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            guardianRepository.delete(id, token)
        }
    }
}
