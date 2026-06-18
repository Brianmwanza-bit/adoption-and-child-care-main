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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity
import com.example.adoption_and_childcare.data.repository.FosterMatchRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for managing and viewing foster placement matches.
 * 
 * Users can view a list of matches between children and families,
 * add new matches, and update match statuses.
 * 
 * @param onBack Callback for navigating back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FosterMatchesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val authManager = remember { AuthManager(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { FosterMatchRepositoryImpl(db.fosterMatchDao(), db.syncQueueDao(), apiService, authManager) }
    
    var matches by remember { mutableStateOf<List<FosterMatchEntity>>(emptyList()) }
    var children by remember { mutableStateOf<List<com.example.adoption_and_childcare.data.db.entities.ChildEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedMatch by remember { mutableStateOf<FosterMatchEntity?>(null) }
    
    var familyId by remember { mutableStateOf(TextFieldValue("")) }
    var caseWorkerId by remember { mutableStateOf(TextFieldValue("")) }
    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var status by remember { mutableStateOf("Pending") }
    var notes by remember { mutableStateOf(TextFieldValue("")) }

    val statuses = listOf("Pending", "Under Review", "Approved", "Rejected", "Completed")
    var showStatusDropdown by remember { mutableStateOf(false) }

    // Load from local DB
    LaunchedEffect(Unit) {
        db.fosterMatchDao().observeAll().collectLatest { list ->
            matches = list
        }
    }
    LaunchedEffect(Unit) {
        db.childDao().observeAll().collectLatest { list -> children = list }
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
                title = { Text(stringResource(R.string.foster_matches_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.foster_matches_back_desc))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.foster_matches_add_desc))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
        ) {
            if (matches.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PeopleAlt, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.foster_matches_no_matches), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(matches) { match ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text(stringResource(R.string.foster_matches_item_id, match.matchId), style = MaterialTheme.typography.titleMedium)
                                    Text(stringResource(R.string.foster_matches_family_id_label, match.familyId), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    match.childId?.let { Text(stringResource(R.string.foster_matches_child_id_label, it), style = MaterialTheme.typography.bodySmall) }
                                    match.caseWorkerId?.let { Text(stringResource(R.string.foster_matches_case_worker_label, it), style = MaterialTheme.typography.bodySmall) }
                                    Text(stringResource(R.string.foster_matches_status_label, match.status ?: ""), style = MaterialTheme.typography.bodySmall, color = when(match.status) {
                                        "Approved", "Completed" -> MaterialTheme.colorScheme.primary
                                        "Under Review" -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.outline
                                    })
                                    match.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                    match.createdAt?.let { Text(stringResource(R.string.foster_matches_created_label, it), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedMatch = match
                                        showEditDialog = true
                                        familyId = TextFieldValue(match.familyId.toString())
                                        caseWorkerId = TextFieldValue(match.caseWorkerId?.toString() ?: "")
                                        selectedChildId = match.childId
                                        status = match.status ?: "Pending"
                                        notes = TextFieldValue(match.notes ?: "")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.foster_tasks_edit_desc), tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedMatch = match
                                        showDeleteDialog = true
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.foster_tasks_delete_desc), tint = MaterialTheme.colorScheme.error)
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
            title = { Text(stringResource(R.string.foster_matches_add_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text(stringResource(R.string.foster_matches_field_family_id)) }, singleLine = true)
                    OutlinedTextField(value = caseWorkerId, onValueChange = { caseWorkerId = it }, label = { Text(stringResource(R.string.foster_matches_field_cw_id)) }, singleLine = true)
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { selectedChildId = it.childId }
                    )
                    ExposedDropdownMenuBox(expanded = showStatusDropdown, onExpandedChange = { showStatusDropdown = !showStatusDropdown }) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.foster_matches_field_status)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
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
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.foster_matches_field_notes)) }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fid = familyId.text.toIntOrNull()
                    val cwId = caseWorkerId.text.toIntOrNull()
                    val cid = selectedChildId
                    if (fid != null) {
                        scope.launch {
                            db.fosterMatchDao().insert(
                                FosterMatchEntity(
                                    familyId = fid,
                                    caseWorkerId = cwId,
                                    childId = cid,
                                    status = status,
                                    notes = notes.text.ifBlank { null }
                                )
                            )
                            showCreate = false
                            familyId = TextFieldValue("")
                            caseWorkerId = TextFieldValue("")
                            selectedChildId = null
                            status = "Pending"
                            notes = TextFieldValue("")
                        }
                    }
                }) { Text(stringResource(R.string.foster_matches_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.foster_matches_cancel)) }
            }
        )
    }

    // Edit Dialog
    if (showEditDialog && selectedMatch != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.foster_matches_edit_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text(stringResource(R.string.foster_matches_field_family_id)) }, singleLine = true)
                    OutlinedTextField(value = caseWorkerId, onValueChange = { caseWorkerId = it }, label = { Text(stringResource(R.string.foster_matches_field_cw_id)) }, singleLine = true)
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { selectedChildId = it.childId },
                        label = stringResource(R.string.reports_label_child_name)
                    )
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.foster_matches_field_notes_edit)) }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fid = familyId.text.toIntOrNull()
                    val cwId = caseWorkerId.text.toIntOrNull()
                    val cid = selectedChildId
                    if (fid != null) {
                        scope.launch {
                            val match = selectedMatch ?: return@launch
                            db.fosterMatchDao().update(
                                match.copy(
                                    familyId = fid,
                                    caseWorkerId = cwId,
                                    childId = cid,
                                    notes = notes.text.ifBlank { null }
                                )
                            )
                            showEditDialog = false
                            selectedMatch = null
                            selectedChildId = null
                        }
                    }
                }) { Text(stringResource(R.string.foster_matches_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedMatch = null; selectedChildId = null }) { Text(stringResource(R.string.foster_matches_cancel)) }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog && selectedMatch != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.foster_matches_delete_dialog_title)) },
            text = { Text(stringResource(R.string.foster_matches_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        selectedMatch?.let { match ->
                            db.fosterMatchDao().delete(match)
                        }
                        showDeleteDialog = false
                        selectedMatch = null
                    }
                }) { Text(stringResource(R.string.foster_matches_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedMatch = null }) { Text(stringResource(R.string.foster_matches_cancel)) }
            }
        )
    }
}


/**
 * Helper function to fetch foster matches from API.
 */
private fun fetchFromApi(
    repository: FosterMatchRepositoryImpl,
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
            onLoading(false, "Failed to fetch foster matches: ${e.message}")
        }
    }
}