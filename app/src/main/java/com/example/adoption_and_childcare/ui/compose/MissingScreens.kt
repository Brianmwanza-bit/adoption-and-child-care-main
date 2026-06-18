package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.*
import com.example.adoption_and_childcare.data.repository.NotificationRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import com.example.adoption_and_childcare.utils.AuthManager
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ==================== COMMON UTILS & BASE SCREENS ====================

private fun getCurrentDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBackScreen(title: String, onBack: () -> Unit, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        content(padding)
    }
}

@Composable
fun <T> DataListScreen(
    title: String,
    onBack: () -> Unit,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    onAddClick: () -> Unit
) {
    SimpleBackScreen(title, onBack) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data available. Click + to add.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) { item ->
                        itemContent(item)
                    }
                }
            }
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}

@Composable
fun GenericCard(
    title: String,
    subtitle: String,
    status: String? = null,
    icon: ImageVector = Icons.Default.Info
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
            if (status != null) {
                Text(
                    status,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (status == "Critical" || status == "Urgent") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

// ==================== SHARED COMPONENT WRAPPERS ====================

@Composable
fun TaskStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = color)
        }
    }
}

@Composable
fun DeadlineStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) = TaskStatCard(label, count, color, modifier)

@Composable
fun ApprovalStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) = TaskStatCard(label, count, color, modifier)

@Composable
fun CriticalDateStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) = TaskStatCard(label, count, color, modifier)

@Composable
fun ActivityStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) = TaskStatCard(label, count, color, modifier)

@Composable
fun ActionItemStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) = TaskStatCard(label, count, color, modifier)

@Composable
fun UrgencyStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) = TaskStatCard(label, count, color, modifier)

@Composable
fun WorkloadStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) = TaskStatCard(label, count, color, modifier)

