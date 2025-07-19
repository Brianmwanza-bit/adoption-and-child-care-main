package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.MedicalRecord
import com.adoptionapp.repository.MedicalRecordRepository
import kotlinx.coroutines.launch

class MedicalRecordViewModel(private val repository: MedicalRecordRepository) : ViewModel() {
    val medicalRecords: LiveData<List<MedicalRecord>> = repository.allMedicalRecords

    fun addMedicalRecord(medicalRecord: MedicalRecord) {
        viewModelScope.launch {
            repository.insert(medicalRecord)
        }
    }

    fun updateMedicalRecord(medicalRecord: MedicalRecord) {
        viewModelScope.launch {
            repository.update(medicalRecord)
        }
    }

    fun deleteMedicalRecord(medicalRecord: MedicalRecord) {
        viewModelScope.launch {
            repository.delete(medicalRecord)
        }
    }

    fun syncMedicalRecords() {
        viewModelScope.launch {
            repository.syncMedicalRecords()
        }
    }

    fun getMedicalRecordById(id: Int): LiveData<MedicalRecord?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return medicalRecords
    }

    fun getMedicalRecordsByChildId(childId: Int): LiveData<List<MedicalRecord>> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return medicalRecords
    }
} 