package com.adoptionapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adoptionapp.repository.PlacementRepository

class PlacementViewModelFactory(private val repository: PlacementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlacementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 