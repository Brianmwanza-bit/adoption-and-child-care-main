package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.Placement
import com.adoptionapp.repository.PlacementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlacementViewModel(private val repository: PlacementRepository) : ViewModel() {
    val placements: LiveData<List<Placement>> = repository.allPlacements
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun addPlacement(placement: Placement) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.insert(placement)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun updatePlacement(placement: Placement) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.update(placement)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun deletePlacement(placement: Placement) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.delete(placement)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun syncPlacements() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.syncPlacements()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun getPlacementById(id: Int): LiveData<Placement?> {
        return placements
    }

    fun getPlacementsByChildId(childId: Int): LiveData<List<Placement>> {
        return placements
    }
} 