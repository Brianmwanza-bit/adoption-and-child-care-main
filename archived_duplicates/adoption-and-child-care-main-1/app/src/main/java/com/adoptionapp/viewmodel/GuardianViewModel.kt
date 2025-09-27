package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.Guardian
import com.adoptionapp.repository.GuardianRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GuardianViewModel(private val repository: GuardianRepository) : ViewModel() {
    val guardians: LiveData<List<Guardian>> = repository.allGuardians
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun addGuardian(guardian: Guardian) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.insert(guardian)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun updateGuardian(guardian: Guardian) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.update(guardian)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun deleteGuardian(guardian: Guardian) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.delete(guardian)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun syncGuardians() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.syncGuardians()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun getGuardianById(id: Int): LiveData<Guardian?> {
        return guardians
    }
} 