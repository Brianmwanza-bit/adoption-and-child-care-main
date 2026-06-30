package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
 * Returns the current date in the application's default format.
 *
 * @return Formatted date string.
 */
@Composable
private fun getCurrentDate(): String {
    val format = stringResource(id = R.string.common_date_format)
    val locale = LocalConfiguration.current.locales[0]
    return remember(format, locale) { SimpleDateFormat(format, locale) }.format(Date())
}

/**
 * A simple screen with a top bar and a back button.
 *
 * @param title The title to display in the top bar.
 * @param onBack Callback when the back button is clicked.
 * @param content The content of the screen receiving [PaddingValues].
 * @param padding The padding values provided by the Scaffold to the content lambda.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBackScreen(
    title: String,
    onBack: () -> Unit,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc))
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        content(padding)
    }
}

/**
 * A screen that displays a list of data items with an add button.
 *
 * @param T The type of items in the list.
 * @param title The screen title.
 * @param onBack Callback for back navigation.
 * @param items The list of items to display.
 * @param itemContent Composable to render each item. Receives [listItem].
 * @param onAddClick Callback when the FAB is clicked.
 * @param innerPadding The padding values provided to the root box.
 * @param listItem The current item being rendered in the list.
 */
@Composable
fun <T> DataListScreen(
    title: String,
    onBack: () -> Unit,
    items: List<T>,
    itemContent: @Composable (listItem: T) -> Unit,
    onAddClick: () -> Unit
) {
    SimpleBackScreen(title = title, onBack = onBack) { innerPadding: PaddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding)) {
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(id = R.string.common_no_data))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    items.forEach { listItem: T ->
                        item {
                            itemContent(listItem)
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.align(alignment = Alignment.BottomEnd).padding(all = 16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.common_btn_add))
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
        Row(modifier = Modifier.padding(all = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(size = 32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(width = 16.dp))
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
            }
            if (status != null) {
                val critical = stringResource(id = R.string.status_critical)
                val urgent = stringResource(id = R.string.status_urgent)
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (status == critical || status == urgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
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
            modifier = Modifier.padding(all = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = color)
        }
    }
}

/**
 * A statistical card for deadlines.
 *
 * @param label The label for the statistic.
 * @param count The numeric value to display.
 * @param color The color theme for the card.
 * @param modifier Modifier for layout.
 */
@Composable
fun DeadlineStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    TaskStatCard(label = label, count = count, color = color, modifier = modifier)
}

/**
 * A statistical card for approvals.
 *
 * @param label The label for the statistic.
 * @param count The numeric value to display.
 * @param color The color theme for the card.
 * @param modifier Modifier for layout.
 */
@Composable
fun ApprovalStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    TaskStatCard(label = label, count = count, color = color, modifier = modifier)
}

/**
 * A statistical card for critical dates.
 *
 * @param label The label for the statistic.
 * @param count The numeric value to display.
 * @param color The color theme for the card.
 * @param modifier Modifier for layout.
 */
@Composable
fun CriticalDateStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    TaskStatCard(label = label, count = count, color = color, modifier = modifier)
}

/**
 * A statistical card for activities.
 *
 * @param label The label for the statistic.
 * @param count The numeric value to display.
 * @param color The color theme for the card.
 * @param modifier Modifier for layout.
 */
@Composable
fun ActivityStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    TaskStatCard(label = label, count = count, color = color, modifier = modifier)
}

/**
 * A statistical card for action items.
 *
 * @param label The label for the statistic.
 * @param count The numeric value to display.
 * @param color The color theme for the card.
 * @param modifier Modifier for layout.
 */
@Composable
fun ActionItemStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    TaskStatCard(label = label, count = count, color = color, modifier = modifier)
}

/**
 * A statistical card for urgency flags.
 *
 * @param label The label for the statistic.
 * @param count The numeric value to display.
 * @param color The color theme for the card.
 * @param modifier Modifier for layout.
 */
@Composable
fun UrgencyStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    TaskStatCard(label = label, count = count, color = color, modifier = modifier)
}

/**
 * A statistical card for workload tracking.
 *
 * @param label The label for the statistic.
 * @param count The numeric value to display.
 * @param color The color theme for the card.
 * @param modifier Modifier for layout.
 */
@Composable
fun WorkloadStatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    TaskStatCard(label = label, count = count, color = color, modifier = modifier)
}

// ==================== MAIN SCREENS ====================

