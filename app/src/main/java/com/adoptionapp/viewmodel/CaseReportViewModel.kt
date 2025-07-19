package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.CaseReport
import com.adoptionapp.repository.CaseReportRepository
import kotlinx.coroutines.launch

class CaseReportViewModel(private val repository: CaseReportRepository) : ViewModel() {
    val caseReports: LiveData<List<CaseReport>> = repository.allCaseReports

    fun addCaseReport(caseReport: CaseReport) {
        viewModelScope.launch {
            repository.insert(caseReport)
        }
    }

    fun updateCaseReport(caseReport: CaseReport) {
        viewModelScope.launch {
            repository.update(caseReport)
        }
    }

    fun deleteCaseReport(caseReport: CaseReport) {
        viewModelScope.launch {
            repository.delete(caseReport)
        }
    }

    fun syncCaseReports() {
        viewModelScope.launch {
            repository.syncCaseReports()
        }
    }

    fun getCaseReportById(id: Int): LiveData<CaseReport?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return caseReports
    }

    fun getCaseReportsByChildId(childId: Int): LiveData<List<CaseReport>> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return caseReports
    }
} 