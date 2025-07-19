package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.EducationRecord
import com.adoptionapp.repository.EducationRecordRepository
import kotlinx.coroutines.launch

class EducationRecordViewModel(private val repository: EducationRecordRepository) : ViewModel() {
    val educationRecords: LiveData<List<EducationRecord>> = repository.allEducationRecords

    fun addEducationRecord(educationRecord: EducationRecord) {
        viewModelScope.launch {
            repository.insert(educationRecord)
        }
    }

    fun updateEducationRecord(educationRecord: EducationRecord) {
        viewModelScope.launch {
            repository.update(educationRecord)
        }
    }

    fun deleteEducationRecord(educationRecord: EducationRecord) {
        viewModelScope.launch {
            repository.delete(educationRecord)
        }
    }

    fun syncEducationRecords() {
        viewModelScope.launch {
            repository.syncEducationRecords()
        }
    }

    fun getEducationRecordById(id: Int): LiveData<EducationRecord?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return educationRecords
    }

    fun getEducationRecordsByChildId(childId: Int): LiveData<List<EducationRecord>> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return educationRecords
    }
} 