package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.viewmodel.ChildrenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildrenListScreen(
    onBack: () -> Unit = {},
    viewModel: ChildrenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val children by viewModel.children.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Dialogs
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var selectedChild by remember { mutableStateOf<ChildEntity?>(null) }
    
    // Form fields
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var middleName by remember { mutableStateOf(TextFieldValue("")) }
    var gender by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf(TextFieldValue("")) }
    var currentCounty by remember { mutableStateOf(TextFieldValue("")) }
    var currentStatus by remember { mutableStateOf("Active") }

    val genders = listOf("Male", "Female")
    val statuses = listOf("Active", "Placed", "Pending", "Adopted", "Inactive")
    var showGenderDropdown by remember { mutableStateOf(false) }
    var showStatusDropdown by remember { mutableStateOf(false) }

    val current = selectedChild
    if (showDetails && current != null) {
        ChildProfileScreen(
            child = current,
            onBack = { showDetails = false; selectedChild = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.module_children)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.search_back_desc))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.card_bg_checks_summary)) // Reusing summary key for "Add"
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp)
            ) {
                if (children.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ChildCare, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.risk_assessment_empty), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(children) { child ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    selectedChild = child
                                    showDetails = true
                                }
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(Modifier.padding(12.dp).weight(1f)) {
                                        Text(listOfNotNull(child.firstName, child.middleName, child.lastName).joinToString(" "), style = MaterialTheme.typography.titleMedium)
                                        val caseNum = child.caseNumber
                                        if (caseNum != null) {
                                            Text(stringResource(R.string.map_case_format, caseNum, ""), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                        val details = buildString {
                                            val g = child.gender
                                            if (g != null) append("${stringResource(R.string.medical_label_type)}: $g  ")
                                            val dob = child.dateOfBirth
                                            if (dob != null) append("DOB: $dob")
                                        }
                                        if (details.isNotBlank()) Text(text = details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        val county = child.currentCounty
                                        if (county != null) {
                                            Text(stringResource(R.string.search_county_label, county), style = MaterialTheme.typography.bodySmall)
                                        }
                                        val status = child.currentStatus
                                        if (status != null) {
                                            Text(stringResource(R.string.search_status_label, status), style = MaterialTheme.typography.bodySmall, color = if (status == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.Top) {
                                        IconButton(onClick = {
                                            selectedChild = child
                                            showEditDialog = true
                                            firstName = TextFieldValue(child.firstName)
                                            lastName = TextFieldValue(child.lastName)
                                            middleName = TextFieldValue(child.middleName ?: "")
                                            gender = child.gender ?: ""
                                            dateOfBirth = TextFieldValue(child.dateOfBirth ?: "")
                                            currentCounty = TextFieldValue(child.currentCounty ?: "")
                                            currentStatus = child.currentStatus ?: "Active"
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.user_management_edit), tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = {
                                            selectedChild = child
                                            showDeleteDialog = true
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.user_management_delete), tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Child Dialog
    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text(stringResource(R.string.footer_action_new_case)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text(stringResource(R.string.register_username_hint)) }, singleLine = true)
                    OutlinedTextField(value = middleName, onValueChange = { middleName = it }, label = { Text(stringResource(R.string.families_field_secondary)) }, singleLine = true)
                    OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text(stringResource(R.string.families_field_primary)) }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = showGenderDropdown, onExpandedChange = { showGenderDropdown = !showGenderDropdown }) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.medical_label_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGenderDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showGenderDropdown, onDismissRequest = { showGenderDropdown = false }) {
                            genders.forEach { g ->
                                DropdownMenuItem(text = { Text(g) }, onClick = {
                                    gender = g
                                    showGenderDropdown = false
                                })
                            }
                        }
                    }
                    ExposedDropdownMenuBox(expanded = showStatusDropdown, onExpandedChange = { showStatusDropdown = !showStatusDropdown }) {
                        OutlinedTextField(
                            value = currentStatus,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.search_status_label, "")) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showStatusDropdown, onDismissRequest = { showStatusDropdown = false }) {
                            statuses.forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = {
                                    currentStatus = s
                                    showStatusDropdown = false
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = { Text(stringResource(R.string.finance_date_field)) }, singleLine = true)
                    OutlinedTextField(value = currentCounty, onValueChange = { currentCounty = it }, label = { Text(stringResource(R.string.register_county_hint)) }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (firstName.text.isNotBlank() && lastName.text.isNotBlank()) {
                        viewModel.insertChild(
                            ChildEntity(
                                firstName = firstName.text,
                                middleName = middleName.text.ifBlank { null },
                                lastName = lastName.text,
                                gender = gender.ifBlank { null },
                                dateOfBirth = dateOfBirth.text.ifBlank { null },
                                currentCounty = currentCounty.text.ifBlank { null },
                                currentStatus = currentStatus
                            )
                        )
                        showCreate = false
                        firstName = TextFieldValue("")
                        lastName = TextFieldValue("")
                        middleName = TextFieldValue("")
                        gender = ""
                        dateOfBirth = TextFieldValue("")
                        currentCounty = TextFieldValue("")
                        currentStatus = "Active"
                    }
                }) { Text(stringResource(R.string.user_management_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.user_management_cancel)) }
            }
        )
    }

    // Edit Child Dialog
    if (showEditDialog && (selectedChild != null)) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.user_management_edit)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text(stringResource(R.string.register_username_hint)) }, singleLine = true)
                    OutlinedTextField(value = middleName, onValueChange = { middleName = it }, label = { Text(stringResource(R.string.families_field_secondary)) }, singleLine = true)
                    OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text(stringResource(R.string.families_field_primary)) }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = showGenderDropdown, onExpandedChange = { showGenderDropdown = !showGenderDropdown }) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.medical_label_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGenderDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showGenderDropdown, onDismissRequest = { showGenderDropdown = false }) {
                            genders.forEach { g ->
                                DropdownMenuItem(text = { Text(g) }, onClick = {
                                    gender = g
                                    showGenderDropdown = false
                                })
                            }
                        }
                    }
                    ExposedDropdownMenuBox(expanded = showStatusDropdown, onExpandedChange = { showStatusDropdown = !showStatusDropdown }) {
                        OutlinedTextField(
                            value = currentStatus,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.search_status_label, "")) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showStatusDropdown, onDismissRequest = { showStatusDropdown = false }) {
                            statuses.forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = {
                                    currentStatus = s
                                    showStatusDropdown = false
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = { Text(stringResource(R.string.finance_date_field)) }, singleLine = true)
                    OutlinedTextField(value = currentCounty, onValueChange = { currentCounty = it }, label = { Text(stringResource(R.string.register_county_hint)) }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (firstName.text.isNotBlank() && lastName.text.isNotBlank()) {
                        val child = selectedChild ?: return@TextButton
                        val updated = child.copy(
                            firstName = firstName.text,
                            middleName = middleName.text.ifBlank { null },
                            lastName = lastName.text,
                            gender = gender.ifBlank { null },
                            dateOfBirth = dateOfBirth.text.ifBlank { null },
                            currentCounty = currentCounty.text.ifBlank { null },
                            currentStatus = currentStatus
                        )
                        viewModel.updateChild(updated)
                        showEditDialog = false
                        selectedChild = null
                    }
                }) { Text(stringResource(R.string.user_management_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedChild = null }) { Text(stringResource(R.string.user_management_cancel)) }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && (selectedChild != null)) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedChild = null },
            title = { Text(stringResource(R.string.user_management_delete)) },
            text = { Text(stringResource(R.string.user_management_delete_confirm, "${selectedChild?.firstName ?: ""} ${selectedChild?.lastName ?: ""}")) },
            confirmButton = {
                TextButton(onClick = {
                    selectedChild?.let { child ->
                        viewModel.deleteChild(child.childId)
                    }
                    showDeleteDialog = false
                    selectedChild = null
                }) { 
                    Text(stringResource(R.string.user_management_delete), color = MaterialTheme.colorScheme.error) 
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedChild = null }) { Text(stringResource(R.string.user_management_cancel)) }
            }
        )
    }
}
