package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.Guardian
import com.adoptionapp.repository.GuardianRepository
import kotlinx.coroutines.launch

class GuardianViewModel(private val repository: GuardianRepository) : ViewModel() {
    val guardians: LiveData<List<Guardian>> = repository.allGuardians

    fun addGuardian(guardian: Guardian) {
        viewModelScope.launch {
            repository.insert(guardian)
        }
    }

    fun updateGuardian(guardian: Guardian) {
        viewModelScope.launch {
            repository.update(guardian)
        }
    }

    fun deleteGuardian(guardian: Guardian) {
        viewModelScope.launch {
            repository.delete(guardian)
        }
    }

    fun syncGuardians() {
        viewModelScope.launch {
            repository.syncGuardians()
        }
    }

    fun getGuardianById(id: Int): LiveData<Guardian?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return guardians
    }
} 