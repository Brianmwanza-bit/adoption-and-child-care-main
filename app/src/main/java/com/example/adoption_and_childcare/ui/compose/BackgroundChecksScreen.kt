package com.example.adoption_and_childcare.ui.compose

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import com.example.adoption_and_childcare.data.repository.BackgroundCheckRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for managing background checks for users.
 *
 * @param onBack Callback invoked when the user navigates back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundChecksScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val authManager = remember { AuthManager(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { BackgroundCheckRepositoryImpl(db.backgroundCheckDao(), db.syncQueueDao(), apiService, authManager) }
    
    var checks by remember { mutableStateOf<List<BackgroundCheckEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedCheck by remember { mutableStateOf<BackgroundCheckEntity?>(null) }

    var userId by remember { mutableStateOf(TextFieldValue("")) }
    var status by remember { mutableStateOf("Pending") }
    var result by remember { mutableStateOf(TextFieldValue("")) }
    
    val statuses = listOf("Pending", "Processing", "Completed", "Failed")
    var showStatusDropdown by remember { mutableStateOf(false) }

    // Load from local DB
    LaunchedEffect(Unit) {
        db.backgroundCheckDao().observeAll().collectLatest { list ->
            checks = list
        }
    }
    
    // Fetch from API
    LaunchedEffect(Unit) {
        fetchFromApi(repository, authManager, scope) { loading, error ->
            isLoading = loading
            errorMessage = error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Background Checks") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Check")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (checks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No background checks found", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(checks) { check ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.padding(12.dp).weight(1f)) {
                                    Text("User ID: ${check.userId}", style = MaterialTheme.typography.titleMedium)
                                    Text("Status: ${check.status}", style = MaterialTheme.typography.bodySmall, color = if (check.status == "Completed") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                    check.result?.let { Text("Result: $it", style = MaterialTheme.typography.bodySmall) }
                                    Text("Requested: ${check.requestedAt}", style = MaterialTheme.typography.bodySmall)
                                    check.completedAt?.let { Text("Completed: $it", style = MaterialTheme.typography.bodySmall) }
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedCheck = check
                                        showEditDialog = true
                                        userId = TextFieldValue(check.userId.toString())
                                        status = check.status ?: "Pending"
                                        result = TextFieldValue(check.result ?: "")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedCheck = check
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

    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text("Add Background Check") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") }, singleLine = true)
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
                            statuses.forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = {
                                    status = s
                                    showStatusDropdown = false
                                })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val uid = userId.text.toIntOrNull()
                    if (uid != null) {
                        scope.launch {
                            db.backgroundCheckDao().insertWithSync(
                                BackgroundCheckEntity(
                                    userId = uid,
                                    status = status,
                                    requestedAt = System.currentTimeMillis().toString()
                                ),
                                db.syncQueueDao()
                            )
                            showCreate = false
                            userId = TextFieldValue("")
                            status = "Pending"
                        }
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("Cancel") }
            }
        )
    }

    if (showEditDialog && selectedCheck != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Background Check") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") }, singleLine = true, enabled = false)
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
                            statuses.forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = {
                                    status = s
                                    showStatusDropdown = false
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = result, onValueChange = { result = it }, label = { Text("Result (optional)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val updated = selectedCheck!!.copy(
                            status = status,
                            result = result.text.ifBlank { null },
                            completedAt = if (status == "Completed") System.currentTimeMillis().toString() else null
                        )
                        db.backgroundCheckDao().updateWithSync(updated, db.syncQueueDao())
                        showEditDialog = false
                        selectedCheck = null
                    }
                }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedCheck = null }) { Text("Cancel") }
            }
        )
    }

    if (showDeleteDialog && selectedCheck != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedCheck = null },
            title = { Text("Delete Background Check") },
            text = { Text("Are you sure you want to delete check #${selectedCheck!!.checkId}?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        db.backgroundCheckDao().deleteByIdWithSync(selectedCheck!!.checkId, db.syncQueueDao())
                        showDeleteDialog = false
                        selectedCheck = null
                    }
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedCheck = null }) { Text("Cancel") }
            }
        )
    }
}

/**
 * Helper function to fetch background checks from API.
 */
private fun fetchFromApi(
    repository: BackgroundCheckRepositoryImpl,
    authManager: AuthManager,
    scope: kotlinx.coroutines.CoroutineScope,
    onLoading: (Boolean, String?) -> Unit
) {
    scope.launch {
        onLoading(true, null)
        try {
            val token = authManager.getAuthToken() ?: ""
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
            onLoading(false, "Failed to fetch background checks: ${e.message}")
        }
    }
}
