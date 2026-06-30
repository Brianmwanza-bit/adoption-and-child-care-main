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

@HiltViewModel
class FamilyHubViewModel @Inject constructor(
    private val childDao: ChildDao,
    private val placementDao: PlacementDao,
    private val backgroundCheckDao: BackgroundCheckDao,
    private val familyDao: FamilyDao,
    private val medicalRecordDao: MedicalRecordDao,
    private val educationRecordDao: EducationRecordDao
) : ViewModel() {

    private val _placedChildren = MutableStateFlow<List<ChildEntity>>(emptyList())
    val placedChildren: StateFlow<List<ChildEntity>> = _placedChildren.asStateFlow()

    private val _backgroundChecks = MutableStateFlow<List<BackgroundCheckEntity>>(emptyList())
    val backgroundChecks: StateFlow<List<BackgroundCheckEntity>> = _backgroundChecks.asStateFlow()

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
