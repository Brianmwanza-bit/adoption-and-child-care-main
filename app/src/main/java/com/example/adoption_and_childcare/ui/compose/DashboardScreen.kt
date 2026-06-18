package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.adoption_and_childcare.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel
import com.example.adoption_and_childcare.data.repository.DashboardMetricsRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import com.example.adoption_and_childcare.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

/**
 * Data class representing a card in the dashboard.
 * @property title The title displayed on the card.
 * @property icon The icon associated with the module.
 * @property count The numeric value to display.
 * @property summary A brief description or status.
 * @property color The theme color for the card.
 * @property route The navigation route associated with the card.
 */
data class DashboardCard(
    val title: String,
    val icon: ImageVector,
    val count: Int,
    val summary: String,
    val color: Color,
    val route: String? = null
)

/**
 * Data class representing an actionable task for the user.
 * @property id Unique identifier for the task.
 * @property title Title of the action item.
 * @property priority Level of urgency (urgent, high, normal).
 * @property dueDate When the task is due.
 * @property assignee The person responsible for the task.
 * @property caseId ID of the associated case.
 * @property childName Name of the child involved.
 */
data class ActionItem(
    val id: String,
    val title: String,
    val priority: String,
    val dueDate: String,
    val assignee: String,
    val caseId: String = "",
    val childName: String = ""
)

/**
 * Data class representing the status of a specific case.
 * @property id Unique identifier for the case.
 * @property childName Name of the child in the case.
 * @property status Current status of the case.
 * @property urgency Level of urgency (critical, high, normal).
 * @property daysInStatus Number of days since the last status change.
 * @property nextDeadline Next important date for the case.
 * @property assignee Case worker assigned.
 * @property familyName Name of the family involved.
 */
data class CaseStatus(
    val id: String,
    val childName: String,
    val status: String,
    val urgency: String, // critical, high, normal
    val daysInStatus: Int,
    val nextDeadline: String,
    val assignee: String,
    val familyName: String = ""
)

