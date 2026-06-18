package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
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

/**
 * Returns the current date in yyyy-MM-dd format.
 */
private fun getCurrentDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

/**
 * A simple screen with a top bar and a back button.
 *
 * @param title The title to display in the top bar.
 * @param onBack Callback when the back button is clicked.
 * @param content The content of the screen, receiving padding from the Scaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBackScreen(title: String, onBack: () -> Unit, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                    }
                }
            )
        }
    ) { padding ->
        content(padding)
    }
}

/**
 * A screen that displays a list of data items with an add button.
 *
 * @param title The screen title.
 * @param onBack Callback for back navigation.
 * @param items The list of items to display.
 * @param itemContent Composable to render each item.
 * @param onAddClick Callback when the FAB is clicked.
 */
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
                    Text(stringResource(R.string.common_no_data))
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
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.common_btn_add))
            }
        }
    }
}

/**
 * A generic card component with an icon, title, and optional status.
 *
 * @param title The main text.
 * @param subtitle The secondary text.
 * @param status Optional status text.
 * @param icon The icon to display.
 */
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

/**
 * A statistical card displaying a count and a label.
 *
 * @param label The label for the statistic.
 * @param count The numeric value to display.
 * @param color The color theme for the card.
 * @param modifier Modifier for layout.
 */
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

/**
 * Screen for managing user tasks.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for tasks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    val filterAll = stringResource(R.string.tasks_label_all)
    val filterPending = stringResource(R.string.tasks_label_pending)
    val filterInProgress = stringResource(R.string.tasks_label_in_progress)
    val filterCompleted = stringResource(R.string.tasks_label_completed)
    
    var selectedFilter by remember { mutableStateOf(filterAll) }
    val filters = listOf(filterAll, filterPending, filterInProgress, filterCompleted)
    
    val filteredTasks = when (selectedFilter) {
        filterPending -> tasks.filter { it.status == "Pending" }
        filterInProgress -> tasks.filter { it.status == "In Progress" }
        filterCompleted -> tasks.filter { it.status == "Completed" }
        else -> tasks
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tasks_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.tasks_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.tasks_add_desc)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskStatCard(filterPending, tasks.count { it.status == "Pending" }, Color(0xFFFF9800), Modifier.weight(1f))
                TaskStatCard(filterInProgress, tasks.count { it.status == "In Progress" }, Color(0xFF2196F3), Modifier.weight(1f))
                TaskStatCard(filterCompleted, tasks.count { it.status == "Completed" }, Color(0xFF4CAF50), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.tasks_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
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

/**
 * Screen for managing action items.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for action items.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionItemsScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val items by viewModel.actionItems.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    val filterAll = stringResource(R.string.tasks_label_all)
    val filterOpen = stringResource(R.string.action_items_label_open)
    val filterInProgress = stringResource(R.string.tasks_label_in_progress)
    val filterCompleted = stringResource(R.string.tasks_label_completed)
    
    var selectedFilter by remember { mutableStateOf(filterAll) }
    val filters = listOf(filterAll, filterOpen, filterInProgress, filterCompleted)
    
    val filteredItems = when (selectedFilter) {
        filterOpen -> items.filter { it.status == "Open" }
        filterInProgress -> items.filter { it.status == "In Progress" }
        filterCompleted -> items.filter { it.status == "Completed" }
        else -> items
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.action_items_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_items_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_items_add_desc)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionItemStatCard(filterOpen, items.count { it.status == "Open" }, Color(0xFFF44336), Modifier.weight(1f))
                ActionItemStatCard(filterInProgress, items.count { it.status == "In Progress" }, Color(0xFFFF9800), Modifier.weight(1f))
                ActionItemStatCard(filterCompleted, items.count { it.status == "Completed" }, Color(0xFF4CAF50), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.action_items_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
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

/**
 * Screen for displaying and adding permanency plans.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for permanency plans.
 */
