package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adoption_and_childcare.data.db.entities.AuditLogEntity
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import com.example.adoption_and_childcare.data.db.entities.NotificationEntity
import com.example.adoption_and_childcare.viewmodel.DashboardViewModel
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel
import com.yourdomain.adoptionchildcare.R

/**
 * Data class representing a recent activity item in the dashboard.
 * 
 * @property title The title of the activity.
 * @property description A brief description of what happened.
 * @property time When the activity occurred.
 * @property icon The icon associated with this type of activity.
 * @property color The theme color for the activity icon background.
 */
data class ActivityItem(
    val title: String,
    val description: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

/**
 * Data class representing a priority alert in the dashboard.
 * 
 * @property title The title of the alert.
 * @property description Details about the alert.
 * @property priority The priority level (e.g., "High", "Medium", "Low").
 * @property icon The icon associated with the alert.
 * @property color The theme color for the alert.
 */
data class AlertItem(
    val title: String,
    val description: String,
    val priority: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

/**
 * Data class representing an upcoming event in the dashboard.
 * 
 * @property title The title of the event.
 * @property date When the event is scheduled.
 * @property type The category of the event (e.g., "Legal", "Review").
 * @property icon The icon associated with the event type.
 * @property color The theme color for the event.
 */
data class UpcomingEvent(
    val title: String,
    val date: String,
    val type: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

/**
 * Data class representing a statistic item in the dashboard.
 * 
 * @property label The name of the statistic.
 * @property value The numerical or string value of the statistic.
 * @property icon The icon representing the statistic.
 * @property color The theme color for the statistic.
 * @property route The navigation route to open when clicked.
 */
data class StatItem(
    val label: String,
    val value: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val route: String?
)

/**
 * Data class representing a management module in the dashboard grid.
 * 
 * @property title The display name of the module.
 * @property icon The icon representing the module.
 * @property color The theme color for the module.
 * @property route The navigation route for the module.
 */
data class ManagementModule(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val route: String
)

/**
 * The main dashboard screen displaying overview statistics, modules, alerts, and activities.
 * 
 * @param onNavigate Callback for navigation to other screens.
 * @param notificationsViewModel The ViewModel providing notification state.
 * @param dashboardViewModel The ViewModel providing dashboard statistics and data.
 */
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit = {}, 
    notificationsViewModel: NotificationsViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val loading by notificationsViewModel.loading.collectAsState()
    val error by notificationsViewModel.error.collectAsState()
    
    // Database data
    val childCount by dashboardViewModel.childCount.collectAsState()
    val placementCount by dashboardViewModel.placementCount.collectAsState()
    val applicationCount by dashboardViewModel.applicationCount.collectAsState()
    val homeStudyCount by dashboardViewModel.homeStudyCount.collectAsState()
    val auditLogs by dashboardViewModel.recentActivities.collectAsState()
    val alertsData by dashboardViewModel.priorityAlerts.collectAsState()
    val eventsData by dashboardViewModel.upcomingEvents.collectAsState()
    
    // Scroll state for LazyColumn
    val listState = rememberLazyListState()
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    
    // Data for statistics - Now using DB counts
    val stats = listOf(
        StatItem(stringResource(R.string.stat_children_care), childCount.toString(), Icons.Default.ChildCare, Color(0xFF4CAF50), stringResource(R.string.route_children_list)),
        StatItem(stringResource(R.string.stat_active_placements), placementCount.toString(), Icons.Default.Home, Color(0xFF2196F3), stringResource(R.string.route_placements)),
        StatItem(stringResource(R.string.stat_pending_apps), applicationCount.toString(), Icons.Default.Folder, Color(0xFFFF9800), stringResource(R.string.route_adoption_applications)),
        StatItem(stringResource(R.string.stat_home_studies), homeStudyCount.toString(), Icons.Default.AssignmentTurnedIn, Color(0xFF9C27B0), stringResource(R.string.route_home_studies))
    )
    
    // Real data for recent activities
    val recentActivities = auditLogs.map { log ->
        ActivityItem(
            title = "${log.action} ${log.tableName.capitalize()}",
            description = "Record #${log.recordId} modified",
            time = log.changedAt ?: "Just now",
            icon = when(log.action) {
                "INSERT" -> Icons.Default.Add
                "DELETE" -> Icons.Default.Delete
                else -> Icons.Default.Edit
            },
            color = when(log.action) {
                "INSERT" -> Color(0xFF4CAF50)
                "DELETE" -> Color(0xFFF44336)
                else -> Color(0xFF2196F3)
            }
        )
    }
    
    // Real data for priority alerts
    val priorityAlerts = alertsData.map { notif ->
        AlertItem(
            title = notif.title,
            description = notif.message,
            priority = "High", // Mapping could be improved if notification had priority field
            icon = Icons.Default.Notifications,
            color = Color(0xFFF44336)
        )
    }
    
    // Real data for upcoming events
    val upcomingEvents = eventsData.map { event ->
        when (event) {
            is CourtCaseEntity -> UpcomingEvent(
                title = "Court: ${event.courtName}",
                date = event.hearingDate ?: "TBD",
                type = "Legal",
                icon = Icons.Default.Gavel,
                color = Color(0xFFE91E63)
            )
            is HomeStudyEntity -> UpcomingEvent(
                title = "Study: Family #${event.familyId}",
                date = event.startedAt ?: "TBD",
                type = "Review",
                icon = Icons.Default.AssignmentTurnedIn,
                color = Color(0xFFFF9800)
            )
            else -> UpcomingEvent("Unknown", "N/A", "System", Icons.Default.Info, Color.Gray)
        }
    }

    val managementModules = listOf(
        ManagementModule(stringResource(R.string.module_children), Icons.Default.ChildCare, Color(0xFF4CAF50), stringResource(R.string.route_children_list)),
        ManagementModule(stringResource(R.string.module_families), Icons.Default.FamilyRestroom, Color(0xFF2196F3), stringResource(R.string.route_families)),
        ManagementModule(stringResource(R.string.module_applications), Icons.Default.Assignment, Color(0xFFFF9800), stringResource(R.string.route_adoption_applications)),
        ManagementModule(stringResource(R.string.module_home_studies), Icons.Default.AssignmentTurnedIn, Color(0xFF9C27B0), stringResource(R.string.route_home_studies)),
        ManagementModule(stringResource(R.string.module_documents), Icons.Default.Description, Color(0xFF607D8B), stringResource(R.string.route_documents)),
        ManagementModule(stringResource(R.string.module_placements), Icons.Default.LocationOn, Color(0xFFE91E63), stringResource(R.string.route_placements)),
        ManagementModule(stringResource(R.string.module_reports), Icons.Default.Assessment, Color(0xFF795548), stringResource(R.string.route_reports)),
        ManagementModule(stringResource(R.string.module_education), Icons.Default.School, Color(0xFF009688), stringResource(R.string.route_education)),
        ManagementModule(stringResource(R.string.module_medical), Icons.Default.LocalHospital, Color(0xFFF44336), stringResource(R.string.route_medical)),
        ManagementModule(stringResource(R.string.module_finance), Icons.Default.AttachMoney, Color(0xFFFFC107), stringResource(R.string.route_finance))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val currentError = error
            if (currentError != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(currentError, color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Search Bar
                    item {
                        SearchBarComponent(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it }
                        )
                    }

                    // Management Modules Grid
                    item {
                        ManagementModulesGrid(
                            modules = managementModules,
                            onNavigate = onNavigate
                        )
                    }

                    // Overview Statistics Panel
                    item {
                        OverviewStatsPanel(
                            stats = stats,
                            onNavigate = onNavigate
                        )
                    }
                    
                    // Priority Alerts Section
                    item {
                        PriorityAlertsSection(
                            alerts = priorityAlerts
                        )
                    }
                    
                    // Recent Activity Feed
                    item {
                        RecentActivityFeed(
                            activities = recentActivities
                        )
                    }
                    
                    // Upcoming Events Section
                    item {
                        UpcomingEventsSection(
                            events = upcomingEvents
                        )
                    }
                    
                    // Extra padding at bottom to ensure scrolling works well
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                // Custom Scrollbar on the far right
                DashboardScrollbar(
                    state = listState,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(8.dp)
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * A custom scrollbar for the dashboard's LazyColumn.
 * 
 * @param state The scroll state of the LazyColumn.
 * @param modifier The modifier for the scrollbar canvas.
 */
@Composable
fun DashboardScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val totalItems = state.layoutInfo.totalItemsCount
        if (totalItems > 0) {
            val visibleItems = state.layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val viewportHeight = size.height
                
                // Calculate how much of the total content is visible
                val totalContentHeight = totalItems.toFloat() // Using count as a proxy for height
                val visibleContentHeight = visibleItems.size.toFloat()
                
                val scrollbarHeight = (visibleContentHeight / totalContentHeight) * viewportHeight
                
                // Calculate scroll position
                val firstVisibleIndex = state.firstVisibleItemIndex
                
                // Approximate position
                val scrollPosition = (firstVisibleIndex.toFloat() / totalItems) * viewportHeight
                
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.5f),
                    topLeft = Offset(x = 2.dp.toPx(), y = scrollPosition),
                    size = Size(width = 4.dp.toPx(), height = scrollbarHeight.coerceAtLeast(40.dp.toPx())),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }
}

/**
 * A grid display of management modules.
 * 
 * @param modules List of management modules to display.
 * @param onNavigate Callback for when a module is clicked.
 */
@Composable
fun ManagementModulesGrid(
    modules: List<ManagementModule>,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_title_management),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Chunking modules into rows of 2 for a grid look within LazyColumn
                for (rowModules in modules.chunked(2)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (module in rowModules) {
                        ModuleCard(
                            module = module,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(module.route) }
                        )
                    }
                    // If row has only 1 item, add a spacer to keep width
                    if (rowModules.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * A card representing an individual management module.
 * 
 * @param module The module data.
 * @param modifier The modifier for the card.
 * @param onClick Callback for when the card is clicked.
 */
@Composable
fun ModuleCard(
    module: ManagementModule,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = module.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = module.color
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = module.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = module.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * A search bar component for filtering dashboard content.
 * 
 * @param query The current search query string.
 * @param onQueryChange Callback for when the search query changes.
 */
@Composable
fun SearchBarComponent(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.dashboard_search_hint)) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.dashboard_search_desc))
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.dashboard_clear_desc))
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

/**
 * A panel displaying overview statistics.
 * 
 * @param stats List of statistics items to display.
 * @param onNavigate Callback for when a statistic card is clicked.
 */
@Composable
fun OverviewStatsPanel(
    stats: List<StatItem>,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_title_overview),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (stat in stats) {
                    StatCard(
                        stat = stat,
                        modifier = Modifier.weight(1f),
                        onClick = { stat.route?.let { onNavigate(it) } }
                    )
                }
            }
        }
    }
}

