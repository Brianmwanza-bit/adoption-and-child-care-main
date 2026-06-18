package com.example.adoption_and_childcare.ui.compose

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import com.example.adoption_and_childcare.data.repository.HomeStudyRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for managing and viewing home study assessments for families.
 * 
 * Users can view a list of assessments, add new ones, and update existing
 * study results and notes.
 * 
 * @param onBack Callback for navigating back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeStudiesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val authManager = remember { AuthManager(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { HomeStudyRepositoryImpl(db.homeStudyDao(), db.syncQueueDao(), apiService, authManager) }
    
    var studies by remember { mutableStateOf<List<HomeStudyEntity>>(emptyList()) }
    var families by remember { mutableStateOf<List<com.example.adoption_and_childcare.data.db.entities.FamilyEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedStudy by remember { mutableStateOf<HomeStudyEntity?>(null) }
    
    var familyId by remember { mutableStateOf(TextFieldValue("")) }
    var result by remember { mutableStateOf(TextFieldValue("")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }

    // Load from local DB
    LaunchedEffect(Unit) {
        db.homeStudyDao().observeAll().collectLatest { list ->
            studies = list
        }
    }
    LaunchedEffect(Unit) {
        db.familyDao().observeAll().collectLatest { list ->
            families = list
        }
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
                title = { Text(stringResource(R.string.home_studies_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.home_studies_back_desc))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.home_studies_add_desc))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (studies.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.home_studies_no_studies), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(studies) { hs ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text(stringResource(R.string.home_studies_item_id, hs.homeStudyId), style = MaterialTheme.typography.titleMedium)
                                    Text(stringResource(R.string.home_studies_family_id_label, hs.familyId), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    val family = families.find { it.familyId == hs.familyId }
                                    if (family != null) {
                                        Text(stringResource(R.string.home_studies_family_name_label, family.primaryContactName), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    }
                                    hs.result?.let { Text(stringResource(R.string.home_studies_result_label, it), style = MaterialTheme.typography.bodySmall) }
                                    hs.startedAt?.let { Text(stringResource(R.string.home_studies_started_label, it), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                    hs.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2) }
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedStudy = hs
                                        showEditDialog = true
                                        familyId = TextFieldValue(hs.familyId.toString())
                                        result = TextFieldValue(hs.result ?: "")
                                        notes = TextFieldValue(hs.notes ?: "")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.foster_tasks_edit_desc), tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedStudy = hs
                                        showDeleteDialog = true
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.foster_tasks_delete_desc), tint = MaterialTheme.colorScheme.error)
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
            title = { Text(stringResource(R.string.home_studies_add_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text(stringResource(R.string.home_studies_field_family_id)) }, singleLine = true)
                    OutlinedTextField(value = result, onValueChange = { result = it }, label = { Text(stringResource(R.string.home_studies_field_result)) }, singleLine = true)
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.home_studies_field_notes)) })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fam = familyId.text.toIntOrNull()
                    if (fam != null) {
                        scope.launch {
                            db.homeStudyDao().insertWithSync(
                                HomeStudyEntity(
                                    familyId = fam,
                                    result = result.text.ifBlank { null },
                                    notes = notes.text.ifBlank { null },
                                    startedAt = System.currentTimeMillis().toString()
                                ),
                                db.syncQueueDao()
                            )
                            showCreate = false
                            familyId = TextFieldValue("")
                            result = TextFieldValue("")
                            notes = TextFieldValue("")
                        }
                    }
                }) { Text(stringResource(R.string.home_studies_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.home_studies_cancel)) }
            }
        )
    }

    if (showEditDialog && selectedStudy != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.home_studies_edit_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text(stringResource(R.string.home_studies_field_family_id)) }, singleLine = true, enabled = false)
                    OutlinedTextField(value = result, onValueChange = { result = it }, label = { Text(stringResource(R.string.home_studies_field_result_edit)) }, singleLine = true)
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.home_studies_field_notes_edit)) })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val current = selectedStudy ?: return@launch
                        val updated = current.copy(
                            result = result.text.ifBlank { null },
                            notes = notes.text.ifBlank { null }
                        )
                        db.homeStudyDao().updateWithSync(updated, db.syncQueueDao())
                        showEditDialog = false
                        selectedStudy = null
                    }
                }) { Text(stringResource(R.string.home_studies_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedStudy = null }) { Text(stringResource(R.string.home_studies_cancel)) }
            }
        )
    }

    if (showDeleteDialog && selectedStudy != null) {
        val current = selectedStudy ?: return
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedStudy = null },
            title = { Text(stringResource(R.string.home_studies_delete_dialog_title)) },
            text = { Text(stringResource(R.string.home_studies_delete_confirm, current.homeStudyId)) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        db.homeStudyDao().deleteByIdWithSync(current.homeStudyId, db.syncQueueDao())
                        showDeleteDialog = false
                        selectedStudy = null
                    }
                }) { Text(stringResource(R.string.home_studies_delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedStudy = null }) { Text(stringResource(R.string.home_studies_cancel)) }
            }
        )
    }
}

/**
 * Helper function to fetch home studies from API.
 */
private fun fetchFromApi(
    repository: HomeStudyRepositoryImpl,
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
            onLoading(false, "Failed to fetch home studies: ${e.message}")
        }
    }
}
