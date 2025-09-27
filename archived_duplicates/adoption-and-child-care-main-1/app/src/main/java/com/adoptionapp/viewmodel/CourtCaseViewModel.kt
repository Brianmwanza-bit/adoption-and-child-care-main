package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.CourtCase
import com.adoptionapp.repository.CourtCaseRepository
import kotlinx.coroutines.launch

class CourtCaseViewModel(private val repository: CourtCaseRepository) : ViewModel() {
    val courtCases: LiveData<List<CourtCase>> = repository.allCourtCases

    fun addCourtCase(courtCase: CourtCase) {
        viewModelScope.launch {
            repository.insert(courtCase)
        }
    }

    fun updateCourtCase(courtCase: CourtCase) {
        viewModelScope.launch {
            repository.update(courtCase)
        }
    }

    fun deleteCourtCase(courtCase: CourtCase) {
        viewModelScope.launch {
            repository.delete(courtCase)
        }
    }

    fun syncCourtCases() {
        viewModelScope.launch {
            repository.syncCourtCases()
        }
    }

    fun getCourtCaseById(id: Int): LiveData<CourtCase?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return courtCases
    }

    fun getCourtCasesByChildId(childId: Int): LiveData<List<CourtCase>> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return courtCases
    }
} 