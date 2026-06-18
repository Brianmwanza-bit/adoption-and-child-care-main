package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.GuardianEntity
import com.example.adoption_and_childcare.utils.AuthManager
import com.example.adoption_and_childcare.viewmodel.GuardiansViewModel
import kotlinx.coroutines.launch

/**
 * Screen for managing guardians.
 * 
 * @param onBack Callback for navigating back.
 * @param viewModel ViewModel for business logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardiansScreen(
    onBack: () -> Unit = {},
    viewModel: GuardiansViewModel = hiltViewModel()
) {
    val guardians by viewModel.guardians.collectAsState(initial = emptyList())
    val children by viewModel.children.collectAsState(initial = emptyList())
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var selectedGuardian by remember { mutableStateOf<GuardianEntity?>(null) }

    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var relationship by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }

    val currentGuardian = selectedGuardian
    if (showDetails && currentGuardian != null) {
        GuardianDetailScreen(
            guardian = currentGuardian,
            child = children.find { it.childId == currentGuardian.childId },
            onBack = { showDetails = false; selectedGuardian = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.guardians_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.guardians_back_desc))
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshFromApi() }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.guardians_refresh_desc))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.guardians_add_desc))
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Guardian Summary
                if (guardians.isNotEmpty()) {
                    GuardianSummaryCard(guardians)
                    Spacer(Modifier.height(16.dp))
                }

                if (guardians.isEmpty() && !isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SupervisorAccount, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.guardians_no_found), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(guardians) { guardian ->
                            FormRecordCard(
                                title = stringResource(R.string.guardians_record_id, guardian.guardianId),
                                subtitle = "${guardian.firstName} ${guardian.lastName}",
                                pageNumber = 1,
                                onEdit = {
                                    selectedGuardian = guardian
                                    showEditDialog = true
                                    selectedChildId = guardian.childId
                                    firstName = TextFieldValue(guardian.firstName)
                                    lastName = TextFieldValue(guardian.lastName)
                                    relationship = TextFieldValue(guardian.relationship)
                                    phone = TextFieldValue(guardian.phone ?: "")
                                },
                                onDelete = {
                                    viewModel.deleteGuardian(guardian.guardianId)
                                },
                                headerIcon = Icons.Default.SupervisorAccount,
                                onClick = {
                                    selectedGuardian = guardian
                                    showDetails = true
                                }
                            ) {
                                FormDetailRow(label = stringResource(R.string.guardians_label_child_id), value = guardian.childId.toString())
                                val child = children.find { it.childId == guardian.childId }
                                if (child != null) {
                                    FormDetailRow(label = stringResource(R.string.guardians_label_child_name), value = "${child.firstName} ${child.lastName}")
                                }
                                FormDetailRow(label = stringResource(R.string.guardians_label_relationship), value = guardian.relationship, valueColor = MaterialTheme.colorScheme.primary)
                                FormDetailRow(label = stringResource(R.string.guardians_label_phone), value = guardian.phone ?: stringResource(R.string.guardians_not_set))
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
            title = { Text(stringResource(R.string.guardians_add_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { selectedChildId = it.childId }
                    )
                    OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text(stringResource(R.string.guardians_field_first_name)) })
                    OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text(stringResource(R.string.guardians_field_last_name)) })
                    OutlinedTextField(value = relationship, onValueChange = { relationship = it }, label = { Text(stringResource(R.string.guardians_label_relationship)) })
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(stringResource(R.string.guardians_label_phone)) })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val cid = selectedChildId
                    if (cid != null && firstName.text.isNotBlank() && lastName.text.isNotBlank()) {
                        viewModel.insertGuardian(
                            GuardianEntity(
                                childId = cid,
                                firstName = firstName.text,
                                lastName = lastName.text,
                                relationship = relationship.text,
                                phone = phone.text.ifBlank { null }
                            )
                        )
                        showCreate = false
                        selectedChildId = null
                        firstName = TextFieldValue("")
                        lastName = TextFieldValue("")
                        relationship = TextFieldValue("")
                        phone = TextFieldValue("")
                    }
                }) { Text(stringResource(R.string.guardians_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.guardians_cancel)) }
            }
        )
    }

    if (showEditDialog && selectedGuardian != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.guardians_edit_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { selectedChildId = it.childId },
                        label = stringResource(R.string.guardians_child_readonly)
                    )
                    OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text(stringResource(R.string.guardians_field_first_name)) })
                    OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text(stringResource(R.string.guardians_field_last_name)) })
                    OutlinedTextField(value = relationship, onValueChange = { relationship = it }, label = { Text(stringResource(R.string.guardians_label_relationship)) })
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(stringResource(R.string.guardians_label_phone)) })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (firstName.text.isNotBlank() && lastName.text.isNotBlank()) {
                        val current = selectedGuardian ?: return@TextButton
                        val updated = current.copy(
                            firstName = firstName.text,
                            lastName = lastName.text,
                            relationship = relationship.text,
                            phone = phone.text.ifBlank { null }
                        )
                        viewModel.updateGuardian(updated)
                        showEditDialog = false
                        selectedGuardian = null
                        selectedChildId = null
                    }
                }) { Text(stringResource(R.string.guardians_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedGuardian = null }) { Text(stringResource(R.string.guardians_cancel)) }
            }
        )
    }
}

@Composable
fun GuardianSummaryCard(guardians: List<GuardianEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.guardians_summary_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.guardians_total_registered))
                Text("${guardians.size}", fontWeight = FontWeight.Bold)
            }
            val uniqueChildren = guardians.map { it.childId }.distinct().size
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.guardians_children_covered))
                Text("$uniqueChildren", fontWeight = FontWeight.Medium)
            }
        }
    }
}

/**
 * Helper function to fetch guardians from API.
 */
fun fetchFromApi(
    repository: com.example.adoption_and_childcare.data.repository.GuardianRepositoryImpl,
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
                onLoading(false, null)
            }
        } catch (_: Exception) {
            onLoading(false, null)
        }
    }
}
