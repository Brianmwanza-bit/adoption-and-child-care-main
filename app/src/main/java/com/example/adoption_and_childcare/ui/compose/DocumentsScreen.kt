package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun DocumentsScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.Companion.getInstance(context) }
    var docs by remember { mutableStateOf<List<DocumentEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var docType by remember { mutableStateOf(TextFieldValue("")) }
    var fileName by remember { mutableStateOf(TextFieldValue("")) }
    var filePath by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.documentDao().observeAll().collectLatest { list ->
            docs = list
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
            }
        }
    ) { padding ->
    Column(Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
        Text(text = "Documents", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (docs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No documents yet") }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(docs) { d ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(d.fileName)
                            d.documentType?.let { Text("Type: $it", style = MaterialTheme.typography.bodySmall) }
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
                        OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") })
                        OutlinedTextField(value = docType, onValueChange = { docType = it }, label = { Text("Document type") })
                        OutlinedTextField(value = fileName, onValueChange = { fileName = it }, label = { Text("File name") })
                        OutlinedTextField(value = filePath, onValueChange = { filePath = it }, label = { Text("File path") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val cid = childId.text.toIntOrNull()
                        if (cid != null && docType.text.isNotBlank() && fileName.text.isNotBlank() && filePath.text.isNotBlank()) {
                            scope.launch {
                                db.documentDao().insert(
                                    DocumentEntity(
                                        childId = cid,
                                        documentType = docType.text,
                                        fileName = fileName.text,
                                        filePath = filePath.text
                                    )
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
    }
    }
}
