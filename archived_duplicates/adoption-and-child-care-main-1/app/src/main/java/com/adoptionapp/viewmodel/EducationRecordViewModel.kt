package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.EducationRecord
import com.adoptionapp.repository.EducationRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EducationRecordViewModel(private val repository: EducationRecordRepository) : ViewModel() {
    val educationRecords: LiveData<List<EducationRecord>> = repository.allEducationRecords
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun addEducationRecord(educationRecord: EducationRecord) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.insert(educationRecord)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun updateEducationRecord(educationRecord: EducationRecord) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.update(educationRecord)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun deleteEducationRecord(educationRecord: EducationRecord) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.delete(educationRecord)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun syncEducationRecords() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.syncEducationRecords()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun getEducationRecordById(id: Int): LiveData<EducationRecord?> {
        return educationRecords
    }

    fun getEducationRecordsByChildId(childId: Int): LiveData<List<EducationRecord>> {
        return educationRecords
    }
} 