/**
 * Screen for managing tasks.
 *
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param innerPadding The padding values provided by Scaffold.
 * @param filterLabel The label of the selected filter.
 * @param taskItem The task item being rendered.
 * @param newStatus The new status of the task.
 * @param taskTitle Title for the new task.
 * @param taskDesc Description for the new task.
 * @param taskPriority Priority for the new task.
 * @param _ Unused parameter.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val tasks: List<TaskEntity> by viewModel.tasks.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(value = false) }
    
    val filterAll = stringResource(id = R.string.tasks_label_all)
    val filterPending = stringResource(id = R.string.tasks_label_pending)
    val filterInProgress = stringResource(id = R.string.tasks_label_in_progress)
    val filterCompleted = stringResource(id = R.string.tasks_label_completed)
    
    var selectedFilter by remember { mutableStateOf(value = filterAll) }
    val filters = listOf(filterAll, filterPending, filterInProgress, filterCompleted)
    
    val filteredTasks = when (selectedFilter) {
        filterPending -> tasks.filter { it.status == filterPending }
        filterInProgress -> tasks.filter { it.status == filterInProgress }
        filterCompleted -> tasks.filter { it.status == filterCompleted }
        else -> tasks
    }
    
    val currentDate = getCurrentDate()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.tasks_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.tasks_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.tasks_add_desc)) }
        }
    ) { innerPadding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp), horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                TaskStatCard(label = filterPending, count = tasks.count { it.status == filterPending }, color = Color(0xFFFF9800), modifier = Modifier.weight(weight = 1f))
                TaskStatCard(label = filterInProgress, count = tasks.count { it.status == filterInProgress }, color = Color(0xFF2196F3), modifier = Modifier.weight(weight = 1f))
                TaskStatCard(label = filterCompleted, count = tasks.count { it.status == filterCompleted }, color = Color(0xFF4CAF50), modifier = Modifier.weight(weight = 1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filterLabel: String ->
                    Tab(selected = selectedFilter == filterLabel, onClick = { selectedFilter = filterLabel }, text = { Text(text = filterLabel) })
                }
            }
            if (filteredTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null, modifier = Modifier.size(size = 64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        Text(text = stringResource(id = R.string.tasks_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 16.dp), verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                    filteredTasks.forEach { taskItem: TaskEntity ->
                        item {
                            TaskCard(task = taskItem, onStatusChange = { newStatus: String -> viewModel.saveTask(taskItem.copy(status = newStatus)) })
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        val pendingStatus = stringResource(id = R.string.status_pending)
        AddTaskDialog(onDismiss = { showAddDialog = false }, onTaskAdded = { taskTitle, taskDesc, taskPriority, _ ->
            viewModel.saveTask(
                TaskEntity(
                    title = taskTitle,
                    description = taskDesc,
                    priority = taskPriority,
                    status = pendingStatus,
                    dueDate = currentDate,
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
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param innerPadding The padding values provided by Scaffold.
 * @param filterLabel The label of the selected filter.
 * @param actionItem The action item being rendered.
 * @param newStatus The new status of the item.
 * @param itemTitle Title for the new item.
 * @param itemPriority Priority for the new item.
 * @param _ Unused parameter.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionItemsScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val items: List<ActionItemEntity> by viewModel.actionItems.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(value = false) }
    
    val filterAll = stringResource(id = R.string.tasks_label_all)
    val filterOpen = stringResource(id = R.string.action_items_label_open)
    val filterInProgress = stringResource(id = R.string.tasks_label_in_progress)
    val filterCompleted = stringResource(id = R.string.tasks_label_completed)
    
    var selectedFilter by remember { mutableStateOf(value = filterAll) }
    val filters = listOf(filterAll, filterOpen, filterInProgress, filterCompleted)
    
    val filteredItems = when (selectedFilter) {
        filterOpen -> items.filter { it.status == filterOpen }
        filterInProgress -> items.filter { it.status == filterInProgress }
        filterCompleted -> items.filter { it.status == filterCompleted }
        else -> items
    }
    
    val currentDate = getCurrentDate()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.action_items_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.action_items_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.action_items_add_desc)) }
        }
    ) { innerPadding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp), horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                ActionItemStatCard(label = filterOpen, count = items.count { it.status == filterOpen }, color = Color(0xFFF44336), modifier = Modifier.weight(weight = 1f))
                ActionItemStatCard(label = filterInProgress, count = items.count { it.status == filterInProgress }, color = Color(0xFFFF9800), modifier = Modifier.weight(weight = 1f))
                ActionItemStatCard(label = filterCompleted, count = items.count { it.status == filterCompleted }, color = Color(0xFF4CAF50), modifier = Modifier.weight(weight = 1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filterLabel: String ->
                    Tab(selected = selectedFilter == filterLabel, onClick = { selectedFilter = filterLabel }, text = { Text(text = filterLabel) })
                }
            }
            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(size = 64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        Text(text = stringResource(id = R.string.action_items_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 16.dp), verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                    filteredItems.forEach { actionItem: ActionItemEntity ->
                        item {
                            ActionItemCard(item = actionItem, onStatusChange = { newStatus: String -> viewModel.saveActionItem(actionItem.copy(status = newStatus)) })
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        val openStatus = stringResource(id = R.string.status_open)
        AddActionItemDialog(onDismiss = { showAddDialog = false }, onItemAdded = { itemTitle, itemPriority, _ ->
            viewModel.saveActionItem(
                ActionItemEntity(
                    title = itemTitle,
                    priority = itemPriority,
                    dueDate = currentDate,
                    relatedCaseId = 1,
                    assigneeId = 1,
                    status = openStatus,
                    createdAt = currentDate
                )
            )
            showAddDialog = false
        })
    }
}

/**
 * Screen for managing permanency plans.
 *
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param planItem The plan item being rendered.
 */
@Composable
fun PermanencyPlansScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val plans: List<PermanencyPlanEntity> by viewModel.permanencyPlans.collectAsStateWithLifecycle(initialValue = emptyList())
    val statusActive = stringResource(id = R.string.status_active_caps)
    val defaultPlanNum = stringResource(id = R.string.common_default_plan_number)
    val goalReunification = stringResource(id = R.string.common_goal_reunification)
    val goalAdoption = stringResource(id = R.string.common_goal_adoption)
    val currentDate = getCurrentDate()
    
    DataListScreen(
        title = stringResource(id = R.string.permanency_plans_title),
        onBack = onBack,
        items = plans,
        itemContent = { planItem: PermanencyPlanEntity ->
            GenericCard(
                title = stringResource(id = R.string.permanency_plans_plan_format, planItem.planNumber ?: ""),
                subtitle = stringResource(id = R.string.permanency_plans_goal_format, planItem.primaryGoal ?: ""),
                status = planItem.status,
                icon = Icons.Default.Map
            )
        },
        onAddClick = {
            viewModel.savePermanencyPlan(
                PermanencyPlanEntity(
                    childId = 1,
                    planNumber = defaultPlanNum,
                    primaryGoal = goalReunification,
                    secondaryGoal = goalAdoption,
                    startDate = currentDate,
                    reviewDate = currentDate,
                    status = statusActive,
                    concurrentPlanning = true
                )
            )
        }
    )
}

