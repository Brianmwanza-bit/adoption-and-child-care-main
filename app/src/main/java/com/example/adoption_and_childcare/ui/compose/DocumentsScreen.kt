package com.example.adoption_and_childcare.ui.compose

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
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
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.viewmodel.DocumentsViewModel
import kotlinx.coroutines.launch

/**
 * Screen for managing documents related to child care and adoption.
 * 
 * Users can search, filter, view, add, edit, and delete documents.
 * 
 * @param onBack Callback for navigating back.
 * @param viewModel ViewModel for document management logic.
 */
@Suppress("SpellCheckingInspection")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    onBack: () -> Unit = {},
    viewModel: DocumentsViewModel = hiltViewModel()
) {
    val docs by viewModel.documents.collectAsState(initial = emptyList())
    val children by viewModel.children.collectAsState(initial = emptyList())
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            /**
             * The error message to be displayed in the snackbar.
             */
            snackbarHostState.showSnackbar(message)
        }
    }
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedDoc by remember { mutableStateOf<DocumentEntity?>(null) }
    
    var isViewingPdf by remember { mutableStateOf(false) }

    if (isViewingPdf && selectedDoc != null) {
        val docName = selectedDoc?.fileName ?: ""
        val downloadMessage = stringResource(R.string.docs_downloading, docName)
        
        PdfViewerScreen(
            fileName = if (docName.isNotEmpty()) docName else stringResource(R.string.search_document_label),
            onBack = { isViewingPdf = false; selectedDoc = null },
            onDownload = {
                scope.launch {
                    snackbarHostState.showSnackbar(downloadMessage)
                }
            },
            onShare = {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Sharing document: $docName")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            },
            onPrint = {
                scope.launch {
                    snackbarHostState.showSnackbar("Preparing to print $docName...")
                }
            }
        )
        return
    }

    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var docType by remember { mutableStateOf(TextFieldValue("")) }
    var fileName by remember { mutableStateOf(TextFieldValue("")) }
    var filePath by remember { mutableStateOf(TextFieldValue("")) }

    // Search and Filter (Real-world enhancement)
    var searchQuery by remember { mutableStateOf("") }
    val filteredDocs = docs.filter { doc ->
        /**
         * Filter documents by file name or document type.
         */
        doc.fileName.contains(searchQuery, ignoreCase = true) || 
        (doc.documentType.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.docs_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.docs_back_desc))
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshFromApi() }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.docs_refresh_desc))
                        }
                    }
                )
                SearchBarModern(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.docs_add_desc))
            }
        }
    ) { paddingValues ->
        /**
         * Main content area of the Documents screen.
         */
        Column(Modifier.fillMaxSize().padding(16.dp).padding(paddingValues)) {
            if (filteredDocs.isEmpty() && !isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(if (searchQuery.isEmpty()) stringResource(R.string.docs_no_docs) else stringResource(R.string.docs_no_match, searchQuery), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredDocs) { docItem ->
                        /**
                         * A single document record card.
                         */
                        FormRecordCard(
                            title = stringResource(R.string.docs_item_id, docItem.documentId),
                            subtitle = docItem.fileName,
                            pageNumber = 1,
                            onEdit = {
                                selectedDoc = docItem
                                showEditDialog = true
                                selectedChildId = docItem.childId
                                docType = TextFieldValue(docItem.documentType)
                                fileName = TextFieldValue(docItem.fileName)
                                filePath = TextFieldValue(docItem.filePath ?: "")
                            },
                            onDelete = {
                                selectedDoc = docItem
                                showDeleteDialog = true
                            },
                            onDownloadPdf = {
                                selectedDoc = docItem
                                isViewingPdf = true
                            },
                            headerIcon = Icons.AutoMirrored.Filled.InsertDriveFile
                        ) {
                            FormDetailRow(label = stringResource(R.string.docs_label_child_id), value = docItem.childId.toString())
                            val child = children.find { c -> 
                                /**
                                 * Find the child associated with this document.
                                 */
                                c.childId == docItem.childId 
                            }
                            if (child != null) {
                                FormDetailRow(label = stringResource(R.string.docs_label_child_name), value = "${child.firstName} ${child.lastName}")
                            }
                            FormDetailRow(label = stringResource(R.string.docs_label_type), value = docItem.documentType)
                            val path = docItem.filePath
                            FormDetailRow(label = stringResource(R.string.docs_label_path), value = path ?: stringResource(R.string.docs_no_path))
                            val uploaded = docItem.uploadedAt
                            FormDetailRow(label = stringResource(R.string.docs_label_uploaded), value = uploaded ?: stringResource(R.string.search_na))
                        }
                    }
                }
            }

            if (showCreate) {
                AlertDialog(
                    onDismissRequest = { showCreate = false },
                    title = { Text(stringResource(R.string.docs_add_dialog_title)) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SearchableChildSelector(
                                children = children,
                                selectedChildId = selectedChildId,
                                onChildSelected = { child -> 
                                    /**
                                     * Update selected child ID when a child is selected from the list.
                                     */
                                    selectedChildId = child.childId 
                                }
                            )
                            OutlinedTextField(value = docType, onValueChange = { docType = it }, label = { Text(stringResource(R.string.docs_field_type)) }, singleLine = true)
                            OutlinedTextField(value = fileName, onValueChange = { fileName = it }, label = { Text(stringResource(R.string.docs_field_name)) }, singleLine = true)
                            OutlinedTextField(value = filePath, onValueChange = { filePath = it }, label = { Text(stringResource(R.string.docs_field_path)) }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = selectedChildId
                            if (cid != null && docType.text.isNotBlank() && fileName.text.isNotBlank() && filePath.text.isNotBlank()) {
                                viewModel.insertDocument(
                                    DocumentEntity(
                                        childId = cid,
                                        documentType = docType.text,
                                        fileName = fileName.text,
                                        filePath = filePath.text
                                    )
                                )
                                showCreate = false
                                selectedChildId = null
                                docType = TextFieldValue("")
                                fileName = TextFieldValue("")
                                filePath = TextFieldValue("")
                            }
                        }) { Text(stringResource(R.string.docs_save)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.docs_cancel)) }
                    }
                )
            }

            if (showEditDialog && selectedDoc != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text(stringResource(R.string.docs_edit_dialog_title)) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SearchableChildSelector(
                                children = children,
                                selectedChildId = selectedChildId,
                                onChildSelected = { child -> 
                                    /**
                                     * Update selected child ID for the document being edited.
                                     */
                                    selectedChildId = child.childId 
                                },
                                label = stringResource(R.string.docs_child_readonly)
                            )
                            OutlinedTextField(value = docType, onValueChange = { docType = it }, label = { Text(stringResource(R.string.docs_field_type)) }, singleLine = true)
                            OutlinedTextField(value = fileName, onValueChange = { fileName = it }, label = { Text(stringResource(R.string.docs_field_name)) }, singleLine = true)
                            OutlinedTextField(value = filePath, onValueChange = { filePath = it }, label = { Text(stringResource(R.string.docs_field_path)) }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (fileName.text.isNotBlank() && selectedChildId != null) {
                                selectedDoc?.let { docEntityToUpdate ->
                                    /**
                                     * The document entity to be updated in the database.
                                     */
                                    val updated = docEntityToUpdate.copy(
                                        childId = selectedChildId ?: docEntityToUpdate.childId,
                                        documentType = docType.text,
                                        fileName = fileName.text,
                                        filePath = filePath.text
                                    )
                                    viewModel.updateDocument(updated)
                                    showEditDialog = false
                                    selectedDoc = null
                                    selectedChildId = null
                                }
                            }
                        }) { Text(stringResource(R.string.docs_update)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false; selectedDoc = null; selectedChildId = null }) { Text(stringResource(R.string.docs_cancel)) }
                    }
                )
            }

            if (showDeleteDialog && selectedDoc != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false; selectedDoc = null },
                    title = { Text(stringResource(R.string.docs_delete_dialog_title)) },
                    text = {
                        val name = selectedDoc?.fileName ?: ""
                        Text(stringResource(R.string.docs_delete_confirm, name))
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedDoc?.let { docItemToDelete ->
                                /**
                                 * The document item selected for deletion.
                                 */
                                viewModel.deleteDocument(docItemToDelete.documentId)
                            }
                            showDeleteDialog = false
                            selectedDoc = null
                        }) { Text(stringResource(R.string.docs_delete), color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false; selectedDoc = null }) { Text(stringResource(R.string.docs_cancel)) }
                    }
                )
            }
        }
    }
}
