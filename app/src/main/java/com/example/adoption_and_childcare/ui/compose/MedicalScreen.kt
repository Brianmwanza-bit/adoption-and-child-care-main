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
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MedicalScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var records by remember { mutableStateOf<List<MedicalRecordEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var visitDate by remember { mutableStateOf(TextFieldValue("")) }
    var diagnosis by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.medicalRecordDao().observeAll().collectLatest { list -> records = list }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Medical Record")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
            Text(text = "Medical", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            if (records.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No medical records yet") }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(records) { r ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Record #${r.recordId}")
                                Text("Child ID: ${r.childId}")
                                r.diagnosis?.let { Text("Diagnosis: $it", style = MaterialTheme.typography.bodySmall) }
                            }
                        }
                    }
                }
            }

            if (showCreate) {
                AlertDialog(
                    onDismissRequest = { showCreate = false },
                    title = { Text("Add Medical Record") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") })
                            OutlinedTextField(value = visitDate, onValueChange = { visitDate = it }, label = { Text("Visit date (YYYY-MM-DD)") })
                            OutlinedTextField(value = diagnosis, onValueChange = { diagnosis = it }, label = { Text("Diagnosis (optional)") })
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = childId.text.toIntOrNull()
                            if (cid != null && visitDate.text.isNotBlank()) {
                                scope.launch {
                                    db.medicalRecordDao().insert(
                                        MedicalRecordEntity(
                                            childId = cid,
                                            visitDate = visitDate.text,
                                            diagnosis = diagnosis.text.ifBlank { null }
                                        )
                                    )
                                    showCreate = false
                                    childId = TextFieldValue("")
                                    visitDate = TextFieldValue("")
                                    diagnosis = TextFieldValue("")
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