/**
 * Main dashboard screen providing an overview of cases, tasks, and system modules.
 * @param onNavigate Callback for navigation events.
 * @param notificationsViewModel ViewModel for managing notification state.
 */
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit = {}, 
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    caseToolsViewModel: CaseToolsViewModel = hiltViewModel()
) {
    val loading by notificationsViewModel.loading.collectAsState()
    val error by notificationsViewModel.error.collectAsState()
    val unreadCount by notificationsViewModel.unreadCount.collectAsState()

    // Dashboard Preferences
    val dashboardPrefs by caseToolsViewModel.dashboardPreferences.collectAsState()
    val latestPrefs = dashboardPrefs.lastOrNull()

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val authManager = remember { AuthManager(context) }
    val metricsRepository = remember { DashboardMetricsRepositoryImpl(db.dashboardMetricDao(), apiService, authManager) }
    val scope = rememberCoroutineScope()
    
    val userRole = sessionManager.getRole() ?: "Guest"
    var childrenCount by remember { mutableStateOf(0) }
    var familiesCount by remember { mutableStateOf(0) }
    var adoptionAppsCount by remember { mutableStateOf(0) }
    var homeStudiesCount by remember { mutableStateOf(0) }
    var documentsCount by remember { mutableStateOf(0) }
    var placementsCount by remember { mutableStateOf(0) }
    var reportsCount by remember { mutableStateOf(0) }
    var educationCount by remember { mutableStateOf(0) }
    var medicalCount by remember { mutableStateOf(0) }
    var financeCount by remember { mutableStateOf(0) }
    var overdueTasks by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        notificationsViewModel.loadNotifications()
        
        // Load from local DB
        withContext(Dispatchers.IO) {
            val c = db.childDao().count()
            val f = db.familyDao().count()
            val aa = db.adoptionApplicationDao().count()
            val hs = db.homeStudyDao().count()
            val d = db.documentDao().count()
            val p = db.placementDao().count()
            val r = db.caseReportDao().count()
            val e = db.educationRecordDao().count()
            val m = db.medicalRecordDao().count()
            val fin = db.moneyRecordDao().count()
            withContext(Dispatchers.Main) {
                childrenCount = c
                familiesCount = f
                adoptionAppsCount = aa
                homeStudiesCount = hs
                documentsCount = d
                placementsCount = p
                reportsCount = r
                educationCount = e
                medicalCount = m
                financeCount = fin
                overdueTasks = (c * 0.15).toInt()
            }
        }
        
        /* 
        // Fetch real-time metrics from API - Disabled for local-only Room mode
        scope.launch {
            try {
                val token = authManager.getAuthToken() ?: ""
                if (token.isNotEmpty()) {
                    val result = metricsRepository.fetchFromApi(token)
                    if (result.isSuccess) {
                        println("Dashboard metrics fetched from API successfully")
                    }
                }
            } catch (e: Exception) {
                println("Failed to fetch dashboard metrics: ${e.message}")
            }
        }
        */
    }

    val allModules = listOf(
        DashboardCard(stringResource(R.string.dashboard_active_cases), Icons.Default.FolderOpen, childrenCount + familiesCount, stringResource(R.string.dashboard_active_cases_summary), MaterialTheme.colorScheme.primary, route = "children_list"),
        DashboardCard(stringResource(R.string.dashboard_awaiting_placement), Icons.Default.Schedule, adoptionAppsCount, stringResource(R.string.dashboard_awaiting_placement_summary), MaterialTheme.colorScheme.secondary, route = "adoption_applications"),
        DashboardCard(stringResource(R.string.dashboard_in_placement), Icons.Default.Home, placementsCount, stringResource(R.string.dashboard_in_placement_summary), MaterialTheme.colorScheme.tertiary, route = "placements"),
        DashboardCard(stringResource(R.string.dashboard_home_studies), Icons.Default.AssignmentTurnedIn, homeStudiesCount, stringResource(R.string.dashboard_home_studies_summary), MaterialTheme.colorScheme.error, route = "home_studies"),
        DashboardCard(stringResource(R.string.dashboard_documents), Icons.Default.Description, documentsCount, stringResource(R.string.dashboard_documents_summary), MaterialTheme.colorScheme.outline, route = "documents"),
        DashboardCard(stringResource(R.string.dashboard_medical), Icons.Default.LocalHospital, medicalCount, stringResource(R.string.dashboard_medical_summary), MaterialTheme.colorScheme.errorContainer, route = "medical"),
        DashboardCard(stringResource(R.string.dashboard_education), Icons.Default.School, educationCount, stringResource(R.string.dashboard_education_summary), MaterialTheme.colorScheme.primaryContainer, route = "education"),
        DashboardCard(stringResource(R.string.dashboard_finance), Icons.Default.AttachMoney, financeCount, stringResource(R.string.dashboard_finance_summary), MaterialTheme.colorScheme.secondaryContainer, route = "finance"),
        DashboardCard(stringResource(R.string.dashboard_reports), Icons.Default.Assessment, reportsCount, stringResource(R.string.dashboard_reports_summary), MaterialTheme.colorScheme.tertiaryContainer, route = "reports"),
        DashboardCard(stringResource(R.string.dashboard_families_module), Icons.Default.FamilyRestroom, familiesCount, stringResource(R.string.dashboard_families_summary), MaterialTheme.colorScheme.inversePrimary, route = "families")
    )


    // Role-based module filtering
    val filteredModules = when (userRole) {
        "Admin", "Case Worker", "Supervisor" -> allModules // Full access
        "Social Worker" -> allModules.filter {
            it.route in listOf("children_list", "families", "documents", "reports", "home_studies", "placements")
        }
        "Guardian" -> allModules.filter {
            it.route in listOf("children_list", "documents", "education", "medical")
        }
        else -> allModules.filter {
            it.route in listOf("children_list", "documents")
        } // Limited access for other roles
    }
    val criticalCases = listOf(
        CaseStatus("001", "Emma Smith", "Waiting for match", "critical", 45, "2024-07-30", "Sarah Chen", "Approved Family"),
        CaseStatus("002", "Noah Johnson", "In placement", "high", 15, "2024-07-22", "Mike Wilson", "Foster Keller Family"),
        CaseStatus("003", "Sophia Brown", "Matched", "high", 7, "2024-07-15", "Lisa Anderson", "Baker Family")
    )

    val actionItems = listOf(
        ActionItem("1", "Home Study Review - Smith Family", "urgent", "Today", "You", "001", "Emma S."),
        ActionItem("2", "Medical Follow-up - Noah J.", "urgent", "Tomorrow", "Dr. Brown", "002", "Noah J."),
        ActionItem("3", "Document Collection - Sophia B.", "high", "In 2 days", "You", "003", "Sophia B."),
        ActionItem("4", "Placement Stability Check", "normal", "In 7 days", "Mike Wilson", "002", "Noah J.")
    )

    Scaffold { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (loading) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                DashboardHeaderSection(unreadCount, overdueTasks)
                                SearchHeaderBar(
                                    query = searchQuery,
                                    onQueryChange = { searchQuery = it },
                                    onSearch = { /* Implement search */ }
                                )
                                if (latestPrefs?.showAlerts != false) {
                                    CriticalAlertsSection(criticalCases, overdueTasks, onNavigate)
                                }
                                TodaysWorkloadSection(actionItems.filter { it.dueDate == "Today" || it.dueDate == "Tomorrow" })
                            }
                        }
                    }

                    if (latestPrefs?.showActionItems != false) {
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    stringResource(R.string.dashboard_all_action_items),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                actionItems.forEach { ActionItemCard(it) }
                            }
                        }
                    }

                    if (latestPrefs?.showQuickActions != false) {
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    stringResource(R.string.dashboard_system_modules),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                
                                // Modern Item Arrangement (Rows and weights)
                                filteredModules.chunked(2).forEach { rowModules ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        rowModules.forEach { card ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                CompactModuleCard(card) { card.route?.let { onNavigate(it) } }
                                            }
                                        }
                                        // Add a spacer if there's only one item in the row to maintain layout
                                        if (rowModules.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

/**
 * Displays an individual action item as a card.
 * @param item The action item data to display.
 */
@Composable
fun ActionItemCard(item: ActionItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        when (item.priority) {
                            "urgent" -> Color(0xFFE91E63)
                            "high" -> Color(0xFFFF9800)
                            else -> Color(0xFF4CAF50)
                        },
                        CircleShape
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.dueDate, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(item.assignee, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Icon(Icons.Default.ChevronRight, "Navigate", tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

/**
 * Header section of the dashboard showing key summary statistics.
 * @param unreadCount Number of unread notifications.
 * @param overdueTasks Number of tasks that are past their due date.
 */
@Composable
fun DashboardHeaderSection(unreadCount: Int, overdueTasks: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderStatCard(stringResource(R.string.dashboard_stat_urgent_cases), "3", MaterialTheme.colorScheme.error, Modifier.weight(1f))
        HeaderStatCard(stringResource(R.string.dashboard_stat_overdue), overdueTasks.toString(), MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
        HeaderStatCard(stringResource(R.string.dashboard_stat_todays_tasks), "4", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
        HeaderStatCard(stringResource(R.string.dashboard_stat_messages), unreadCount.toString(), MaterialTheme.colorScheme.inversePrimary, Modifier.weight(1f))
    }
}

/**
 * A small stat card used within the dashboard header.
 * @param label Description of the stat.
 * @param value The value to display.
 * @param color Color used for background accent (not currently used for background color).
 * @param modifier Modifier for layout adjustments.
 */
@Composable
fun HeaderStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.9f))
    }
}

/**
 * Search bar displayed at the top of the dashboard.
 * @param query Current search query.
 * @param onQueryChange Callback when the query text changes.
 * @param onSearch Callback when the search action is triggered.
 * @param modifier Modifier for layout adjustments.
 */
@Composable
fun SearchHeaderBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(stringResource(R.string.dashboard_search_hint)) },
        leadingIcon = { Icon(Icons.Default.Search, stringResource(R.string.dashboard_search_desc), tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, stringResource(R.string.dashboard_clear_desc))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        textStyle = MaterialTheme.typography.bodySmall
    )
}

