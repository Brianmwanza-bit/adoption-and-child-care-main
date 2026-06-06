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
    val route: String? = null
)

data class ActionItem(
    val id: String,
    val title: String,
    val priority: String,
    val dueDate: String,
    val assignee: String,
    val caseId: String = "",
    val childName: String = ""
)

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

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit = {}, notificationsViewModel: NotificationsViewModel = viewModel()) {
    val loading by notificationsViewModel.loading.collectAsState()
    val error by notificationsViewModel.error.collectAsState()
    val unreadCount by notificationsViewModel.unreadCount.collectAsState()

    val context = LocalContext.current
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
        val db = AppDatabase.Companion.getInstance(context)
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
    }

    val allModules = listOf(
        DashboardCard("Active Cases", Icons.Default.FolderOpen, childrenCount + familiesCount, "Total active cases", Color(0xFF2196F3), route = "children_list"),
        DashboardCard("Awaiting Placement", Icons.Default.Schedule, adoptionAppsCount, "Cases pending match", Color(0xFFFF9800), route = "adoption_applications"),
        DashboardCard("In Placement", Icons.Default.Home, placementsCount, "Currently placed", Color(0xFF4CAF50), route = "placements"),
        DashboardCard("Home Studies", Icons.Default.AssignmentTurnedIn, homeStudiesCount, "Pending reviews", Color(0xFF9C27B0), route = "home_studies"),
        DashboardCard("Documents", Icons.Default.Description, documentsCount, "Files to process", Color(0xFF607D8B), route = "documents"),
        DashboardCard("Medical Records", Icons.Default.LocalHospital, medicalCount, "Health updates", Color(0xFFF44336), route = "medical"),
        DashboardCard("Education", Icons.Default.School, educationCount, "Educational info", Color(0xFF3F51B5), route = "education"),
        DashboardCard("Finance", Icons.Default.AttachMoney, financeCount, "Transactions", Color(0xFF00897B), route = "finance"),
        DashboardCard("Reports", Icons.Default.Assessment, reportsCount, "Case reports", Color(0xFFE91E63), route = "reports"),
        DashboardCard("Families", Icons.Default.FamilyRestroom, familiesCount, "Registered families", Color(0xFF6A1B9A), route = "families")
    )

    val criticalCases = listOf(
        CaseStatus("001", "Emma Smith", "Waiting for match", "critical", 45, "2024-07-30", "Sarah Chen", "Approved Family"),
        CaseStatus("002", "Noah Johnson", "In placement", "high", 15, "2024-07-22", "Mike Wilson", "Foster Kellers"),
        CaseStatus("003", "Sophia Brown", "Matched", "high", 7, "2024-07-15", "Lisa Anderson", "Baker Family")
    )

    val actionItems = listOf(
        ActionItem("1", "Home Study Review - Smith Family", "urgent", "Today", "You", "001", "Emma S."),
        ActionItem("2", "Medical Follow-up - Noah J.", "urgent", "Tomorrow", "Dr. Brown", "002", "Noah J."),
        ActionItem("3", "Document Collection - Sophia B.", "high", "In 2 days", "You", "003", "Sophia B."),
        ActionItem("4", "Placement Stability Check", "normal", "In 7 days", "Mike Wilson", "002", "Noah J.")
    )

    Scaffold(
        bottomBar = {
            ModernFooterBar(onNavigate)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
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
                                CriticalAlertsSection(criticalCases, overdueTasks, onNavigate)
                                TodaysWorkloadSection(actionItems.filter { it.dueDate == "Today" || it.dueDate == "Tomorrow" })
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "All Action Items",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            actionItems.forEach { ActionItemCard(it) }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "System Modules",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(allModules) { card ->
                                    CompactModuleCard(card) { card.route?.let { onNavigate(it) } }
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

@Composable
fun DashboardHeaderSection(unreadCount: Int, overdueTasks: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2196F3), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderStatCard("Urgent Cases", "3", Color(0xFFFF5252), Modifier.weight(1f))
        HeaderStatCard("Overdue", overdueTasks.toString(), Color(0xFFFF9800), Modifier.weight(1f))
        HeaderStatCard("Today's Tasks", "4", Color(0xFF4CAF50), Modifier.weight(1f))
        HeaderStatCard("Messages", unreadCount.toString(), Color(0xFFFFC107), Modifier.weight(1f))
    }
}

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
        placeholder = { Text("Search cases, children, families...") },
        leadingIcon = { Icon(Icons.Default.Search, "Search", tint = Color(0xFF2196F3)) },
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
            focusedBorderColor = Color(0xFF2196F3),
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
        ),
        textStyle = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun CriticalAlertsSection(cases: List<CaseStatus>, overdueTasks: Int, onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("🚨 Require Immediate Attention", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                        Text("⚠️ $overdueTasks Tasks Overdue", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                        Text("Action required today", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Icon(Icons.Default.ChevronRight, "Navigate", tint = Color(0xFFE91E63))
                }
            }
        }
    }
}

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

@Composable
fun TodaysWorkloadSection(tasks: List<ActionItem>) {
    if (tasks.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("📋 Today's Workload", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            tasks.forEach { task ->
                CompactActionCard(task)
            }
        }
    }
}

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

@Composable
fun ActionItemCard(item: ActionItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
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
                    Text("Case: ${item.caseId}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(item.dueDate, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(item.assignee, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Icon(Icons.Default.ChevronRight, "Navigate", tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

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

@Composable
fun ModernFooterBar(onNavigate: (String) -> Unit) {
    BottomAppBar(
        containerColor = Color.White,
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
        modifier = Modifier
            .height(75.dp)
            .fillMaxWidth(),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterActionButton(
                icon = Icons.Default.AddCircle,
                label = "New Case",
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            ) { onNavigate("children_list") }

            FooterActionButton(
                icon = Icons.Default.EditNote,
                label = "Log Visit",
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            ) { onNavigate("reports") }

            FooterActionButton(
                icon = Icons.Default.CheckCircle,
                label = "Approve",
                color = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f)
            ) { onNavigate("user_management") }

            FooterActionButton(
                icon = Icons.Default.FileUpload,
                label = "Upload",
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            ) { onNavigate("documents") }

            FooterActionButton(
                icon = Icons.Default.AssignmentTurnedIn,
                label = "Home Study",
                color = Color(0xFFE91E63),
                modifier = Modifier.weight(1f)
            ) { onNavigate("home_studies") }
        }
    }
}

@Composable
fun FooterActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
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
        Icon(icon, label, tint = color, modifier = Modifier.size(22.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, maxLines = 1, color = color)
    }
}