/**
 * Screen for tracking case activities.
 *
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param innerPadding The padding values provided by Scaffold.
 * @param filterLabel The label of the selected filter.
 * @param activityItem The activity item being rendered.
 * @param activityTitle Title for the new activity.
 * @param activityKind Kind of activity.
 * @param _ Unused parameter.
 * @param activityNotes Notes for the new activity.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseActivitiesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val activities: List<CaseActivityEntity> by viewModel.caseActivities.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(value = false) }
    
    val filterAll = stringResource(id = R.string.tasks_label_all)
    val filterHomeVisit = stringResource(id = R.string.activities_type_home_visit)
    val filterCaseReview = stringResource(id = R.string.activities_type_case_review)
    val filterCourtHearing = stringResource(id = R.string.activities_type_court_hearing)
    
    var selectedFilter by remember { mutableStateOf(value = filterAll) }
    val filters = listOf(filterAll, filterHomeVisit, filterCaseReview, filterCourtHearing)
    
    val filteredActivities = when (selectedFilter) {
        filterHomeVisit -> activities.filter { it.activityType == filterHomeVisit }
        filterCaseReview -> activities.filter { it.activityType == filterCaseReview }
        filterCourtHearing -> activities.filter { it.activityType == filterCourtHearing }
        else -> activities
    }
    
    val currentDate = getCurrentDate()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.activities_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.activities_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.activities_add_desc)) }
        }
    ) { innerPadding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp), horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                ActivityStatCard(label = stringResource(id = R.string.activities_label_total), count = activities.size, color = Color(0xFF673AB7), modifier = Modifier.weight(weight = 1f))
                ActivityStatCard(label = stringResource(id = R.string.activities_label_this_month), count = activities.count { it.activityDate?.startsWith(currentDate.substring(0, 7)) == true }, color = Color(0xFF2196F3), modifier = Modifier.weight(weight = 1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filterLabel: String ->
                    Tab(selected = selectedFilter == filterLabel, onClick = { selectedFilter = filterLabel }, text = { Text(text = filterLabel) })
                }
            }
            if (filteredActivities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(size = 64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        Text(text = stringResource(id = R.string.activities_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 16.dp), verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                    filteredActivities.forEach { activityItem: CaseActivityEntity ->
                        item {
                            ActivityTimelineCard(activity = activityItem)
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        val locationLabel = stringResource(id = R.string.location_office)
        val outcomeLabel = stringResource(id = R.string.status_completed)
        val defaultActivityTime = stringResource(id = R.string.common_default_activity_time)
        AddActivityDialog(onDismiss = { showAddDialog = false }, onActivityAdded = { activityTitle, activityKind, _, _, activityNotes ->
            viewModel.saveCaseActivity(
                CaseActivityEntity(
                    caseId = 1,
                    activityType = activityKind,
                    activityDate = currentDate,
                    activityTime = defaultActivityTime,
                    title = activityTitle,
                    notes = activityNotes,
                    caseworkerId = 1,
                    location = locationLabel,
                    outcome = outcomeLabel
                )
            )
            showAddDialog = false
        })
    }
}

/**
 * Screen for managing case deadlines.
 *
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param innerPadding The padding values provided by Scaffold.
 * @param filterLabel The label of the selected filter.
 * @param deadlineItem The deadline item being rendered.
 * @param newStatus The new status of the deadline.
 * @param dTitle Title for the new deadline.
 * @param dDate Due date for the new deadline.
 * @param dDesc Description for the new deadline.
 * @param dPriority Priority for the new deadline.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDeadlinesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val deadlines: List<CaseDeadlineEntity> by viewModel.deadlines.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(value = false) }
    
    val filterAll = stringResource(id = R.string.tasks_label_all)
    val filterPending = stringResource(id = R.string.tasks_label_pending)
    val filterInProgress = stringResource(id = R.string.tasks_label_in_progress)
    val filterCompleted = stringResource(id = R.string.tasks_label_completed)
    val filterOverdue = stringResource(id = R.string.deadlines_label_overdue)
    
    val highPriority = stringResource(id = R.string.status_high)
    
    var selectedFilter by remember { mutableStateOf(value = filterAll) }
    val filters = listOf(filterAll, filterPending, filterInProgress, filterCompleted, filterOverdue)
    
    val filteredDeadlines = when (selectedFilter) {
        filterPending -> deadlines.filter { it.status == filterPending }
        filterInProgress -> deadlines.filter { it.status == filterInProgress }
        filterCompleted -> deadlines.filter { it.status == filterCompleted }
        filterOverdue -> deadlines.filter { it.priority == highPriority && it.status != filterCompleted }
        else -> deadlines
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.deadlines_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.deadlines_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.deadlines_add_desc)) }
        }
    ) { innerPadding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp), horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                DeadlineStatCard(label = filterPending, count = deadlines.count { it.status == filterPending }, color = Color(0xFFFF9800), modifier = Modifier.weight(weight = 1f))
                DeadlineStatCard(label = stringResource(id = R.string.deadlines_label_high_priority), count = deadlines.count { it.priority == highPriority }, color = Color(0xFFF44336), modifier = Modifier.weight(weight = 1f))
                DeadlineStatCard(label = filterCompleted, count = deadlines.count { it.status == filterCompleted }, color = Color(0xFF4CAF50), modifier = Modifier.weight(weight = 1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filterLabel: String ->
                    Tab(selected = selectedFilter == filterLabel, onClick = { selectedFilter = filterLabel }, text = { Text(text = filterLabel) })
                }
            }
            if (filteredDeadlines.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(size = 64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        Text(text = stringResource(id = R.string.deadlines_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 16.dp), verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                    filteredDeadlines.forEach { deadlineItem: CaseDeadlineEntity ->
                        item {
                            DeadlineCard(deadline = deadlineItem, onStatusChange = { newStatus: String -> viewModel.saveDeadline(deadlineItem.copy(status = newStatus)) })
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        val requiredType = stringResource(id = R.string.status_required)
        val pendingStatus = stringResource(id = R.string.status_pending)
        AddDeadlineDialog(onDismiss = { showAddDialog = false }, onDeadlineAdded = { dTitle, dDate, dDesc, dPriority ->
            viewModel.saveDeadline(
                CaseDeadlineEntity(
                    caseId = 1,
                    title = dTitle,
                    dueDate = dDate,
                    description = dDesc,
                    deadlineType = requiredType,
                    status = pendingStatus,
                    priority = dPriority
                )
            )
            showAddDialog = false
        })
    }
}

/**
 * Screen for managing case approvals.
 *
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param innerPadding The padding values provided by Scaffold.
 * @param filterLabel The label of the selected filter.
 * @param approvalItem The approval item being rendered.
 * @param newStatus The new status of the approval.
 * @param appType Type of the new approval request.
 * @param _ Unused parameter.
 * @param appDesc Description for the new approval request.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseApprovalsScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val approvals: List<CaseApprovalEntity> by viewModel.approvals.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(value = false) }
    
    val filterAll = stringResource(id = R.string.tasks_label_all)
    val filterPending = stringResource(id = R.string.status_pending)
    val filterApproved = stringResource(id = R.string.status_approved)
    val filterRejected = stringResource(id = R.string.status_rejected)
    
    var selectedFilter by remember { mutableStateOf(value = filterAll) }
    val filters = listOf(filterAll, filterPending, filterApproved, filterRejected)
    
    val filteredApprovals = when (selectedFilter) {
        filterPending -> approvals.filter { it.status == filterPending }
        filterApproved -> approvals.filter { it.status == filterApproved }
        filterRejected -> approvals.filter { it.status == filterRejected }
        else -> approvals
    }
    
    val currentDate = getCurrentDate()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.approvals_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.approvals_request_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.approvals_request_desc)) }
        }
    ) { innerPadding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp), horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                ApprovalStatCard(label = filterPending, count = approvals.count { it.status == filterPending }, color = Color(0xFFFF9800), modifier = Modifier.weight(weight = 1f))
                ApprovalStatCard(label = filterApproved, count = approvals.count { it.status == filterApproved }, color = Color(0xFF4CAF50), modifier = Modifier.weight(weight = 1f))
                ApprovalStatCard(label = filterRejected, count = approvals.count { it.status == filterRejected }, color = Color(0xFFF44336), modifier = Modifier.weight(weight = 1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filterLabel: String ->
                    Tab(selected = selectedFilter == filterLabel, onClick = { selectedFilter = filterLabel }, text = { Text(text = filterLabel) })
                }
            }
            if (filteredApprovals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(size = 64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        Text(text = stringResource(id = R.string.approvals_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 16.dp), verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                    filteredApprovals.forEach { approvalItem: CaseApprovalEntity ->
                        item {
                            ApprovalCard(approval = approvalItem, onStatusChange = { newStatus: String -> viewModel.saveApproval(approvalItem.copy(status = newStatus)) })
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        val pendingStatus = stringResource(id = R.string.status_pending)
        AddApprovalDialog(onDismiss = { showAddDialog = false }, onApprovalAdded = { appType, _, appDesc ->
            viewModel.saveApproval(
                CaseApprovalEntity(
                    caseId = 1,
                    approvalType = appType,
                    status = pendingStatus,
                    submittedBy = 1,
                    submissionComments = appDesc,
                    submittedDate = currentDate
                )
            )
            showAddDialog = false
        })
    }
}

/**
 * Screen for displaying case urgency flags.
 *
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param innerPadding The padding values provided by Scaffold.
 * @param flagItem The urgency flag item being rendered.
 * @param fType Type for the new flag.
 * @param fReason Reason for the new flag.
 * @param fRisk Risk level for the new flag.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseUrgencyFlagsScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val flags: List<CaseUrgencyFlagEntity> by viewModel.urgencyFlags.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(value = false) }
    
    val currentDate = getCurrentDate()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.urgency_flags_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.urgency_flags_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = Color(0xFFF44336)) { Icon(imageVector = Icons.Default.Warning, contentDescription = stringResource(id = R.string.urgency_flags_add_desc)) }
        }
    ) { innerPadding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding)) {
            val highRisk = stringResource(id = R.string.status_high)
            val mediumRisk = stringResource(id = R.string.status_medium)
            val highRiskCount = flags.count { it.riskLevel == highRisk }
            if (highRiskCount > 0) {
                Card(modifier = Modifier.fillMaxWidth().padding(all = 16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336).copy(alpha = 0.1f))) {
                    Row(modifier = Modifier.padding(all = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF44336), modifier = Modifier.size(size = 24.dp))
                        Column {
                            Text(text = stringResource(id = R.string.urgency_flags_banner_text, highRiskCount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
                            Text(text = stringResource(id = R.string.urgency_flags_banner_subtext), style = MaterialTheme.typography.bodySmall, color = Color(0xFFF44336))
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                UrgencyStatCard(label = stringResource(id = R.string.urgency_flags_label_total), count = flags.size, color = Color(0xFF673AB7), modifier = Modifier.weight(weight = 1f))
                UrgencyStatCard(label = stringResource(id = R.string.urgency_flags_label_high_risk), count = highRiskCount, color = Color(0xFFF44336), modifier = Modifier.weight(weight = 1f))
                UrgencyStatCard(label = stringResource(id = R.string.urgency_flags_label_medium_risk), count = flags.count { it.riskLevel == mediumRisk }, color = Color(0xFFFF9800), modifier = Modifier.weight(weight = 1f))
            }
            if (flags.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.PriorityHigh, contentDescription = null, modifier = Modifier.size(size = 64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        Text(text = stringResource(id = R.string.urgency_flags_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 16.dp), verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                    flags.forEach { flagItem: CaseUrgencyFlagEntity ->
                        item {
                            UrgencyFlagCard(flag = flagItem)
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddUrgencyFlagDialog(onDismiss = { showAddDialog = false }, onFlagAdded = { fType, fReason, fRisk ->
            viewModel.saveUrgencyFlag(
                CaseUrgencyFlagEntity(
                    caseId = 1,
                    flagType = fType,
                    reason = fReason,
                    riskLevel = fRisk,
                    createdAt = currentDate,
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
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param innerPadding The padding values provided by Scaffold.
 * @param filterLabel The label of the selected filter.
 * @param dateItem The critical date item being rendered.
 * @param cDateType Type for the new critical date.
 * @param _ Unused parameter.
 * @param cDesc Description for the new critical date.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriticalDatesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val dates: List<CriticalDateEntity> by viewModel.criticalDates.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(value = false) }
    
    val filterAll = stringResource(id = R.string.tasks_label_all)
    val filterUpcoming = stringResource(id = R.string.critical_dates_label_upcoming)
    val filterCompleted = stringResource(id = R.string.tasks_label_completed)
    
    var selectedFilter by remember { mutableStateOf(value = filterAll) }
    val filters = listOf(filterAll, filterUpcoming, filterCompleted)
    
    val filteredDates = when (selectedFilter) {
        filterUpcoming -> dates.filter { !it.isCompleted }
        filterCompleted -> dates.filter { it.isCompleted }
        else -> dates
    }
    
    val currentDate = getCurrentDate()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.critical_dates_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.critical_dates_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.critical_dates_add_desc)) }
        }
    ) { innerPadding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp), horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                CriticalDateStatCard(label = stringResource(id = R.string.urgency_flags_label_total), count = dates.size, color = Color(0xFF673AB7), modifier = Modifier.weight(weight = 1f))
                CriticalDateStatCard(label = filterUpcoming, count = dates.count { !it.isCompleted }, color = Color(0xFFFF9800), modifier = Modifier.weight(weight = 1f))
                CriticalDateStatCard(label = filterCompleted, count = dates.count { it.isCompleted }, color = Color(0xFF4CAF50), modifier = Modifier.weight(weight = 1f))
            }
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEach { filterLabel: String ->
                    Tab(selected = selectedFilter == filterLabel, onClick = { selectedFilter = filterLabel }, text = { Text(text = filterLabel) })
                }
            }
            if (filteredDates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Event, contentDescription = null, modifier = Modifier.size(size = 64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        Text(text = stringResource(id = R.string.critical_dates_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 16.dp), verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                    filteredDates.forEach { dateItem: CriticalDateEntity ->
                        item {
                            CriticalDateCard(date = dateItem, onToggleComplete = { viewModel.saveCriticalDate(dateItem.copy(isCompleted = !dateItem.isCompleted)) })
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddCriticalDateDialog(onDismiss = { showAddDialog = false }, onDateAdded = { cDateType, _, cDesc ->
            viewModel.saveCriticalDate(
                CriticalDateEntity(
                    childId = 1,
                    dateType = cDateType,
                    eventDate = currentDate,
                    notes = cDesc
                )
            )
            showAddDialog = false
        })
    }
}

/**
 * Screen providing a dashboard for caseworker workload tracking.
 *
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param padding The padding values provided by Scaffold.
 * @param pair A pair of label and value for workload breakdown.
 * @param dateValue Date for the new workload entry.
 * @param casesVal Number of active cases for the new workload entry.
 * @param _ Unused parameter.
 * @param notesVal Notes for the new workload entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkloadDashboardScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val workload: List<WorkloadTrackingEntity> by viewModel.workload.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(value = false) }
    val latestWorkload = workload.maxByOrNull { it.trackingDate }
    
    val currentDate = getCurrentDate()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.workload_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.workload_add_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(imageVector = Icons.Default.DonutLarge, contentDescription = stringResource(id = R.string.workload_add_desc)) }
        }
    ) { padding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues = padding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(all = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(all = 16.dp)) {
                    Text(text = stringResource(id = R.string.workload_today_overview), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = latestWorkload?.trackingDate ?: currentDate, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                    WorkloadStatCard(label = stringResource(id = R.string.workload_label_active_cases), count = latestWorkload?.totalActiveCases ?: 0, color = Color(0xFF2196F3), modifier = Modifier.weight(weight = 1f))
                    WorkloadStatCard(label = stringResource(id = R.string.workload_label_pending_tasks), count = latestWorkload?.tasksPending ?: 0, color = Color(0xFFFF9800), modifier = Modifier.weight(weight = 1f))
                }
                Spacer(modifier = Modifier.height(height = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                    WorkloadStatCard(label = stringResource(id = R.string.workload_label_pending_approvals), count = latestWorkload?.approvalsPending ?: 0, color = Color(0xFF673AB7), modifier = Modifier.weight(weight = 1f))
                    WorkloadStatCard(label = stringResource(id = R.string.workload_label_overdue), count = latestWorkload?.deadlinesOverdue ?: 0, color = Color(0xFFF44336), modifier = Modifier.weight(weight = 1f))
                }
            }
            if (latestWorkload != null) {
                Card(modifier = Modifier.fillMaxWidth().padding(all = 16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(modifier = Modifier.padding(all = 16.dp)) {
                        Text(text = stringResource(id = R.string.workload_breakdown_header), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                        listOf(
                            stringResource(id = R.string.workload_label_visits_scheduled) to latestWorkload.homeVisitsScheduled.toString(),
                            stringResource(id = R.string.workload_label_visits_completed) to latestWorkload.homeVisitsCompleted.toString(),
                            stringResource(id = R.string.workload_label_reports_submitted) to latestWorkload.reportsSubmitted.toString(),
                            stringResource(id = R.string.workload_label_productivity) to "${latestWorkload.productivityScore}%"
                        ).forEach { pair: Pair<String, String> ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = pair.first, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Text(text = pair.second, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddWorkloadDialog(onDismiss = { showAddDialog = false }, onWorkloadAdded = { dateValue, casesVal, _, _, _, notesVal ->
            viewModel.saveWorkload(
                WorkloadTrackingEntity(
                    caseworkerId = 1,
                    trackingDate = dateValue,
                    totalActiveCases = casesVal,
                    notes = notesVal
                )
            )
            showAddDialog = false
        })
    }
}

/**
 * Screen for viewing messages related to caseworkers.
 *
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param messageItem The message item being rendered.
 */
