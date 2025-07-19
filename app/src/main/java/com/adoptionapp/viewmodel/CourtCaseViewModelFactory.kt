package com.adoptionapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adoptionapp.repository.CourtCaseRepository

class CourtCaseViewModelFactory(private val repository: CourtCaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourtCaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourtCaseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 