package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.CaseReportEntity
import com.example.adoption_and_childcare.data.db.dao.SystemSettingDao
import com.example.adoption_and_childcare.data.repository.CaseReportRepositoryImpl
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing case reports and related operations.
 */
@HiltViewModel
class CaseReportsViewModel @Inject constructor(
    private val caseReportRepository: CaseReportRepositoryImpl,
    private val childRepository: ChildRepository,
    private val systemSettingDao: SystemSettingDao,
    private val authManager: AuthManager
) : ViewModel() {

    val reports = caseReportRepository.observeAll()
    val children = childRepository.observeAll()
    
    /** Flow of system settings for lookups like document types. */
    val lookupSettings = systemSettingDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
            
            val result = caseReportRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertReport(report: CaseReportEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            caseReportRepository.insert(report, token)
        }
    }

    fun updateReport(report: CaseReportEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            caseReportRepository.update(report, token)
        }
    }

    fun deleteReport(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            caseReportRepository.delete(id, token)
        }
    }
}
