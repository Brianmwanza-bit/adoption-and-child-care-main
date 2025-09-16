package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.MedicalRecord
import com.adoptionapp.repository.MedicalRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicalRecordViewModel(private val repository: MedicalRecordRepository) : ViewModel() {
    val medicalRecords: LiveData<List<MedicalRecord>> = repository.allMedicalRecords
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun addMedicalRecord(medicalRecord: MedicalRecord) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.insert(medicalRecord)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun updateMedicalRecord(medicalRecord: MedicalRecord) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.update(medicalRecord)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun deleteMedicalRecord(medicalRecord: MedicalRecord) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.delete(medicalRecord)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun syncMedicalRecords() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.syncMedicalRecords()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun getMedicalRecordById(id: Int): LiveData<MedicalRecord?> {
        return medicalRecords
    }

    fun getMedicalRecordsByChildId(childId: Int): LiveData<List<MedicalRecord>> {
        return medicalRecords
    }
} 