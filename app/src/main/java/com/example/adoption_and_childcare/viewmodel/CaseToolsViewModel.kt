package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.dao.*
import com.example.adoption_and_childcare.data.db.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaseToolsViewModel @Inject constructor(
    private val riskAssessmentDao: RiskAssessmentDao,
    private val permanencyPlanDao: PermanencyPlanDao,
    private val caseActivityDao: CaseActivityDao,
    private val caseDeadlineDao: CaseDeadlineDao,
    private val caseApprovalDao: CaseApprovalDao,
    private val caseUrgencyFlagDao: CaseUrgencyFlagDao,
    private val criticalDateDao: CriticalDateDao,
    private val placementCompatibilityDao: PlacementCompatibilityDao,
    private val taskDao: TaskDao,
    private val actionItemDao: ActionItemDao,
    private val workerMessageDao: WorkerMessageDao,
    private val workloadTrackingDao: WorkloadTrackingDao,
    private val dashboardPreferenceDao: DashboardPreferenceDao
) : ViewModel() {

    // Risk Assessments
    val riskAssessments: StateFlow<List<RiskAssessmentEntity>> = riskAssessmentDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveRiskAssessment(assessment: RiskAssessmentEntity) {
        viewModelScope.launch { riskAssessmentDao.insert(assessment) }
    }

    // Permanency Plans
    val permanencyPlans: StateFlow<List<PermanencyPlanEntity>> = permanencyPlanDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun savePermanencyPlan(plan: PermanencyPlanEntity) {
        viewModelScope.launch { permanencyPlanDao.insert(plan) }
    }

    // Case Activities
    val caseActivities: StateFlow<List<CaseActivityEntity>> = caseActivityDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCaseActivity(activity: CaseActivityEntity) {
        viewModelScope.launch { caseActivityDao.insert(activity) }
    }

    // Deadlines
    val deadlines: StateFlow<List<CaseDeadlineEntity>> = caseDeadlineDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveDeadline(deadline: CaseDeadlineEntity) {
        viewModelScope.launch { caseDeadlineDao.insert(deadline) }
    }

    // Approvals
    val approvals: StateFlow<List<CaseApprovalEntity>> = caseApprovalDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveApproval(approval: CaseApprovalEntity) {
        viewModelScope.launch { caseApprovalDao.insert(approval) }
    }

    // Urgency Flags
    val urgencyFlags: StateFlow<List<CaseUrgencyFlagEntity>> = caseUrgencyFlagDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveUrgencyFlag(flag: CaseUrgencyFlagEntity) {
        viewModelScope.launch { caseUrgencyFlagDao.insert(flag) }
    }

    // Critical Dates
    val criticalDates: StateFlow<List<CriticalDateEntity>> = criticalDateDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCriticalDate(date: CriticalDateEntity) {
        viewModelScope.launch { criticalDateDao.insert(date) }
    }

    // Placement Compatibility
    val compatibilityRecords: StateFlow<List<PlacementCompatibilityEntity>> = placementCompatibilityDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCompatibilityRecord(record: PlacementCompatibilityEntity) {
        viewModelScope.launch { placementCompatibilityDao.insert(record) }
    }

    // Tasks
    val tasks: StateFlow<List<TaskEntity>> = taskDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveTask(task: TaskEntity) {
        viewModelScope.launch { taskDao.insert(task) }
    }

    // Action Items
    val actionItems: StateFlow<List<ActionItemEntity>> = actionItemDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveActionItem(item: ActionItemEntity) {
        viewModelScope.launch { actionItemDao.insert(item) }
    }

    // Worker Messages
    val workerMessages: StateFlow<List<WorkerMessageEntity>> = workerMessageDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveWorkerMessage(message: WorkerMessageEntity) {
        viewModelScope.launch { workerMessageDao.insert(message) }
    }

    // Workload Tracking
    val workload: StateFlow<List<WorkloadTrackingEntity>> = workloadTrackingDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveWorkload(entry: WorkloadTrackingEntity) {
        viewModelScope.launch { workloadTrackingDao.insert(entry) }
    }
    
    // Dashboard Preferences
    val dashboardPreferences: StateFlow<List<DashboardPreferenceEntity>> = dashboardPreferenceDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    fun saveDashboardPreference(pref: DashboardPreferenceEntity) {
        viewModelScope.launch { dashboardPreferenceDao.insert(pref) }
    }
}
