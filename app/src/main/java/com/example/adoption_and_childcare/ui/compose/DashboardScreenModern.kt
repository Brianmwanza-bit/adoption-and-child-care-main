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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DashboardCard(
    val title: String,
    val icon: ImageVector,
    val count: Int,
    val summary: String,
    val color: Color,
    val route: String? = null,
    val trend: String = "",
    val status: String = "normal" // urgent, at-risk, upcoming, normal
)

data class ActionItem(
    val id: String,
    val title: String,
    val priority: String, // urgent, high, normal
    val dueDate: String,
    val assignee: String,
    val relatedEntity: String
)

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit = {}, notificationsViewModel: NotificationsViewModel = viewModel()) {
    val context = LocalContext.current
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Data states
    var childrenCount by remember { mutableStateOf(0) }
    var familiesCount by remember { mutableStateOf(0) }
    var adoptionAppsCount by remember { mutableStateOf(0) }
    var placementsCount by remember { mutableStateOf(0) }
    var overdueTasks by remember { mutableStateOf(0) }
    var atRiskCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        notificationsViewModel.loadNotifications()
        val db = AppDatabase.getInstance(context)
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
                overdueTasks = (c * 0.15).toInt() // Demo: 15% overdue
                atRiskCount = (p * 0.2).toInt() // Demo: 20% at risk
            }
        }
    }

    Scaffold(
        topBar = {
            DashboardTopBar(
                onMenuClick = { /* Drawer toggle */ },
                onSearchClick = { showSearch = !showSearch },
                unreadCount = 0
            )
        },
        bottomBar = {
            DashboardFooterBar(onActionClick = { action ->
                when (action) {
                    "add_child" -> onNavigate("children_list")
                    "new_placement" -> onNavigate("placements")
                    "new_case" -> onNavigate("reports")
                    "quick_report" -> onNavigate("reports")
                    "view_tasks" -> onNavigate("reports") // TODO: Create tasks screen
                }
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Bar (conditional)
            if (showSearch) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { /* Perform search */ },
                    onClose = { showSearch = false },
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
                    PriorityAlertSection(
                        urgentCount = 2,
                        atRiskCount = atRiskCount,
                        upcomingCount = 5
                    )
                }

                // KEY METRICS SECTION
                item {
                    KeyMetricsSection(
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
                        OverdueTasksSection(count = overdueTasks) {
                            onNavigate("reports")
                        }
                    }
                }

                // ACTION ITEMS SECTION
                item {
                    ActionItemsSection(
                        items = listOf(
                            ActionItem("1", "Home Study Review - Smith Family", "urgent", "Today", "Sarah Chen", "Home Studies"),
                            ActionItem("2", "Medical Follow-up - Johnny M.", "high", "In 2 days", "Dr. Brown", "Medical"),
                            ActionItem("3", "Placement Stability Check", "normal", "In 7 days", "Mike Wilson", "Placements")
                        ),
                        onNavigate = onNavigate
                    )
                }

                // RECENT UPDATES SECTION
                item {
                    RecentUpdatesSection(
                        updates = listOf(
                            "New adoption application: Brown Family",
                            "Document uploaded: School Records (Alex)",
                            "Placement changed: Foster Home -> Kinship Care",
                            "Court hearing scheduled: 2024-07-15"
                        )
                    )
                }

                // QUICK ACCESS MODULES
                item {
                    QuickAccessModules(onNavigate = onNavigate)
                }
            }
        }
    }
}

@Composable
fun DashboardTopBar(
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    unreadCount: Int
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Dashboard",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Button
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, "Search")
                    }
                    // Notifications Button
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge {
                                    Text(
                                        if (unreadCount > 99) "99+" else unreadCount.toString(),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { /* Navigate to notifications */ }) {
                            Icon(Icons.Default.Notifications, "Notifications")
                        }
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, "Menu")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF9C27B0),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search children, families, cases...") },
        leadingIcon = { Icon(Icons.Default.Search, "Search") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, "Clear")
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

