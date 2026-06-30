package com.example.adoption_and_childcare.ui.compose

import android.util.Log
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.viewmodel.EducationViewModel

private const val TAG = "EducationScreen"

/**
 * Screen for managing and viewing education records for children.
 *
 * @param onBack Callback to navigate back to the previous screen.
 * @param viewModel The ViewModel handling education record data and logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationScreen(
    onBack: () -> Unit = {},
    viewModel: EducationViewModel = hiltViewModel()
) {
    val records by viewModel.educationRecords.collectAsStateWithLifecycle(initialValue = emptyList())
    val children by viewModel.children.collectAsStateWithLifecycle(initialValue = emptyList())
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle(initialValue = null)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<EducationRecordEntity?>(null) }
    
    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var school by remember { mutableStateOf(TextFieldValue("")) }
    var grade by remember { mutableStateOf(TextFieldValue("")) }
    var enrollmentDate by remember { mutableStateOf(TextFieldValue("")) }
    var performance by remember { mutableStateOf(TextFieldValue("")) }

    val currentSelected = selectedRecord
    if (showDetails && currentSelected != null) {
        EducationDetailScreen(
            recordId = currentSelected.recordId,
            onBack = { showDetails = false; selectedRecord = null }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.edu_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.edu_back_desc))
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshFromApi() }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.edu_refresh_desc))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.edu_add_desc))
                }
            }
        ) { scaffoldPadding: PaddingValues ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = scaffoldPadding)
                    .verticalScroll(state = scrollState)
                    .padding(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(space = 16.dp)
            ) {
                if (records.isNotEmpty()) {
                    EducationSummaryCard(records)
                }

                if (records.isEmpty() && !isLoading) {
                    Box(Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.edu_no_records), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    for ((index, e) in records.withIndex()) {
                        FormRecordCard(
                            title = stringResource(R.string.edu_record_header, e.recordId),
                            subtitle = stringResource(R.string.edu_school_subtitle, e.schoolName),
                            pageNumber = index + 1,
                            onEdit = {
                                selectedRecord = e
                                showEditDialog = true
                                selectedChildId = e.childId
                                school = TextFieldValue(e.schoolName)
                                grade = TextFieldValue(e.grade ?: "")
                                enrollmentDate = TextFieldValue(e.enrollmentDate ?: "")
                                performance = TextFieldValue(e.performance ?: "")
                            },
                            onDelete = {
                                selectedRecord = e
                                showDeleteDialog = true
                            },
                            onDownloadPdf = {
                                Log.d(TAG, "Generating Education PDF for #${e.recordId}")
                            },
                            headerIcon = Icons.Default.School,
                            onClick = {
                                selectedRecord = e
                                showDetails = true
                            }
                        ) {
                            FormDetailRow(label = stringResource(R.string.edu_label_child_id), value = e.childId.toString())
                            var childEntity: com.example.adoption_and_childcare.data.db.entities.ChildEntity? = null
                            for (c in children) {
                                if (c.childId == e.childId) {
                                    childEntity = c
                                    break
                                }
                            }
                            if (childEntity != null) {
                                FormDetailRow(label = stringResource(R.string.edu_label_child_name), value = "${childEntity.firstName} ${childEntity.lastName}")
                            }
                            FormDetailRow(label = stringResource(R.string.edu_label_grade), value = e.grade ?: stringResource(R.string.search_na))
                            FormDetailRow(label = stringResource(R.string.edu_label_enrollment_date), value = e.enrollmentDate ?: stringResource(R.string.search_na))
                            FormDetailRow(label = stringResource(R.string.edu_label_exit_date), value = e.exitDate ?: stringResource(R.string.edu_label_still_enrolled))
                            
                            val perf = e.performance
                            if (!perf.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.edu_label_performance),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = perf,
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
                    title = { Text(stringResource(R.string.edu_add_desc)) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SearchableChildSelector(
                                children = children,
                                selectedChildId = selectedChildId,
                                onChildSelected = { selectedChildId = it.childId }
                            )
                            OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text(stringResource(R.string.edu_field_school_name)) }, singleLine = true)
                            OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text(stringResource(R.string.edu_field_grade)) }, singleLine = true)
                            OutlinedTextField(value = enrollmentDate, onValueChange = { enrollmentDate = it }, label = { Text(stringResource(R.string.edu_field_enrollment_hint)) }, singleLine = true)
                            OutlinedTextField(value = performance, onValueChange = { performance = it }, label = { Text(stringResource(R.string.edu_field_performance_hint)) }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = selectedChildId
                            if (cid != null && school.text.isNotBlank()) {
                                viewModel.insertEducationRecord(
                                    EducationRecordEntity(
                                        childId = cid,
                                        schoolName = school.text,
                                        grade = grade.text.ifBlank { null },
                                        enrollmentDate = enrollmentDate.text.ifBlank { null },
                                        performance = performance.text.ifBlank { null }
                                    )
                                )
                                showCreate = false
                                selectedChildId = null
                                school = TextFieldValue("")
                                grade = TextFieldValue("")
                                enrollmentDate = TextFieldValue("")
                                performance = TextFieldValue("")
                            }
                        }) { Text(stringResource(R.string.edu_save)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.edu_cancel)) }
                    }
                )
            }

            if (showEditDialog && selectedRecord != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text(stringResource(R.string.edu_edit_title)) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SearchableChildSelector(
                                children = children,
                                selectedChildId = selectedChildId,
                                onChildSelected = { selectedChildId = it.childId },
                                label = stringResource(R.string.search_child_label)
                            )
                            OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text(stringResource(R.string.edu_field_school_name)) }, singleLine = true)
                            OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text(stringResource(R.string.edu_field_grade)) }, singleLine = true)
                            OutlinedTextField(value = enrollmentDate, onValueChange = { enrollmentDate = it }, label = { Text(stringResource(R.string.edu_label_enrollment_date)) }, singleLine = true)
                            OutlinedTextField(value = performance, onValueChange = { performance = it }, label = { Text(stringResource(R.string.edu_label_performance)) }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = selectedChildId
                            val toUpdate = selectedRecord
                            if (cid != null && school.text.isNotBlank() && toUpdate != null) {
                                val updated = toUpdate.copy(
                                    childId = cid,
                                    schoolName = school.text,
                                    grade = grade.text.ifBlank { null },
                                    enrollmentDate = enrollmentDate.text.ifBlank { null },
                                    performance = performance.text.ifBlank { null }
                                )
                                viewModel.updateEducationRecord(updated)
                                showEditDialog = false
                                selectedRecord = null
                                selectedChildId = null
                            }
                        }) { Text(stringResource(R.string.edu_update)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false; selectedRecord = null }) { Text(stringResource(R.string.edu_cancel)) }
                    }
                )
            }

            if (showDeleteDialog && selectedRecord != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false; selectedRecord = null },
                    title = { Text(stringResource(R.string.edu_delete_title)) },
                    text = { 
                        val toDelete = selectedRecord
                        if (toDelete != null) {
                            Text(stringResource(R.string.edu_delete_confirm, toDelete.schoolName)) 
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val toDelete = selectedRecord
                            if (toDelete != null) {
                                viewModel.deleteEducationRecord(toDelete.recordId)
                            }
                            showDeleteDialog = false
                            selectedRecord = null
                        }) { Text(stringResource(R.string.edu_delete), color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false; selectedRecord = null }) { Text(stringResource(R.string.edu_cancel)) }
                    }
                )
            }
        }
    }
}

