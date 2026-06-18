package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel
import com.example.adoption_and_childcare.MainActivityConstants
import com.example.adoption_and_childcare.canAccessRoute
import com.yourdomain.adoptionchildcare.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

private const val PERCENT_OVERDUE = 0.15
private const val PERCENT_AT_RISK = 0.2

/**
 * Data class representing a modern action item on the dashboard.
 *
 * @property id Unique identifier for the action item.
 * @property title Title of the action item.
 * @property priority Priority level (e.g., "urgent", "high", "normal").
 * @property dueDate When the item is due.
 * @property assignee Person assigned to the item.
 * @property relatedEntity The entity or module this item relates to.
 */
data class ActionItemModern(
    val id: String,
    val title: String,
    val priority: String,
    val dueDate: String,
    val assignee: String,
    val relatedEntity: String
)

/**
 * Main dashboard screen providing a modern overview of the adoption system.
 *
 * @param userRole The role of the current user.
 * @param onNavigate Callback for navigation to different routes.
 * @param notificationsViewModel ViewModel for managing notifications.
 * @param caseToolsViewModel ViewModel for managing case-specific tools and dashboard preferences.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenModern(
    userRole: String = stringResource(R.string.dashboard_guest),
    onNavigate: (String) -> Unit = {},
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    caseToolsViewModel: CaseToolsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Dashboard Preferences
    val dashboardPrefs by caseToolsViewModel.dashboardPreferences.collectAsState()
    val latestPrefs = dashboardPrefs.lastOrNull()

    // Data states
    var childrenCount by remember { mutableIntStateOf(0) }
    var familiesCount by remember { mutableIntStateOf(0) }
    var adoptionAppsCount by remember { mutableIntStateOf(0) }
    var placementsCount by remember { mutableIntStateOf(0) }
    var overdueTasks by remember { mutableIntStateOf(0) }
    var atRiskCount by remember { mutableIntStateOf(0) }
    
    var dynamicActionItems by remember { mutableStateOf<List<ActionItemModern>>(emptyList()) }
    var dynamicUpdates by remember { mutableStateOf<List<String>>(emptyList()) }

    val db = remember { AppDatabase.getInstance(context) }

    LaunchedEffect(Unit) {
        notificationsViewModel.loadNotifications()
        
        withContext(Dispatchers.IO) {
            val c = db.childDao().count()
            val f = db.familyDao().count()
            val aa = db.adoptionApplicationDao().count()
            val p = db.placementDao().count()
            withContext(Dispatchers.Main) {
                childrenCount = c
                familiesCount = f
                adoptionAppsCount = aa
                placementsCount = p
                overdueTasks = (c * PERCENT_OVERDUE).toInt()
                atRiskCount = (p * PERCENT_AT_RISK).toInt()
            }
        }
    }

    // Observe tasks for dynamic items
    LaunchedEffect(Unit) {
        db.fosterTaskDao().observeAll().collectLatest { tasksList ->
            val taskDefault = context.getString(R.string.dashboard_task_default)
            val priorityNormal = context.getString(R.string.dashboard_priority_normal)
            val noDate = context.getString(R.string.dashboard_no_date)
            val staffAssignee = context.getString(R.string.dashboard_staff_assignee)
            val fosterTasksEntity = context.getString(R.string.dashboard_foster_tasks_entity)

            dynamicActionItems = tasksList.take(3).map { task ->
                ActionItemModern(
                    id = task.taskId.toString(),
                    title = task.description ?: taskDefault,
                    priority = task.status?.lowercase() ?: priorityNormal,
                    dueDate = task.dueDate ?: noDate,
                    assignee = staffAssignee,
                    relatedEntity = fosterTasksEntity
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        db.auditLogDao().observeAll().collectLatest { auditLogs ->
            dynamicUpdates = auditLogs.take(4).map { log ->
                context.getString(R.string.dashboard_audit_update_format, log.action, log.tableName, log.recordId)
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Bar (conditional)
            if (showSearch) {
                SearchBarModern(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // PRIORITY ALERT SECTION
                if (latestPrefs?.showAlerts != false) {
                    item {
                        PriorityAlertSectionModern(
                            urgentCount = 2,
                            atRiskCount = atRiskCount,
                            upcomingCount = 5
                        )
                    }
                }

                // KEY METRICS SECTION
                if (latestPrefs?.showStats != false) {
                    item {
                        KeyMetricsSectionModern(
                            userRole = userRole,
                            childrenCount = childrenCount,
                            familiesCount = familiesCount,
                            placementsCount = placementsCount,
                            adoptionAppsCount = adoptionAppsCount,
                            onNavigate = onNavigate
                        )
                    }
                }

                // OVERDUE TASKS SECTION
                item {
                    if (overdueTasks > 0) {
                        OverdueTasksSectionModern(count = overdueTasks) {
                            onNavigate(context.getString(R.string.route_reports))
                        }
                    }
                }

                // ACTION ITEMS SECTION
                if (latestPrefs?.showActionItems != false) {
                    item {
                        ActionItemsSectionModern(
                            items = dynamicActionItems,
                            onNavigate = onNavigate
                        )
                    }
                }

                // RECENT UPDATES SECTION
                if (latestPrefs?.showRecentActivity != false) {
                    item {
                        RecentUpdatesSectionModern(
                            updates = dynamicUpdates,
                            onViewAll = { onNavigate(context.getString(R.string.route_audit_logs)) }
                        )
                    }
                }

                // QUICK ACCESS MODULES
                if (latestPrefs?.showQuickActions != false) {
                    item {
                        QuickAccessModulesModern(userRole = userRole, onNavigate = onNavigate)
                    }
                }

                // ALL SCREENS GRID
                item {
                    AllScreensGridModern(userRole = userRole, onNavigate = onNavigate)
                }

                // CASE TOOLS SECTION
                item {
                    CaseToolsGridModern(userRole = userRole, onNavigate = onNavigate)
                }
            }
        }
    }
}

/**
 * Grid displaying case-specific tools with modern card design.
 *
 * @param userRole The role of the current user.
 * @param onNavigate Navigation callback.
 */
