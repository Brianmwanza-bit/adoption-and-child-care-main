package com.adoptionapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adoptionapp.repository.CaseReportRepository

class CaseReportViewModelFactory(private val repository: CaseReportRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaseReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CaseReportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 