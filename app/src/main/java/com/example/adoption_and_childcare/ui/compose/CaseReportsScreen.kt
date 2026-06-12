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
import com.example.adoption_and_childcare.data.db.entities.CaseReportEntity
import com.example.adoption_and_childcare.data.repository.CaseReportRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseReportsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { CaseReportRepositoryImpl(db.caseReportDao(), apiService) }
    var reports by remember { mutableStateOf<List<CaseReportEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedReport by remember { mutableStateOf<CaseReportEntity?>(null) }
    
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var userId by remember { mutableStateOf(TextFieldValue("")) }
    var date by remember { mutableStateOf(TextFieldValue("")) }
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.caseReportDao().observeAll().collectLatest { list -> reports = list }
    }
    
    // Fetch from API
    LaunchedEffect(Unit) {
        fetchCaseReportsFromApi(repository, scope) { loading, error ->
            isLoading = loading
            errorMessage = error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Case Reports") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Report")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
            if (reports.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No reports yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(reports) { r ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text(r.reportTitle, style = MaterialTheme.typography.titleMedium)
                                    Text("Child ID: ${r.childId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    Text("Date: ${r.reportDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(r.content, style = MaterialTheme.typography.bodySmall, maxLines = 3)
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedReport = r
                                        showEditDialog = true
                                        childId = TextFieldValue(r.childId.toString())
                                        userId = TextFieldValue(r.userId.toString())
                                        date = TextFieldValue(r.reportDate)
                                        title = TextFieldValue(r.reportTitle)
                                        content = TextFieldValue(r.content)
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedReport = r
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

            if (showCreate) {
                AlertDialog(
                    onDismissRequest = { showCreate = false },
                    title = { Text("Add Case Report") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") }, singleLine = true)
                            OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") }, singleLine = true)
                            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Report date (YYYY-MM-DD)") }, singleLine = true)
                            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true)
                            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") })
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = childId.text.toIntOrNull()
                            val uid = userId.text.toIntOrNull()
                            if (cid != null && uid != null && date.text.isNotBlank() && title.text.isNotBlank() && content.text.isNotBlank()) {
                                scope.launch {
                                    db.caseReportDao().insertWithSync(
                                        CaseReportEntity(
                                            childId = cid,
                                            userId = uid,
                                            reportDate = date.text,
                                            reportTitle = title.text,
                                            content = content.text
                                        ),
                                        db.syncQueueDao()
                                    )
                                    showCreate = false
                                    childId = TextFieldValue("")
                                    userId = TextFieldValue("")
                                    date = TextFieldValue("")
                                    title = TextFieldValue("")
                                    content = TextFieldValue("")
                                }
                            }
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreate = false }) { Text("Cancel") }
                    }
                )
            }

            if (showEditDialog && selectedReport != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit Case Report") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") }, singleLine = true, enabled = false)
                            OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") }, singleLine = true, enabled = false)
                            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Report date") }, singleLine = true)
                            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true)
                            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") })
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (title.text.isNotBlank() && content.text.isNotBlank()) {
                                scope.launch {
                                    val updated = selectedReport!!.copy(
                                        reportDate = date.text,
                                        reportTitle = title.text,
                                        content = content.text
                                    )
                                    db.caseReportDao().updateWithSync(updated, db.syncQueueDao())
                                    showEditDialog = false
                                    selectedReport = null
                                }
                            }
                        }) { Text("Update") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false; selectedReport = null }) { Text("Cancel") }
                    }
                )
            }

            if (showDeleteDialog && selectedReport != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false; selectedReport = null },
                    title = { Text("Delete Case Report") },
                    text = { Text("Are you sure you want to delete '${selectedReport!!.reportTitle}'?") },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                db.caseReportDao().deleteByIdWithSync(selectedReport!!.reportId, db.syncQueueDao())
                                showDeleteDialog = false
                                selectedReport = null
                            }
                        }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false; selectedReport = null }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}

/**
 * Helper function to fetch case reports from API.
 */
private fun fetchCaseReportsFromApi(
    repository: CaseReportRepositoryImpl,
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
            onLoading(false, "Failed to fetch case reports: ${e.message}")
        }
    }
}
