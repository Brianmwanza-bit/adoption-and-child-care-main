package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel
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
 * @param onNavigate Callback for navigation to different routes.
 * @param notificationsViewModel ViewModel for managing notifications.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenModern(
    onNavigate: (String) -> Unit = {},
    notificationsViewModel: NotificationsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

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

    // Observe tasks and audit logs for dynamic items
    LaunchedEffect(Unit) {
        db.fosterTaskDao().observeAll().collectLatest { tasks ->
            dynamicActionItems = tasks.take(3).map {
                ActionItemModern(
                    id = it.taskId.toString(),
                    title = it.description ?: "Task",
                    priority = it.status?.lowercase() ?: "normal",
                    dueDate = it.dueDate ?: "No date",
                    assignee = "Staff",
                    relatedEntity = "Foster Tasks"
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        db.auditLogDao().observeAll().collectLatest { logs ->
            dynamicUpdates = logs.take(4).map {
                "${it.action} on ${it.tableName} (ID: ${it.recordId})"
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
                item {
                    PriorityAlertSectionModern(
                        urgentCount = 2,
                        atRiskCount = atRiskCount,
                        upcomingCount = 5
                    )
                }

                // KEY METRICS SECTION
                item {
                    KeyMetricsSectionModern(
                        childrenCount = childrenCount,
                        familiesCount = familiesCount,
                        placementsCount = placementsCount,
                        adoptionAppsCount = adoptionAppsCount,
                        onNavigate = onNavigate
                    )
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
                item {
                    ActionItemsSectionModern(
                        items = dynamicActionItems,
                        onNavigate = onNavigate
                    )
                }

                // RECENT UPDATES SECTION
                item {
                    RecentUpdatesSectionModern(
                        updates = dynamicUpdates
                    )
                }

                // QUICK ACCESS MODULES
                item {
                    QuickAccessModulesModern(onNavigate = onNavigate)
                }

                // ALL SCREENS GRID (14 items)
                item {
                    AllScreensGridModern(onNavigate = onNavigate)
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
 * @param childrenCount Total children.
 * @param familiesCount Total families.
 * @param placementsCount Total active placements.
 * @param adoptionAppsCount Total adoption applications.
 * @param onNavigate Navigation callback.
 */
@Composable
fun KeyMetricsSectionModern(
    childrenCount: Int,
    familiesCount: Int,
    placementsCount: Int,
    adoptionAppsCount: Int,
    onNavigate: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.dashboard_key_metrics),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCardModern(
                title = stringResource(R.string.dashboard_metric_children),
                count = childrenCount,
                icon = Icons.Default.ChildCare,
                color = Color(0xFF4CAF50),
                trend = stringResource(R.string.dashboard_trend_children),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("children_list") }
            )
            MetricCardModern(
                title = stringResource(R.string.dashboard_metric_families),
                count = familiesCount,
                icon = Icons.Default.FamilyRestroom,
                color = Color(0xFF2196F3),
                trend = stringResource(R.string.dashboard_trend_families),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("families") }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCardModern(
                title = stringResource(R.string.dashboard_metric_placements),
                count = placementsCount,
                icon = Icons.Default.Home,
                color = Color(0xFF8BC34A),
                trend = stringResource(R.string.dashboard_trend_placements),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("placements") }
            )
            MetricCardModern(
                title = stringResource(R.string.dashboard_metric_applications),
                count = adoptionAppsCount,
                icon = Icons.Default.Folder,
                color = Color(0xFF9C27B0),
                trend = stringResource(R.string.dashboard_trend_applications),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("adoption_applications") }
            )
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
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
                "No pending action items",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            items.forEach { item ->
                ActionItemCardModern(item)
            }
        }
    }
}

/**
 * A card representing a single action item.
 *
 * @param item The action item details.
 */
@Composable
fun ActionItemCardModern(item: ActionItemModern) {
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
                        when (item.priority) {
                            urgentVal -> Color(0xFFE91E63)
                            highVal -> Color(0xFFFF9800)
                            else -> Color(0xFF4CAF50)
                        },
                        CircleShape
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(item.dueDate, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(item.assignee, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Icon(Icons.Default.ChevronRight, stringResource(R.string.footer_action_approve), tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

/**
 * Section displaying a list of recent updates from the system.
 *
 * @param updates List of update description strings.
 */
@Composable
fun RecentUpdatesSectionModern(updates: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.dashboard_recent_updates),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (updates.isEmpty()) {
            Text(
                "No recent system updates",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            updates.forEach { update ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Info, stringResource(R.string.activity_doc_uploaded), tint = Color(0xFF9C27B0), modifier = Modifier.size(20.dp))
                    Text(update, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Section providing quick access to document, medical, education, and finance modules.
 *
 * @param onNavigate Navigation callback.
 */
@Composable
fun QuickAccessModulesModern(onNavigate: (String) -> Unit) {
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
            QuickAccessButtonModern(
                stringResource(R.string.dashboard_module_documents),
                Icons.Default.Description,
                Color(0xFF607D8B),
                Modifier.weight(1f)
            ) {
                onNavigate("documents")
            }
            QuickAccessButtonModern(
                stringResource(R.string.dashboard_module_medical),
                Icons.Default.LocalHospital,
                Color(0xFFF44336),
                Modifier.weight(1f)
            ) {
                onNavigate("medical")
            }
            QuickAccessButtonModern(
                stringResource(R.string.dashboard_module_education),
                Icons.Default.School,
                Color(0xFF3F51B5),
                Modifier.weight(1f)
            ) {
                onNavigate("education")
            }
            QuickAccessButtonModern(
                stringResource(R.string.dashboard_module_finance),
                Icons.Default.AttachMoney,
                Color(0xFF4CAF50),
                Modifier.weight(1f)
            ) {
                onNavigate("finance")
            }
        }
    }
}

/**
 * Grid displaying all 14 main screens for easy navigation with modern card design.
 *
 * @param onNavigate Navigation callback.
 */
@Composable
fun AllScreensGridModern(onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "All Screens",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        
        // Row 1: Analytics, Guardians
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernScreenCard(
                label = "Analytics",
                description = "View insights & reports",
                icon = Icons.Default.Analytics,
                color = Color(0xFF673AB7),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("analytics") }
            )
            ModernScreenCard(
                label = "Guardians",
                description = "Manage guardians",
                icon = Icons.Default.People,
                color = Color(0xFF00BCD4),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("guardians") }
            )
        }
        
        // Row 2: Court Cases, Foster Tasks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernScreenCard(
                label = "Court Cases",
                description = "Legal proceedings",
                icon = Icons.Default.Gavel,
                color = Color(0xFF795548),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("court_cases") }
            )
            ModernScreenCard(
                label = "Foster Tasks",
                description = "Task management",
                icon = Icons.Default.Assignment,
                color = Color(0xFFFF5722),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("foster_tasks") }
            )
        }
        
        // Row 3: Foster Matches, User Management
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernScreenCard(
                label = "Foster Matches",
                description = "Placement matching",
                icon = Icons.Default.CompareArrows,
                color = Color(0xFF8BC34A),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("foster_matches") }
            )
            ModernScreenCard(
                label = "User Management",
                description = "Manage users",
                icon = Icons.Default.AdminPanelSettings,
                color = Color(0xFF607D8B),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("user_management") }
            )
        }
        
        // Row 4: Notifications, Audit Logs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernScreenCard(
                label = "Notifications",
                description = "Alerts & messages",
                icon = Icons.Default.Notifications,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("notifications") }
            )
            ModernScreenCard(
                label = "Audit Logs",
                description = "System activity",
                icon = Icons.Default.History,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("audit_logs") }
            )
        }
        
        // Row 5: Background Checks, Map
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernScreenCard(
                label = "Background Checks",
                description = "Verification status",
                icon = Icons.Default.Shield,
                color = Color(0xFF3F51B5),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("background_checks") }
            )
            ModernScreenCard(
                label = "Map",
                description = "Location view",
                icon = Icons.Default.Map,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("map") }
            )
        }
        
        // Row 6: User Roles, Camera
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernScreenCard(
                label = "User Roles",
                description = "Role management",
                icon = Icons.Default.Badge,
                color = Color(0xFFE91E63),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("user_roles") }
            )
            ModernScreenCard(
                label = "Camera",
                description = "Photo capture",
                icon = Icons.Default.CameraAlt,
                color = Color(0xFF009688),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("camera") }
            )
        }
        
        // Row 7: Analytics2, Guardians2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernScreenCard(
                label = "Home Studies",
                description = "Home assessments",
                icon = Icons.Default.Home,
                color = Color(0xFF8BC34A),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("home_studies") }
            )
            ModernScreenCard(
                label = "Adoption Apps",
                description = "Applications",
                icon = Icons.Default.Folder,
                color = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("adoption_applications") }
            )
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
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
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
