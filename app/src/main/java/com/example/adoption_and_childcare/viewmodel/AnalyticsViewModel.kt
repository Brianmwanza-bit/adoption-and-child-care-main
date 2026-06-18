package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.repository.ChildRepository
import com.example.adoption_and_childcare.data.repository.PlacementRepositoryImpl
import com.example.adoption_and_childcare.data.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for analytics and reporting functionality.
 * 
 * This ViewModel loads and displays real-world analytics data including
 * placement trends, child status distribution, and summary statistics.
 */
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val childRepository: ChildRepository,
    private val placementRepository: PlacementRepositoryImpl,
    private val familyRepository: FamilyRepository
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _summary = MutableStateFlow<String?>(null)
    val summary: StateFlow<String?> = _summary.asStateFlow()

    private val _placementsOverTime = MutableStateFlow<List<String>>(emptyList())
    val placementsOverTime: StateFlow<List<String>> = _placementsOverTime.asStateFlow()

    private val _childrenByStatus = MutableStateFlow<Map<String, Int>>(emptyMap())
    val childrenByStatus: StateFlow<Map<String, Int>> = _childrenByStatus.asStateFlow()

    private val _totalFamilies = MutableStateFlow(0)
    val totalFamilies: StateFlow<Int> = _totalFamilies.asStateFlow()

    fun loadAnalytics() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Fetch real data from repositories
                val children = childRepository.observeAll().first()
                val placements = placementRepository.observeAll().first()
                val families = familyRepository.observeAll().first()

                _totalFamilies.value = families.size

                // Calculate children by status distribution
                val statusMap = children.groupBy { it.currentStatus ?: "Active" }
                    .mapValues { it.value.size }
                _childrenByStatus.value = statusMap

                // Calculate placement labels for display
                val recentPlacements = placements.takeLast(10).map { 
                    "${it.placementType ?: "Foster"} - ${it.startDate}"
                }
                
                if (recentPlacements.isEmpty()) {
                    _placementsOverTime.value = listOf(
                        "No placements recorded yet.",
                        "System is ready for data entry."
                    )
                } else {
                    _placementsOverTime.value = recentPlacements.reversed()
                }

                _summary.value = "Analytics synchronized with latest database records."
            } catch (e: Exception) {
                _error.value = "Failed to load analytics: ${e.localizedMessage}"
                // Fallback to empty states if real fetch fails
                _summary.value = "Offline data display."
            } finally {
                _loading.value = false
            }
        }
    }
}
