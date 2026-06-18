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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.CourtCaseEntity
import com.example.adoption_and_childcare.data.repository.CourtCaseRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import com.example.adoption_and_childcare.utils.AuthManager
import com.example.adoption_and_childcare.viewmodel.CourtCasesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtCasesScreen(
    onBack: () -> Unit = {},
    viewModel: CourtCasesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cases by viewModel.courtCases.collectAsState(initial = emptyList())
    val children by viewModel.children.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showCreate by remember { mutableStateOf(false) }

    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var caseNumber by remember { mutableStateOf(TextFieldValue("")) }
    var courtName by remember { mutableStateOf(TextFieldValue("")) }
    var status by remember { mutableStateOf("Pending") }
    val statuses = listOf("Pending", "Ongoing", "Closed", "Appealed")
    var showStatusDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Court Cases") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshFromApi() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
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
            // Case Summary (Building on what's available)
            if (cases.isNotEmpty()) {
                CourtCaseSummaryCard(cases)
                Spacer(Modifier.height(16.dp))
            }

            if (cases.isEmpty() && !isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Gavel, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No court cases found", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cases) { courtCase ->
                        FormRecordCard(
                            title = "CASE #${courtCase.caseNumber}",
                            subtitle = "Court: ${courtCase.courtName}",
                            pageNumber = 1,
                            onEdit = {
                                // Implement detailed edit if needed
                            },
                            onDelete = {
                                viewModel.deleteCase(courtCase.caseId)
                            },
                            onDownloadPdf = {
                                println("Generating Legal PDF for Case ${courtCase.caseNumber}")
                            },
                            headerIcon = Icons.Default.Gavel
                        ) {
                            FormDetailRow(label = "Child ID", value = courtCase.childId.toString())
                            val child = children.find { it.childId == courtCase.childId }
                            if (child != null) {
                                FormDetailRow(label = "Child Name", value = "${child.firstName} ${child.lastName}")
                            }
                            FormDetailRow(label = "Status", value = courtCase.status ?: "Pending", valueColor = MaterialTheme.colorScheme.primary)
                            FormDetailRow(label = "Hearing Date", value = courtCase.hearingDate ?: "Not set")
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
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { selectedChildId = it.childId }
                    )
                    OutlinedTextField(value = caseNumber, onValueChange = { caseNumber = it }, label = { Text("Case Number") }, singleLine = true)
                    OutlinedTextField(value = courtName, onValueChange = { courtName = it }, label = { Text("Court Name") }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = showStatusDropdown, onExpandedChange = { showStatusDropdown = !showStatusDropdown }) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
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
                    val cid = selectedChildId
                    if (cid != null && caseNumber.text.isNotBlank()) {
                        viewModel.insertCase(
                            CourtCaseEntity(
                                childId = cid,
                                caseNumber = caseNumber.text,
                                courtName = courtName.text,
                                status = status
                            )
                        )
                        showCreate = false
                        selectedChildId = null
                        caseNumber = TextFieldValue("")
                        courtName = TextFieldValue("")
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CourtCaseSummaryCard(cases: List<CourtCaseEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Legal Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Active Cases:")
                Text("${cases.count { it.status != "Closed" }}", fontWeight = FontWeight.Bold)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Ongoing Hearings:")
                Text("${cases.count { it.status == "Ongoing" }}", fontWeight = FontWeight.Medium)
            }
        }
    }
}

/**
 * Helper function to fetch court cases from API.
 */
fun fetchFromApi(
    repository: CourtCaseRepositoryImpl,
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
            onLoading(false, "Failed to fetch court cases: ${e.message}")
        }
    }
}
