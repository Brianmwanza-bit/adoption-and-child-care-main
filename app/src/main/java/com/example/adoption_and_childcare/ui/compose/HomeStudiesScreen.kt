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
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeStudiesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var studies by remember { mutableStateOf<List<HomeStudyEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedStudy by remember { mutableStateOf<HomeStudyEntity?>(null) }
    
    var familyId by remember { mutableStateOf(TextFieldValue("")) }
    var result by remember { mutableStateOf(TextFieldValue("")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.homeStudyDao().observeAll().collectLatest { list ->
            studies = list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Studies") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Home Study")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
            if (studies.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No home studies yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(studies) { hs ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text("Home Study #${hs.homeStudyId}", style = MaterialTheme.typography.titleMedium)
                                    Text("Family ID: ${hs.familyId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    hs.result?.let { Text("Result: $it", style = MaterialTheme.typography.bodySmall) }
                                    hs.startedAt?.let { Text("Started: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                    hs.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2) }
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedStudy = hs
                                        showEditDialog = true
                                        familyId = TextFieldValue(hs.familyId.toString())
                                        result = TextFieldValue(hs.result ?: "")
                                        notes = TextFieldValue(hs.notes ?: "")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedStudy = hs
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
                    title = { Text("Add Home Study") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text("Family ID") }, singleLine = true)
                            OutlinedTextField(value = result, onValueChange = { result = it }, label = { Text("Result (optional)") }, singleLine = true)
                            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") })
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val fam = familyId.text.toIntOrNull()
                            if (fam != null) {
                                scope.launch {
                                    db.homeStudyDao().insertWithSync(
                                        HomeStudyEntity(
                                            familyId = fam,
                                            result = result.text.ifBlank { null },
                                            notes = notes.text.ifBlank { null },
                                            startedAt = System.currentTimeMillis().toString()
                                        ),
                                        db.syncQueueDao()
                                    )
                                    showCreate = false
                                    familyId = TextFieldValue("")
                                    result = TextFieldValue("")
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

            if (showEditDialog && selectedStudy != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit Home Study") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text("Family ID") }, singleLine = true, enabled = false)
                            OutlinedTextField(value = result, onValueChange = { result = it }, label = { Text("Result") }, singleLine = true)
                            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") })
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                val updated = selectedStudy!!.copy(
                                    result = result.text.ifBlank { null },
                                    notes = notes.text.ifBlank { null }
                                )
                                db.homeStudyDao().updateWithSync(updated, db.syncQueueDao())
                                showEditDialog = false
                                selectedStudy = null
                            }
                        }) { Text("Update") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false; selectedStudy = null }) { Text("Cancel") }
                    }
                )
            }

            if (showDeleteDialog && selectedStudy != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false; selectedStudy = null },
                    title = { Text("Delete Home Study") },
                    text = { Text("Are you sure you want to delete home study #${selectedStudy!!.homeStudyId}?") },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                db.homeStudyDao().deleteByIdWithSync(selectedStudy!!.homeStudyId, db.syncQueueDao())
                                showDeleteDialog = false
                                selectedStudy = null
                            }
                        }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false; selectedStudy = null }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}
