package com.adoptionapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adoptionapp.repository.GuardianRepository

class GuardianViewModelFactory(private val repository: GuardianRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GuardianViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GuardianViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 