package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.CaseReport
import com.adoptionapp.repository.CaseReportRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CaseReportViewModel(private val repository: CaseReportRepository) : ViewModel() {
    val caseReports: LiveData<List<CaseReport>> = repository.allCaseReports
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun addCaseReport(caseReport: CaseReport) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.insert(caseReport)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun updateCaseReport(caseReport: CaseReport) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.update(caseReport)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun deleteCaseReport(caseReport: CaseReport) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.delete(caseReport)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun syncCaseReports() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.syncCaseReports()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun getCaseReportById(id: Int): LiveData<CaseReport?> {
        return caseReports
    }

    fun getCaseReportsByChildId(childId: Int): LiveData<List<CaseReport>> {
        return caseReports
    }
} 