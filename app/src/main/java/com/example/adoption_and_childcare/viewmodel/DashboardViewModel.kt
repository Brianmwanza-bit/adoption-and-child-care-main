package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.dao.*
import com.example.adoption_and_childcare.data.db.entities.*
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.network.AnalyticsSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Dashboard screen, providing real-time data from multiple DAOs and backend API.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val childDao: ChildDao,
    private val familyDao: FamilyDao,
    private val adoptionApplicationDao: AdoptionApplicationDao,
    private val homeStudyDao: HomeStudyDao,
    private val placementDao: PlacementDao,
    private val auditLogDao: AuditLogDao,
    private val notificationDao: NotificationDao,
    private val courtCaseDao: CourtCaseDao,
    private val fosterTaskDao: FosterTaskDao,
    private val dashboardMetricDao: DashboardMetricDao,
    private val systemSettingDao: SystemSettingDao,
    private val apiService: ApiService
) : ViewModel() {

    private val _childCount = MutableStateFlow(0)
    val childCount: StateFlow<Int> = _childCount.asStateFlow()

    private val _familyCount = MutableStateFlow(0)
    val familyCount: StateFlow<Int> = _familyCount.asStateFlow()

    private val _placementCount = MutableStateFlow(0)
    val placementCount: StateFlow<Int> = _placementCount.asStateFlow()

    private val _applicationCount = MutableStateFlow(0)
    val applicationCount: StateFlow<Int> = _applicationCount.asStateFlow()

    private val _overdueTasksCount = MutableStateFlow(0)
    val overdueTasksCount: StateFlow<Int> = _overdueTasksCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _analyticsSummary = MutableStateFlow<AnalyticsSummary?>(null)
    val analyticsSummary: StateFlow<AnalyticsSummary?> = _analyticsSummary.asStateFlow()

    /** Flow of dashboard metrics for UI display. */
    val metrics: StateFlow<List<DashboardMetricEntity>> = dashboardMetricDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Flow of system settings (e.g., Features, Testimonials). */
    val settings: StateFlow<List<SystemSettingEntity>> = systemSettingDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Flow of recent system activities from the audit logs. */
    val recentActivities: StateFlow<List<AuditLogEntity>> = auditLogDao.observeAll()
        .map { list -> list.take(10) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Flow of pending tasks. */
    val pendingTasks: StateFlow<List<FosterTaskEntity>> = fosterTaskDao.observeAll()
        .map { list -> list.filter { it.status?.uppercase() != "COMPLETED" }.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Flow of priority alerts from system-wide notifications. */
    val priorityAlerts: StateFlow<List<NotificationEntity>> = notificationDao.observeAll()
        .map { list -> list.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshData()
    }

    /**
     * Refreshes the aggregate counts for statistics from all data sources.
     */
    fun refreshData() {
        viewModelScope.launch {
            _childCount.value = childDao.count()
            _familyCount.value = familyDao.count()
            _placementCount.value = placementDao.count()
            _applicationCount.value = adoptionApplicationDao.count()
            
            val allTasks = fosterTaskDao.getAll()
            val now = System.currentTimeMillis().toString()
            _overdueTasksCount.value = allTasks.count { 
                it.status?.uppercase() == "PENDING" && it.dueDate != null && it.dueDate < now
            }
        }
    }

    /**
     * Fetches analytics summary from the backend API.
     */
    fun fetchAnalyticsFromBackend(token: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val authHeader = if (token.isNotEmpty()) "Bearer $token" else ""
                val response = apiService.getAnalyticsSummary(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    _analyticsSummary.value = response.body()
                } else {
                    _error.value = "Failed to fetch analytics: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
                refreshData()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