@Composable
fun WorkerMessagesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val messages: List<WorkerMessageEntity> by viewModel.workerMessages.collectAsStateWithLifecycle(initialValue = emptyList())
    val readLabel = stringResource(id = R.string.worker_messages_label_read)
    val newLabel = stringResource(id = R.string.worker_messages_label_new)
    
    val msgTitle = stringResource(id = R.string.common_msg_title_meeting)
    val msgContent = stringResource(id = R.string.common_msg_content_reminder)
    val currentDate = getCurrentDate()
    
    DataListScreen(
        title = stringResource(id = R.string.worker_messages_title),
        onBack = onBack,
        items = messages,
        itemContent = { messageItem: WorkerMessageEntity ->
            GenericCard(title = messageItem.title, subtitle = messageItem.content ?: "", status = if (messageItem.isRead) readLabel else newLabel, icon = Icons.AutoMirrored.Filled.Message)
        },
        onAddClick = {
            viewModel.saveWorkerMessage(
                WorkerMessageEntity(
                    senderId = 1,
                    recipientId = 2,
                    caseId = 1,
                    title = msgTitle,
                    content = msgContent,
                    createdAt = currentDate
                )
            )
        }
    )
}

/**
 * Screen for configuring dashboard UI preferences.
 *
 * @param onBack Callback when the back button is clicked.
 * @param viewModel The ViewModel for case tools.
 * @param padding The padding values provided by Scaffold.
 * @param newValue The new value for a preference toggle.
 * @param isChecked The new state of a preference toggle.
 * @param layout The selected layout type.
 * @param isDark Whether dark mode is enabled.
 * @param stats Whether stats are shown.
 * @param quick Whether quick actions are shown.
 * @param recent Whether recent activity is shown.
 * @param freq Update frequency preference.
 * @param push Whether push notifications are enabled.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardPreferencesScreen(onBack: () -> Unit = {}, viewModel: CaseToolsViewModel = hiltViewModel<CaseToolsViewModel>()) {
    val prefs: List<DashboardPreferenceEntity> by viewModel.dashboardPreferences.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(value = false) }
    val latestPrefs = prefs.maxByOrNull { it.userId }
    
    val layoutGrid = stringResource(id = R.string.dash_prefs_layout_grid)
    val freqDaily = stringResource(id = R.string.dash_prefs_freq_daily)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.dash_prefs_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc)) } },
                actions = { IconButton(onClick = { showAddDialog = true }) { Icon(imageVector = Icons.Default.Settings, contentDescription = stringResource(id = R.string.dash_prefs_edit_desc)) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(imageVector = Icons.Default.Tune, contentDescription = stringResource(id = R.string.dash_prefs_config_desc)) }
        }
    ) { padding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues = padding)) {
            if (latestPrefs == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(size = 64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        Text(text = stringResource(id = R.string.dash_prefs_no_found), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 16.dp), verticalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Column(modifier = Modifier.padding(all = 16.dp)) {
                                Text(text = stringResource(id = R.string.dash_prefs_layout_header), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                                PreferenceRow(label = stringResource(id = R.string.dash_prefs_label_layout_type), value = latestPrefs.layoutType ?: layoutGrid, icon = Icons.Default.Dashboard)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceRow(label = stringResource(id = R.string.dash_prefs_label_update_freq), value = latestPrefs.updateFrequency ?: freqDaily, icon = Icons.Default.Update)
                            }
                        }
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Column(modifier = Modifier.padding(all = 16.dp)) {
                                Text(text = stringResource(id = R.string.dash_prefs_display_header), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                                PreferenceToggleRow(
                                    label = stringResource(id = R.string.dash_prefs_label_dark_mode), 
                                    enabled = latestPrefs.darkMode, 
                                    icon = Icons.Default.DarkMode,
                                    onCheckedChange = { isChecked: Boolean ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(darkMode = isChecked))
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceToggleRow(
                                    label = stringResource(id = R.string.dash_prefs_label_show_stats), 
                                    enabled = latestPrefs.showStats, 
                                    icon = Icons.Default.BarChart,
                                    onCheckedChange = { isChecked: Boolean ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(showStats = isChecked))
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceToggleRow(
                                    label = stringResource(id = R.string.dash_prefs_label_show_actions), 
                                    enabled = latestPrefs.showQuickActions, 
                                    icon = Icons.Default.Favorite,
                                    onCheckedChange = { isChecked: Boolean ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(showQuickActions = isChecked))
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                PreferenceToggleRow(
                                    label = stringResource(id = R.string.dash_prefs_label_show_activity), 
                                    enabled = latestPrefs.showRecentActivity,
                                    icon = Icons.Default.History,
                                    onCheckedChange = { isChecked: Boolean ->
                                        viewModel.saveDashboardPreference(latestPrefs.copy(showRecentActivity = isChecked))
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
        EditPreferencesDialog(currentPrefs = latestPrefs, onDismiss = { showAddDialog = false }, onPreferencesSaved = { layout, isDark, stats, quick, recent, freq, push ->
            viewModel.saveDashboardPreference(
                DashboardPreferenceEntity(
                    userId = 1,
                    layoutType = layout,
                    darkMode = isDark,
                    showStats = stats,
                    showQuickActions = quick,
                    showRecentActivity = recent,
                    updateFrequency = freq,
                    notificationsEnabled = push
                )
            )
            showAddDialog = false
        })
    }
}

/**
 * Screen for managing and viewing system notifications.
 *
 * @param onBack Callback when the back button is clicked.
 * @param list The list of notifications received.
 * @param padding The padding values provided by Scaffold.
 * @param msg The error message string.
 * @param notificationItem The notification item being rendered.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val authManager = remember { AuthManager(context) }
    val repository = remember { NotificationRepositoryImpl(db.notificationDao(), apiService, authManager) }
    var notifications by remember { mutableStateOf(value = emptyList<NotificationEntity>()) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(value = false) }
    var errorMessage by remember { mutableStateOf<String?>(value = null) }
    
    LaunchedEffect(Unit) {
        db.notificationDao().observeAll().collectLatest { list: List<NotificationEntity> -> notifications = list }
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
            TopAppBar(title = { Text(text = stringResource(id = R.string.notifications_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back_desc)) } })
        }
    ) { padding: PaddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues = padding)) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.Center))
            else if (errorMessage != null) {
                errorMessage?.let { msg: String ->
                    Text(text = stringResource(id = R.string.notifications_error_format, msg), color = MaterialTheme.colorScheme.error, modifier = Modifier.align(alignment = Alignment.Center))
                }
            } else if (notifications.isEmpty()) Text(text = stringResource(id = R.string.notifications_no_found), modifier = Modifier.align(alignment = Alignment.Center))
            else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(all = 16.dp), verticalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                    notifications.forEach { notificationItem: NotificationEntity ->
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(all = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(weight = 1f)) {
                                        Text(text = notificationItem.title, style = MaterialTheme.typography.titleMedium)
                                        Text(text = notificationItem.message, style = MaterialTheme.typography.bodySmall)
                                    }
                                    if (!notificationItem.isRead) {
                                        IconButton(onClick = { scope.launch { val token = authManager.getAuthToken() ?: ""; if (token.isNotEmpty()) repository.markAsRead(notificationItem.notificationId, token) } }) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = stringResource(id = R.string.notifications_mark_read_desc), tint = MaterialTheme.colorScheme.primary)
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
}

// ==================== SHARED SCREEN COMPONENTS ====================

/**
 * A card representing a single task item.
 *
 * @param task The task entity to display.
 * @param onStatusChange Callback when the status of the task is changed.
 * @param statusLabel The label of the status option selected from the menu.
 */
