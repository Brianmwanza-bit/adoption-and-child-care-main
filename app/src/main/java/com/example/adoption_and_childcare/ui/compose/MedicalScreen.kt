package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.clickable
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
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.data.repository.MedicalRecordRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalScreen(
    onBack: () -> Unit = {},
    onRecordClick: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val authManager = remember { AuthManager(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { MedicalRecordRepositoryImpl(db.medicalRecordDao(), db.syncQueueDao(), apiService, authManager) }
    
    var records by remember { mutableStateOf<List<MedicalRecordEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<MedicalRecordEntity?>(null) }
    
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var visitDate by remember { mutableStateOf(TextFieldValue("")) }
    var hospitalName by remember { mutableStateOf(TextFieldValue("")) }
    var diagnosis by remember { mutableStateOf(TextFieldValue("")) }
    var treatment by remember { mutableStateOf(TextFieldValue("")) }

    // Load from local DB
    LaunchedEffect(Unit) {
        db.medicalRecordDao().observeAll().collectLatest { list -> records = list }
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
                title = { Text("Medical Records") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Medical Record")
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
            if (records.isEmpty()) {
                Box(Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No medical records yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                records.forEachIndexed { index, r ->
                    FormRecordCard(
                        title = "MEDICAL RECORD #${r.recordId}",
                        subtitle = "Hospital: ${r.hospitalName ?: "N/A"}",
                        pageNumber = index + 1,
                        onEdit = {
                            selectedRecord = r
                            showEditDialog = true
                            childId = TextFieldValue(r.childId.toString())
                            visitDate = TextFieldValue(r.visitDate)
                            hospitalName = TextFieldValue(r.hospitalName ?: "")
                            diagnosis = TextFieldValue(r.diagnosis ?: "")
                            treatment = TextFieldValue(r.treatment ?: "")
                        },
                        onDelete = {
                            selectedRecord = r
                            showDeleteDialog = true
                        },
                        onDownloadPdf = {
                            // TODO: Implement PDF Generation
                        },
                        headerIcon = Icons.Default.LocalHospital
                    ) {
                        FormDetailRow(label = "Child ID", value = r.childId.toString())
                        FormDetailRow(label = "Visit Date", value = r.visitDate)
                        FormDetailRow(label = "Diagnosis", value = r.diagnosis ?: "N/A")
                        
                        if (!r.treatment.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Treatment",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = r.treatment!!,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            if (showCreate) {
                AlertDialog(
                    onDismissRequest = { showCreate = false },
                    title = { Text("Add Medical Record") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") }, singleLine = true)
                            OutlinedTextField(value = visitDate, onValueChange = { visitDate = it }, label = { Text("Visit date (YYYY-MM-DD)") }, singleLine = true)
                            OutlinedTextField(value = hospitalName, onValueChange = { hospitalName = it }, label = { Text("Hospital name (optional)") }, singleLine = true)
                            OutlinedTextField(value = diagnosis, onValueChange = { diagnosis = it }, label = { Text("Diagnosis (optional)") }, singleLine = true)
                            OutlinedTextField(value = treatment, onValueChange = { treatment = it }, label = { Text("Treatment (optional)") }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = childId.text.toIntOrNull()
                            if (cid != null && visitDate.text.isNotBlank()) {
                                scope.launch {
                                    db.medicalRecordDao().insertWithSync(
                                        MedicalRecordEntity(
                                            childId = cid,
                                            visitDate = visitDate.text,
                                            hospitalName = hospitalName.text.ifBlank { null },
                                            diagnosis = diagnosis.text.ifBlank { null },
                                            treatment = treatment.text.ifBlank { null }
                                        ),
                                        db.syncQueueDao()
                                    )
                                    showCreate = false
                                    childId = TextFieldValue("")
                                    visitDate = TextFieldValue("")
                                    hospitalName = TextFieldValue("")
                                    diagnosis = TextFieldValue("")
                                    treatment = TextFieldValue("")
                                }
                            }
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreate = false }) { Text("Cancel") }
                    }
                )
            }

            if (showEditDialog && selectedRecord != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit Medical Record") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") }, singleLine = true, enabled = false)
                            OutlinedTextField(value = visitDate, onValueChange = { visitDate = it }, label = { Text("Visit date (YYYY-MM-DD)") }, singleLine = true)
                            OutlinedTextField(value = hospitalName, onValueChange = { hospitalName = it }, label = { Text("Hospital name (optional)") }, singleLine = true)
                            OutlinedTextField(value = diagnosis, onValueChange = { diagnosis = it }, label = { Text("Diagnosis (optional)") }, singleLine = true)
                            OutlinedTextField(value = treatment, onValueChange = { treatment = it }, label = { Text("Treatment (optional)") }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = childId.text.toIntOrNull()
                            if (cid != null && visitDate.text.isNotBlank()) {
                                scope.launch {
                                    val updated = selectedRecord!!.copy(
                                        visitDate = visitDate.text,
                                        hospitalName = hospitalName.text.ifBlank { null },
                                        diagnosis = diagnosis.text.ifBlank { null },
                                        treatment = treatment.text.ifBlank { null }
                                    )
                                    db.medicalRecordDao().updateWithSync(updated, db.syncQueueDao())
                                    showEditDialog = false
                                    selectedRecord = null
                                }
                            }
                        }) { Text("Update") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false; selectedRecord = null }) { Text("Cancel") }
                    }
                )
            }

            if (showDeleteDialog && selectedRecord != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false; selectedRecord = null },
                    title = { Text("Delete Medical Record") },
                    text = { Text("Are you sure you want to delete medical record #${selectedRecord!!.recordId}?") },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                selectedRecord?.let {
                                    db.medicalRecordDao().deleteByIdWithSync(it.recordId, db.syncQueueDao())
                                }
                                showDeleteDialog = false
                                selectedRecord = null
                            }
                        }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false; selectedRecord = null }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}

/**
 * Helper function to fetch medical records from API.
 */
private fun fetchFromApi(
    repository: MedicalRecordRepositoryImpl,
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
            onLoading(false, "Failed to fetch medical records: ${e.message}")
        }
    }
}