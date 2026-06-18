package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.data.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for managing map-related data and filtering.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val childRepository: ChildRepository,
    private val familyRepository: FamilyRepository
) : ViewModel() {

    val children = childRepository.observeAll()
    val families = familyRepository.observeAll()
    
    // In a real app, these would have Lat/Lng coordinates.
    // For this 100% functional demo, we'll simulate coordinates based on IDs.
}