@Composable
fun TaskCard(task: TaskEntity, onStatusChange: (newStatus: String) -> Unit) {
    var expanded by remember { mutableStateOf(value = false) }
    val pending = stringResource(id = R.string.status_pending)
    val inProgress = stringResource(id = R.string.status_in_progress)
    val completed = stringResource(id = R.string.status_completed)
    val high = stringResource(id = R.string.status_high)
    val medium = stringResource(id = R.string.status_medium)
    val low = stringResource(id = R.string.status_low)

    val statusColor = when (task.status) { 
        pending -> MaterialTheme.colorScheme.error
        inProgress -> MaterialTheme.colorScheme.primary
        completed -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    val priorityColor = when (task.priority) { 
        high -> MaterialTheme.colorScheme.error
        medium -> MaterialTheme.colorScheme.secondary
        low -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(weight = 1f)) {
                    Text(text = task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = task.description ?: stringResource(id = R.string.common_no_desc), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.common_options_desc)) }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf(pending, inProgress, completed).forEach { statusLabel: String ->
                            DropdownMenuItem(text = { Text(text = statusLabel) }, onClick = { onStatusChange(statusLabel); expanded = false })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(height = 12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                Surface(shape = RoundedCornerShape(size = 12.dp), color = priorityColor.copy(alpha = 0.1f)) { Text(text = task.priority ?: medium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = priorityColor) }
                Surface(shape = RoundedCornerShape(size = 12.dp), color = statusColor.copy(alpha = 0.1f)) { Text(text = task.status ?: pending, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor) }
            }
        }
    }
}