@Composable
fun PriorityAlertSection(
    urgentCount: Int,
    atRiskCount: Int,
    upcomingCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Priority Alerts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Urgent Alert Card
            AlertCard(
                count = urgentCount,
                label = "Urgent",
                color = Color(0xFFE91E63),
                modifier = Modifier.weight(1f)
            )
            // At-Risk Alert Card
            AlertCard(
                count = atRiskCount,
                label = "At-Risk",
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            // Upcoming Alert Card
            AlertCard(
                count = upcomingCount,
                label = "Upcoming",
                color = Color(0xFFFFC107),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AlertCard(
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

@Composable
fun KeyMetricsSection(
    childrenCount: Int,
    familiesCount: Int,
    placementsCount: Int,
    adoptionAppsCount: Int,
    onNavigate: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Key Metrics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Children",
                count = childrenCount,
                icon = Icons.Default.ChildCare,
                color = Color(0xFF4CAF50),
                trend = "+12% from last week",
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("children_list") }
            )
            MetricCard(
                title = "Families",
                count = familiesCount,
                icon = Icons.Default.FamilyRestroom,
                color = Color(0xFF2196F3),
                trend = "+8% from last week",
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("families") }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Placements",
                count = placementsCount,
                icon = Icons.Default.Home,
                color = Color(0xFF8BC34A),
                trend = "89% stability rate",
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("placements") }
            )
            MetricCard(
                title = "Applications",
                count = adoptionAppsCount,
                icon = Icons.Default.Folder,
                color = Color(0xFF9C27B0),
                trend = "12 pending review",
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("adoption_applications") }
            )
        }
    }
}

@Composable
fun MetricCard(
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

@Composable
fun OverdueTasksSection(count: Int, onViewTasks: () -> Unit) {
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
                    "$count Tasks Overdue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63)
                )
                Text(
                    "Action required today",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Icon(Icons.Default.Warning, "Overdue", tint = Color(0xFFE91E63), modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun ActionItemsSection(
    items: List<ActionItem>,
    onNavigate: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Action Items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "View All",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9C27B0),
                modifier = Modifier.clickable { onNavigate("reports") }
            )
        }
        items.forEach { item ->
            ActionItemCard(item)
        }
    }
}

@Composable
fun ActionItemCard(item: ActionItem) {
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
                            "urgent" -> Color(0xFFE91E63)
                            "high" -> Color(0xFFFF9800)
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
            Icon(Icons.Default.ChevronRight, "Navigate", tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun RecentUpdatesSection(updates: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Recent Updates",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        updates.forEach { update ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Info, "Update", tint = Color(0xFF9C27B0), modifier = Modifier.size(20.dp))
                Text(update, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun QuickAccessModules(onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Quick Access",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickAccessButton("Documents", Icons.Default.Description, Color(0xFF607D8B), Modifier.weight(1f)) {
                onNavigate("documents")
            }
            QuickAccessButton("Medical", Icons.Default.LocalHospital, Color(0xFFF44336), Modifier.weight(1f)) {
                onNavigate("medical")
            }
            QuickAccessButton("Education", Icons.Default.School, Color(0xFF3F51B5), Modifier.weight(1f)) {
                onNavigate("education")
            }
            QuickAccessButton("Finance", Icons.Default.AttachMoney, Color(0xFF4CAF50), Modifier.weight(1f)) {
                onNavigate("finance")
            }
        }
    }
}

@Composable
fun QuickAccessButton(
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

@Composable
fun DashboardFooterBar(onActionClick: (String) -> Unit) {
    BottomAppBar(
        containerColor = Color.White,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        modifier = Modifier.height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterActionButton(
                icon = Icons.Default.PersonAdd,
                label = "Add Child",
                modifier = Modifier.weight(1f)
            ) { onActionClick("add_child") }

            FooterActionButton(
                icon = Icons.Default.Home,
                label = "New Placement",
                modifier = Modifier.weight(1f)
            ) { onActionClick("new_placement") }

            FooterActionButton(
                icon = Icons.Default.FolderOpen,
                label = "New Case",
                modifier = Modifier.weight(1f)
            ) { onActionClick("new_case") }

            FooterActionButton(
                icon = Icons.Default.Assessment,
                label = "Report",
                modifier = Modifier.weight(1f)
            ) { onActionClick("quick_report") }

            FooterActionButton(
                icon = Icons.Default.TaskAlt,
                label = "Tasks",
                modifier = Modifier.weight(1f)
            ) { onActionClick("view_tasks") }
        }
    }
}

@Composable
fun FooterActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, label, tint = Color(0xFF9C27B0), modifier = Modifier.size(20.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, maxLines = 1)
    }
}
