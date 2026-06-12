package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import com.example.adoption_and_childcare.data.repository.CourtCaseRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for managing court cases related to children.
 *
 * @param onBack Callback invoked when the user navigates back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtCasesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { CourtCaseRepositoryImpl(db.courtCaseDao(), apiService) }
    
    var cases by remember { mutableStateOf<List<CourtCaseEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreate by remember { mutableStateOf(false) }

    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var caseNumber by remember { mutableStateOf(TextFieldValue("")) }
    var courtName by remember { mutableStateOf(TextFieldValue("")) }
    var status by remember { mutableStateOf("Pending") }
    val statuses = listOf("Pending", "Ongoing", "Closed", "Appealed")
    var showStatusDropdown by remember { mutableStateOf(false) }

    // Load from local DB
    LaunchedEffect(Unit) {
        db.courtCaseDao().observeAll().collectLatest { list ->
            cases = list
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
                title = { Text("Court Cases") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Case")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (cases.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No court cases found", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cases) { courtCase ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Case: ${courtCase.caseNumber}", style = MaterialTheme.typography.titleMedium)
                                    Text("Status: ${courtCase.status}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    Text("Court: ${courtCase.courtName}", style = MaterialTheme.typography.bodySmall)
                                    Text("Child ID: ${courtCase.childId}", style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        db.courtCaseDao().deleteById(courtCase.caseId)
                                    }
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

    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text("Add Court Case") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") })
                    OutlinedTextField(value = caseNumber, onValueChange = { caseNumber = it }, label = { Text("Case Number") })
                    OutlinedTextField(value = courtName, onValueChange = { courtName = it }, label = { Text("Court Name") })
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
                    val cid = childId.text.toIntOrNull()
                    if (cid != null && caseNumber.text.isNotBlank()) {
                        scope.launch {
                            db.courtCaseDao().insert(
                                CourtCaseEntity(
                                    childId = cid,
                                    caseNumber = caseNumber.text,
                                    courtName = courtName.text,
                                    status = status
                                )
                            )
                            showCreate = false
                        }
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("Cancel") }
            }
        )
    }
}

/**
 * Helper function to fetch court cases from API.
 */
private fun fetchFromApi(
    repository: CourtCaseRepositoryImpl,
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
            onLoading(false, "Failed to fetch court cases: ${e.message}")
        }
    }
}
