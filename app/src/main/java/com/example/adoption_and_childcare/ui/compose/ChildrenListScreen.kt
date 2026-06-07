package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
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
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildrenListScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var children by remember { mutableStateOf<List<ChildEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
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

    LaunchedEffect(Unit) {
        db.childDao().observeAll().collectLatest { list ->
            children = list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Children") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Child")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
        ) {
            if (children.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ChildCare, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No children yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(children) { child ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text(listOfNotNull(child.firstName, child.middleName, child.lastName).joinToString(" "), style = MaterialTheme.typography.titleMedium)
                                    child.caseNumber?.let { Text("Case: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
                                    val details = buildString {
                                        child.gender?.let { append("Gender: $it  ") }
                                        child.dateOfBirth?.let { append("DOB: $it") }
                                    }
                                    if (details.isNotBlank()) Text(text = details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    child.currentCounty?.let { Text("County: $it", style = MaterialTheme.typography.bodySmall) }
                                    child.currentStatus?.let { Text("Status: $it", style = MaterialTheme.typography.bodySmall, color = if (it == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) }
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
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedChild = child
                                        showDeleteDialog = true
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                    IconButton(onClick = {
                                        // Download as CSV
                                        val csvContent = "ID,Case Number,First Name,Middle Name,Last Name,Gender,DOB,County,Status\n${child.childId},${child.caseNumber},${child.firstName},${child.middleName},${child.lastName},${child.gender},${child.dateOfBirth},${child.currentCounty},${child.currentStatus}"
                                        // Save to file implementation
                                    }) {
                                        Icon(Icons.Default.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.secondary)
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
            title = { Text("Add Child") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First name") }, singleLine = true)
                    OutlinedTextField(value = middleName, onValueChange = { middleName = it }, label = { Text("Middle name (optional)") }, singleLine = true)
                    OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last name") }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = showGenderDropdown, onExpandedChange = { showGenderDropdown = !showGenderDropdown }) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Gender") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGenderDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
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
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
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
                    OutlinedTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = { Text("Date of Birth (YYYY-MM-DD)") }, singleLine = true)
                    OutlinedTextField(value = currentCounty, onValueChange = { currentCounty = it }, label = { Text("County (optional)") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (firstName.text.isNotBlank() && lastName.text.isNotBlank()) {
                        scope.launch {
                            db.childDao().insertWithSync(
                                ChildEntity(
                                    firstName = firstName.text,
                                    middleName = middleName.text.ifBlank { null },
                                    lastName = lastName.text,
                                    gender = gender.ifBlank { null },
                                    dateOfBirth = dateOfBirth.text.ifBlank { null },
                                    currentCounty = currentCounty.text.ifBlank { null },
                                    currentStatus = currentStatus
                                ),
                                db.syncQueueDao()
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
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("Cancel") }
            }
        )
    }

    // Edit Child Dialog
    if (showEditDialog && selectedChild != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Child") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First name") }, singleLine = true)
                    OutlinedTextField(value = middleName, onValueChange = { middleName = it }, label = { Text("Middle name (optional)") }, singleLine = true)
                    OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last name") }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = showGenderDropdown, onExpandedChange = { showGenderDropdown = !showGenderDropdown }) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Gender") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGenderDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
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
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
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
                    OutlinedTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = { Text("Date of Birth (YYYY-MM-DD)") }, singleLine = true)
                    OutlinedTextField(value = currentCounty, onValueChange = { currentCounty = it }, label = { Text("County (optional)") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (firstName.text.isNotBlank() && lastName.text.isNotBlank()) {
                        scope.launch {
                            val updated = selectedChild!!.copy(
                                firstName = firstName.text,
                                middleName = middleName.text.ifBlank { null },
                                lastName = lastName.text,
                                gender = gender.ifBlank { null },
                                dateOfBirth = dateOfBirth.text.ifBlank { null },
                                currentCounty = currentCounty.text.ifBlank { null },
                                currentStatus = currentStatus
                            )
                            db.childDao().updateWithSync(updated, db.syncQueueDao())
                            showEditDialog = false
                            selectedChild = null
                        }
                    }
                }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedChild = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && selectedChild != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedChild = null },
            title = { Text("Delete Child") },
            text = { Text("Are you sure you want to delete ${selectedChild!!.firstName} ${selectedChild!!.lastName}?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        db.childDao().deleteByIdWithSync(selectedChild!!.childId, db.syncQueueDao())
                        showDeleteDialog = false
                        selectedChild = null
                    }
                }) { 
                    Text("Delete", color = MaterialTheme.colorScheme.error) 
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedChild = null }) { Text("Cancel") }
            }
        )
    }
}