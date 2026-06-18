package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.data.repository.FamilyRepository
import com.example.adoption_and_childcare.data.repository.PlacementRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing child placements and related operations.
 */
@HiltViewModel
class PlacementsViewModel @Inject constructor(
    private val placementRepository: PlacementRepositoryImpl,
    private val childRepository: ChildRepository,
    private val familyRepository: FamilyRepository,
    private val authManager: AuthManager
) : ViewModel() {

    val placements = placementRepository.observeAll()
    val children = childRepository.observeAll()
    val families = familyRepository.observeAll()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        refreshFromApi()
    }

    fun refreshFromApi() {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: return@launch
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = placementRepository.fetchFromApi(token)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun insertPlacement(placement: PlacementEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            placementRepository.insert(placement, token)
        }
    }

    fun updatePlacement(placement: PlacementEntity) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            placementRepository.update(placement, token)
        }
    }

    fun deletePlacement(id: Int) {
        viewModelScope.launch {
            val token = authManager.getAuthToken() ?: ""
            placementRepository.delete(id, token)
        }
    }
}