/**
 * Dialog for adding a new task.
 *
 * @param onDismiss Callback to dismiss the dialog.
 * @param onTaskAdded Callback when a task is successfully added.
 * @param newValue The new text value for an input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onTaskAdded: (title: String, description: String, priority: String, dueDate: String) -> Unit) {
    var title by remember { mutableStateOf(value = "") }
    var description by remember { mutableStateOf(value = "") }
    val mediumPriority = stringResource(id = R.string.status_medium)
    var priority by remember { mutableStateOf(value = mediumPriority) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.tasks_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                OutlinedTextField(
                    value = title, 
                    onValueChange = { newValue: String -> title = newValue }, 
                    label = { Text(text = stringResource(id = R.string.common_label_title)) }, 
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description, 
                    onValueChange = { newValue: String -> description = newValue }, 
                    label = { Text(text = stringResource(id = R.string.common_label_desc)) }, 
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { TextButton(onClick = { onTaskAdded(title, description, priority, "") }, enabled = title.isNotBlank()) { Text(text = stringResource(id = R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.common_btn_cancel)) } }
    )
}

/**
 * A card representing a case deadline.
 *
 * @param deadline The deadline entity to display.
 * @param onStatusChange Callback when the status of the deadline is changed.
 * @param statusLabel The label of the status option selected from the menu.
 */
@Composable
fun DeadlineCard(deadline: CaseDeadlineEntity, onStatusChange: (newStatus: String) -> Unit) {
    var expanded by remember { mutableStateOf(value = false) }
    val pending = stringResource(id = R.string.status_pending)
    val inProgress = stringResource(id = R.string.status_in_progress)
    val completed = stringResource(id = R.string.status_completed)

    val statusColor = when (deadline.status) { 
        pending -> MaterialTheme.colorScheme.error
        inProgress -> MaterialTheme.colorScheme.primary
        completed -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(weight = 1f)) {
                    Text(text = deadline.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = deadline.description ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.common_options_desc)) }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf(pending, inProgress, completed).forEach { statusLabel: String ->
                            DropdownMenuItem(text = { Text(text = statusLabel) }, onClick = { onStatusChange(statusLabel); expanded = false })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(height = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(size = 12.dp), color = statusColor.copy(alpha = 0.1f)) { Text(text = deadline.status ?: pending, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor) }
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(text = stringResource(id = R.string.search_due_label, deadline.dueDate), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

/**
 * Dialog for adding a new deadline.
 *
 * @param onDismiss Callback to dismiss the dialog.
 * @param onDeadlineAdded Callback when a deadline is successfully added.
 * @param newValue The new text value for an input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeadlineDialog(onDismiss: () -> Unit, onDeadlineAdded: (title: String, dueDate: String, description: String, priority: String) -> Unit) {
    var title by remember { mutableStateOf(value = "") }
    var dueDate by remember { mutableStateOf(value = "") }
    var description by remember { mutableStateOf(value = "") }
    val medium = stringResource(id = R.string.status_medium)
    
    val currentDate = getCurrentDate()
    LaunchedEffect(Unit) { dueDate = currentDate }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.deadlines_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                OutlinedTextField(value = title, onValueChange = { newValue: String -> title = newValue }, label = { Text(text = stringResource(id = R.string.common_label_title)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dueDate, onValueChange = { newValue: String -> dueDate = newValue }, label = { Text(text = stringResource(id = R.string.deadlines_due_date_field)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { newValue: String -> description = newValue }, label = { Text(text = stringResource(id = R.string.common_label_desc)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onDeadlineAdded(title, dueDate, description, medium) }, enabled = title.isNotBlank()) { Text(text = stringResource(id = R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.common_btn_cancel)) } }
    )
}

/**
 * A card representing a case approval request.
 *
 * @param approval The approval entity to display.
 * @param onStatusChange Callback when the status of the approval is changed.
 * @param statusLabel The label of the status option selected from the menu.
 */
@Composable
fun ApprovalCard(approval: CaseApprovalEntity, onStatusChange: (newStatus: String) -> Unit) {
    var expanded by remember { mutableStateOf(value = false) }
    val pending = stringResource(id = R.string.status_pending)
    val approved = stringResource(id = R.string.status_approved)
    val rejected = stringResource(id = R.string.status_rejected)

    val statusColor = when (approval.status) { 
        pending -> MaterialTheme.colorScheme.error
        approved -> MaterialTheme.colorScheme.tertiary
        rejected -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(weight = 1f)) {
                    Text(text = approval.approvalType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = stringResource(id = R.string.approvals_requested_format, approval.submittedDate ?: ""), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.common_options_desc)) }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf(pending, approved, rejected).forEach { statusLabel: String ->
                            DropdownMenuItem(text = { Text(text = statusLabel) }, onClick = { onStatusChange(statusLabel); expanded = false })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(height = 8.dp))
            Surface(shape = RoundedCornerShape(size = 12.dp), color = statusColor.copy(alpha = 0.1f)) { Text(text = approval.status ?: pending, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor) }
        }
    }
}

