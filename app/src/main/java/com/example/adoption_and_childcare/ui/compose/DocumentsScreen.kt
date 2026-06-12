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
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.data.repository.DocumentRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { DocumentRepositoryImpl(db.documentDao(), apiService) }
    
    var docs by remember { mutableStateOf<List<DocumentEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedDoc by remember { mutableStateOf<DocumentEntity?>(null) }
    
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var docType by remember { mutableStateOf(TextFieldValue("")) }
    var fileName by remember { mutableStateOf(TextFieldValue("")) }
    var filePath by remember { mutableStateOf(TextFieldValue("")) }

    // Load from local DB
    LaunchedEffect(Unit) {
        db.documentDao().observeAll().collectLatest { list ->
            docs = list
        }
    }
    
    // Fetch from API
    LaunchedEffect(Unit) {
        fetchFromApi(repository, scope) { loading, error ->
            isLoading = loading
            errorMessage = error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Documents") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
            if (docs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No documents yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(docs) { d ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text(d.fileName, style = MaterialTheme.typography.titleMedium)
                                    Text("Child ID: ${d.childId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    d.documentType?.let { Text("Type: $it", style = MaterialTheme.typography.bodySmall) }
                                    d.uploadedAt?.let { Text("Uploaded: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedDoc = d
                                        showEditDialog = true
                                        childId = TextFieldValue(d.childId.toString())
                                        docType = TextFieldValue(d.documentType ?: "")
                                        fileName = TextFieldValue(d.fileName)
                                        filePath = TextFieldValue(d.filePath ?: "")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedDoc = d
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

            if (showCreate) {
                AlertDialog(
                    onDismissRequest = { showCreate = false },
                    title = { Text("Add Document") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") }, singleLine = true)
                            OutlinedTextField(value = docType, onValueChange = { docType = it }, label = { Text("Document type") }, singleLine = true)
                            OutlinedTextField(value = fileName, onValueChange = { fileName = it }, label = { Text("File name") }, singleLine = true)
                            OutlinedTextField(value = filePath, onValueChange = { filePath = it }, label = { Text("File path") }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = childId.text.toIntOrNull()
                            if (cid != null && docType.text.isNotBlank() && fileName.text.isNotBlank() && filePath.text.isNotBlank()) {
                                scope.launch {
                                    db.documentDao().insertWithSync(
                                        DocumentEntity(
                                            childId = cid,
                                            documentType = docType.text,
                                            fileName = fileName.text,
                                            filePath = filePath.text
                                        ),
                                        db.syncQueueDao()
                                    )
                                    showCreate = false
                                    childId = TextFieldValue("")
                                    docType = TextFieldValue("")
                                    fileName = TextFieldValue("")
                                    filePath = TextFieldValue("")
                                }
                            }
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreate = false }) { Text("Cancel") }
                    }
                )
            }

            if (showEditDialog && selectedDoc != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit Document") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") }, singleLine = true, enabled = false)
                            OutlinedTextField(value = docType, onValueChange = { docType = it }, label = { Text("Document type") }, singleLine = true)
                            OutlinedTextField(value = fileName, onValueChange = { fileName = it }, label = { Text("File name") }, singleLine = true)
                            OutlinedTextField(value = filePath, onValueChange = { filePath = it }, label = { Text("File path") }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (fileName.text.isNotBlank()) {
                                scope.launch {
                                    val updated = selectedDoc!!.copy(
                                        documentType = docType.text,
                                        fileName = fileName.text,
                                        filePath = filePath.text
                                    )
                                    db.documentDao().updateWithSync(updated, db.syncQueueDao())
                                    showEditDialog = false
                                    selectedDoc = null
                                }
                            }
                        }) { Text("Update") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false; selectedDoc = null }) { Text("Cancel") }
                    }
                )
            }

            if (showDeleteDialog && selectedDoc != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false; selectedDoc = null },
                    title = { Text("Delete Document") },
                    text = { Text("Are you sure you want to delete ${selectedDoc!!.fileName}?") },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                db.documentDao().deleteByIdWithSync(selectedDoc!!.documentId, db.syncQueueDao())
                                showDeleteDialog = false
                                selectedDoc = null
                            }
                        }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false; selectedDoc = null }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}

/**
 * Helper function to fetch documents from API.
 */
private fun fetchFromApi(
    repository: DocumentRepositoryImpl,
    scope: kotlinx.coroutines.CoroutineScope,
    onLoading: (Boolean, String?) -> Unit
) {
    scope.launch {
        onLoading(true, null)
        try {
            val token = "" // TODO: Get actual auth token
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
            onLoading(false, "Failed to fetch documents: ${e.message}")
        }
    }
}
