package com.adoptionapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel(private val repository: AnalyticsRepository) : ViewModel() {
    private val _summary = MutableStateFlow<String?>(null)
    val summary: StateFlow<String?> = _summary
    private val _placementsOverTime = MutableStateFlow<List<String>>(emptyList())
    val placementsOverTime: StateFlow<List<String>> = _placementsOverTime
    private val _childrenByStatus = MutableStateFlow<Map<String, Int>>(emptyMap())
    val childrenByStatus: StateFlow<Map<String, Int>> = _childrenByStatus
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAnalytics() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _summary.value = repository.getSummary()
                _placementsOverTime.value = repository.getPlacementsOverTime()
                _childrenByStatus.value = repository.getChildrenByStatus()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }
} 