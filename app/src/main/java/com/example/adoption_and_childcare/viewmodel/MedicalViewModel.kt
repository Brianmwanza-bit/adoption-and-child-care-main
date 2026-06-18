package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.data.repository.MedicalRecordRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the medical records list and related operations.
 *
 * @property medicalRecordRepository The repository for medical record data operations.
 * @property childRepository The repository for child data operations.
 * @property authManager Manager for authentication tokens.
 */
@HiltViewModel
class MedicalViewModel @Inject constructor(
    private val medicalRecordRepository: MedicalRecordRepositoryImpl,
    private val childRepository: ChildRepository,
    private val authManager: AuthManager
) : ViewModel() {

    /**
     * Flow of all medical records from the local database.
     */
    val medicalRecords = medicalRecordRepository.observeAll()

    /**
     * Flow of all children from the local database.
     */
    val children = childRepository.observeAll()

    private val _isLoading = MutableStateFlow(false)

    /**
     * StateFlow indicating if a background operation is in progress.
     */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)

    /**
     * StateFlow containing the current error message, if any.
     */
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        refreshFromApi()
    }

    /**
     * Refreshes medical records by fetching the latest data from the remote API.
     */
    fun refreshFromApi() {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: return@launch
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = medicalRecordRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    /**
     * Inserts a new medical record into the database and syncs with the API.
     *
     * @param record The medical record entity to insert.
     */
    fun insertMedicalRecord(record: MedicalRecordEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            medicalRecordRepository.insert(record, token)
        }
    }

    /**
     * Updates an existing medical record in the database and syncs with the API.
     *
     * @param record The medical record entity to update.
     */
    fun updateMedicalRecord(record: MedicalRecordEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            medicalRecordRepository.update(record, token)
        }
    }

    /**
     * Deletes a medical record by its ID and syncs with the API.
     *
     * @param id The unique ID of the medical record to delete.
     */
    fun deleteMedicalRecord(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            medicalRecordRepository.delete(id, token)
        }
    }
}
