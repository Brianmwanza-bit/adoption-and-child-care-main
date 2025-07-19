package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.Placement
import com.adoptionapp.repository.PlacementRepository
import kotlinx.coroutines.launch

class PlacementViewModel(private val repository: PlacementRepository) : ViewModel() {
    val placements: LiveData<List<Placement>> = repository.allPlacements

    fun addPlacement(placement: Placement) {
        viewModelScope.launch {
            repository.insert(placement)
        }
    }

    fun updatePlacement(placement: Placement) {
        viewModelScope.launch {
            repository.update(placement)
        }
    }

    fun deletePlacement(placement: Placement) {
        viewModelScope.launch {
            repository.delete(placement)
        }
    }

    fun syncPlacements() {
        viewModelScope.launch {
            repository.syncPlacements()
        }
    }

    fun getPlacementById(id: Int): LiveData<Placement?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return placements
    }

    fun getPlacementsByChildId(childId: Int): LiveData<List<Placement>> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return placements
    }
} 