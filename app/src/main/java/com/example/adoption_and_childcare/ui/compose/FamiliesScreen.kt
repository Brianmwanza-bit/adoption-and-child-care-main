package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import com.example.adoption_and_childcare.viewmodel.FamiliesViewModel
import kotlinx.coroutines.launch

/**
 * Screen for managing families in the system.
 * 
 * Provides features for listing all families, viewing profiles,
 * and performing basic CRUD operations.
 * 
 * @param onBack Callback for navigating back.
 * @param viewModel ViewModel for families management logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamiliesScreen(
    onBack: () -> Unit = {},
    viewModel: FamiliesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val families by viewModel.families.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Dialogs
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
    val statusActive = stringResource(R.string.status_active)
    val statusInactive = stringResource(R.string.status_inactive)
    val statusPending = stringResource(R.string.status_pending)

    var status by remember { mutableStateOf(statusActive) }

    val statuses = listOf(statusActive, statusInactive, statusPending)
    var showStatusDropdown by remember { mutableStateOf(false) }

    if (showDetails && selectedFamily != null) {
        FamilyHubScreen(
            family = selectedFamily!!,
            onBack = { showDetails = false; selectedFamily = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.families_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.families_back_desc))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.families_add_desc))
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
                if (families.isEmpty()) {
                    Box(Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FamilyRestroom, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.families_no_families), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    families.forEachIndexed { index, family ->
                        FormRecordCard(
                            title = stringResource(R.string.families_item_id, family.familyId),
                            subtitle = family.primaryContactName,
                            pageNumber = index + 1,
                            onEdit = {
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
                                status = family.status ?: statusActive
                            },
                            onDelete = {
                                selectedFamily = family
                                showDeleteDialog = true
                            },
                            onDownloadPdf = {
                                // TODO: Implement PDF Generation
                            },
                            headerIcon = Icons.Default.FamilyRestroom
                        ) {
                            FormDetailRow(label = stringResource(R.string.families_label_primary_contact), value = family.primaryContactName)
                            FormDetailRow(label = stringResource(R.string.families_label_phone), value = family.phone ?: stringResource(R.string.search_na))
                            FormDetailRow(label = stringResource(R.string.families_label_email), value = family.email ?: stringResource(R.string.search_na))
                            FormDetailRow(label = stringResource(R.string.families_label_location), value = listOfNotNull(family.city, family.county).joinToString(", ").ifBlank { stringResource(R.string.search_na) })
                            FormDetailRow(label = stringResource(R.string.families_label_status), value = family.status ?: statusActive, valueColor = if (family.status == statusActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            
                            if (!family.secondaryContactName.isNullOrBlank()) {
                                FormDetailRow(label = stringResource(R.string.families_label_secondary_contact), value = family.secondaryContactName)
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
            title = { Text(stringResource(R.string.families_add_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = primary, onValueChange = { primary = it }, label = { Text(stringResource(R.string.families_field_primary)) }, singleLine = true)
                    OutlinedTextField(value = secondary, onValueChange = { secondary = it }, label = { Text(stringResource(R.string.families_field_secondary)) }, singleLine = true)
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.families_field_email)) }, singleLine = true)
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(stringResource(R.string.families_label_phone)) }, singleLine = true)
                    OutlinedTextField(value = nationalId, onValueChange = { nationalId = it }, label = { Text(stringResource(R.string.families_field_national_id)) }, singleLine = true)
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text(stringResource(R.string.families_field_address)) }, singleLine = true)
                    OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text(stringResource(R.string.families_field_city)) }, singleLine = true)
                    OutlinedTextField(value = county, onValueChange = { county = it }, label = { Text(stringResource(R.string.families_field_county)) }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = showStatusDropdown, onExpandedChange = { showStatusDropdown = !showStatusDropdown }) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.families_label_status)) },
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
                    if (primary.text.isNotBlank()) {
                        viewModel.insertFamily(
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
                            )
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
                        status = statusActive
                    }
                }) { Text(stringResource(R.string.families_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.families_cancel)) }
            }
        )
    }

    if (showEditDialog && selectedFamily != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.families_edit_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = primary, onValueChange = { primary = it }, label = { Text(stringResource(R.string.families_field_primary)) }, singleLine = true)
                    OutlinedTextField(value = secondary, onValueChange = { secondary = it }, label = { Text(stringResource(R.string.families_field_secondary)) }, singleLine = true)
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.families_field_email)) }, singleLine = true)
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(stringResource(R.string.families_label_phone)) }, singleLine = true)
                    OutlinedTextField(value = nationalId, onValueChange = { nationalId = it }, label = { Text(stringResource(R.string.families_field_national_id)) }, singleLine = true)
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text(stringResource(R.string.families_field_address)) }, singleLine = true)
                    OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text(stringResource(R.string.families_field_city)) }, singleLine = true)
                    OutlinedTextField(value = county, onValueChange = { county = it }, label = { Text(stringResource(R.string.families_field_county)) }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = showStatusDropdown, onExpandedChange = { showStatusDropdown = !showStatusDropdown }) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.families_label_status)) },
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
                    if (primary.text.isNotBlank()) {
                        val family = selectedFamily ?: return@TextButton
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
                        viewModel.updateFamily(updated)
                        showEditDialog = false
                        selectedFamily = null
                    }
                }) { Text(stringResource(R.string.families_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedFamily = null }) { Text(stringResource(R.string.families_cancel)) }
            }
        )
    }

    if (showDeleteDialog && selectedFamily != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedFamily = null },
            title = { Text(stringResource(R.string.families_delete_dialog_title)) },
            text = { Text(stringResource(R.string.families_delete_confirm, selectedFamily?.primaryContactName ?: "")) },
            confirmButton = {
                TextButton(onClick = {
                    selectedFamily?.let { family ->
                        viewModel.deleteFamily(family.familyId)
                    }
                    showDeleteDialog = false
                    selectedFamily = null
                }) {
                    Text(stringResource(R.string.families_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedFamily = null }) { Text(stringResource(R.string.families_cancel)) }
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
                title = { Text(stringResource(R.string.families_profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.families_back_desc))
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
                text = stringResource(R.string.families_id_format, family.familyId),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            DetailSection(stringResource(R.string.families_section_contact)) {
                DetailRow(stringResource(R.string.families_label_primary_contact), family.primaryContactName)
                DetailRow(stringResource(R.string.families_label_secondary_contact), family.secondaryContactName ?: stringResource(R.string.search_na))
                DetailRow(stringResource(R.string.families_label_phone), family.phone ?: stringResource(R.string.search_na))
                DetailRow(stringResource(R.string.families_label_email), family.email ?: stringResource(R.string.search_na))
                DetailRow(stringResource(R.string.families_label_national_id), family.nationalIdNo ?: stringResource(R.string.search_na))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailSection(stringResource(R.string.families_section_address)) {
                DetailRow(stringResource(R.string.families_label_address), family.address ?: stringResource(R.string.search_na))
                DetailRow(stringResource(R.string.families_label_city), family.city ?: stringResource(R.string.search_na))
                DetailRow(stringResource(R.string.families_label_county), family.county ?: stringResource(R.string.search_na))
                DetailRow(stringResource(R.string.families_label_country), family.country ?: stringResource(R.string.search_na))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailSection(stringResource(R.string.families_section_status)) {
                DetailRow(stringResource(R.string.families_label_status), family.status ?: stringResource(R.string.status_active))
                DetailRow(stringResource(R.string.families_label_sync_status), family.syncStatus)
                DetailRow(stringResource(R.string.families_label_last_synced), family.lastSyncedAt?.toString() ?: stringResource(R.string.families_never_synced))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
