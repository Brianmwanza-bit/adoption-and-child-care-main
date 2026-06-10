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
import com.example.adoption_and_childcare.data.db.entities.FosterMatchEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FosterMatchesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var matches by remember { mutableStateOf<List<FosterMatchEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedMatch by remember { mutableStateOf<FosterMatchEntity?>(null) }
    
    var familyId by remember { mutableStateOf(TextFieldValue("")) }
    var caseWorkerId by remember { mutableStateOf(TextFieldValue("")) }
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var status by remember { mutableStateOf("Pending") }
    var notes by remember { mutableStateOf(TextFieldValue("")) }

    val statuses = listOf("Pending", "Under Review", "Approved", "Rejected", "Completed")
    var showStatusDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.fosterMatchDao().observeAll().collectLatest { list ->
            matches = list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Foster Matches") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Foster Match")
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
                        Text("No foster matches yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(matches) { match ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text("Match #${match.matchId}", style = MaterialTheme.typography.titleMedium)
                                    Text("Family ID: ${match.familyId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    match.childId?.let { Text("Child ID: $it", style = MaterialTheme.typography.bodySmall) }
                                    match.caseWorkerId?.let { Text("Case Worker: $it", style = MaterialTheme.typography.bodySmall) }
                                    Text("Status: ${match.status}", style = MaterialTheme.typography.bodySmall, color = when(match.status) {
                                        "Approved", "Completed" -> MaterialTheme.colorScheme.primary
                                        "Under Review" -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.outline
                                    })
                                    match.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                    match.createdAt?.let { Text("Created: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedMatch = match
                                        showEditDialog = true
                                        familyId = TextFieldValue(match.familyId.toString())
                                        caseWorkerId = TextFieldValue(match.caseWorkerId?.toString() ?: "")
                                        childId = TextFieldValue(match.childId?.toString() ?: "")
                                        status = match.status ?: "Pending"
                                        notes = TextFieldValue(match.notes ?: "")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedMatch = match
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
            title = { Text("Add Foster Match") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text("Family ID") }, singleLine = true)
                    OutlinedTextField(value = caseWorkerId, onValueChange = { caseWorkerId = it }, label = { Text("Case Worker ID (optional)") }, singleLine = true)
                    OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID (optional)") }, singleLine = true)
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
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fid = familyId.text.toIntOrNull()
                    val cwId = caseWorkerId.text.toIntOrNull()
                    val cid = childId.text.toIntOrNull()
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
                            childId = TextFieldValue("")
                            status = "Pending"
                            notes = TextFieldValue("")
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
    if (showEditDialog && selectedMatch != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Foster Match") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text("Family ID") }, singleLine = true)
                    OutlinedTextField(value = caseWorkerId, onValueChange = { caseWorkerId = it }, label = { Text("Case Worker ID (optional)") }, singleLine = true)
                    OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID (optional)") }, singleLine = true)
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fid = familyId.text.toIntOrNull()
                    val cwId = caseWorkerId.text.toIntOrNull()
                    val cid = childId.text.toIntOrNull()
                    if (fid != null) {
                        scope.launch {
                            db.fosterMatchDao().update(
                                selectedMatch!!.copy(
                                    familyId = fid,
                                    caseWorkerId = cwId,
                                    childId = cid,
                                    notes = notes.text.ifBlank { null }
                                )
                            )
                            showEditDialog = false
                            selectedMatch = null
                        }
                    }
                }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedMatch = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog && selectedMatch != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Foster Match") },
            text = { Text("Are you sure you want to delete this foster match?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        db.fosterMatchDao().delete(selectedMatch!!)
                        showDeleteDialog = false
                        selectedMatch = null
                    }
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedMatch = null }) { Text("Cancel") }
            }
        )
    }
}