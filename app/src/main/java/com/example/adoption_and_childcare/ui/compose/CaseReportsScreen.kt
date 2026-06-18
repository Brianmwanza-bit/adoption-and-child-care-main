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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.entities.CaseReportEntity
import com.example.adoption_and_childcare.viewmodel.CaseReportsViewModel

/**
 * Screen for managing and viewing case reports.
 * 
 * Provides features for listing all reports, adding new ones,
 * and performing basic CRUD operations.
 * 
 * @param onBack Callback for navigating to the previous screen.
 * @param viewModel ViewModel for handling case reports business logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseReportsScreen(
    onBack: () -> Unit = {},
    viewModel: CaseReportsViewModel = hiltViewModel()
) {
    val reports by viewModel.reports.collectAsState(initial = emptyList())
    val children by viewModel.children.collectAsState(initial = emptyList())
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()

    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var selectedReport by remember { mutableStateOf<CaseReportEntity?>(null) }
    
    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var userId by remember { mutableStateOf(TextFieldValue("")) }
    var date by remember { mutableStateOf(TextFieldValue("")) }
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }

    val currentReport = selectedReport
    if (showDetails && currentReport != null) {
        CaseReportDetailScreen(
            reportId = currentReport.reportId,
            onBack = { showDetails = false; selectedReport = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.reports_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.reports_back_desc))
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshFromApi() }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.reports_refresh_desc))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.reports_add_desc))
                }
            }
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (reports.isEmpty() && !isLoading) {
                    Box(Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.reports_no_reports), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    for ((index, r) in reports.withIndex()) {
                        FormRecordCard(
                            title = stringResource(R.string.reports_report_id, r.reportId),
                            subtitle = r.reportTitle,
                            pageNumber = index + 1,
                            onEdit = {
                                selectedReport = r
                                showEditDialog = true
                                selectedChildId = r.childId
                                userId = TextFieldValue(r.userId.toString())
                                date = TextFieldValue(r.reportDate)
                                title = TextFieldValue(r.reportTitle)
                                content = TextFieldValue(r.content)
                            },
                            onDelete = {
                                selectedReport = r
                                showDeleteDialog = true
                            },
                            onDownloadPdf = {
                                // Implement PDF Generation
                            },
                            headerIcon = Icons.Default.Assessment,
                            onClick = {
                                selectedReport = r
                                showDetails = true
                            }
                        ) {
                            FormDetailRow(label = stringResource(R.string.reports_label_child_id), value = r.childId.toString())
                            var child: com.example.adoption_and_childcare.data.db.entities.ChildEntity? = null
                            for (c in children) {
                                if (c.childId == r.childId) {
                                    child = c
                                    break
                                }
                            }
                            if (child != null) {
                                FormDetailRow(label = stringResource(R.string.reports_label_child_name), value = "${child.firstName} ${child.lastName}")
                            }
                            FormDetailRow(label = stringResource(R.string.reports_label_author_id), value = r.userId.toString())
                            FormDetailRow(label = stringResource(R.string.reports_label_report_date), value = r.reportDate)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.reports_label_content),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = r.content,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text(stringResource(R.string.reports_add_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { selectedChildId = it.childId }
                    )
                    OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text(stringResource(R.string.reports_field_user_id)) }, singleLine = true)
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text(stringResource(R.string.reports_field_date)) }, singleLine = true)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.reports_field_title)) }, singleLine = true)
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text(stringResource(R.string.reports_field_content)) })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val cid = selectedChildId
                    val uid = userId.text.toIntOrNull()
                    if (cid != null && uid != null && date.text.isNotBlank() && title.text.isNotBlank() && content.text.isNotBlank()) {
                        viewModel.insertReport(
                            CaseReportEntity(
                                childId = cid,
                                userId = uid,
                                reportDate = date.text,
                                reportTitle = title.text,
                                content = content.text
                            )
                        )
                        showCreate = false
                        selectedChildId = null
                        userId = TextFieldValue("")
                        date = TextFieldValue("")
                        title = TextFieldValue("")
                        content = TextFieldValue("")
                    }
                }) { Text(stringResource(R.string.reports_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.reports_cancel)) }
            }
        )
    }

    if (showEditDialog && selectedReport != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.reports_edit_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { selectedChildId = it.childId },
                        label = stringResource(R.string.reports_field_child_readonly)
                    )
                    OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text(stringResource(R.string.reports_field_user_id)) }, singleLine = true, enabled = false)
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text(stringResource(R.string.reports_field_date_edit)) }, singleLine = true)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.reports_field_title)) }, singleLine = true)
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text(stringResource(R.string.reports_field_content)) })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val cid = selectedChildId
                    val reportToUpdate = selectedReport
                    if (reportToUpdate != null) {
                        if (title.text.isNotBlank() && content.text.isNotBlank() && cid != null) {
                            val updated = reportToUpdate.copy(
                                childId = cid,
                                reportDate = date.text,
                                reportTitle = title.text,
                                content = content.text
                            )
                            viewModel.updateReport(updated)
                            showEditDialog = false
                            selectedReport = null
                            selectedChildId = null
                        }
                    }
                }) { Text(stringResource(R.string.reports_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedReport = null; selectedChildId = null }) { Text(stringResource(R.string.reports_cancel)) }
            }
        )
    }

    if (showDeleteDialog && selectedReport != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedReport = null },
            title = { Text(stringResource(R.string.reports_delete_dialog_title)) },
            text = { 
                val reportToDelete = selectedReport
                if (reportToDelete != null) {
                    Text(stringResource(R.string.reports_delete_confirm, reportToDelete.reportTitle))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val reportToDelete = selectedReport
                    if (reportToDelete != null) {
                        viewModel.deleteReport(reportToDelete.reportId)
                    }
                    showDeleteDialog = false
                    selectedReport = null
                }) { Text(stringResource(R.string.reports_delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedReport = null }) { Text(stringResource(R.string.reports_cancel)) }
            }
        )
    }
}

