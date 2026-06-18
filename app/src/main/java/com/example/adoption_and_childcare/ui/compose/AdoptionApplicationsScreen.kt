package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import com.example.adoption_and_childcare.data.repository.AdoptionApplicationRepository
import com.example.adoption_and_childcare.utils.AuthManager
import com.example.adoption_and_childcare.viewmodel.AdoptionApplicationsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionApplicationsScreen(
    onBack: () -> Unit = {},
    viewModel: AdoptionApplicationsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val apps by viewModel.applications.collectAsState(initial = emptyList())
    val children by viewModel.children.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showCreate by remember { mutableStateOf(false) }
    var familyId by remember { mutableStateOf(TextFieldValue("")) }
    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var status by remember { mutableStateOf(TextFieldValue("Pending")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adoption Applications") },
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
                Icon(Icons.Default.Add, contentDescription = "Add Application")
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
            if (apps.isEmpty() && !isLoading) {
                Box(Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                    Text("No applications yet")
                }
            } else {
                apps.forEachIndexed { index, a ->
                    FormRecordCard(
                        title = "ADOPTION APPLICATION #${a.applicationId}",
                        subtitle = "Application No: ${a.applicationNumber ?: "N/A"}",
                        pageNumber = index + 1,
                        onEdit = {
                            // Implement Edit logic
                        },
                        onDelete = {
                            viewModel.deleteApplication(a.applicationId)
                        },
                        onDownloadPdf = {
                            // Implement PDF Generation
                        },
                        headerIcon = Icons.Default.AssignmentTurnedIn
                    ) {
                        FormDetailRow(label = "Family ID", value = a.familyId.toString())
                        FormDetailRow(label = "Child ID", value = a.childId?.toString() ?: "N/A")
                        val child = children.find { it.childId == a.childId }
                        if (child != null) {
                            FormDetailRow(label = "Child Name", value = "${child.firstName} ${child.lastName}")
                        }
                        FormDetailRow(label = "Status", value = a.status ?: "Pending")
                        FormDetailRow(label = "Submitted Date", value = a.submittedAt ?: "N/A")

                        // Real-world: Adoption Progress Stage Tracker
                        Spacer(modifier = Modifier.height(16.dp))
                        AdoptionStageTracker(a.status ?: "Pending")

                        if (!a.notes.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = a.notes,
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
                title = { Text("Add Application") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text("Family ID") })
                        SearchableChildSelector(
                            children = children,
                            selectedChildId = selectedChildId,
                            onChildSelected = { selectedChildId = it.childId },
                            label = "Child (optional)"
                        )
                        OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") })
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val fam = familyId.text.toIntOrNull()
                        val child = selectedChildId
                        if (fam != null) {
                            viewModel.insertApplication(
                                AdoptionApplicationEntity(
                                    familyId = fam,
                                    childId = child,
                                    status = status.text.ifBlank { "Pending" },
                                    notes = notes.text.ifBlank { null }
                                )
                            )
                            showCreate = false
                            familyId = TextFieldValue("")
                            selectedChildId = null
                            status = TextFieldValue("Pending")
                            notes = TextFieldValue("")
                        }
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showCreate = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun AdoptionStageTracker(currentStatus: String) {
    val stages = listOf("Applied", "Review", "Home Study", "Placement", "Finalized")
    val currentIdx = when (currentStatus.lowercase()) {
        "applied", "pending" -> 0
        "review", "processing" -> 1
        "home study", "study" -> 2
        "placement", "matched" -> 3
        "finalized", "approved" -> 4
        else -> 0
    }

    Column {
        Text("Adoption Journey", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            stages.forEachIndexed { index, stage ->
                val color = if (index <= currentIdx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(Modifier.size(24.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
                        if (index < currentIdx) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        else Text("${index + 1}", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                    Text(stage, style = MaterialTheme.typography.labelSmall, color = color, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}

/**
 * Helper function to fetch adoption applications from API.
 */
fun fetchFromApi(
    repository: AdoptionApplicationRepository,
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
            onLoading(false, "Failed to fetch adoption applications: ${e.message}")
        }
    }
}