/**
 * Dialog for requesting a new case approval.
 *
 * @param onDismiss Callback to dismiss the dialog.
 * @param onApprovalAdded Callback when an approval request is successfully added.
 * @param newValue The new text value for an input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddApprovalDialog(onDismiss: () -> Unit, onApprovalAdded: (approvalType: String, priority: String, description: String) -> Unit) {
    val defaultAppType = stringResource(id = R.string.approvals_default_type)
    var approvalType by remember { mutableStateOf(value = defaultAppType) }
    var description by remember { mutableStateOf(value = "") }
    val medium = stringResource(id = R.string.status_medium)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.approvals_request_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                OutlinedTextField(value = approvalType, onValueChange = { newValue: String -> approvalType = newValue }, label = { Text(text = stringResource(id = R.string.approvals_field_type)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { newValue: String -> description = newValue }, label = { Text(text = stringResource(id = R.string.common_label_desc)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onApprovalAdded(approvalType, medium, description) }, enabled = approvalType.isNotBlank()) { Text(text = stringResource(id = R.string.common_btn_submit)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.common_btn_cancel)) } }
    )
}

/**
 * A card representing a critical case date.
 *
 * @param date The critical date entity to display.
 * @param onToggleComplete Callback when the completion status of the date is toggled.
 * @param _ Unused parameter from checked change.
 */
@Composable
fun CriticalDateCard(date: CriticalDateEntity, onToggleComplete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(all = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(text = date.dateType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = date.eventDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                if (date.notes != null) Text(text = date.notes, style = MaterialTheme.typography.bodyMedium)
            }
            Checkbox(checked = date.isCompleted, onCheckedChange = { _: Boolean -> onToggleComplete() })
        }
    }
}

/**
 * Dialog for adding a new critical date.
 *
 * @param onDismiss Callback to dismiss the dialog.
 * @param onDateAdded Callback when a critical date is successfully added.
 * @param newValue The new text value for an input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCriticalDateDialog(onDismiss: () -> Unit, onDateAdded: (dateType: String, eventDate: String, description: String) -> Unit) {
    val defaultType = stringResource(id = R.string.common_court_hearing)
    var dateType by remember { mutableStateOf(value = defaultType) }
    var eventDate by remember { mutableStateOf(value = "") }
    var description by remember { mutableStateOf(value = "") }
    
    val currentDate = getCurrentDate()
    LaunchedEffect(Unit) { eventDate = currentDate }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.critical_dates_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                OutlinedTextField(value = dateType, onValueChange = { newValue: String -> dateType = newValue }, label = { Text(text = stringResource(id = R.string.critical_dates_field_type)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = eventDate, onValueChange = { newValue: String -> eventDate = newValue }, label = { Text(text = stringResource(id = R.string.common_label_date)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { newValue: String -> description = newValue }, label = { Text(text = stringResource(id = R.string.common_label_desc)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onDateAdded(dateType, eventDate, description) }, enabled = dateType.isNotBlank()) { Text(text = stringResource(id = R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.common_btn_cancel)) } }
    )
}

/**
 * A card representing an action item.
 *
 * @param item The action item entity to display.
 * @param onStatusChange Callback when the status of the action item is changed.
 * @param statusLabel The label of the status option selected from the menu.
 */
@Composable
fun ActionItemCard(item: ActionItemEntity, onStatusChange: (newStatus: String) -> Unit) {
    var expanded by remember { mutableStateOf(value = false) }
    val open = stringResource(id = R.string.status_open)
    val inProgress = stringResource(id = R.string.status_in_progress)
    val completed = stringResource(id = R.string.status_completed)
    val high = stringResource(id = R.string.status_high)
    val medium = stringResource(id = R.string.status_medium)
    val low = stringResource(id = R.string.status_low)

    val statusColor = when (item.status) { 
        open -> MaterialTheme.colorScheme.error
        inProgress -> MaterialTheme.colorScheme.primary
        completed -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    val priorityColor = when (item.priority) { 
        high -> MaterialTheme.colorScheme.error
        medium -> MaterialTheme.colorScheme.secondary
        low -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(weight = 1f)) {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    val dueLabel = stringResource(id = R.string.search_due_label, item.dueDate ?: stringResource(id = R.string.common_no_due_date))
                    Text(text = dueLabel, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { expanded = true }) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.common_options_desc)) }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf(open, inProgress, completed).forEach { statusLabel: String ->
                            DropdownMenuItem(text = { Text(text = statusLabel) }, onClick = { onStatusChange(statusLabel); expanded = false })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(height = 12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                Surface(shape = RoundedCornerShape(size = 12.dp), color = priorityColor.copy(alpha = 0.1f)) { Text(text = item.priority ?: medium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = priorityColor) }
                Surface(shape = RoundedCornerShape(size = 12.dp), color = statusColor.copy(alpha = 0.1f)) { Text(text = item.status ?: open, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor) }
            }
        }
    }
}

/**
 * Dialog for adding a new action item.
 *
 * @param onDismiss Callback to dismiss the dialog.
 * @param onItemAdded Callback when an action item is successfully added.
 * @param newValue The new text value for an input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActionItemDialog(onDismiss: () -> Unit, onItemAdded: (title: String, priority: String, dueDate: String) -> Unit) {
    var title by remember { mutableStateOf(value = "") }
    val mediumPriority = stringResource(id = R.string.status_medium)
    var priority by remember { mutableStateOf(value = mediumPriority) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.action_items_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                OutlinedTextField(value = title, onValueChange = { newValue: String -> title = newValue }, label = { Text(text = stringResource(id = R.string.common_label_title)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onItemAdded(title, priority, "") }, enabled = title.isNotBlank()) { Text(text = stringResource(id = R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.common_btn_cancel)) } }
    )
}

/**
 * A card representing a case activity in a timeline format.
 *
 * @param activity The case activity entity to display.
 */