@Composable
fun PermanencyPlansScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val plans by viewModel.permanencyPlans.collectAsState()
    DataListScreen(
        title = stringResource(R.string.permanency_plans_title),
        onBack = onBack,
        items = plans,
        itemContent = { item ->
            GenericCard(
                stringResource(R.string.permanency_plans_plan_format, item.planNumber ?: ""),
                stringResource(R.string.permanency_plans_goal_format, item.primaryGoal ?: ""),
                item.status,
                Icons.Default.Map
            )
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

/**
 * Screen for tracking case activities.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for case activities.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseActivitiesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val activities by viewModel.caseActivities.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    val filterAll = stringResource(R.string.tasks_label_all)
    val filterHomeVisit = stringResource(R.string.activities_type_home_visit)
    val filterCaseReview = stringResource(R.string.activities_type_case_review)
    val filterCourtHearing = stringResource(R.string.activities_type_court_hearing)
    
    var selectedFilter by remember { mutableStateOf(filterAll) }
    val filters = listOf(filterAll, filterHomeVisit, filterCaseReview, filterCourtHearing)
    
    val filteredActivities = when (selectedFilter) {
        filterHomeVisit -> activities.filter { it.activityType == "Home Visit" }
        filterCaseReview -> activities.filter { it.activityType == "Case Review" }
        filterCourtHearing -> activities.filter { it.activityType == "Court Hearing" }
        else -> activities
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.activities_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.activities_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.activities_add_desc)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActivityStatCard(stringResource(R.string.activities_label_total), activities.size, Color(0xFF673AB7), Modifier.weight(1f))
                ActivityStatCard(stringResource(R.string.activities_label_this_month), activities.count { it.activityDate?.startsWith(getCurrentDate().substring(0, 7)) == true }, Color(0xFF2196F3), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredActivities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.activities_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
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

/**
 * Screen for managing case deadlines.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for deadlines.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDeadlinesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val deadlines by viewModel.deadlines.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    val filterAll = stringResource(R.string.tasks_label_all)
    val filterPending = stringResource(R.string.tasks_label_pending)
    val filterInProgress = stringResource(R.string.tasks_label_in_progress)
    val filterCompleted = stringResource(R.string.tasks_label_completed)
    val filterOverdue = stringResource(R.string.deadlines_label_overdue)
    
    var selectedFilter by remember { mutableStateOf(filterAll) }
    val filters = listOf(filterAll, filterPending, filterInProgress, filterCompleted, filterOverdue)
    
    val filteredDeadlines = when (selectedFilter) {
        filterPending -> deadlines.filter { it.status == "Pending" }
        filterInProgress -> deadlines.filter { it.status == "In Progress" }
        filterCompleted -> deadlines.filter { it.status == "Completed" }
        filterOverdue -> deadlines.filter { it.priority == "High" && it.status != "Completed" }
        else -> deadlines
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.deadlines_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.deadlines_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.deadlines_add_desc)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DeadlineStatCard(filterPending, deadlines.count { it.status == "Pending" }, Color(0xFFFF9800), Modifier.weight(1f))
                DeadlineStatCard(stringResource(R.string.deadlines_label_high_priority), deadlines.count { it.priority == "High" }, Color(0xFFF44336), Modifier.weight(1f))
                DeadlineStatCard(filterCompleted, deadlines.count { it.status == "Completed" }, Color(0xFF4CAF50), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredDeadlines.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.deadlines_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
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

/**
 * Screen for managing case approval requests.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for approvals.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseApprovalsScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val approvals by viewModel.approvals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    val filterAll = stringResource(R.string.tasks_label_all)
    val filterPending = stringResource(R.string.tasks_label_pending)
    val filterApproved = stringResource(R.string.approvals_label_approved)
    val filterRejected = stringResource(R.string.approvals_label_rejected)
    
    var selectedFilter by remember { mutableStateOf(filterAll) }
    val filters = listOf(filterAll, filterPending, filterApproved, filterRejected)
    
    val filteredApprovals = when (selectedFilter) {
        filterPending -> approvals.filter { it.status == "Pending" }
        filterApproved -> approvals.filter { it.status == "Approved" }
        filterRejected -> approvals.filter { it.status == "Rejected" }
        else -> approvals
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.approvals_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.approvals_request_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.approvals_request_desc)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ApprovalStatCard(filterPending, approvals.count { it.status == "Pending" }, Color(0xFFFF9800), Modifier.weight(1f))
                ApprovalStatCard(filterApproved, approvals.count { it.status == "Approved" }, Color(0xFF4CAF50), Modifier.weight(1f))
                ApprovalStatCard(filterRejected, approvals.count { it.status == "Rejected" }, Color(0xFFF44336), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredApprovals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.approvals_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
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

/**
 * Screen for displaying urgency flags.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for urgency flags.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseUrgencyFlagsScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val flags by viewModel.urgencyFlags.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.urgency_flags_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.urgency_flags_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = Color(0xFFF44336)) { Icon(Icons.Default.Warning, contentDescription = stringResource(R.string.urgency_flags_add_desc)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            val highRiskCount = flags.count { it.riskLevel == "High" }
            if (highRiskCount > 0) {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336).copy(alpha = 0.1f))) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF44336), modifier = Modifier.size(24.dp))
                        Column {
                            Text(text = stringResource(R.string.urgency_flags_banner_text, highRiskCount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
                            Text(text = stringResource(R.string.urgency_flags_banner_subtext), style = MaterialTheme.typography.bodySmall, color = Color(0xFFF44336))
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UrgencyStatCard(stringResource(R.string.urgency_flags_label_total), flags.size, Color(0xFF673AB7), Modifier.weight(1f))
                UrgencyStatCard(stringResource(R.string.urgency_flags_label_high_risk), highRiskCount, Color(0xFFF44336), Modifier.weight(1f))
                UrgencyStatCard(stringResource(R.string.urgency_flags_label_medium_risk), flags.count { it.riskLevel == "Medium" }, Color(0xFFFF9800), Modifier.weight(1f))
            }
            if (flags.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PriorityHigh, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.urgency_flags_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
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

/**
 * Screen for tracking critical case dates.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for critical dates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriticalDatesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val dates by viewModel.criticalDates.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    val filterAll = stringResource(R.string.tasks_label_all)
    val filterUpcoming = stringResource(R.string.critical_dates_label_upcoming)
    val filterCompleted = stringResource(R.string.tasks_label_completed)
    
    var selectedFilter by remember { mutableStateOf(filterAll) }
    val filters = listOf(filterAll, filterUpcoming, filterCompleted)
    
    val filteredDates = when (selectedFilter) {
        filterUpcoming -> dates.filter { !it.isCompleted }
        filterCompleted -> dates.filter { it.isCompleted }
        else -> dates
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.critical_dates_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.critical_dates_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.critical_dates_add_desc)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CriticalDateStatCard(stringResource(R.string.urgency_flags_label_total), dates.size, Color(0xFF673AB7), Modifier.weight(1f))
                CriticalDateStatCard(filterUpcoming, dates.count { !it.isCompleted }, Color(0xFFFF9800), Modifier.weight(1f))
                CriticalDateStatCard(filterCompleted, dates.count { it.isCompleted }, Color(0xFF4CAF50), Modifier.weight(1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filter ->
                    Tab(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, text = { Text(filter) })
                }
            }
            if (filteredDates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.critical_dates_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
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

/**
 * Screen providing a dashboard for caseworker workload tracking.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for workload data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkloadDashboardScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val workload by viewModel.workload.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val latestWorkload = workload.maxByOrNull { it.trackingDate ?: "" }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.workload_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.workload_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.DonutLarge, contentDescription = stringResource(R.string.workload_add_desc)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.workload_today_overview), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = latestWorkload?.trackingDate ?: getCurrentDate(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkloadStatCard(stringResource(R.string.workload_label_active_cases), latestWorkload?.totalActiveCases ?: 0, Color(0xFF2196F3), Modifier.weight(1f))
                    WorkloadStatCard(stringResource(R.string.workload_label_pending_tasks), latestWorkload?.tasksPending ?: 0, Color(0xFFFF9800), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkloadStatCard(stringResource(R.string.workload_label_pending_approvals), latestWorkload?.approvalsPending ?: 0, Color(0xFF673AB7), Modifier.weight(1f))
                    WorkloadStatCard(stringResource(R.string.workload_label_overdue), latestWorkload?.deadlinesOverdue ?: 0, Color(0xFFF44336), Modifier.weight(1f))
                }
            }
            if (latestWorkload != null) {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = stringResource(R.string.workload_breakdown_header), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                        listOf(
                            stringResource(R.string.workload_label_visits_scheduled) to latestWorkload.homeVisitsScheduled.toString(),
                            stringResource(R.string.workload_label_visits_completed) to latestWorkload.homeVisitsCompleted.toString(),
                            stringResource(R.string.workload_label_reports_submitted) to latestWorkload.reportsSubmitted.toString(),
                            stringResource(R.string.workload_label_productivity) to "${latestWorkload.productivityScore}%"
                        ).forEach { (label, value) ->
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

/**
 * Screen for viewing messages related to cases.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for messages.
 */
@Composable
fun WorkerMessagesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val messages by viewModel.workerMessages.collectAsState()
    val readLabel = stringResource(R.string.worker_messages_label_read)
    val newLabel = stringResource(R.string.worker_messages_label_new)
    
    DataListScreen(
        title = stringResource(R.string.worker_messages_title),
        onBack = onBack,
        items = messages,
        itemContent = { item ->
            GenericCard(item.title, item.content ?: "", if (item.isRead) readLabel else newLabel, Icons.AutoMirrored.Filled.Message)
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

/**
 * Screen for viewing child-family placement compatibility results.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for compatibility records.
 */
@Composable
fun PlacementCompatibilityScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val records by viewModel.compatibilityRecords.collectAsState()
    DataListScreen(
        title = stringResource(R.string.placement_compat_title),
        onBack = onBack,
        items = records,
        itemContent = { item ->
            GenericCard(
                stringResource(R.string.placement_compat_score_format, item.compatibilityScore ?: 0.0),
                stringResource(R.string.placement_compat_child_family_format, item.childId, item.familyId),
                stringResource(R.string.placement_compat_assessed_format, item.assessmentDate ?: ""),
                Icons.AutoMirrored.Filled.CompareArrows
            )
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

/**
 * Screen for configuring dashboard UI preferences.
 *
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for preferences.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardPreferencesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val prefs by viewModel.dashboardPreferences.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val latestPrefs = prefs.maxByOrNull { it.userId }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dash_prefs_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.dash_prefs_edit_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Tune, contentDescription = stringResource(R.string.dash_prefs_config_desc)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (latestPrefs == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.dash_prefs_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = stringResource(R.string.dash_prefs_layout_header), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                                PreferenceRow(label = stringResource(R.string.dash_prefs_label_layout_type), value = latestPrefs.layoutType ?: "Grid", icon = Icons.Default.Dashboard)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceRow(label = stringResource(R.string.dash_prefs_label_update_freq), value = latestPrefs.updateFrequency ?: "Daily", icon = Icons.Default.Update)
                            }
                        }
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = stringResource(R.string.dash_prefs_display_header), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                                PreferenceToggleRow(
                                    label = stringResource(R.string.dash_prefs_label_dark_mode), 
                                    enabled = latestPrefs.darkMode, 
                                    icon = Icons.Default.DarkMode,
                                    onCheckedChange = { newValue ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(darkMode = newValue))
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceToggleRow(
                                    label = stringResource(R.string.dash_prefs_label_show_stats), 
                                    enabled = latestPrefs.showStats, 
                                    icon = Icons.Default.BarChart,
                                    onCheckedChange = { newValue ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(showStats = newValue))
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceToggleRow(
                                    label = stringResource(R.string.dash_prefs_label_show_actions), 
                                    enabled = latestPrefs.showQuickActions, 
                                    icon = Icons.Default.Favorite,
                                    onCheckedChange = { newValue ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(showQuickActions = newValue))
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceToggleRow(
                                    label = stringResource(R.string.dash_prefs_label_show_activity), 
                                    enabled = latestPrefs.showRecentActivity,
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

/**
 * Screen for managing and viewing system notifications.
 *
 * @param onBack Callback for back navigation.
 */
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
            TopAppBar(title = { Text(stringResource(R.string.notifications_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc)) } })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            else if (errorMessage != null) {
                val error = errorMessage
                if (error != null) {
                    Text(stringResource(R.string.notifications_error_format, error), color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
            } else if (notifications.isEmpty()) Text(stringResource(R.string.notifications_no_found), modifier = Modifier.align(Alignment.Center))
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
                                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.notifications_mark_read_desc), tint = MaterialTheme.colorScheme.primary)
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

/**
 * A card representing a single task item.
 *
 * @param task The task entity.
 * @param onStatusChange Callback when the status is updated.
 */
@Composable
fun TaskCard(task: TaskEntity, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = when (task.status) { 
        "Pending" -> MaterialTheme.colorScheme.error
        "In Progress" -> MaterialTheme.colorScheme.primary
        "Completed" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    val priorityColor = when (task.priority) { 
        "High" -> MaterialTheme.colorScheme.error
        "Medium" -> MaterialTheme.colorScheme.secondary
        "Low" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = task.description ?: stringResource(R.string.common_no_desc), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.common_options_desc)) }
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

/**
 * Dialog for adding a new task.
 *
 * @param onDismiss Callback to dismiss the dialog.
 * @param onTaskAdded Callback when a new task is provided.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onTaskAdded: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.tasks_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.common_label_title)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.common_label_desc)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onTaskAdded(title, description, priority, getCurrentDate()) }, enabled = title.isNotBlank()) { Text(stringResource(R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_btn_cancel)) } }
    )
}

/**
 * Card representing a case deadline.
 *
 * @param deadline The deadline entity.
 * @param onStatusChange Callback for status updates.
 */
@Composable
fun DeadlineCard(deadline: CaseDeadlineEntity, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = when (deadline.status) { 
        "Pending" -> MaterialTheme.colorScheme.error
        "In Progress" -> MaterialTheme.colorScheme.primary
        "Completed" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = deadline.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = deadline.description ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.common_options_desc)) }
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

/**
 * Dialog for adding a new deadline.
 *
 * @param onDismiss Callback to dismiss.
 * @param onDeadlineAdded Callback when data is confirmed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeadlineDialog(onDismiss: () -> Unit, onDeadlineAdded: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(getCurrentDate()) }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.deadlines_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.common_label_title)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text(stringResource(R.string.deadlines_due_date_field)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.common_label_desc)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onDeadlineAdded(title, dueDate, description, "Medium") }, enabled = title.isNotBlank()) { Text(stringResource(R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_btn_cancel)) } }
    )
}

/**
 * Card representing a case approval request.
 *
 * @param approval The approval entity.
 * @param onStatusChange Callback for status updates.
 */
@Composable
fun ApprovalCard(approval: CaseApprovalEntity, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = when (approval.status) { 
        "Pending" -> MaterialTheme.colorScheme.error
        "Approved" -> MaterialTheme.colorScheme.tertiary
        "Rejected" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = approval.approvalType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Requested: ${approval.submittedDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.common_options_desc)) }
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

/**
 * Dialog for requesting a new case approval.
 *
 * @param onDismiss Callback to dismiss.
 * @param onApprovalAdded Callback when submitted.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddApprovalDialog(onDismiss: () -> Unit, onApprovalAdded: (String, String, String) -> Unit) {
    var approvalType by remember { mutableStateOf("Case Closure") }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.approvals_request_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = approvalType, onValueChange = { approvalType = it }, label = { Text(stringResource(R.string.approvals_field_type)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.common_label_desc)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onApprovalAdded(approvalType, "Medium", description) }, enabled = approvalType.isNotBlank()) { Text(stringResource(R.string.common_btn_submit)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_btn_cancel)) } }
    )
}

/**
 * Card representing a critical case date.
 *
 * @param date The critical date entity.
 * @param onToggleComplete Callback when checkbox is toggled.
 */
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

/**
 * Dialog for adding a new critical case date.
 *
 * @param onDismiss Callback to dismiss.
 * @param onDateAdded Callback when added.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCriticalDateDialog(onDismiss: () -> Unit, onDateAdded: (String, String, String) -> Unit) {
    var dateType by remember { mutableStateOf("Court Hearing") }
    var eventDate by remember { mutableStateOf(getCurrentDate()) }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.critical_dates_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = dateType, onValueChange = { dateType = it }, label = { Text(stringResource(R.string.critical_dates_field_type)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = eventDate, onValueChange = { eventDate = it }, label = { Text(stringResource(R.string.common_label_date)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.common_label_desc)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onDateAdded(dateType, eventDate, description) }, enabled = dateType.isNotBlank()) { Text(stringResource(R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_btn_cancel)) } }
    )
}

/**
 * Card representing an action item.
 *
 * @param item The action item entity.
 * @param onStatusChange Callback for status changes.
 */
@Composable
fun ActionItemCard(item: ActionItemEntity, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = when (item.status) { 
        "Open" -> MaterialTheme.colorScheme.error
        "In Progress" -> MaterialTheme.colorScheme.primary
        "Completed" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    val priorityColor = when (item.priority) { 
        "High" -> MaterialTheme.colorScheme.error
        "Medium" -> MaterialTheme.colorScheme.secondary
        "Low" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Due: ${item.dueDate ?: "No due date"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.common_options_desc)) }
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

/**
 * Dialog for adding a new action item.
 *
 * @param onDismiss Callback to dismiss.
 * @param onItemAdded Callback when added.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActionItemDialog(onDismiss: () -> Unit, onItemAdded: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.action_items_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.common_label_title)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onItemAdded(title, priority, getCurrentDate()) }, enabled = title.isNotBlank()) { Text(stringResource(R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_btn_cancel)) } }
    )
}

/**
 * Card representing a case activity in a timeline.
 *
 * @param activity The activity entity.
 */
@Composable
fun ActivityTimelineCard(activity: CaseActivityEntity) {
    val activityColor = when (activity.activityType) { 
        "Home Visit" -> MaterialTheme.colorScheme.primary
        "Case Review" -> MaterialTheme.colorScheme.secondary
        "Court Hearing" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }
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

/**
 * Dialog for logging a new case activity.
 *
 * @param onDismiss Callback to dismiss.
 * @param onActivityAdded Callback when added.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(onDismiss: () -> Unit, onActivityAdded: (String, String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var activityType by remember { mutableStateOf("Home Visit") }
    var notes by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.activities_log_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.common_label_title)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.common_label_notes)) }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            }
        },
        confirmButton = { TextButton(onClick = { onActivityAdded(title, activityType, getCurrentDate(), "10:00 AM", notes) }, enabled = title.isNotBlank()) { Text(stringResource(R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_btn_cancel)) } }
    )
}

// ==================== MISSING COMPONENT DEFINITIONS ====================

/**
 * Card representing an urgency flag.
 *
 * @param flag The urgency flag entity.
 */
@Composable
fun UrgencyFlagCard(flag: CaseUrgencyFlagEntity) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = flag.flagType ?: "Urgency Flag", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = flag.reason ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            val riskColor = if (flag.riskLevel == "High") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
            Surface(shape = RoundedCornerShape(12.dp), color = riskColor.copy(alpha = 0.1f)) {
                Text(text = flag.riskLevel ?: "Medium", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = riskColor)
            }
        }
    }
}

/**
 * Dialog for adding a new urgency flag.
 *
 * @param onDismiss Callback to dismiss.
 * @param onFlagAdded Callback when added.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUrgencyFlagDialog(onDismiss: () -> Unit, onFlagAdded: (String, String, String) -> Unit) {
    var type by remember { mutableStateOf("Safety Concern") }
    var reason by remember { mutableStateOf("") }
    var risk by remember { mutableStateOf("High") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.urgency_flags_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text(stringResource(R.string.urgency_flags_field_type)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text(stringResource(R.string.common_label_reason)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onFlagAdded(type, reason, risk) }, enabled = type.isNotBlank()) { Text(stringResource(R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_btn_cancel)) } }
    )
}

/**
 * Dialog for adding a new workload tracking entry.
 *
 * @param onDismiss Callback to dismiss.
 * @param onWorkloadAdded Callback when entry is added.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkloadDialog(onDismiss: () -> Unit, onWorkloadAdded: (String, Int, Int, Int, Int, String) -> Unit) {
    var date by remember { mutableStateOf(getCurrentDate()) }
    var cases by remember { mutableStateOf("5") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.workload_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text(stringResource(R.string.common_label_date)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cases, onValueChange = { cases = it }, label = { Text(stringResource(R.string.workload_label_active_cases)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onWorkloadAdded(date, cases.toIntOrNull() ?: 0, 0, 0, 0, "") }) { Text(stringResource(R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_btn_cancel)) } }
    )
}

/**
 * Displays a single row representing a preference.
 *
 * @param label The preference label.
 * @param value The current value.
 * @param icon The icon to represent the preference.
 */
@Composable
fun PreferenceRow(label: String, value: String, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

/**
 * Displays a single row representing a toggleable preference.
 *
 * @param label The preference label.
 * @param enabled Whether the preference is enabled.
 * @param icon The icon for the preference.
 * @param onCheckedChange Callback when the toggle is changed.
 */
@Composable
fun PreferenceToggleRow(label: String, enabled: Boolean, icon: ImageVector, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = enabled, onCheckedChange = onCheckedChange)
    }
}

/**
 * Dialog for editing dashboard preferences.
 *
 * @param currentPrefs The current dashboard preferences entity.
 * @param onDismiss Callback to dismiss.
 * @param onPreferencesSaved Callback when preferences are updated.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPreferencesDialog(currentPrefs: DashboardPreferenceEntity?, onDismiss: () -> Unit, onPreferencesSaved: (String, Boolean, Boolean, Boolean, Boolean, String, Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dash_prefs_edit_desc)) },
        text = { Text(stringResource(R.string.dash_prefs_dialog_text)) },
        confirmButton = { TextButton(onClick = { onPreferencesSaved("Grid", false, true, true, true, "Daily", true) }) { Text(stringResource(R.string.common_btn_save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_btn_cancel)) } }
    )
}
