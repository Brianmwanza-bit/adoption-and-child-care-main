package com.adoptionapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adoptionapp.repository.ChildRepository

class ChildViewModelFactory(private val repository: ChildRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChildViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChildViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 