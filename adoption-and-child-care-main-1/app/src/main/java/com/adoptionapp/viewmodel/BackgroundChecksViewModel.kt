package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.BackgroundChecksEntity
import com.adoptionapp.BackgroundChecksRepository
import kotlinx.coroutines.launch

class BackgroundChecksViewModel(private val repository: BackgroundChecksRepository) : ViewModel() {
    private val _checks = MutableLiveData<List<BackgroundChecksEntity>>(emptyList())
    val checks: LiveData<List<BackgroundChecksEntity>> = _checks

    fun loadChecks() {
        viewModelScope.launch {
            _checks.postValue(repository.getAll())
        }
    }

    fun addCheck(check: BackgroundChecksEntity) {
        viewModelScope.launch {
            repository.insert(check)
            loadChecks()
        }
    }

    fun updateCheck(check: BackgroundChecksEntity) {
        viewModelScope.launch {
            repository.update(check)
            loadChecks()
        }
    }

    fun deleteCheck(check: BackgroundChecksEntity) {
        viewModelScope.launch {
            repository.delete(check)
            loadChecks()
        }
    }
} 