/**
 * Section highlighting critical alerts and overdue tasks.
 * @param cases List of cases requiring immediate attention.
 * @param overdueTasks Total number of overdue tasks.
 * @param onNavigate Callback for navigation when an alert is clicked.
 */
@Composable
fun CriticalAlertsSection(cases: List<CaseStatus>, overdueTasks: Int, onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.dashboard_section_urgent_attention), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        cases.forEach { case ->
            CriticalCaseCard(case)
        }
        if (overdueTasks > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("reports") },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE91E63))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.dashboard_overdue_tasks_alert, overdueTasks), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                        Text(stringResource(R.string.dashboard_action_required_today), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Icon(Icons.Default.ChevronRight, "Navigate", tint = Color(0xFFE91E63))
                }
            }
        }
    }
}

/**
 * Card representing a single critical case alert.
 * @param case The case status data to display.
 */
@Composable
fun CriticalCaseCard(case: CaseStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (case.urgency) {
                "critical" -> Color(0xFFFFEBEE)
                "high" -> Color(0xFFFFF3E0)
                else -> Color(0xFFF5F5F5)
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            when (case.urgency) {
                "critical" -> Color(0xFFE91E63)
                "high" -> Color(0xFFFF9800)
                else -> Color(0xFFBDBDBD)
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(case.childName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(case.familyName, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Badge(
                    modifier = Modifier.align(Alignment.Top),
                    containerColor = when (case.urgency) {
                        "critical" -> Color(0xFFE91E63)
                        "high" -> Color(0xFFFF9800)
                        else -> Color(0xFF9CCC65)
                    }
                ) {
                    Text(case.status, style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 8.sp)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Days in status: ${case.daysInStatus}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("Deadline: ${case.nextDeadline}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Text(case.assignee, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
            }
        }
    }
}

/**
 * Section displaying the user's workload for the current day.
 * @param tasks List of tasks scheduled for today or tomorrow.
 */
@Composable
fun TodaysWorkloadSection(tasks: List<ActionItem>) {
    if (tasks.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.dashboard_section_todays_workload), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            tasks.forEach { task ->
                CompactActionCard(task)
            }
        }
    }
}

/**
 * A more compact version of an action card for list displays.
 * @param item The action item data.
 */
@Composable
fun CompactActionCard(item: ActionItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        when (item.priority) {
                            "urgent" -> Color(0xFFE91E63)
                            "high" -> Color(0xFFFF9800)
                            else -> Color(0xFF4CAF50)
                        },
                        CircleShape
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("${item.childName} • ${item.dueDate}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, "Navigate", modifier = Modifier.size(18.dp), tint = Color.Gray)
        }
    }
}


/**
 * A compact square card representing a system module.
 * @param card The module data.
 * @param onClick Callback when the module is clicked.
 */
@Composable
fun CompactModuleCard(card: DashboardCard, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = card.route != null) { onClick() },
        colors = CardDefaults.cardColors(containerColor = card.color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, card.color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(card.icon, card.title, tint = card.color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(card.count.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = card.color)
            Text(card.title, style = MaterialTheme.typography.labelSmall, color = card.color, fontSize = 10.sp)
            Text(card.summary, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 8.sp)
        }
    }
}

