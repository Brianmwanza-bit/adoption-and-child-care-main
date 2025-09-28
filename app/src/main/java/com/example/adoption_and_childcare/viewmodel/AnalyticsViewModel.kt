package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _summary = MutableStateFlow<String?>(null)
    val summary: StateFlow<String?> = _summary.asStateFlow()

    private val _placementsOverTime = MutableStateFlow<List<Int>>(emptyList())
    val placementsOverTime: StateFlow<List<Int>> = _placementsOverTime.asStateFlow()

    private val _childrenByStatus = MutableStateFlow<Map<String, Int>>(emptyMap())
    val childrenByStatus: StateFlow<Map<String, Int>> = _childrenByStatus.asStateFlow()

    fun loadAnalytics() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Simulate loading analytics data
                _summary.value = "Analytics loaded successfully"
                _placementsOverTime.value = listOf(5, 8, 12, 15, 10)
                _childrenByStatus.value = mapOf(
                    "Available" to 24,
                    "Placed" to 18,
                    "Pending" to 6
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
