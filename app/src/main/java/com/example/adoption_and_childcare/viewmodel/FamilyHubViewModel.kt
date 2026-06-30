package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.dao.*
import com.example.adoption_and_childcare.data.db.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Family Hub screen.
 * 
 * Manages the state and business logic for displaying family-related information,
 * including children placed with the family and relevant background checks.
 * 
 * @property childDao DAO for accessing child data.
 * @property placementDao DAO for accessing placement data.
 * @property backgroundCheckDao DAO for accessing background check data.
 */
@HiltViewModel
class FamilyHubViewModel @Inject constructor(
    private val childDao: ChildDao,
    private val placementDao: PlacementDao,
    private val backgroundCheckDao: BackgroundCheckDao
) : ViewModel() {

    private val _placedChildren = MutableStateFlow<List<ChildEntity>>(emptyList())
    /** Observable list of children currently placed with the family. */
    val placedChildren: StateFlow<List<ChildEntity>> = _placedChildren.asStateFlow()

    private val _backgroundChecks = MutableStateFlow<List<BackgroundCheckEntity>>(emptyList())
    /** Observable list of relevant background checks. */
    val backgroundChecks: StateFlow<List<BackgroundCheckEntity>> = _backgroundChecks.asStateFlow()

    /**
     * Loads family data including placed children and background checks.
     * 
     * @param familyId The ID of the family to load data for.
     */
    fun loadData(familyId: Int) {
        viewModelScope.launch {
            placementDao.observeAll().collectLatest { placements ->
                val childIds = placements.filter { it.destinationFamilyId == familyId && it.isCurrent }.map { it.childId }
                val children = mutableListOf<ChildEntity>()
                childIds.forEach { id ->
                    childDao.findById(id)?.let { children.add(it) }
                }
                _placedChildren.value = children
            }
        }
        
        viewModelScope.launch {
            backgroundCheckDao.observeAll().collectLatest { checks ->
                // Filtering checks related to this family is tricky without a user link
                // For now, show all checks as a placeholder or logic could be added to link users to families
                _backgroundChecks.value = checks.take(5) 
            }
        }
    }
}
