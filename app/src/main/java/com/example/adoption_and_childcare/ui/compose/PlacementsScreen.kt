package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val authManager = remember { AuthManager(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { PlacementRepositoryImpl(db.placementDao(), db.syncQueueDao(), apiService, authManager) }
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No placements yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                items.forEachIndexed { index, p ->
                    FormRecordCard(
                        title = "PLACEMENT RECORD #${p.placementId}",
                        subtitle = "Type: ${p.placementType ?: "N/A"}",
                        pageNumber = index + 1,
                        onEdit = {
                            selectedPlacement = p
                            showEditDialog = true
                            childId = TextFieldValue(p.childId.toString())
                            destinationFamilyId = TextFieldValue(p.destinationFamilyId?.toString() ?: "")
                            startDate = TextFieldValue(p.startDate)
                            endDate = TextFieldValue(p.endDate ?: "")
                            placementType = p.placementType ?: "Foster Home"
                            notes = TextFieldValue(p.notes ?: "")
                        },
                        onDelete = {
                            selectedPlacement = p
                            showDeleteDialog = true
                        },
                        onDownloadPdf = {
                            // TODO: Implement PDF Generation
                        },
                        headerIcon = Icons.Default.Place
                    ) {
                        FormDetailRow(label = "Child ID", value = p.childId.toString())
                        FormDetailRow(label = "Family ID", value = p.destinationFamilyId?.toString() ?: "N/A")
                        FormDetailRow(label = "Start Date", value = p.startDate)
                        FormDetailRow(label = "End Date", value = p.endDate ?: "Active")
                        FormDetailRow(label = "Status", value = if (p.isCurrent) "Current Placement" else "Past Placement", valueColor = if (p.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)

                        if (!p.notes.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = p.notes!!,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 4.dp)
                            )
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