// ==================== MAIN SCREENS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    
    val filteredTasks = when (selectedFilter) {
        "Pending" -> tasks.filter { it.status == "Pending" }
        "In Progress" -> tasks.filter { it.status == "In Progress" }
        "Completed" -> tasks.filter { it.status == "Completed" }
        else -> tasks
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Add Task") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = "Add Task") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskStatCard("Pending", tasks.count { it.status == "Pending" }, Color(0xFFFF9800), Modifier.weight(1f))
                TaskStatCard("In Progress", tasks.count { it.status == "In Progress" }, Color(0xFF2196F3), Modifier.weight(1f))
                TaskStatCard("Completed", tasks.count { it.status == "Completed" }, Color(0xFF4CAF50), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = listOf("All", "Pending", "In Progress", "Completed").indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                listOf("All", "Pending", "In Progress", "Completed").forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No tasks found", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredTasks) { task ->
                        TaskCard(task = task, onStatusChange = { newStatus -> viewModel.saveTask(task.copy(status = newStatus)) })
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddTaskDialog(onDismiss = { showAddDialog = false }, onTaskAdded = { title, description, priority, dueDate ->
            viewModel.saveTask(
                TaskEntity(
                    taskId = 0,
                    title = title,
                    description = description,
                    priority = priority,
                    status = "Pending",
                    dueDate = dueDate,
                    assignedTo = null,
                    createdBy = 1
                )
            )
            showAddDialog = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionItemsScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val items by viewModel.actionItems.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    
    val filteredItems = when (selectedFilter) {
        "Open" -> items.filter { it.status == "Open" }
        "In Progress" -> items.filter { it.status == "In Progress" }
        "Completed" -> items.filter { it.status == "Completed" }
        else -> items
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Action Items") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Add Action Item") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = "Add Action Item") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionItemStatCard("Open", items.count { it.status == "Open" }, Color(0xFFF44336), Modifier.weight(1f))
                ActionItemStatCard("In Progress", items.count { it.status == "In Progress" }, Color(0xFFFF9800), Modifier.weight(1f))
                ActionItemStatCard("Completed", items.count { it.status == "Completed" }, Color(0xFF4CAF50), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = listOf("All", "Open", "In Progress", "Completed").indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                listOf("All", "Open", "In Progress", "Completed").forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No action items found", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredItems) { item ->
                        ActionItemCard(item = item, onStatusChange = { newStatus -> viewModel.saveActionItem(item.copy(status = newStatus)) })
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddActionItemDialog(onDismiss = { showAddDialog = false }, onItemAdded = { title, priority, dueDate ->
            viewModel.saveActionItem(
                ActionItemEntity(
                    actionId = 0,
                    title = title,
                    priority = priority,
                    dueDate = dueDate,
                    relatedCaseId = 1,
                    assigneeId = 1,
                    status = "Open",
                    createdAt = getCurrentDate()
                )
            )
            showAddDialog = false
        })
    }
}

@Composable
fun PermanencyPlansScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val plans by viewModel.permanencyPlans.collectAsState()
    DataListScreen(
        title = "Permanency Plans",
        onBack = onBack,
        items = plans,
        itemContent = { item ->
            GenericCard("Plan #${item.planNumber}", "Goal: ${item.primaryGoal}", item.status, Icons.Default.Map)
        },
        onAddClick = {
            viewModel.savePermanencyPlan(
                PermanencyPlanEntity(
                    planId = 0,
                    childId = 1,
                    planNumber = "PLN-101",
                    primaryGoal = "Reunification",
                    secondaryGoal = "Adoption",
                    tertiaryGoal = null,
                    startDate = getCurrentDate(),
                    reviewDate = getCurrentDate(),
                    completionDate = null,
                    status = "Active",
                    concurrentPlanning = true
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseActivitiesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val activities by viewModel.caseActivities.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    
    val filteredActivities = when (selectedFilter) {
        "Home Visit" -> activities.filter { it.activityType == "Home Visit" }
        "Case Review" -> activities.filter { it.activityType == "Case Review" }
        "Court Hearing" -> activities.filter { it.activityType == "Court Hearing" }
        else -> activities
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Case Activities") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Add Activity") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = "Add Activity") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActivityStatCard("Total", activities.size, Color(0xFF673AB7), Modifier.weight(1f))
                ActivityStatCard("This Month", activities.count { it.activityDate?.startsWith(getCurrentDate().substring(0, 7)) == true }, Color(0xFF2196F3), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = listOf("All", "Home Visit", "Case Review", "Court Hearing").indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                listOf("All", "Home Visit", "Case Review", "Court Hearing").forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredActivities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No activities recorded", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredActivities) { activity ->
                        ActivityTimelineCard(activity = activity)
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddActivityDialog(onDismiss = { showAddDialog = false }, onActivityAdded = { title, activityType, activityDate, activityTime, notes ->
            viewModel.saveCaseActivity(
                CaseActivityEntity(
                    caseId = 1,
                    activityType = activityType,
                    activityDate = activityDate,
                    activityTime = activityTime,
                    title = title,
                    notes = notes,
                    caseworkerId = 1,
                    location = "Office",
                    outcome = "Completed"
                )
            )
            showAddDialog = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDeadlinesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val deadlines by viewModel.deadlines.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    
    val filteredDeadlines = when (selectedFilter) {
        "Pending" -> deadlines.filter { it.status == "Pending" }
        "In Progress" -> deadlines.filter { it.status == "In Progress" }
        "Completed" -> deadlines.filter { it.status == "Completed" }
        "Overdue" -> deadlines.filter { it.priority == "High" && it.status != "Completed" }
        else -> deadlines
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Case Deadlines") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Add Deadline") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = "Add Deadline") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DeadlineStatCard("Pending", deadlines.count { it.status == "Pending" }, Color(0xFFFF9800), Modifier.weight(1f))
                DeadlineStatCard("High Priority", deadlines.count { it.priority == "High" }, Color(0xFFF44336), Modifier.weight(1f))
                DeadlineStatCard("Completed", deadlines.count { it.status == "Completed" }, Color(0xFF4CAF50), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = listOf("All", "Pending", "In Progress", "Completed", "Overdue").indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                listOf("All", "Pending", "In Progress", "Completed", "Overdue").forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredDeadlines.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No deadlines found", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredDeadlines) { deadline ->
                        DeadlineCard(deadline = deadline, onStatusChange = { newStatus -> viewModel.saveDeadline(deadline.copy(status = newStatus)) })
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddDeadlineDialog(onDismiss = { showAddDialog = false }, onDeadlineAdded = { title, dueDate, description, priority ->
            viewModel.saveDeadline(
                CaseDeadlineEntity(
                    deadlineId = 0,
                    caseId = 1,
                    title = title,
                    dueDate = dueDate,
                    description = description,
                    deadlineType = "Required",
                    status = "Pending",
                    priority = priority
                )
            )
            showAddDialog = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseApprovalsScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val approvals by viewModel.approvals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    
    val filteredApprovals = when (selectedFilter) {
        "Pending" -> approvals.filter { it.status == "Pending" }
        "Approved" -> approvals.filter { it.status == "Approved" }
        "Rejected" -> approvals.filter { it.status == "Rejected" }
        else -> approvals
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Case Approvals") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Request Approval") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = "Request Approval") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ApprovalStatCard("Pending", approvals.count { it.status == "Pending" }, Color(0xFFFF9800), Modifier.weight(1f))
                ApprovalStatCard("Approved", approvals.count { it.status == "Approved" }, Color(0xFF4CAF50), Modifier.weight(1f))
                ApprovalStatCard("Rejected", approvals.count { it.status == "Rejected" }, Color(0xFFF44336), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = listOf("All", "Pending", "Approved", "Rejected").indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                listOf("All", "Pending", "Approved", "Rejected").forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredApprovals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No approval requests", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredApprovals) { approval ->
                        ApprovalCard(approval = approval, onStatusChange = { newStatus -> viewModel.saveApproval(approval.copy(status = newStatus)) })
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddApprovalDialog(onDismiss = { showAddDialog = false }, onApprovalAdded = { approvalType, priority, description ->
            viewModel.saveApproval(
                CaseApprovalEntity(
                    approvalId = 0,
                    caseId = 1,
                    approvalType = approvalType,
                    status = "Pending",
                    submittedBy = 1,
                    reviewedBy = null,
                    submissionComments = description,
                    reviewComments = null,
                    revisionRequestedOn = null,
                    submittedDate = getCurrentDate()
                )
            )
            showAddDialog = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseUrgencyFlagsScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val flags by viewModel.urgencyFlags.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Urgency Flags") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Add Flag") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = Color(0xFFF44336)) { Icon(Icons.Default.Warning, contentDescription = "Add Urgency Flag") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            val highRiskCount = flags.count { it.riskLevel == "High" }
            if (highRiskCount > 0) {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336).copy(alpha = 0.1f))) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF44336), modifier = Modifier.size(24.dp))
                        Column {
                            Text(text = "$highRiskCount High Risk Case(s) Require Immediate Attention", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
                            Text(text = "Review and address these flags urgently", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF44336))
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UrgencyStatCard("Total", flags.size, Color(0xFF673AB7), Modifier.weight(1f))
                UrgencyStatCard("High Risk", highRiskCount, Color(0xFFF44336), Modifier.weight(1f))
                UrgencyStatCard("Medium Risk", flags.count { it.riskLevel == "Medium" }, Color(0xFFFF9800), Modifier.weight(1f))
            }
            if (flags.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PriorityHigh, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No urgency flags", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(flags) { flag -> UrgencyFlagCard(flag = flag) }
                }
            }
        }
    }
    if (showAddDialog) {
        AddUrgencyFlagDialog(onDismiss = { showAddDialog = false }, onFlagAdded = { flagType, reason, riskLevel ->
            viewModel.saveUrgencyFlag(
                CaseUrgencyFlagEntity(
                    flagId = 0,
                    caseId = 1,
                    flagType = flagType,
                    reason = reason,
                    riskLevel = riskLevel,
                    description = null,
                    createdAt = getCurrentDate(),
                    createdBy = 1
                )
            )
            showAddDialog = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriticalDatesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val dates by viewModel.criticalDates.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    
    val filteredDates = when (selectedFilter) {
        "Upcoming" -> dates.filter { !it.isCompleted }
        "Completed" -> dates.filter { it.isCompleted }
        else -> dates
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Critical Dates") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Add Date") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = "Add Date") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CriticalDateStatCard("Total", dates.size, Color(0xFF673AB7), Modifier.weight(1f))
                CriticalDateStatCard("Upcoming", dates.count { !it.isCompleted }, Color(0xFFFF9800), Modifier.weight(1f))
                CriticalDateStatCard("Completed", dates.count { it.isCompleted }, Color(0xFF4CAF50), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = listOf("All", "Upcoming", "Completed").indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                listOf("All", "Upcoming", "Completed").forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredDates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No critical dates recorded", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredDates) { date ->
                        CriticalDateCard(date = date, onToggleComplete = { viewModel.saveCriticalDate(date.copy(isCompleted = !date.isCompleted)) })
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddCriticalDateDialog(onDismiss = { showAddDialog = false }, onDateAdded = { dateType, eventDate, description ->
            viewModel.saveCriticalDate(
                CriticalDateEntity(
                    dateId = 0,
                    childId = 1,
                    dateType = dateType,
                    eventDate = eventDate,
                    isCompleted = false,
                    completedDate = null,
                    notes = description
                )
            )
            showAddDialog = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkloadDashboardScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val workload by viewModel.workload.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val latestWorkload = workload.maxByOrNull { it.trackingDate ?: "" }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workload Dashboard") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Add Entry") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.DonutLarge, contentDescription = "Add Workload Entry") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Today's Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = latestWorkload?.trackingDate ?: getCurrentDate(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkloadStatCard("Active Cases", latestWorkload?.totalActiveCases ?: 0, Color(0xFF2196F3), Modifier.weight(1f))
                    WorkloadStatCard("Pending Tasks", latestWorkload?.tasksPending ?: 0, Color(0xFFFF9800), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkloadStatCard("Pending Approvals", latestWorkload?.approvalsPending ?: 0, Color(0xFF673AB7), Modifier.weight(1f))
                    WorkloadStatCard("Overdue", latestWorkload?.deadlinesOverdue ?: 0, Color(0xFFF44336), Modifier.weight(1f))
                }
            }
            if (latestWorkload != null) {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Detailed Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                        listOf("Home Visits Scheduled" to latestWorkload.homeVisitsScheduled.toString(), "Home Visits Completed" to latestWorkload.homeVisitsCompleted.toString(), "Reports Submitted" to latestWorkload.reportsSubmitted.toString(), "Productivity Score" to "${latestWorkload.productivityScore}%").forEach { (label, value) ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddWorkloadDialog(onDismiss = { showAddDialog = false }, onWorkloadAdded = { date, cases, tasks, approvals, reports, notes ->
            viewModel.saveWorkload(
                WorkloadTrackingEntity(
                    workloadId = 0,
                    caseworkerId = 1,
                    trackingDate = date,
                    totalActiveCases = cases,
                    tasksPending = tasks,
                    approvalsPending = approvals,
                    reportsSubmitted = reports,
                    productivityScore = 80.0,
                    notes = notes
                )
            )
            showAddDialog = false
        })
    }
}

@Composable
fun WorkerMessagesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val messages by viewModel.workerMessages.collectAsState()
    DataListScreen(
        title = "Worker Messages",
        onBack = onBack,
        items = messages,
        itemContent = { item ->
            GenericCard(item.title, item.content ?: "", if (item.isRead) "Read" else "New", Icons.AutoMirrored.Filled.Message)
        },
        onAddClick = {
            viewModel.saveWorkerMessage(
                WorkerMessageEntity(
                    messageId = 0,
                    senderId = 1,
                    recipientId = 2,
                    caseId = 1,
                    title = "Meeting Tomorrow",
                    content = "Don't forget the case file",
                    isRead = false,
                    readAt = null,
                    createdAt = getCurrentDate()
                )
            )
        }
    )
}

@Composable
fun PlacementCompatibilityScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val records by viewModel.compatibilityRecords.collectAsState()
    DataListScreen(
        title = "Placement Compatibility",
        onBack = onBack,
        items = records,
        itemContent = { item ->
            GenericCard("Score: ${item.compatibilityScore}", "Child: ${item.childId}, Family: ${item.familyId}", "Assessed: ${item.assessmentDate}", Icons.AutoMirrored.Filled.CompareArrows)
        },
        onAddClick = {
            viewModel.saveCompatibilityRecord(
                PlacementCompatibilityEntity(
                    compatibilityId = 0,
                    childId = 1,
                    familyId = 1,
                    compatibilityScore = 85.0,
                    medicalNeedsSupport = 90.0,
                    behavioralNeedsSupport = 80.0,
                    educationalNeedsSupport = 75.0,
                    emotionalSupportCapacity = 85.0,
                    geographicPreferencesMatch = 95.0,
                    religiousPreferenceMatch = 70.0,
                    culturalFitScore = 80.0,
                    specialConsiderations = "Good fit",
                    notes = "Follow up needed",
                    assessmentDate = getCurrentDate(),
                    assessedBy = 1
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardPreferencesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val prefs by viewModel.dashboardPreferences.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val latestPrefs = prefs.maxByOrNull { it.userId ?: 0 }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Preferences") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Settings, contentDescription = "Edit Preferences") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Tune, contentDescription = "Configure Dashboard") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (latestPrefs == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No preferences configured", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Layout Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                                PreferenceRow(label = "Layout Type", value = latestPrefs.layoutType ?: "Grid", icon = Icons.Default.Dashboard)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceRow(label = "Update Frequency", value = latestPrefs.updateFrequency ?: "Daily", icon = Icons.Default.Update)
                            }
                        }
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Display Options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                                PreferenceToggleRow(
                                    label = "Dark Mode", 
                                    enabled = latestPrefs.darkMode ?: false, 
                                    icon = Icons.Default.DarkMode,
                                    onCheckedChange = { newValue ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(darkMode = newValue))
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceToggleRow(
                                    label = "Show Statistics", 
                                    enabled = latestPrefs.showStats ?: true, 
                                    icon = Icons.Default.BarChart,
                                    onCheckedChange = { newValue ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(showStats = newValue))
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceToggleRow(
                                    label = "Show Quick Actions", 
                                    enabled = latestPrefs.showQuickActions ?: true, 
                                    icon = Icons.Default.Favorite,
                                    onCheckedChange = { newValue ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(showQuickActions = newValue))
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceToggleRow(
                                    label = "Show Recent Activity", 
                                    enabled = latestPrefs.showRecentActivity ?: true, 
                                    icon = Icons.Default.History,
                                    onCheckedChange = { newValue ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(showRecentActivity = newValue))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        EditPreferencesDialog(currentPrefs = latestPrefs, onDismiss = { showAddDialog = false }, onPreferencesSaved = { layoutType, darkMode, showStats, showQuickActions, showRecentActivity, updateFrequency, notificationsEnabled ->
            viewModel.saveDashboardPreference(
                DashboardPreferenceEntity(
                    preferenceId = 0,
                    userId = 1,
                    layoutType = layoutType,
                    darkMode = darkMode,
                    showStats = showStats,
                    showQuickActions = showQuickActions,
                    showRecentActivity = showRecentActivity,
                    updateFrequency = updateFrequency,
                    notificationsEnabled = notificationsEnabled
                )
            )
            showAddDialog = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val authManager = remember { AuthManager(context) }
    val repository = remember { NotificationRepositoryImpl(db.notificationDao(), apiService, authManager) }
    var notifications by remember { mutableStateOf<List<NotificationEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        db.notificationDao().observeAll().collectLatest { list -> notifications = list }
    }
    
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            val token = authManager.getAuthToken() ?: ""
            if (token.isNotEmpty()) {
                val result = repository.fetchFromApi(token)
                errorMessage = if (result.isFailure) result.exceptionOrNull()?.message else null
            }
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notifications") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            else if (errorMessage != null) Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            else if (notifications.isEmpty()) Text("No notifications yet", modifier = Modifier.align(Alignment.Center))
            else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(notifications) { notification ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(notification.title, style = MaterialTheme.typography.titleMedium)
                    Text(notification.message, style = MaterialTheme.typography.bodySmall)
                }
                                if (!notification.isRead) {
                                    IconButton(onClick = { scope.launch { val token = authManager.getAuthToken() ?: ""; if (token.isNotEmpty()) repository.markAsRead(notification.notificationId, token) } }) {
                                        Icon(Icons.Default.Check, contentDescription = "Mark as read", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== SHARED SCREEN COMPONENTS ====================

@Composable
fun TaskCard(task: TaskEntity, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = when (task.status) { "Pending" -> Color(0xFFFF9800); "In Progress" -> Color(0xFF2196F3); "Completed" -> Color(0xFF4CAF50); else -> Color.Gray }
    val priorityColor = when (task.priority) { "High" -> Color(0xFFF44336); "Medium" -> Color(0xFFFF9800); "Low" -> Color(0xFF4CAF50); else -> Color.Gray }
    
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = task.description ?: "No description", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Options") }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("Pending", "In Progress", "Completed").forEach { status ->
                            DropdownMenuItem(text = { Text(status) }, onClick = { onStatusChange(status); expanded = false })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(12.dp), color = priorityColor.copy(alpha = 0.1f)) { Text(text = task.priority ?: "Medium", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = priorityColor) }
                Surface(shape = RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) { Text(text = task.status ?: "Pending", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onTaskAdded: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onTaskAdded(title, description, priority, getCurrentDate()) }, enabled = title.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun DeadlineCard(deadline: CaseDeadlineEntity, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = when (deadline.status) { "Pending" -> Color(0xFFFF9800); "In Progress" -> Color(0xFF2196F3); "Completed" -> Color(0xFF4CAF50); else -> Color.Gray }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = deadline.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = deadline.description ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Options") }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("Pending", "In Progress", "Completed").forEach { status ->
                            DropdownMenuItem(text = { Text(status) }, onClick = { onStatusChange(status); expanded = false })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) { Text(text = deadline.status ?: "Pending", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor) }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Due: ${deadline.dueDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeadlineDialog(onDismiss: () -> Unit, onDeadlineAdded: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(getCurrentDate()) }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Deadline") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onDeadlineAdded(title, dueDate, description, "Medium") }, enabled = title.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ApprovalCard(approval: CaseApprovalEntity, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = when (approval.status) { "Pending" -> Color(0xFFFF9800); "Approved" -> Color(0xFF4CAF50); "Rejected" -> Color(0xFFF44336); else -> Color.Gray }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = approval.approvalType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Requested: ${approval.submittedDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Options") }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("Pending", "Approved", "Rejected").forEach { status ->
                            DropdownMenuItem(text = { Text(status) }, onClick = { onStatusChange(status); expanded = false })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) { Text(text = approval.status ?: "Pending", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddApprovalDialog(onDismiss: () -> Unit, onApprovalAdded: (String, String, String) -> Unit) {
    var approvalType by remember { mutableStateOf("Case Closure") }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Approval") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = approvalType, onValueChange = { approvalType = it }, label = { Text("Approval Type") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onApprovalAdded(approvalType, "Medium", description) }, enabled = approvalType.isNotBlank()) { Text("Submit") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun CriticalDateCard(date: CriticalDateEntity, onToggleComplete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = date.dateType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = date.eventDate ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                if (date.notes != null) Text(text = date.notes, style = MaterialTheme.typography.bodyMedium)
            }
            Checkbox(checked = date.isCompleted, onCheckedChange = { onToggleComplete() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCriticalDateDialog(onDismiss: () -> Unit, onDateAdded: (String, String, String) -> Unit) {
    var dateType by remember { mutableStateOf("Court Hearing") }
    var eventDate by remember { mutableStateOf(getCurrentDate()) }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Critical Date") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = dateType, onValueChange = { dateType = it }, label = { Text("Date Type") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = eventDate, onValueChange = { eventDate = it }, label = { Text("Event Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onDateAdded(dateType, eventDate, description) }, enabled = dateType.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ActionItemCard(item: ActionItemEntity, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = when (item.status) { "Open" -> Color(0xFFF44336); "In Progress" -> Color(0xFFFF9800); "Completed" -> Color(0xFF4CAF50); else -> Color.Gray }
    val priorityColor = when (item.priority) { "High" -> Color(0xFFF44336); "Medium" -> Color(0xFFFF9800); "Low" -> Color(0xFF4CAF50); else -> Color.Gray }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Due: ${item.dueDate ?: "No due date"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Options") }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("Open", "In Progress", "Completed").forEach { status ->
                            DropdownMenuItem(text = { Text(status) }, onClick = { onStatusChange(status); expanded = false })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(12.dp), color = priorityColor.copy(alpha = 0.1f)) { Text(text = item.priority ?: "Medium", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = priorityColor) }
                Surface(shape = RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) { Text(text = item.status ?: "Open", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActionItemDialog(onDismiss: () -> Unit, onItemAdded: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Action Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onItemAdded(title, priority, getCurrentDate()) }, enabled = title.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ActivityTimelineCard(activity: CaseActivityEntity) {
    val activityColor = when (activity.activityType) { "Home Visit" -> Color(0xFF4CAF50); "Case Review" -> Color(0xFF2196F3); "Court Hearing" -> Color(0xFFF44336); else -> Color(0xFF673AB7) }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(12.dp).background(activityColor, CircleShape))
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.width(2.dp).height(40.dp).background(activityColor.copy(alpha = 0.3f)))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = activity.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = activity.activityDate ?: "No date", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(text = activity.activityTime ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(12.dp), color = activityColor.copy(alpha = 0.1f)) { Text(text = activity.activityType ?: "Activity", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = activityColor) }
                if (activity.notes?.isNotEmpty() == true) { Spacer(modifier = Modifier.height(8.dp)); Text(text = activity.notes, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(onDismiss: () -> Unit, onActivityAdded: (String, String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var activityType by remember { mutableStateOf("Home Visit") }
    var notes by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log New Activity") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            }
        },
        confirmButton = { TextButton(onClick = { onActivityAdded(title, activityType, getCurrentDate(), "10:00 AM", notes) }, enabled = title.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// ==================== MISSING COMPONENT DEFINITIONS ====================

@Composable
fun UrgencyFlagCard(flag: CaseUrgencyFlagEntity) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = flag.flagType ?: "Urgency Flag", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = flag.reason ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Surface(shape = RoundedCornerShape(12.dp), color = if (flag.riskLevel == "High") Color(0xFFF44336).copy(alpha = 0.1f) else Color(0xFFFF9800).copy(alpha = 0.1f)) {
                Text(text = flag.riskLevel ?: "Medium", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = if (flag.riskLevel == "High") Color(0xFFF44336) else Color(0xFFFF9800))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUrgencyFlagDialog(onDismiss: () -> Unit, onFlagAdded: (String, String, String) -> Unit) {
    var type by remember { mutableStateOf("Safety Concern") }
    var reason by remember { mutableStateOf("") }
    var risk by remember { mutableStateOf("High") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Urgency Flag") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Flag Type") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Reason") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onFlagAdded(type, reason, risk) }, enabled = type.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkloadDialog(onDismiss: () -> Unit, onWorkloadAdded: (String, Int, Int, Int, Int, String) -> Unit) {
    var date by remember { mutableStateOf(getCurrentDate()) }
    var cases by remember { mutableStateOf("5") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Workload Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cases, onValueChange = { cases = it }, label = { Text("Active Cases") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onWorkloadAdded(date, cases.toIntOrNull() ?: 0, 0, 0, 0, "") }) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun PreferenceRow(label: String, value: String, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PreferenceToggleRow(label: String, enabled: Boolean, icon: ImageVector, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = enabled, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPreferencesDialog(currentPrefs: DashboardPreferenceEntity?, onDismiss: () -> Unit, onPreferencesSaved: (String, Boolean, Boolean, Boolean, Boolean, String, Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Preferences") },
        text = { Text("Preference configuration options here.") },
        confirmButton = { TextButton(onClick = { onPreferencesSaved("Grid", false, true, true, true, "Daily", true) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