@Composable
fun CaseToolsGridModern(userRole: String, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    
    val allTools = listOf(
        Triple(R.string.dashboard_label_risk_assessments, R.string.dashboard_desc_risk_assessments, Triple(Icons.Default.Shield, Color(0xFFE91E63), MainActivityConstants.RISK_ASSESSMENTS_ROUTE)),
        Triple(R.string.dashboard_label_permanency_plans, R.string.dashboard_desc_permanency_plans, Triple(Icons.Default.Map, Color(0xFF2196F3), MainActivityConstants.PERMANENCY_PLANS_ROUTE)),
        Triple(R.string.dashboard_label_case_activities, R.string.dashboard_desc_case_activities, Triple(Icons.Default.History, Color(0xFF4CAF50), MainActivityConstants.CASE_ACTIVITIES_ROUTE)),
        Triple(R.string.dashboard_label_deadlines, R.string.dashboard_desc_deadlines, Triple(Icons.Default.Schedule, Color(0xFFFF9800), MainActivityConstants.CASE_DEADLINES_ROUTE)),
        Triple(R.string.dashboard_label_approvals, R.string.dashboard_desc_approvals, Triple(Icons.Default.CheckCircle, Color(0xFF9C27B0), MainActivityConstants.CASE_APPROVALS_ROUTE)),
        Triple(R.string.dashboard_label_urgency_flags, R.string.dashboard_desc_urgency_flags, Triple(Icons.Default.PriorityHigh, Color(0xFFF44336), MainActivityConstants.CASE_URGENCY_FLAGS_ROUTE)),
        Triple(R.string.dashboard_label_critical_dates, R.string.dashboard_desc_critical_dates, Triple(Icons.Default.Event, Color(0xFF607D8B), MainActivityConstants.CRITICAL_DATES_ROUTE)),
        Triple(R.string.dashboard_label_compatibility, R.string.dashboard_desc_compatibility, Triple(Icons.AutoMirrored.Filled.CompareArrows, Color(0xFF009688), MainActivityConstants.PLACEMENT_COMPATIBILITY_ROUTE)),
        Triple(R.string.dashboard_label_investigations, R.string.dashboard_desc_investigations, Triple(Icons.Default.Search, Color(0xFF795548), MainActivityConstants.INVESTIGATIONS_ROUTE)),
        Triple(R.string.dashboard_label_service_plans, R.string.dashboard_desc_service_plans, Triple(Icons.AutoMirrored.Filled.Assignment, Color(0xFF00BCD4), MainActivityConstants.SERVICE_PLANS_ROUTE)),
        Triple(R.string.dashboard_label_visitation, R.string.dashboard_desc_visitation, Triple(Icons.Default.Event, Color(0xFFCDDC39), MainActivityConstants.VISITATION_SCHEDULES_ROUTE)),
        Triple(R.string.dashboard_label_aftercare, R.string.dashboard_desc_aftercare, Triple(Icons.Default.LocalHospital, Color(0xFF795548), MainActivityConstants.AFTERCARE_PLANS_ROUTE)),
        Triple(R.string.dashboard_label_disruptions, R.string.dashboard_desc_disruptions, Triple(Icons.Default.WarningAmber, Color(0xFFFF5722), MainActivityConstants.PLACEMENT_DISRUPTIONS_ROUTE)),
        Triple(R.string.dashboard_label_foster_training_alt, R.string.dashboard_desc_foster_training_alt, Triple(Icons.Default.School, Color(0xFF3F51B5), MainActivityConstants.FOSTER_TRAINING_ROUTE))
    )

    val filteredTools = allTools.filter { canAccessRoute(it.third.third, userRole) }

    if (filteredTools.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                stringResource(R.string.dashboard_case_tools),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            
            filteredTools.chunked(2).forEach { rowTools ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowTools.forEach { tool ->
                        ModernScreenCard(
                            label = stringResource(tool.first),
                            description = stringResource(tool.second),
                            icon = tool.third.first,
                            color = tool.third.second,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(tool.third.third) }
                        )
                    }
                    if (rowTools.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Modern search bar for filtering dashboard content.
 *
 * @param query Current search query.
 * @param onQueryChange Callback for text changes.
 * @param modifier Layout modifier.
 */
@Composable
fun SearchBarModern(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.dashboard_search_hint)) },
        leadingIcon = { Icon(Icons.Default.Search, stringResource(R.string.dashboard_search_desc)) },
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
            focusedBorderColor = Color(0xFF9C27B0),
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
        )
    )
}

