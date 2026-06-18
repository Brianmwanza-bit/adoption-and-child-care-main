package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.data.repository.EducationRecordRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the education records list and related operations.
 */
@HiltViewModel
class EducationViewModel @Inject constructor(
    private val educationRecordRepository: EducationRecordRepositoryImpl,
    private val childRepository: ChildRepository,
    private val authManager: AuthManager
) : ViewModel() {

    val educationRecords = educationRecordRepository.observeAll()
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
            
            val result = educationRecordRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertEducationRecord(record: EducationRecordEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            educationRecordRepository.insert(record, token)
        }
    }

    fun updateEducationRecord(record: EducationRecordEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            educationRecordRepository.update(record, token)
        }
    }

    fun deleteEducationRecord(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            educationRecordRepository.delete(id, token)
        }
    }
}
