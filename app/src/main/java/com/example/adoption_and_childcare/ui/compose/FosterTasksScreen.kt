package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.FosterTaskEntity
import com.example.adoption_and_childcare.data.repository.FosterTaskRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FosterTasksScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { FosterTaskRepositoryImpl(db.fosterTaskDao(), apiService) }
    
    var tasks by remember { mutableStateOf<List<FosterTaskEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<FosterTaskEntity?>(null) }
    
    var familyId by remember { mutableStateOf(TextFieldValue("")) }
    var caseWorkerId by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var status by remember { mutableStateOf("Pending") }
    var dueDate by remember { mutableStateOf(TextFieldValue("")) }

    val statuses = listOf("Pending", "In Progress", "Completed", "Cancelled")
    var showStatusDropdown by remember { mutableStateOf(false) }

    // Load from local DB
    LaunchedEffect(Unit) {
        db.fosterTaskDao().observeAll().collectLatest { list ->
            tasks = list
        }
    }
    
    // Fetch from API
    LaunchedEffect(Unit) {
        fetchFromApi(repository, scope) { loading, error ->
            isLoading = loading
            errorMessage = error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Foster Tasks") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Foster Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
        ) {
            if (tasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Task, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No foster tasks yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tasks) { task ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text("Task #${task.taskId}", style = MaterialTheme.typography.titleMedium)
                                    Text("Family ID: ${task.familyId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    task.caseWorkerId?.let { Text("Case Worker: $it", style = MaterialTheme.typography.bodySmall) }
                                    task.description?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                    Text("Status: ${task.status}", style = MaterialTheme.typography.bodySmall, color = when(task.status) {
                                        "Completed" -> MaterialTheme.colorScheme.primary
                                        "In Progress" -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.outline
                                    })
                                    task.dueDate?.let { Text("Due: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                    task.createdAt?.let { Text("Created: $it", style = MaterialTheme.typography.bodySmall) }
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedTask = task
                                        showEditDialog = true
                                        familyId = TextFieldValue(task.familyId.toString())
                                        caseWorkerId = TextFieldValue(task.caseWorkerId?.toString() ?: "")
                                        description = TextFieldValue(task.description ?: "")
                                        status = task.status ?: "Pending"
                                        dueDate = TextFieldValue(task.dueDate ?: "")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedTask = task
                                        showDeleteDialog = true
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
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
            title = { Text("Add Foster Task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text("Family ID") }, singleLine = true)
                    OutlinedTextField(value = caseWorkerId, onValueChange = { caseWorkerId = it }, label = { Text("Case Worker ID (optional)") }, singleLine = true)
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description (optional)") }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = showStatusDropdown, onExpandedChange = { showStatusDropdown = !showStatusDropdown }) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showStatusDropdown, onDismissRequest = { showStatusDropdown = false }) {
                            statuses.forEach { statusOption ->
                                DropdownMenuItem(text = { Text(statusOption) }, onClick = {
                                    status = statusOption
                                    showStatusDropdown = false
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (YYYY-MM-DD, optional)") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fid = familyId.text.toIntOrNull()
                    val cwId = caseWorkerId.text.toIntOrNull()
                    if (fid != null) {
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
                            status = "Pending"
                            dueDate = TextFieldValue("")
                        }
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("Cancel") }
            }
        )
    }

    // Edit Dialog
    if (showEditDialog && selectedTask != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Foster Task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text("Family ID") }, singleLine = true)
                    OutlinedTextField(value = caseWorkerId, onValueChange = { caseWorkerId = it }, label = { Text("Case Worker ID (optional)") }, singleLine = true)
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, singleLine = true)
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (YYYY-MM-DD)") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fid = familyId.text.toIntOrNull()
                    val cwId = caseWorkerId.text.toIntOrNull()
                    if (fid != null && selectedTask != null) {
                        scope.launch {
                            db.fosterTaskDao().updateWithSync(
                                selectedTask!!.copy(
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
                    }
                }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedTask = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog && selectedTask != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Foster Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        selectedTask?.let {
                            db.fosterTaskDao().deleteByIdWithSync(it.taskId, db.syncQueueDao())
                        }
                        showDeleteDialog = false
                        selectedTask = null
                    }
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedTask = null }) { Text("Cancel") }
            }
        )
    }
}

/**
 * Helper function to fetch foster tasks from API.
 */
private fun fetchFromApi(
    repository: FosterTaskRepositoryImpl,
    scope: kotlinx.coroutines.CoroutineScope,
    onLoading: (Boolean, String?) -> Unit
) {
    scope.launch {
        onLoading(true, null)
        try {
            val token = "" // TODO: Get actual auth token
            if (token.isNotEmpty()) {
                val result = repository.fetchFromApi(token)
                if (result.isFailure) {
                    onLoading(false, result.exceptionOrNull()?.message)
                } else {
                    onLoading(false, null)
                }
            } else {
                onLoading(false, "No authentication token available")
            }
        } catch (e: Exception) {
            onLoading(false, "Failed to fetch foster tasks: ${e.message}")
        }
    }
}