/**
 * Section displaying priority alerts for urgent, at-risk, and upcoming cases.
 *
 * @param urgentCount Number of urgent alerts.
 * @param atRiskCount Number of at-risk cases.
 * @param upcomingCount Number of upcoming events.
 */
@Composable
fun PriorityAlertSectionModern(
    urgentCount: Int,
    atRiskCount: Int,
    upcomingCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.dashboard_priority_alerts),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AlertCardModern(
                count = urgentCount,
                label = stringResource(R.string.dashboard_urgent),
                color = Color(0xFFE91E63),
                modifier = Modifier.weight(1f)
            )
            AlertCardModern(
                count = atRiskCount,
                label = stringResource(R.string.dashboard_at_risk),
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            AlertCardModern(
                count = upcomingCount,
                label = stringResource(R.string.dashboard_upcoming),
                color = Color(0xFFFFC107),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * A card representing a specific alert category.
 *
 * @param count Numeric value to display.
 * @param label Category label.
 * @param color Theme color for the card.
 * @param modifier Layout modifier.
 */
@Composable
fun AlertCardModern(
    count: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

/**
 * Section displaying key metrics like children, families, and placements count.
 *
 * @param userRole The role of the current user.
 * @param childrenCount Total children.
 * @param familiesCount Total families.
 * @param placementsCount Total active placements.
 * @param adoptionAppsCount Total adoption applications.
 * @param onNavigate Navigation callback.
 */
@Composable
fun KeyMetricsSectionModern(
    userRole: String,
    childrenCount: Int,
    familiesCount: Int,
    placementsCount: Int,
    adoptionAppsCount: Int,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    
    val allMetrics = listOf(
        Triple(R.string.dashboard_metric_children, childrenCount, Triple(Icons.Default.ChildCare, Color(0xFF4CAF50), MainActivityConstants.CHILDREN_LIST_ROUTE)),
        Triple(R.string.dashboard_metric_families, familiesCount, Triple(Icons.Default.FamilyRestroom, Color(0xFF2196F3), MainActivityConstants.FAMILIES_ROUTE)),
        Triple(R.string.dashboard_metric_placements, placementsCount, Triple(Icons.Default.Home, Color(0xFF8BC34A), MainActivityConstants.PLACEMENTS_ROUTE)),
        Triple(R.string.dashboard_metric_applications, adoptionAppsCount, Triple(Icons.Default.Folder, Color(0xFF9C27B0), MainActivityConstants.ADOPTION_APPS_ROUTE))
    )

    val filteredMetrics = allMetrics.filter { canAccessRoute(it.third.third, userRole) }

    if (filteredMetrics.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.dashboard_key_metrics),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            filteredMetrics.chunked(2).forEach { rowMetrics ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowMetrics.forEach { metric ->
                        MetricCardModern(
                            title = stringResource(metric.first),
                            count = metric.second,
                            icon = metric.third.first,
                            color = metric.third.second,
                            trend = when (metric.first) {
                                R.string.dashboard_metric_children -> stringResource(R.string.dashboard_trend_children)
                                R.string.dashboard_metric_families -> stringResource(R.string.dashboard_trend_families)
                                R.string.dashboard_metric_placements -> stringResource(R.string.dashboard_trend_placements)
                                else -> stringResource(R.string.dashboard_trend_applications)
                            },
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(metric.third.third) }
                        )
                    }
                    if (rowMetrics.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * A card displaying a key metric with an icon and trend information.
 *
 * @param title Metric title.
 * @param count Metric value.
 * @param icon Associated icon.
 * @param color Theme color.
 * @param trend Trend description text.
 * @param modifier Layout modifier.
 * @param onClick Click callback.
 */
@Composable
fun MetricCardModern(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    trend: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, title, tint = color, modifier = Modifier.size(24.dp))
                Text(count.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(trend, style = MaterialTheme.typography.bodySmall, color = color, fontSize = 10.sp)
        }
    }
}

/**
 * Section alerting the user about overdue tasks.
 *
 * @param count Number of overdue tasks.
 * @param onViewTasks Callback to view task details.
 */
@Composable
fun OverdueTasksSectionModern(count: Int, onViewTasks: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewTasks() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE91E63))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    stringResource(R.string.dashboard_overdue_tasks_count, count),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63)
                )
                Text(
                    stringResource(R.string.dashboard_action_required_today),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Icon(Icons.Default.Warning, stringResource(R.string.dashboard_stat_overdue), tint = Color(0xFFE91E63), modifier = Modifier.size(24.dp))
        }
    }
}

