package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.data.repository.CourtCaseRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing court cases and related operations.
 */
@HiltViewModel
class CourtCasesViewModel @Inject constructor(
    private val courtCaseRepository: CourtCaseRepositoryImpl,
    private val childRepository: ChildRepository,
    private val authManager: AuthManager
) : ViewModel() {

    val courtCases = courtCaseRepository.observeAll()
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
            
            val result = courtCaseRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertCase(courtCase: CourtCaseEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            courtCaseRepository.insert(courtCase, token)
        }
    }

    fun updateCase(courtCase: CourtCaseEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            courtCaseRepository.update(courtCase, token)
        }
    }

    fun deleteCase(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            courtCaseRepository.delete(id, token)
        }
    }
}