@Composable
fun ActivityTimelineCard(activity: CaseActivityEntity) {
    val homeVisit = stringResource(id = R.string.activities_type_home_visit)
    val caseReview = stringResource(id = R.string.activities_type_case_review)
    val courtHearing = stringResource(id = R.string.activities_type_court_hearing)
    val activityColor = when (activity.activityType) { 
        homeVisit -> MaterialTheme.colorScheme.primary
        caseReview -> MaterialTheme.colorScheme.secondary
        courtHearing -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp), horizontalArrangement = Arrangement.spacedBy(space = 12.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(size = 12.dp).background(color = activityColor, shape = CircleShape))
                Spacer(modifier = Modifier.height(height = 4.dp))
                Box(modifier = Modifier.width(width = 2.dp).height(height = 40.dp).background(activityColor.copy(alpha = 0.3f)))
            }
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(text = activity.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = activity.activityDate ?: stringResource(id = R.string.dashboard_no_date), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(text = activity.activityTime ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(height = 8.dp))
                Surface(shape = RoundedCornerShape(size = 12.dp), color = activityColor.copy(alpha = 0.1f)) { Text(text = activity.activityType, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = activityColor) }
                if (activity.notes?.isNotEmpty() == true) { Spacer(modifier = Modifier.height(height = 8.dp)); Text(text = activity.notes, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
            }
        }
    }
}

/**
 * Dialog for logging a new activity.
 *
 * @param onDismiss Callback to dismiss the dialog.
 * @param onActivityAdded Callback when an activity is successfully added.
 * @param newValue The new text value for an input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(onDismiss: () -> Unit, onActivityAdded: (title: String, activityType: String, activityDate: String, activityTime: String, notes: String) -> Unit) {
    var title by remember { mutableStateOf(value = "") }
    val homeVisitType = stringResource(id = R.string.activities_type_home_visit)
    var activityType by remember { mutableStateOf(value = homeVisitType) }
    var notes by remember { mutableStateOf(value = "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.activities_log_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                OutlinedTextField(value = title, onValueChange = { newValue: String -> title = newValue }, label = { Text(text = stringResource(id = R.string.common_label_title)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { newValue: String -> notes = newValue }, label = { Text(text = stringResource(id = R.string.common_label_notes)) }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            }
        },
        confirmButton = { TextButton(onClick = { onActivityAdded(title, activityType, "", "", notes) }, enabled = title.isNotBlank()) { Text(text = stringResource(id = R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.common_btn_cancel)) } }
    )
}

// ==================== MISSING COMPONENT DEFINITIONS ====================

/**
 * A card representing an urgency flag.
 *
 * @param flag The urgency flag entity to display.
 */
@Composable
fun UrgencyFlagCard(flag: CaseUrgencyFlagEntity) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(all = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(text = flag.flagType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = flag.reason ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            val highRisk = stringResource(id = R.string.status_high)
            val mediumRisk = stringResource(id = R.string.status_medium)
            val riskColor = if (flag.riskLevel == highRisk) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
            Surface(shape = RoundedCornerShape(size = 12.dp), color = riskColor.copy(alpha = 0.1f)) {
                Text(text = flag.riskLevel ?: mediumRisk, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = riskColor)
            }
        }
    }
}

/**
 * Dialog for adding a new urgency flag.
 *
 * @param onDismiss Callback to dismiss the dialog.
 * @param onFlagAdded Callback when an urgency flag is successfully added.
 * @param newValue The new text value for an input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUrgencyFlagDialog(onDismiss: () -> Unit, onFlagAdded: (type: String, reason: String, risk: String) -> Unit) {
    val defaultAppType = stringResource(id = R.string.urgency_flags_default_type)
    var type by remember { mutableStateOf(value = defaultAppType) }
    var reason by remember { mutableStateOf(value = "") }
    val highRisk = stringResource(id = R.string.status_high)
    var risk by remember { mutableStateOf(highRisk) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.urgency_flags_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                OutlinedTextField(value = type, onValueChange = { newValue: String -> type = newValue }, label = { Text(text = stringResource(id = R.string.urgency_flags_field_type)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = reason, onValueChange = { newValue: String -> reason = newValue }, label = { Text(text = stringResource(id = R.string.common_label_reason)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onFlagAdded(type, reason, risk) }, enabled = type.isNotBlank()) { Text(text = stringResource(id = R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.common_btn_cancel)) } }
    )
}

/**
 * Dialog for adding a new workload entry.
 *
 * @param onDismiss Callback to dismiss the dialog.
 * @param onWorkloadAdded Callback when a workload entry is successfully added.
 * @param newValue The new text value for an input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkloadDialog(
    onDismiss: () -> Unit,
    onWorkloadAdded: (date: String, cases: Int, tasks: Int, approvals: Int, reports: Int, notes: String) -> Unit
) {
    var date by remember { mutableStateOf(value = "") }
    var cases by remember { mutableStateOf(value = "5") }
    
    val currentDate = getCurrentDate()
    LaunchedEffect(Unit) { date = currentDate }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.workload_add_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                OutlinedTextField(value = date, onValueChange = { newValue: String -> date = newValue }, label = { Text(text = stringResource(id = R.string.common_label_date)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cases, onValueChange = { newValue: String -> cases = newValue }, label = { Text(text = stringResource(id = R.string.workload_label_active_cases)) }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onWorkloadAdded(date, cases.toIntOrNull() ?: 0, 0, 0, 0, "") }) { Text(text = stringResource(id = R.string.common_btn_add)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.common_btn_cancel)) } }
    )
}

/**
 * A row displaying a dashboard preference.
 *
 * @param label The label for the preference.
 * @param value The value of the preference.
 * @param icon The icon representing the preference.
 */
@Composable
fun PreferenceRow(label: String, value: String, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(size = 24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(width = 16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(weight = 1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

/**
 * A row with a switch to toggle a dashboard preference.
 *
 * @param label The label for the preference.
 * @param enabled Whether the preference is enabled.
 * @param icon The icon representing the preference.
 * @param onCheckedChange Callback when the toggle state is changed.
     */
@Composable
fun PreferenceToggleRow(label: String, enabled: Boolean, icon: ImageVector, onCheckedChange: (newValue: Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(size = 24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(width = 16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(weight = 1f))
        Switch(checked = enabled, onCheckedChange = onCheckedChange)
    }
}

/**
 * Dialog for editing dashboard preferences.
 *
 * @param currentPrefs The current dashboard preferences entity.
 * @param onDismiss Callback to dismiss the dialog.
 * @param onPreferencesSaved Callback when preferences are successfully saved.
 * @param layout Layout type preference.
 * @param isDark Dark mode preference.
 * @param stats Show stats preference.
 * @param quick Show quick actions preference.
 * @param recent Show recent activity preference.
 * @param freq Update frequency preference.
 * @param push Notifications enabled preference.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPreferencesDialog(
    currentPrefs: DashboardPreferenceEntity?,
    onDismiss: () -> Unit,
    onPreferencesSaved: (layout: String, isDark: Boolean, stats: Boolean, quick: Boolean, recent: Boolean, freq: String, push: Boolean) -> Unit
) {
    val layoutGrid = stringResource(id = R.string.dash_prefs_layout_grid)
    val freqDaily = stringResource(id = R.string.dash_prefs_freq_daily)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.dash_prefs_edit_desc)) },
        text = { Text(text = stringResource(id = R.string.dash_prefs_dialog_text)) },
        confirmButton = { TextButton(onClick = { onPreferencesSaved(layoutGrid, false, true, true, true, freqDaily, true) }) { Text(text = stringResource(id = R.string.common_btn_save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.common_btn_cancel)) } }
    )
}
