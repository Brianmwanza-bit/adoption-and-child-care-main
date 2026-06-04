package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel
import kotlinx.coroutines.delay

// Data classes for mock data
data class ActivityItem(
    val title: String,
    val description: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

data class AlertItem(
    val title: String,
    val description: String,
    val priority: String, // "High", "Medium", "Low"
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

data class QuickAction(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val route: String
)

data class UpcomingEvent(
    val title: String,
    val date: String,
    val type: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

data class StatItem(
    val label: String,
    val value: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val route: String?
)

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit = {}, notificationsViewModel: NotificationsViewModel = viewModel()) {
    val loading by notificationsViewModel.loading.collectAsState()
    val error by notificationsViewModel.error.collectAsState()
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    // Mock data for statistics
    val stats = listOf(
        StatItem("Children in Care", "24", Icons.Default.ChildCare, Color(0xFF4CAF50), "children_list"),
        StatItem("Active Placements", "18", Icons.Default.Home, Color(0xFF2196F3), "placements"),
        StatItem("Pending Applications", "7", Icons.Default.Folder, Color(0xFFFF9800), "adoption_applications"),
        StatItem("Home Studies", "12", Icons.Default.AssignmentTurnedIn, Color(0xFF9C27B0), "home_studies")
    )
    
    // Mock data for recent activities
    val recentActivities = listOf(
        ActivityItem("New Child Added", "John Doe added to system", "2 hours ago", Icons.Default.PersonAdd, Color(0xFF4CAF50)),
        ActivityItem("Placement Updated", "Smith family placement status changed", "5 hours ago", Icons.Default.Home, Color(0xFF2196F3)),
        ActivityItem("Document Uploaded", "Birth certificate added for Jane", "1 day ago", Icons.Default.CloudUpload, Color(0xFF9C27B0)),
        ActivityItem("Home Study Completed", "Johnson family home study approved", "2 days ago", Icons.Default.CheckCircle, Color(0xFF4CAF50)),
        ActivityItem("Medical Record Updated", "Vaccination records added", "3 days ago", Icons.Default.LocalHospital, Color(0xFFF44336))
    )
    
    // Mock data for priority alerts
    val priorityAlerts = listOf(
        AlertItem("Expiring Home Study", "Williams family home study expires in 5 days", "High", Icons.Default.Warning, Color(0xFFF44336)),
        AlertItem("Missing Document", "Background check pending for Thompson family", "Medium", Icons.Default.Error, Color(0xFFFF9800)),
        AlertItem("Court Date", "Adoption hearing for Davis case scheduled for tomorrow", "High", Icons.Default.Gavel, Color(0xFFE91E63))
    )
    
    // Mock data for quick actions
    val quickActions = listOf(
        QuickAction("Add Child", Icons.Default.Add, Color(0xFF4CAF50), "children_list"),
        QuickAction("File Report", Icons.Default.Assignment, Color(0xFF2196F3), "reports"),
        QuickAction("Upload Document", Icons.Default.CloudUpload, Color(0xFF9C27B0), "documents"),
        QuickAction("Schedule Home Study", Icons.Default.CalendarToday, Color(0xFFFF9800), "home_studies")
    )
    
    // Mock data for upcoming events
    val upcomingEvents = listOf(
        UpcomingEvent("Court Hearing", "Tomorrow", "Legal", Icons.Default.Gavel, Color(0xFFE91E63)),
        UpcomingEvent("Home Study Review", "Dec 15", "Review", Icons.Default.AssignmentTurnedIn, Color(0xFFFF9800)),
        UpcomingEvent("Placement Visit", "Dec 18", "Visit", Icons.Default.Home, Color(0xFF2196F3)),
        UpcomingEvent("Case Conference", "Dec 20", "Meeting", Icons.Default.Group, Color(0xFF9C27B0))
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dashboard Title
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Bar
                item {
                    SearchBarComponent(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        isActive = isSearchActive,
                        onActiveChange = { isSearchActive = it }
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
                
                // Quick Actions Panel
                item {
                    QuickActionsPanel(
                        actions = quickActions,
                        onNavigate = onNavigate
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
            }
        }
    }
}

@Composable
fun SearchBarComponent(
    query: String,
    onQueryChange: (String) -> Unit,
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search children, families, cases...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

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
                text = "Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stats.forEach { stat ->
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
                    text = "Priority Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${alerts.size} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            alerts.forEach { alert ->
                AlertCard(alert = alert)
            }
        }
    }
}

@Composable
fun AlertCard(alert: AlertItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when(alert.priority) {
                "High" -> Color(0xFFFFEBEE)
                "Medium" -> Color(0xFFFFF8E1)
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

@Composable
fun QuickActionsPanel(
    actions: List<QuickAction>,
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
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actions.forEach { action ->
                    ActionButton(
                        action = action,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(action.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    action: QuickAction,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = action.color
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
                imageVector = action.icon,
                contentDescription = action.title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = action.title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

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
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            activities.forEach { activity ->
                ActivityItemRow(activity = activity)
            }
        }
    }
}

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
                    text = "Upcoming Events",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View Calendar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            events.forEach { event ->
                EventCard(event = event)
            }
        }
    }
}

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