/**
 * A card representing an individual statistic.
 * 
 * @param stat The statistic data.
 * @param modifier The modifier for the card.
 * @param onClick Callback for when the card is clicked.
 */
@Composable
fun StatCard(
    stat: StatItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = stat.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = stat.label,
                tint = stat.color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = stat.color
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * A section displaying priority alerts.
 * 
 * @param alerts List of alerts to display.
 */
@Composable
fun PriorityAlertsSection(
    alerts: List<AlertItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dashboard_title_alerts),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.dashboard_items_count, alerts.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            for (alert in alerts) {
                AlertCard(alert = alert)
            }
        }
    }
}

/**
 * A card representing an individual alert.
 * 
 * @param alert The alert data.
 */
@Composable
fun AlertCard(alert: AlertItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when(alert.priority) {
                stringResource(R.string.alert_priority_high) -> Color(0xFFFFEBEE)
                stringResource(R.string.alert_priority_medium) -> Color(0xFFFFF8E1)
                else -> Color(0xFFF5F5F5)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = alert.icon,
                contentDescription = null,
                tint = alert.color,
                modifier = Modifier.size(20.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = alert.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = alert.color
            ) {
                Text(
                    text = alert.priority,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * A feed displaying recent activities.
 * 
 * @param activities List of activity items to display.
 */
@Composable
fun RecentActivityFeed(
    activities: List<ActivityItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dashboard_title_activity),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.dashboard_view_all),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            for (activity in activities) {
                ActivityItemRow(activity = activity)
            }
        }
    }
}

/**
 * A row representing an individual activity item.
 * 
 * @param activity The activity data.
 */
@Composable
fun ActivityItemRow(activity: ActivityItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(8.dp),
            color = activity.color.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.icon,
                    contentDescription = null,
                    tint = activity.color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Text(
            text = activity.time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

/**
 * A section displaying upcoming events.
 * 
 * @param events List of events to display.
 */
@Composable
fun UpcomingEventsSection(
    events: List<UpcomingEvent>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dashboard_title_events),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.dashboard_view_calendar),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            for (event in events) {
                EventCard(event = event)
            }
        }
    }
}

/**
 * A card representing an individual event.
 * 
 * @param event The event data.
 */
@Composable
fun EventCard(event: UpcomingEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = event.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = event.icon,
                contentDescription = null,
                tint = event.color,
                modifier = Modifier.size(24.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = event.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Text(
                text = event.date,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = event.color
            )
        }
    }
}