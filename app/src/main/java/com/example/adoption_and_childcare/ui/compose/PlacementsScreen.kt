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
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import com.example.adoption_and_childcare.data.repository.PlacementRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { PlacementRepositoryImpl(db.placementDao(), apiService) }
    var items by remember { mutableStateOf<List<PlacementEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedPlacement by remember { mutableStateOf<PlacementEntity?>(null) }
    
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var destinationFamilyId by remember { mutableStateOf(TextFieldValue("")) }
    var startDate by remember { mutableStateOf(TextFieldValue("")) }
    var endDate by remember { mutableStateOf(TextFieldValue("")) }
    var placementType by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(TextFieldValue("")) }

    val placementTypes = listOf("Foster Home", "Adoption Home", "Kinship Care", "Residential Care", "Hospital", "Other")
    var showTypeDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.placementDao().observeAll().collectLatest { list -> items = list }
    }
    
    // Fetch from API
    LaunchedEffect(Unit) {
        fetchPlacementsFromApi(repository, scope) { loading, error ->
            isLoading = loading
            errorMessage = error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Placements") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Placement")
            }
        }
    ) { padding ->
    Column(Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No placements yet", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { p ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.padding(12.dp).weight(1f)) {
                                Text("Placement #${p.placementId}", style = MaterialTheme.typography.titleMedium)
                                Text("Child ID: ${p.childId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                p.destinationFamilyId?.let { Text("Family ID: $it", style = MaterialTheme.typography.bodySmall) }
                                Text("Start: ${p.startDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                p.endDate?.let { Text("End: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                p.placementType?.let { Text("Type: $it", style = MaterialTheme.typography.bodySmall) }
                                p.isCurrent.let { if (it) Text("Current Placement", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
                            }
                            Row(verticalAlignment = Alignment.Top) {
                                IconButton(onClick = {
                                    selectedPlacement = p
                                    showEditDialog = true
                                    childId = TextFieldValue(p.childId.toString())
                                    destinationFamilyId = TextFieldValue(p.destinationFamilyId?.toString() ?: "")
                                    startDate = TextFieldValue(p.startDate)
                                    endDate = TextFieldValue(p.endDate ?: "")
                                    placementType = p.placementType ?: "Foster Home"
                                    notes = TextFieldValue(p.notes ?: "")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = {
                                    selectedPlacement = p
                                    showDeleteDialog = true
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                                IconButton(onClick = {
                                    val csvContent = "ID,Child ID,Family ID,Start Date,End Date,Type,Notes,Current\n${p.placementId},${p.childId},${p.destinationFamilyId},${p.startDate},${p.endDate},${p.placementType},${p.notes},${p.isCurrent}"
                                }) {
                                    Icon(Icons.Default.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.secondary)
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
                title = { Text("Add Placement") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") }, singleLine = true)
                        OutlinedTextField(value = destinationFamilyId, onValueChange = { destinationFamilyId = it }, label = { Text("Destination Family ID") }, singleLine = true)
                        OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start date (YYYY-MM-DD)") }, singleLine = true)
                        OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("End date (YYYY-MM-DD, optional)") }, singleLine = true)
                        ExposedDropdownMenuBox(expanded = showTypeDropdown, onExpandedChange = { showTypeDropdown = !showTypeDropdown }) {
                            OutlinedTextField(
                                value = placementType,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Placement Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = showTypeDropdown, onDismissRequest = { showTypeDropdown = false }) {
                                placementTypes.forEach { type ->
                                    DropdownMenuItem(text = { Text(type) }, onClick = {
                                        placementType = type
                                        showTypeDropdown = false
                                    })
                                }
                            }
                        }
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") }, singleLine = true)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val cid = childId.text.toIntOrNull()
                        val fid = destinationFamilyId.text.toIntOrNull()
                        if (cid != null && startDate.text.isNotBlank()) {
                            scope.launch {
                                db.placementDao().insertWithSync(
                                    PlacementEntity(
                                        childId = cid,
                                        destinationFamilyId = fid,
                                        startDate = startDate.text,
                                        endDate = endDate.text.ifBlank { null },
                                        placementType = placementType,
                                        notes = notes.text.ifBlank { null },
                                        isCurrent = true
                                    ),
                                    db.syncQueueDao()
                                )
                                showCreate = false
                                childId = TextFieldValue("")
                                destinationFamilyId = TextFieldValue("")
                                startDate = TextFieldValue("")
                                endDate = TextFieldValue("")
                                placementType = "Foster Home"
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
        if (showEditDialog && selectedPlacement != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit Placement") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") }, singleLine = true, enabled = false)
                        OutlinedTextField(value = destinationFamilyId, onValueChange = { destinationFamilyId = it }, label = { Text("Destination Family ID") }, singleLine = true)
                        OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start date (YYYY-MM-DD)") }, singleLine = true)
                        OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("End date (YYYY-MM-DD, optional)") }, singleLine = true)
                        ExposedDropdownMenuBox(expanded = showTypeDropdown, onExpandedChange = { showTypeDropdown = !showTypeDropdown }) {
                            OutlinedTextField(
                                value = placementType,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Placement Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = showTypeDropdown, onDismissRequest = { showTypeDropdown = false }) {
                                placementTypes.forEach { type ->
                                    DropdownMenuItem(text = { Text(type) }, onClick = {
                                        placementType = type
                                        showTypeDropdown = false
                                    })
                                }
                            }
                        }
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") }, singleLine = true)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val fid = destinationFamilyId.text.toIntOrNull()
                        if (startDate.text.isNotBlank()) {
                            scope.launch {
                                val updated = selectedPlacement!!.copy(
                                    destinationFamilyId = fid,
                                    startDate = startDate.text,
                                    endDate = endDate.text.ifBlank { null },
                                    placementType = placementType,
                                    notes = notes.text.ifBlank { null }
                                )
                                db.placementDao().updateWithSync(updated, db.syncQueueDao())
                                showEditDialog = false
                                selectedPlacement = null
                            }
                        }
                    }) { Text("Update") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false; selectedPlacement = null }) { Text("Cancel") }
                }
            )
        }
        if (showDeleteDialog && selectedPlacement != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false; selectedPlacement = null },
                title = { Text("Delete Placement") },
                text = { Text("Are you sure you want to delete placement #${selectedPlacement!!.placementId}?") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            db.placementDao().deleteByIdWithSync(selectedPlacement!!.placementId, db.syncQueueDao())
                            showDeleteDialog = false
                            selectedPlacement = null
                        }
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false; selectedPlacement = null }) { Text("Cancel") }
                }
            )
        }
    }
    }
}

/**
 * Helper function to fetch placements from API.
 */
private fun fetchPlacementsFromApi(
    repository: PlacementRepositoryImpl,
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
            onLoading(false, "Failed to fetch placements: ${e.message}")
        }
    }
}