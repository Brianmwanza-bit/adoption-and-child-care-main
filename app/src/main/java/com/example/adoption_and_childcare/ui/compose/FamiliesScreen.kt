package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamiliesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var families by remember { mutableStateOf<List<FamilyEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var selectedFamily by remember { mutableStateOf<FamilyEntity?>(null) }
    
    var primary by remember { mutableStateOf(TextFieldValue("")) }
    var secondary by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var nationalId by remember { mutableStateOf(TextFieldValue("")) }
    var address by remember { mutableStateOf(TextFieldValue("")) }
    var city by remember { mutableStateOf(TextFieldValue("")) }
    var county by remember { mutableStateOf(TextFieldValue("")) }
    var status by remember { mutableStateOf("Active") }

    val statuses = listOf("Active", "Inactive", "Pending")
    var showStatusDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.familyDao().observeAll().collectLatest { list ->
            families = list
        }
    }

    if (showDetails && selectedFamily != null) {
        FamilyDetailsScreen(
            family = selectedFamily!!,
            onBack = { showDetails = false; selectedFamily = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Families") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Family")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(padding)
            ) {
                if (families.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FamilyRestroom, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No families yet", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(families) { family ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    selectedFamily = family
                                    showDetails = true
                                }
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(Modifier.padding(12.dp).weight(1f)) {
                                        Text(family.primaryContactName, style = MaterialTheme.typography.titleMedium)
                                        family.secondaryContactName?.let { Text("Secondary: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                        family.phone?.let { Text("Phone: $it", style = MaterialTheme.typography.bodySmall) }
                                        family.email?.let { Text("Email: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                        val details = listOfNotNull(family.city, family.county).joinToString(", ")
                                        if (details.isNotBlank()) Text(details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        family.status?.let { Text("Status: $it", style = MaterialTheme.typography.bodySmall, color = if (it == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) }
                                    }
                                    Row(verticalAlignment = Alignment.Top) {
                                        IconButton(onClick = {
                                            selectedFamily = family
                                            showEditDialog = true
                                            primary = TextFieldValue(family.primaryContactName)
                                            secondary = TextFieldValue(family.secondaryContactName ?: "")
                                            email = TextFieldValue(family.email ?: "")
                                            phone = TextFieldValue(family.phone ?: "")
                                            nationalId = TextFieldValue(family.nationalIdNo ?: "")
                                            address = TextFieldValue(family.address ?: "")
                                            city = TextFieldValue(family.city ?: "")
                                            county = TextFieldValue(family.county ?: "")
                                            status = family.status ?: "Active"
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = {
                                            selectedFamily = family
                                            showDeleteDialog = true
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
        }
    }

    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text("Add Family") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = primary, onValueChange = { primary = it }, label = { Text("Primary contact name") }, singleLine = true)
                    OutlinedTextField(value = secondary, onValueChange = { secondary = it }, label = { Text("Secondary contact (optional)") }, singleLine = true)
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email (optional)") }, singleLine = true)
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, singleLine = true)
                    OutlinedTextField(value = nationalId, onValueChange = { nationalId = it }, label = { Text("National ID (optional)") }, singleLine = true)
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address (optional)") }, singleLine = true)
                    OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City (optional)") }, singleLine = true)
                    OutlinedTextField(value = county, onValueChange = { county = it }, label = { Text("County (optional)") }, singleLine = true)
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
                    if (primary.text.isNotBlank()) {
                        scope.launch {
                            db.familyDao().insertWithSync(
                                FamilyEntity(
                                    primaryContactName = primary.text,
                                    secondaryContactName = secondary.text.ifBlank { null },
                                    email = email.text.ifBlank { null },
                                    phone = phone.text.ifBlank { null },
                                    nationalIdNo = nationalId.text.ifBlank { null },
                                    address = address.text.ifBlank { null },
                                    city = city.text.ifBlank { null },
                                    county = county.text.ifBlank { null },
                                    status = status
                                ),
                                db.syncQueueDao()
                            )
                            showCreate = false
                            primary = TextFieldValue("")
                            secondary = TextFieldValue("")
                            email = TextFieldValue("")
                            phone = TextFieldValue("")
                            nationalId = TextFieldValue("")
                            address = TextFieldValue("")
                            city = TextFieldValue("")
                            county = TextFieldValue("")
                            status = "Active"
                        }
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("Cancel") }
            }
        )
    }

    if (showEditDialog && selectedFamily != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Family") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = primary, onValueChange = { primary = it }, label = { Text("Primary contact name") }, singleLine = true)
                    OutlinedTextField(value = secondary, onValueChange = { secondary = it }, label = { Text("Secondary contact (optional)") }, singleLine = true)
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email (optional)") }, singleLine = true)
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, singleLine = true)
                    OutlinedTextField(value = nationalId, onValueChange = { nationalId = it }, label = { Text("National ID (optional)") }, singleLine = true)
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address (optional)") }, singleLine = true)
                    OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City (optional)") }, singleLine = true)
                    OutlinedTextField(value = county, onValueChange = { county = it }, label = { Text("County (optional)") }, singleLine = true)
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
                    if (primary.text.isNotBlank()) {
                        scope.launch {
                            val family = selectedFamily ?: return@launch
                            val updated = family.copy(
                                primaryContactName = primary.text,
                                secondaryContactName = secondary.text.ifBlank { null },
                                email = email.text.ifBlank { null },
                                phone = phone.text.ifBlank { null },
                                nationalIdNo = nationalId.text.ifBlank { null },
                                address = address.text.ifBlank { null },
                                city = city.text.ifBlank { null },
                                county = county.text.ifBlank { null },
                                status = status
                            )
                            db.familyDao().updateWithSync(updated, db.syncQueueDao())
                            showEditDialog = false
                            selectedFamily = null
                        }
                    }
                }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedFamily = null }) { Text("Cancel") }
            }
        )
    }

    if (showDeleteDialog && selectedFamily != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedFamily = null },
            title = { Text("Delete Family") },
            text = { Text("Are you sure you want to delete ${selectedFamily?.primaryContactName ?: ""}?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        selectedFamily?.let { family ->
                            db.familyDao().deleteByIdWithSync(family.familyId, db.syncQueueDao())
                        }
                        showDeleteDialog = false
                        selectedFamily = null
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedFamily = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyDetailsScreen(family: FamilyEntity, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = family.primaryContactName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Family ID: #${family.familyId}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            DetailSection("Contact Information") {
                DetailRow("Primary Contact", family.primaryContactName)
                DetailRow("Secondary Contact", family.secondaryContactName ?: "N/A")
                DetailRow("Phone", family.phone ?: "N/A")
                DetailRow("Email", family.email ?: "N/A")
                DetailRow("National ID", family.nationalIdNo ?: "N/A")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailSection("Address & Location") {
                DetailRow("Address", family.address ?: "N/A")
                DetailRow("City", family.city ?: "N/A")
                DetailRow("County", family.county ?: "N/A")
                DetailRow("Country", family.country ?: "N/A")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailSection("Status & Metadata") {
                DetailRow("Status", family.status ?: "Active")
                DetailRow("Sync Status", family.syncStatus)
                DetailRow("Last Synced", family.lastSyncedAt?.toString() ?: "Never")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
