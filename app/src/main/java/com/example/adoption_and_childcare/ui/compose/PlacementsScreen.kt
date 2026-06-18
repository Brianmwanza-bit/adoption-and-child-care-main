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
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import com.example.adoption_and_childcare.data.repository.PlacementRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import com.example.adoption_and_childcare.viewmodel.PlacementsViewModel
import kotlinx.coroutines.launch

/**
 * Screen for managing child placements.
 * Provides functionality to view, add, edit, and delete placements.
 *
 * @param onBack Callback invoked when the user navigates back.
 * @param viewModel ViewModel for managing placement state and operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementsScreen(
    onBack: () -> Unit = {},
    viewModel: PlacementsViewModel = hiltViewModel()
) {
    val placements by viewModel.placements.collectAsState(initial = emptyList())
    val children by viewModel.children.collectAsState(initial = emptyList())
    val families by viewModel.families.collectAsState(initial = emptyList())
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var selectedPlacement by remember { mutableStateOf<PlacementEntity?>(null) }
    
    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var destinationFamilyId by remember { mutableStateOf(TextFieldValue("")) }
    var startDate by remember { mutableStateOf(TextFieldValue("")) }
    var endDate by remember { mutableStateOf(TextFieldValue("")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    val typeFoster = stringResource(R.string.placement_type_foster)
    val typeAdoption = stringResource(R.string.placement_type_adoption)
    val typeKinship = stringResource(R.string.placement_type_kinship)
    val typeResidential = stringResource(R.string.placement_type_residential)
    val typeHospital = stringResource(R.string.placement_type_hospital)
    val typeOther = stringResource(R.string.placement_type_other)

    var placementType by remember { mutableStateOf(typeFoster) }

    val placementTypes = listOf(typeFoster, typeAdoption, typeKinship, typeResidential, typeHospital, typeOther)
    var showTypeDropdown by remember { mutableStateOf(false) }

    if (showDetails && selectedPlacement != null) {
        val currentPlacement = selectedPlacement ?: return
        PlacementProfileScreen(
            placement = currentPlacement,
            child = children.find { it.childId == currentPlacement.childId },
            family = families.find { it.familyId == currentPlacement.destinationFamilyId },
            onBack = { showDetails = false; selectedPlacement = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.placements_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshFromApi() }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.placements_refresh_desc))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.placements_add_desc))
                }
            }
        ) { innerPadding ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Placement Summary
                if (placements.isNotEmpty()) {
                    PlacementSummaryCard(placements)
                }

                if (placements.isEmpty() && !isLoading) {
                    Box(Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.placements_no_records), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    placements.forEachIndexed { index, placement ->
                        FormRecordCard(
                            title = stringResource(R.string.placements_record_title_format, placement.placementId),
                            subtitle = stringResource(R.string.placements_type_subtitle, placement.placementType ?: stringResource(R.string.search_na)),
                            pageNumber = index + 1,
                            onEdit = {
                                selectedPlacement = placement
                                showEditDialog = true
                                selectedChildId = placement.childId
                                destinationFamilyId = TextFieldValue(placement.destinationFamilyId?.toString() ?: "")
                                startDate = TextFieldValue(placement.startDate)
                                endDate = TextFieldValue(placement.endDate ?: "")
                                placementType = placement.placementType ?: typeFoster
                                notes = TextFieldValue(placement.notes ?: "")
                            },
                            onDelete = {
                                selectedPlacement = placement
                                showDeleteDialog = true
                            },
                            onDownloadPdf = {
                                // Implement PDF Generation
                            },
                            headerIcon = Icons.Default.Place,
                            onClick = {
                                selectedPlacement = placement
                                showDetails = true
                            }
                        ) {
                            FormDetailRow(label = stringResource(R.string.placements_label_child_id), value = placement.childId.toString())
                            val child = children.find { it.childId == placement.childId }
                            if (child != null) {
                                FormDetailRow(label = stringResource(R.string.placements_label_child_name), value = "${child.firstName} ${child.lastName}")
                            }
                            FormDetailRow(label = stringResource(R.string.placements_label_family_id), value = placement.destinationFamilyId?.toString() ?: stringResource(R.string.search_na))
                            val family = families.find { it.familyId == placement.destinationFamilyId }
                            if (family != null) {
                                FormDetailRow(label = stringResource(R.string.placements_label_family_name), value = family.primaryContactName)
                            }
                            FormDetailRow(label = stringResource(R.string.placements_label_start_date), value = placement.startDate)
                            FormDetailRow(label = stringResource(R.string.placements_label_end_date), value = placement.endDate ?: stringResource(R.string.placements_label_active))
                            FormDetailRow(
                                label = stringResource(R.string.placements_label_status),
                                value = if (placement.isCurrent) stringResource(R.string.placements_status_current) else stringResource(R.string.placements_status_past),
                                valueColor = if (placement.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )

                            if (!placement.notes.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.placements_label_notes),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = placement.notes,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
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
            title = { Text(stringResource(R.string.placements_add_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { child -> selectedChildId = child.childId }
                    )
                    OutlinedTextField(value = destinationFamilyId, onValueChange = { destinationFamilyId = it }, label = { Text(stringResource(R.string.placements_field_family_id)) }, singleLine = true)
                    OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text(stringResource(R.string.placements_field_start_date)) }, singleLine = true)
                    OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text(stringResource(R.string.placements_field_end_date)) }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = showTypeDropdown, onExpandedChange = { showTypeDropdown = !showTypeDropdown }) {
                        OutlinedTextField(
                            value = placementType,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.placements_field_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showTypeDropdown, onDismissRequest = { showTypeDropdown = false }) {
                            placementTypes.forEach { typeName ->
                                DropdownMenuItem(text = { Text(typeName) }, onClick = {
                                    placementType = typeName
                                    showTypeDropdown = false
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.placements_field_notes)) }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val cid = selectedChildId
                    val fid = destinationFamilyId.text.toIntOrNull()
                    if (cid != null && startDate.text.isNotBlank()) {
                        viewModel.insertPlacement(
                            PlacementEntity(
                                childId = cid,
                                destinationFamilyId = fid,
                                startDate = startDate.text,
                                endDate = endDate.text.ifBlank { null },
                                placementType = placementType,
                                notes = notes.text.ifBlank { null },
                                isCurrent = true
                            )
                        )
                        showCreate = false
                        selectedChildId = null
                        destinationFamilyId = TextFieldValue("")
                        startDate = TextFieldValue("")
                        endDate = TextFieldValue("")
                        placementType = typeFoster
                        notes = TextFieldValue("")
                    }
                }) { Text(stringResource(R.string.finance_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.finance_cancel)) }
            }
        )
    }

    if (showEditDialog && selectedPlacement != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.placements_edit_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { child -> selectedChildId = child.childId },
                        label = stringResource(R.string.finance_child_readonly)
                    )
                    OutlinedTextField(value = destinationFamilyId, onValueChange = { destinationFamilyId = it }, label = { Text(stringResource(R.string.placements_field_family_id)) }, singleLine = true)
                    OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text(stringResource(R.string.placements_field_start_date)) }, singleLine = true)
                    OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text(stringResource(R.string.placements_field_end_date)) }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = showTypeDropdown, onExpandedChange = { showTypeDropdown = !showTypeDropdown }) {
                        OutlinedTextField(
                            value = placementType,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.placements_field_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showTypeDropdown, onDismissRequest = { showTypeDropdown = false }) {
                            placementTypes.forEach { typeName ->
                                DropdownMenuItem(text = { Text(typeName) }, onClick = {
                                    placementType = typeName
                                    showTypeDropdown = false
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.placements_field_notes)) }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fid = destinationFamilyId.text.toIntOrNull()
                    val cid = selectedChildId
                    val currentPlacement = selectedPlacement ?: return@TextButton
                    if (startDate.text.isNotBlank() && cid != null) {
                        val updated = currentPlacement.copy(
                            childId = cid,
                            destinationFamilyId = fid,
                            startDate = startDate.text,
                            endDate = endDate.text.ifBlank { null },
                            placementType = placementType,
                            notes = notes.text.ifBlank { null }
                        )
                        viewModel.updatePlacement(updated)
                        showEditDialog = false
                        selectedPlacement = null
                        selectedChildId = null
                    }
                }) { Text(stringResource(R.string.finance_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedPlacement = null; selectedChildId = null }) { Text(stringResource(R.string.finance_cancel)) }
            }
        )
    }

    if (showDeleteDialog) {
        selectedPlacement?.let { currentPlacement ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false; selectedPlacement = null },
                title = { Text(stringResource(R.string.placements_delete_title)) },
                text = { Text(stringResource(R.string.placements_delete_confirm, currentPlacement.placementId)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deletePlacement(currentPlacement.placementId)
                        showDeleteDialog = false
                        selectedPlacement = null
                    }) { Text(stringResource(R.string.finance_delete), color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false; selectedPlacement = null }) { Text(stringResource(R.string.finance_cancel)) }
                }
            )
        }
    }
}

/**
 * Card displaying a summary of placement data.
 *
 * @param placements List of placements.
 */
@Composable
fun PlacementSummaryCard(placements: List<PlacementEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.placements_summary_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.placements_summary_current))
                Text("${placements.count { it.isCurrent }}", fontWeight = FontWeight.Bold)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.placements_summary_history))
                Text("${placements.size}", fontWeight = FontWeight.Medium)
            }
        }
    }
}
