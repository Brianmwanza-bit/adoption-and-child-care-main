package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import com.example.adoption_and_childcare.data.repository.BackgroundCheckRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import com.example.adoption_and_childcare.viewmodel.BackgroundChecksViewModel
import kotlinx.coroutines.launch

/**
 * Screen for managing background checks for users.
 * 
 * @param onBack Callback for navigating back.
 * @param viewModel ViewModel for handling business logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundChecksScreen(
    onBack: () -> Unit = {},
    viewModel: BackgroundChecksViewModel = hiltViewModel(),
) {
    val checks by viewModel.backgroundChecks.collectAsState(initial = emptyList())
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showCreate by remember { mutableStateOf(value = false) }
    var showEditDialog by remember { mutableStateOf(value = false) }
    var showDeleteDialog by remember { mutableStateOf(value = false) }
    var showDetails by remember { mutableStateOf(false) }
    var selectedCheck by remember { mutableStateOf<BackgroundCheckEntity?>(value = null) }

    val pendingStatus = stringResource(R.string.bg_checks_status_pending)
    val completedStatus = stringResource(R.string.bg_checks_status_completed)

    var selectedUserId by remember { mutableStateOf<Int?>(value = null) }
    var status by remember { mutableStateOf(value = pendingStatus) }
    var result by remember { mutableStateOf(value = TextFieldValue("")) }
    
    val statuses = listOf(
        stringResource(R.string.bg_checks_status_pending),
        stringResource(R.string.bg_checks_status_processing),
        stringResource(R.string.bg_checks_status_completed),
        stringResource(R.string.bg_checks_status_failed)
    )
    var showStatusDropdown by remember { mutableStateOf(false) }

    if (showDetails && selectedCheck != null) {
        BackgroundCheckDetailScreen(
            check = selectedCheck!!,
            onBack = { showDetails = false; selectedCheck = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.bg_checks_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.bg_checks_back))
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshFromApi() }) {
                            Icon(
                                Icons.Default.Refresh, 
                                contentDescription = stringResource(R.string.reports_refresh_desc)
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.bg_checks_add))
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (checks.isEmpty() && !isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.bg_checks_no_results), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(checks) { check ->
                            Box(modifier = Modifier.clickable { 
                                selectedCheck = check
                                showDetails = true
                            }) {
                                BackgroundCheckCard(
                                    check = check,
                                    onEdit = {
                                        selectedCheck = check
                                        showEditDialog = true
                                        selectedUserId = check.userId
                                        status = check.status ?: pendingStatus
                                        result = TextFieldValue(check.result ?: "")
                                    },
                                    onDelete = {
                                        selectedCheck = check
                                        showDeleteDialog = true
                                    }
                                )
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
            title = { Text(stringResource(R.string.bg_checks_add_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Simple User Selection (Could be improved to Searchable)
                    OutlinedTextField(
                        value = selectedUserId?.toString() ?: "",
                        onValueChange = { selectedUserId = it.toIntOrNull() },
                        label = { Text(stringResource(R.string.bg_checks_user_id_field)) },
                        singleLine = true
                    )
                    ExposedDropdownMenuBox(expanded = showStatusDropdown, onExpandedChange = { showStatusDropdown = !showStatusDropdown }) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.bg_checks_status_field)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
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
                    val uid = selectedUserId
                    if (uid != null) {
                        viewModel.insertCheck(
                            BackgroundCheckEntity(
                                userId = uid,
                                status = status,
                                requestedAt = System.currentTimeMillis().toString()
                            )
                        )
                        showCreate = false
                        selectedUserId = null
                        status = pendingStatus
                    }
                }) { Text(stringResource(R.string.bg_checks_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.bg_checks_cancel)) }
            }
        )
    }

    if (showEditDialog && (selectedCheck != null)) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.bg_checks_edit_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.bg_checks_user_id_label, selectedCheck?.userId ?: 0))
                    ExposedDropdownMenuBox(expanded = showStatusDropdown, onExpandedChange = { showStatusDropdown = !showStatusDropdown }) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.bg_checks_status_field)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
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
                    OutlinedTextField(value = result, onValueChange = { result = it }, label = { Text(stringResource(R.string.bg_checks_result_field)) })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val check = selectedCheck ?: return@TextButton
                    val updated = check.copy(
                        status = status,
                        result = result.text.ifBlank { null },
                        completedAt = if (status == completedStatus) System.currentTimeMillis().toString() else null
                    )
                    viewModel.updateCheck(updated)
                    showEditDialog = false
                    selectedCheck = null
                }) { Text(stringResource(R.string.bg_checks_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedCheck = null }) { Text(stringResource(R.string.bg_checks_cancel)) }
            }
        )
    }

    if (showDeleteDialog && selectedCheck != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedCheck = null },
            title = { Text(stringResource(R.string.bg_checks_delete_dialog_title)) },
            text = { 
                Text(stringResource(R.string.bg_checks_delete_confirm, selectedCheck?.userId ?: 0)) 
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedCheck?.let { viewModel.deleteCheck(it.checkId) }
                    showDeleteDialog = false
                    selectedCheck = null
                }) { Text(stringResource(R.string.bg_checks_delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedCheck = null }) { Text(stringResource(R.string.bg_checks_cancel)) }
            }
        )
    }
}

@Composable
fun BackgroundCheckCard(
    check: BackgroundCheckEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (check.status?.lowercase()) {
        "completed", "passed" -> Color(0xFF4CAF50)
        "failed", "rejected" -> Color(0xFFF44336)
        "processing" -> Color(0xFFFF9800)
        else -> Color.Gray
    }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(40.dp), shape = CircleShape, color = statusColor.copy(alpha = 0.1f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Security, null, tint = statusColor, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("User ID: ${check.userId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(check.status ?: "Unknown Status", color = statusColor, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            FormDetailRow(label = "Requested At", value = check.requestedAt ?: "N/A")
            if (check.completedAt != null) {
                FormDetailRow(label = "Completed At", value = check.completedAt)
            }
            if (!check.result.isNullOrBlank()) {
                Text("Result Summary", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 8.dp))
                Text(check.result, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/**
 * Helper function to fetch background checks from API.
 */
fun fetchFromApi(
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
