package com.example.adoption_and_childcare.ui.compose

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.yourdomain.adoptionchildcare.R
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity
import com.example.adoption_and_childcare.data.repository.FosterTaskRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for managing foster tasks.
 *
 * @param onBack Callback invoked when the user navigates back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FosterTasksScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val authManager = remember { AuthManager(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { FosterTaskRepositoryImpl(db.fosterTaskDao(), db.syncQueueDao(), apiService, authManager) }
    
    var tasks by remember { mutableStateOf<List<FosterTaskEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<FosterTaskEntity?>(null) }
    
    val pendingStatus = stringResource(R.string.foster_tasks_status_pending)
    val inProgressStatus = stringResource(R.string.foster_tasks_status_in_progress)
    val completedStatus = stringResource(R.string.foster_tasks_status_completed)
    val cancelledStatus = stringResource(R.string.foster_tasks_status_cancelled)

    var familyId by remember { mutableStateOf(TextFieldValue("")) }
    var caseWorkerId by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var status by remember { mutableStateOf(pendingStatus) }
    var dueDate by remember { mutableStateOf(TextFieldValue("")) }

    val statuses = listOf(pendingStatus, inProgressStatus, completedStatus, cancelledStatus)
    var showStatusDropdown by remember { mutableStateOf(false) }

    // Load from local DB
    LaunchedEffect(Unit) {
        db.fosterTaskDao().observeAll().collectLatest { taskList: List<FosterTaskEntity> ->
            tasks = taskList
        }
    }
    
    // Fetch from API
    LaunchedEffect(Unit) {
        fetchFromApi(context, repository, authManager, scope) { loading: Boolean, error: String? ->
            isLoading = loading
            error?.let { msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.foster_tasks_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.foster_tasks_back_desc)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                status = pendingStatus
                showCreate = true 
            }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.foster_tasks_add_desc)
                )
            }
        }
    ) { paddingValues: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            if (isLoading && tasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (tasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Task,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.foster_tasks_no_tasks),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tasks) { task: FosterTaskEntity ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text(
                                        stringResource(R.string.foster_tasks_task_id, task.taskId),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        stringResource(R.string.foster_tasks_family_id_label, task.familyId),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    task.caseWorkerId?.let { cwId: Int ->
                                        Text(
                                            stringResource(R.string.foster_tasks_case_worker_label, cwId),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    task.description?.let { desc: String ->
                                        Text(desc, style = MaterialTheme.typography.bodySmall) 
                                    }
                                    Text(
                                        stringResource(R.string.foster_tasks_status_label, task.status ?: ""),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = when(task.status) {
                                            completedStatus -> MaterialTheme.colorScheme.primary
                                            inProgressStatus -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.outline
                                        }
                                    )
                                    task.dueDate?.let { date: String ->
                                        Text(
                                            stringResource(R.string.foster_tasks_due_label, date),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    task.createdAt?.let { created: String ->
                                        Text(
                                            stringResource(R.string.foster_tasks_created_label, created),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedTask = task
                                        showEditDialog = true
                                        familyId = TextFieldValue(task.familyId.toString())
                                        caseWorkerId = TextFieldValue(task.caseWorkerId?.toString() ?: "")
                                        description = TextFieldValue(task.description ?: "")
                                        status = task.status ?: pendingStatus
                                        dueDate = TextFieldValue(task.dueDate ?: "")
                                    }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = stringResource(R.string.foster_tasks_edit_desc),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = {
                                        selectedTask = task
                                        showDeleteDialog = true
                                    }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.foster_tasks_delete_desc),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Dialog
    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text(stringResource(R.string.foster_tasks_add_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = familyId,
                        onValueChange = { familyId = it },
                        label = { Text(stringResource(R.string.foster_tasks_family_id_field)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = caseWorkerId,
                        onValueChange = { caseWorkerId = it },
                        label = { Text(stringResource(R.string.foster_tasks_case_worker_field)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(R.string.foster_tasks_desc_field)) },
                        singleLine = true
                    )
                    ExposedDropdownMenuBox(
                        expanded = showStatusDropdown,
                        onExpandedChange = { showStatusDropdown = !showStatusDropdown }
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.foster_tasks_status_field)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showStatusDropdown,
                            onDismissRequest = { showStatusDropdown = false }
                        ) {
                            statuses.forEach { statusOption: String ->
                                DropdownMenuItem(
                                    text = { Text(statusOption) },
                                    onClick = {
                                        status = statusOption
                                        showStatusDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text(stringResource(R.string.foster_tasks_due_date_field)) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fid = familyId.text.toIntOrNull() ?: return@TextButton
                    val cwId = caseWorkerId.text.toIntOrNull()
                    scope.launch {
                        db.fosterTaskDao().insertWithSync(
                            FosterTaskEntity(
                                familyId = fid,
                                caseWorkerId = cwId,
                                description = description.text.ifBlank { null },
                                status = status,
                                dueDate = dueDate.text.ifBlank { null }
                            ),
                            db.syncQueueDao()
                        )
                        showCreate = false
                        familyId = TextFieldValue("")
                        caseWorkerId = TextFieldValue("")
                        description = TextFieldValue("")
                        status = pendingStatus
                        dueDate = TextFieldValue("")
                    }
                }) { Text(stringResource(R.string.foster_tasks_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) {
                    Text(stringResource(R.string.foster_tasks_cancel))
                }
            }
        )
    }

    // Edit Dialog
    if (showEditDialog) {
        val currentTask: FosterTaskEntity = selectedTask ?: return
        AlertDialog(
            onDismissRequest = { 
                showEditDialog = false
                selectedTask = null
            },
            title = { Text(stringResource(R.string.foster_tasks_edit_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = familyId,
                        onValueChange = { familyId = it },
                        label = { Text(stringResource(R.string.foster_tasks_family_id_field)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = caseWorkerId,
                        onValueChange = { caseWorkerId = it },
                        label = { Text(stringResource(R.string.foster_tasks_case_worker_field)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(R.string.foster_tasks_desc_field_edit)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text(stringResource(R.string.foster_tasks_due_date_field_edit)) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fid = familyId.text.toIntOrNull() ?: return@TextButton
                    val cwId = caseWorkerId.text.toIntOrNull()
                    scope.launch {
                        db.fosterTaskDao().updateWithSync(
                            currentTask.copy(
                                familyId = fid,
                                caseWorkerId = cwId,
                                description = description.text.ifBlank { null },
                                dueDate = dueDate.text.ifBlank { null }
                            ),
                            db.syncQueueDao()
                        )
                        showEditDialog = false
                        selectedTask = null
                    }
                }) { Text(stringResource(R.string.foster_tasks_update)) }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showEditDialog = false
                    selectedTask = null 
                }) {
                    Text(stringResource(R.string.foster_tasks_cancel))
                }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog) {
        val taskToDelete: FosterTaskEntity = selectedTask ?: return
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                selectedTask = null
            },
            title = { Text(stringResource(R.string.foster_tasks_delete_title)) },
            text = { Text(stringResource(R.string.foster_tasks_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        db.fosterTaskDao().deleteByIdWithSync(taskToDelete.taskId, db.syncQueueDao())
                        showDeleteDialog = false
                        selectedTask = null
                    }
                }) { Text(stringResource(R.string.foster_tasks_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteDialog = false
                    selectedTask = null 
                }) {
                    Text(stringResource(R.string.foster_tasks_cancel))
                }
            }
        )
    }
}

/**
 * Helper function to fetch foster tasks from API.
 *
 * @param context The application context for string resources.
 * @param repository The foster task repository.
 * @param authManager The authentication manager.
 * @param scope Coroutine scope for launching the fetch.
 * @param onLoading Callback for loading status and error messages.
 */
private fun fetchFromApi(
    context: Context,
    repository: FosterTaskRepositoryImpl,
    authManager: AuthManager,
    scope: kotlinx.coroutines.CoroutineScope,
    onLoading: (isLoading: Boolean, errorMessage: String?) -> Unit
) {
    scope.launch {
        onLoading(true, null)
        try {
            val token = authManager.getAuthToken()
            if (token != null && token.isNotEmpty()) {
                val result = repository.fetchFromApi(token)
                if (result.isFailure) {
                    onLoading(false, result.exceptionOrNull()?.message)
                } else {
                    onLoading(false, null)
                }
            } else {
                onLoading(false, context.getString(R.string.foster_tasks_error_no_token))
            }
        } catch (e: Exception) {
            onLoading(false, context.getString(R.string.foster_tasks_error_fetch, e.message ?: ""))
        }
    }
}
