package com.adoptionapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adoptionapp.repository.EducationRecordRepository

class EducationRecordViewModelFactory(private val repository: EducationRecordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EducationRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EducationRecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 