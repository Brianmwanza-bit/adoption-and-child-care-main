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
 * 
 * @property childDao DAO for children data.
 * @property familyDao DAO for families data.
 * @property adoptionApplicationDao DAO for adoption applications.
 * @property homeStudyDao DAO for home study records.
 * @property placementDao DAO for placement records.
 * @property auditLogDao DAO for system audit logs.
 * @property notificationDao DAO for user notifications.
 * @property courtCaseDao DAO for legal court cases.
 * @property apiService Retrofit API service for backend communication.
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
    private val apiService: ApiService
) : ViewModel() {

    /** Current count of children in the database. */
    private val _childCount = MutableStateFlow(0)
    val childCount: StateFlow<Int> = _childCount.asStateFlow()

    /** Current count of active placements. */
    private val _placementCount = MutableStateFlow(0)
    val placementCount: StateFlow<Int> = _placementCount.asStateFlow()

    /** Current count of pending adoption applications. */
    private val _applicationCount = MutableStateFlow(0)
    val applicationCount: StateFlow<Int> = _applicationCount.asStateFlow()

    /** Current count of home study records. */
    private val _homeStudyCount = MutableStateFlow(0)
    val homeStudyCount: StateFlow<Int> = _homeStudyCount.asStateFlow()

    /** Backend analytics summary */
    private val _analyticsSummary = MutableStateFlow<AnalyticsSummary?>(null)
    val analyticsSummary: StateFlow<AnalyticsSummary?> = _analyticsSummary.asStateFlow()

    /** Loading state for API calls */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Error state for API calls */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Flow of recent system activities from the audit logs. */
    val recentActivities: StateFlow<List<AuditLogEntity>> = auditLogDao.observeAll()
        .map { list -> list.take(10) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Flow of priority alerts from system-wide notifications. */
    val priorityAlerts: StateFlow<List<NotificationEntity>> = notificationDao.observeAll()
        .map { list -> list.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Flow of combined upcoming events from court cases and home studies. */
    val upcomingEvents: StateFlow<List<Any>> = combine(
        courtCaseDao.observeUpcoming(),
        homeStudyDao.observeUpcoming()
    ) { courtCases, homeStudies ->
        (courtCases + homeStudies).sortedBy { 
            when (it) {
                is CourtCaseEntity -> it.hearingDate
                is HomeStudyEntity -> it.startedAt
                else -> ""
            }
        }.take(10)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshData()
    }

    /**
     * Refreshes the aggregate counts for statistics from all data sources.
     */
    fun refreshData() {
        viewModelScope.launch {
            _childCount.value = childDao.count()
            _placementCount.value = placementDao.count()
            _applicationCount.value = adoptionApplicationDao.count()
            _homeStudyCount.value = homeStudyDao.count()
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
                // Fall back to local data
                refreshData()
            } finally {
                _isLoading.value = false
            }
        }
    }
}