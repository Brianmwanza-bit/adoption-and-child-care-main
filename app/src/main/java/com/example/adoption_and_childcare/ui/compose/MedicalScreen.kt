package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.data.repository.MedicalRecordRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import com.example.adoption_and_childcare.viewmodel.MedicalViewModel
import kotlinx.coroutines.launch

/**
 * Screen for managing medical records related to children in care.
 * Provides functionality to view, add, edit, and delete records.
 *
 * @param onBack Callback invoked when the user navigates back.
 * @param onMedicalRecordClick Callback invoked when a medical record is clicked for details.
 * @param viewModel ViewModel for managing medical record state and operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalScreen(
    onBack: () -> Unit = {},
    onMedicalRecordClick: (Int) -> Unit = {},
    viewModel: MedicalViewModel = hiltViewModel()
) {
    val records by viewModel.medicalRecords.collectAsState(initial = emptyList())
    val children by viewModel.children.collectAsState(initial = emptyList())
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<MedicalRecordEntity?>(null) }
    
    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var visitDate by remember { mutableStateOf(TextFieldValue("")) }
    var hospitalName by remember { mutableStateOf(TextFieldValue("")) }
    var diagnosis by remember { mutableStateOf(TextFieldValue("")) }
    var treatment by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.medical_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshFromApi() }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.medical_refresh_desc))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.medical_add_desc))
            }
        }
    ) { paddingValues ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Real-world Medical Summary
                if (records.isNotEmpty()) {
                    MedicalSummaryCard(records)
                }

                if (records.isEmpty() && !isLoading) {
                    Box(Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.medical_no_records), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    records.forEachIndexed { index, r ->
                        FormRecordCard(
                            title = stringResource(R.string.medical_record_title_format, r.recordId),
                            subtitle = stringResource(R.string.medical_hospital_subtitle, r.hospitalName ?: stringResource(R.string.search_na)),
                            pageNumber = index + 1,
                            onEdit = {
                                selectedRecord = r
                                showEditDialog = true
                                selectedChildId = r.childId
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
                                // Implement PDF Generation
                            },
                            headerIcon = Icons.Default.LocalHospital,
                            onClick = {
                                onMedicalRecordClick(r.recordId)
                            }
                        ) {
                            FormDetailRow(label = stringResource(R.string.reports_label_child_id), value = r.childId.toString())
                            val child = children.find { it.childId == r.childId }
                            if (child != null) {
                                FormDetailRow(label = stringResource(R.string.reports_label_child_name), value = "${child.firstName} ${child.lastName}")
                            }
                            FormDetailRow(label = stringResource(R.string.medical_label_date), value = r.visitDate)
                            FormDetailRow(label = stringResource(R.string.medical_label_diagnosis), value = r.diagnosis ?: stringResource(R.string.search_na), valueColor = MaterialTheme.colorScheme.error)
                            
                            if (!r.treatment.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.medical_label_treatment),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = r.treatment,
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
                    title = { Text(stringResource(R.string.medical_add_title)) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SearchableChildSelector(
                                children = children,
                                selectedChildId = selectedChildId,
                                onChildSelected = { selectedChildId = it.childId }
                            )
                            OutlinedTextField(value = visitDate, onValueChange = { visitDate = it }, label = { Text(stringResource(R.string.medical_field_visit_date)) }, singleLine = true)
                            OutlinedTextField(value = hospitalName, onValueChange = { hospitalName = it }, label = { Text(stringResource(R.string.medical_field_hospital)) }, singleLine = true)
                            OutlinedTextField(value = diagnosis, onValueChange = { diagnosis = it }, label = { Text(stringResource(R.string.medical_field_diagnosis)) }, singleLine = true)
                            OutlinedTextField(value = treatment, onValueChange = { treatment = it }, label = { Text(stringResource(R.string.medical_field_treatment)) }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = selectedChildId
                            if (cid != null && visitDate.text.isNotBlank()) {
                                viewModel.insertMedicalRecord(
                                    MedicalRecordEntity(
                                        childId = cid,
                                        visitDate = visitDate.text,
                                        hospitalName = hospitalName.text.ifBlank { null },
                                        diagnosis = diagnosis.text.ifBlank { null },
                                        treatment = treatment.text.ifBlank { null }
                                    )
                                )
                                showCreate = false
                                selectedChildId = null
                                visitDate = TextFieldValue("")
                                hospitalName = TextFieldValue("")
                                diagnosis = TextFieldValue("")
                                treatment = TextFieldValue("")
                            }
                        }) { Text(stringResource(R.string.finance_save)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.finance_cancel)) }
                    }
                )
            }

            if (showEditDialog && selectedRecord != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text(stringResource(R.string.medical_edit_title)) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SearchableChildSelector(
                                children = children,
                                selectedChildId = selectedChildId,
                                onChildSelected = { selectedChildId = it.childId },
                                label = stringResource(R.string.medical_child_readonly)
                            )
                            OutlinedTextField(value = visitDate, onValueChange = { visitDate = it }, label = { Text(stringResource(R.string.medical_field_visit_date)) }, singleLine = true)
                            OutlinedTextField(value = hospitalName, onValueChange = { hospitalName = it }, label = { Text(stringResource(R.string.medical_field_hospital)) }, singleLine = true)
                            OutlinedTextField(value = diagnosis, onValueChange = { diagnosis = it }, label = { Text(stringResource(R.string.medical_field_diagnosis)) }, singleLine = true)
                            OutlinedTextField(value = treatment, onValueChange = { treatment = it }, label = { Text(stringResource(R.string.medical_field_treatment)) }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = selectedChildId
                            val currentRecord = selectedRecord ?: return@TextButton
                            if (cid != null && visitDate.text.isNotBlank()) {
                                val updated = currentRecord.copy(
                                    childId = cid,
                                    visitDate = visitDate.text,
                                    hospitalName = hospitalName.text.ifBlank { null },
                                    diagnosis = diagnosis.text.ifBlank { null },
                                    treatment = treatment.text.ifBlank { null }
                                )
                                viewModel.updateMedicalRecord(updated)
                                showEditDialog = false
                                selectedRecord = null
                                selectedChildId = null
                            }
                        }) { Text(stringResource(R.string.finance_update)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false; selectedRecord = null }) { Text(stringResource(R.string.finance_cancel)) }
                    }
                )
            }

            if (showDeleteDialog && selectedRecord != null) {
                val currentRecord = selectedRecord ?: return@Scaffold
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false; selectedRecord = null },
                    title = { Text(stringResource(R.string.medical_delete_title)) },
                    text = { Text(stringResource(R.string.medical_delete_confirm, currentRecord.recordId)) },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteMedicalRecord(currentRecord.recordId)
                            showDeleteDialog = false
                            selectedRecord = null
                        }) { Text(stringResource(R.string.finance_delete), color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false; selectedRecord = null }) { Text(stringResource(R.string.finance_cancel)) }
                    }
                )
            }
        }
    }

/**
 * Card displaying a summary of medical records.
 *
 * @param records List of medical records.
 */
@Composable
fun MedicalSummaryCard(records: List<MedicalRecordEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.medical_summary_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.medical_summary_total_visits))
                Text("${records.size}", fontWeight = FontWeight.Bold)
            }
            val uniqueDiagnoses = records.mapNotNull { it.diagnosis }.distinct().size
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.medical_summary_unique_conditions))
                Text("$uniqueDiagnoses", fontWeight = FontWeight.Medium)
            }
        }
    }
}

/**
 * Helper function to fetch medical records from API.
 *
 * @param repository The medical record repository implementation.
 * @param authManager Manager for authentication tokens.
 * @param scope Coroutine scope for network operations.
 * @param onLoading Callback to update loading state and error messages.
 */
fun fetchMedicalFromApi(
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

