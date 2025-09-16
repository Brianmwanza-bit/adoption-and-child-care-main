package com.adoptionapp.ui.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val repository: MapRepository) : ViewModel() {
    private val _familyLocations = MutableStateFlow<List<FamilyLocation>>(emptyList())
    val familyLocations: StateFlow<List<FamilyLocation>> = _familyLocations

    private val _userLocations = MutableStateFlow<List<UserLocation>>(emptyList())
    val userLocations: StateFlow<List<UserLocation>> = _userLocations

    fun loadLocations() {
        viewModelScope.launch {
            _familyLocations.value = repository.getFamilyLocations()
            _userLocations.value = repository.getUserLocations()
        }
    }
} 