/**
 * Section listing actionable items for the user.
 *
 * @param items List of modern action items.
 * @param onNavigate Navigation callback.
 */
@Composable
fun ActionItemsSectionModern(
    items: List<ActionItemModern>,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.dashboard_action_items),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.dashboard_view_all),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9C27B0),
                modifier = Modifier.clickable { onNavigate(context.getString(R.string.route_reports)) }
            )
        }
        if (items.isEmpty()) {
            Text(
                stringResource(R.string.dashboard_no_action_items),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            items.forEach { actionItem ->
                ActionItemCardModern(actionItem)
            }
        }
    }
}

/**
 * A card representing a single action item.
 *
 * @param actionItem The action item details.
 */
@Composable
fun ActionItemCardModern(actionItem: ActionItemModern) {
    val urgentVal = stringResource(R.string.dashboard_priority_urgent_val)
    val highVal = stringResource(R.string.dashboard_priority_high_val)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        when (actionItem.priority) {
                            urgentVal -> Color(0xFFE91E63)
                            highVal -> Color(0xFFFF9800)
                            else -> Color(0xFF4CAF50)
                        },
                        CircleShape
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(actionItem.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(actionItem.dueDate, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(actionItem.assignee, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, stringResource(R.string.footer_action_approve), tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

/**
 * Section displaying a list of recent updates from the system.
 *
 * @param updates List of update description strings.
 * @param onViewAll Callback to navigate to all audit logs.
 */
@Composable
fun RecentUpdatesSectionModern(updates: List<String>, onViewAll: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.dashboard_recent_updates),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.dashboard_view_all),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9C27B0),
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        if (updates.isEmpty()) {
            Text(
                stringResource(R.string.dashboard_no_updates),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            updates.forEach { updateText ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Info, stringResource(R.string.activity_doc_uploaded), tint = Color(0xFF9C27B0), modifier = Modifier.size(20.dp))
                    Text(updateText, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Section providing quick access to document, medical, education, and finance modules.
 *
 * @param userRole The role of the current user.
 * @param onNavigate Navigation callback.
 */
@Composable
fun QuickAccessModulesModern(userRole: String, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val allButtons = listOf(
        Triple(R.string.dashboard_module_documents, Icons.Default.Description, Pair(Color(0xFF607D8B), MainActivityConstants.DOCUMENTS_ROUTE)),
        Triple(R.string.dashboard_module_medical, Icons.Default.LocalHospital, Pair(Color(0xFFF44336), MainActivityConstants.MEDICAL_ROUTE)),
        Triple(R.string.dashboard_module_education, Icons.Default.School, Pair(Color(0xFF3F51B5), MainActivityConstants.EDUCATION_ROUTE)),
        Triple(R.string.dashboard_module_finance, Icons.Default.AttachMoney, Pair(Color(0xFF4CAF50), MainActivityConstants.FINANCE_ROUTE))
    )

    val filteredButtons = allButtons.filter { canAccessRoute(it.third.second, userRole) }

    if (filteredButtons.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.dashboard_quick_access),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredButtons.forEach { btn ->
                    QuickAccessButtonModern(
                        stringResource(btn.first),
                        btn.second,
                        btn.third.first,
                        Modifier.weight(1f)
                    ) {
                        onNavigate(btn.third.second)
                    }
                }
                // Fill up the row if less than 4 items to keep size consistent
                repeat(4 - filteredButtons.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Grid displaying all main screens for easy navigation with modern card design.
 *
 * @param userRole The role of the current user.
 * @param onNavigate Navigation callback.
 */
@Composable
fun AllScreensGridModern(userRole: String, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    
    val allScreens = listOf(
        Triple(R.string.dashboard_label_analytics, R.string.dashboard_desc_analytics, Triple(Icons.Default.Analytics, Color(0xFF673AB7), MainActivityConstants.ANALYTICS_ROUTE)),
        Triple(R.string.dashboard_label_guardians, R.string.dashboard_desc_guardians, Triple(Icons.Default.People, Color(0xFF00BCD4), MainActivityConstants.GUARDIANS_ROUTE)),
        Triple(R.string.dashboard_label_court_cases, R.string.dashboard_desc_court_cases, Triple(Icons.Default.Gavel, Color(0xFF795548), MainActivityConstants.COURT_CASES_ROUTE)),
        Triple(R.string.dashboard_label_foster_tasks, R.string.dashboard_desc_foster_tasks, Triple(Icons.AutoMirrored.Filled.Assignment, Color(0xFFFF5722), MainActivityConstants.FOSTER_TASKS_ROUTE)),
        Triple(R.string.dashboard_label_foster_matches, R.string.dashboard_desc_foster_matches, Triple(Icons.AutoMirrored.Filled.CompareArrows, Color(0xFF8BC34A), MainActivityConstants.FOSTER_MATCHES_ROUTE)),
        Triple(R.string.dashboard_label_notifications, R.string.dashboard_desc_notifications, Triple(Icons.Default.Notifications, Color(0xFFFF9800), MainActivityConstants.NOTIFICATIONS_ROUTE)),
        Triple(R.string.dashboard_label_background_checks, R.string.dashboard_desc_background_checks, Triple(Icons.Default.Shield, Color(0xFF3F51B5), MainActivityConstants.BACKGROUND_CHECKS_ROUTE)),
        Triple(R.string.dashboard_label_map, R.string.dashboard_desc_map, Triple(Icons.Default.Map, Color(0xFF4CAF50), MainActivityConstants.MAP_ROUTE)),
        Triple(R.string.dashboard_label_user_roles, R.string.dashboard_desc_user_roles, Triple(Icons.Default.Badge, Color(0xFFE91E63), MainActivityConstants.USER_ROLES_ROUTE)),
        Triple(R.string.dashboard_label_camera, R.string.dashboard_desc_camera, Triple(Icons.Default.CameraAlt, Color(0xFF009688), MainActivityConstants.CAMERA_ROUTE)),
        Triple(R.string.dashboard_label_home_studies_alt, R.string.dashboard_desc_home_studies_alt, Triple(Icons.Default.Home, Color(0xFF8BC34A), MainActivityConstants.HOME_STUDIES_ROUTE)),
        Triple(R.string.dashboard_label_adoption_apps, R.string.dashboard_desc_adoption_apps, Triple(Icons.Default.Folder, Color(0xFF9C27B0), MainActivityConstants.ADOPTION_APPS_ROUTE)),
        Triple(R.string.dashboard_label_vaccinations, R.string.dashboard_desc_vaccinations, Triple(Icons.Default.Vaccines, Color(0xFF4CAF50), MainActivityConstants.VACCINATION_RECORDS_ROUTE)),
        Triple(R.string.dashboard_label_behavior, R.string.dashboard_desc_behavior, Triple(Icons.Default.Psychology, Color(0xFF9C27B0), MainActivityConstants.BEHAVIOR_ASSESSMENTS_ROUTE)),
        Triple(R.string.dashboard_label_incidents, R.string.dashboard_desc_incidents, Triple(Icons.Default.ReportProblem, Color(0xFFF44336), MainActivityConstants.WELFARE_INCIDENTS_ROUTE)),
        Triple(R.string.dashboard_label_consent, R.string.dashboard_desc_consent, Triple(Icons.Default.Description, Color(0xFF607D8B), MainActivityConstants.CONSENT_RECORDS_ROUTE)),
        Triple(R.string.dashboard_label_partners, R.string.dashboard_desc_partners, Triple(Icons.Default.Business, Color(0xFF2196F3), MainActivityConstants.ORG_PARTNERS_ROUTE)),
        Triple(R.string.dashboard_label_providers, R.string.dashboard_desc_providers, Triple(Icons.Default.MiscellaneousServices, Color(0xFF9C27B0), MainActivityConstants.SERVICE_PROVIDERS_ROUTE)),
        Triple(R.string.dashboard_label_donors, R.string.dashboard_desc_donors, Triple(Icons.Default.VolunteerActivism, Color(0xFF4CAF50), MainActivityConstants.DONOR_FUNDING_ROUTE)),
        Triple(R.string.dashboard_label_budgets, R.string.dashboard_desc_budgets, Triple(Icons.Default.AccountBalance, Color(0xFF607D8B), MainActivityConstants.BUDGET_ALLOCATIONS_ROUTE)),
        Triple(R.string.dashboard_label_counties_alt, R.string.dashboard_desc_counties_alt, Triple(Icons.Default.LocationCity, Color(0xFF3F51B5), MainActivityConstants.COUNTIES_ROUTE)),
        Triple(R.string.dashboard_label_county_offices, R.string.dashboard_desc_county_offices, Triple(Icons.Default.Apartment, Color(0xFF795548), MainActivityConstants.COUNTIES_ROUTE)), // Mapped to COUNTIES_ROUTE for now
        Triple(R.string.dashboard_label_reports_alt, R.string.dashboard_desc_reports_alt, Triple(Icons.Default.PictureAsPdf, Color(0xFFE91E63), MainActivityConstants.REPORTS_ROUTE)),
        Triple(R.string.dashboard_label_emergency, R.string.dashboard_desc_emergency, Triple(Icons.Default.Sos, Color(0xFFF44336), MainActivityConstants.EMERGENCY_EVENTS_ROUTE)),
        Triple(R.string.dashboard_label_doc_storage, R.string.dashboard_desc_doc_storage, Triple(Icons.Default.CloudUpload, Color(0xFF607D8B), MainActivityConstants.DOCUMENTS_ROUTE)),
        Triple(R.string.dashboard_label_transfers, R.string.dashboard_desc_transfers, Triple(Icons.Default.SwapHoriz, Color(0xFFFF9800), MainActivityConstants.INTER_COUNTY_TRANSFERS_ROUTE)),
        Triple(R.string.dashboard_label_siblings, R.string.dashboard_desc_siblings, Triple(Icons.Default.FamilyRestroom, Color(0xFF00BCD4), MainActivityConstants.SIBLINGS_ROUTE)),
        Triple(R.string.dashboard_label_worker_loc, R.string.dashboard_desc_worker_loc, Triple(Icons.Default.LocationOn, Color(0xFF4CAF50), MainActivityConstants.WORKER_LOCATIONS_ROUTE))
    )

    val filteredScreens = allScreens.filter { canAccessRoute(it.third.third, userRole) }

    if (filteredScreens.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(R.string.dashboard_all_screens),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            
            filteredScreens.chunked(2).forEach { rowScreens ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowScreens.forEach { screen ->
                        ModernScreenCard(
                            label = stringResource(screen.first),
                            description = stringResource(screen.second),
                            icon = screen.third.first,
                            color = screen.third.second,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(screen.third.third) }
                        )
                    }
                    if (rowScreens.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * A modern card representing a single screen with icon, label, and description.
 *
 * @param label Screen label.
 * @param description Screen description.
 * @param icon Screen icon.
 * @param color Theme color.
 * @param modifier Layout modifier.
 * @param onClick Click callback.
 */
@Composable
fun ModernScreenCard(
    label: String,
    description: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon with colored background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, label, tint = color, modifier = Modifier.size(28.dp))
            }
            
            // Label and description
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * A square button providing quick access to a system module.
 *
 * @param label Button label.
 * @param icon Module icon.
 * @param color Theme color.
 * @param modifier Layout modifier.
 * @param onClick Click callback.
 */
@Composable
fun QuickAccessButtonModern(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, label, tint = color, modifier = Modifier.size(28.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontSize = 10.sp)
        }
    }
}
