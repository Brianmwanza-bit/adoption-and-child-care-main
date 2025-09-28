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
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun EducationScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var records by remember { mutableStateOf<List<EducationRecordEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var school by remember { mutableStateOf(TextFieldValue("")) }
    var grade by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.educationRecordDao().observeAll().collectLatest { list -> records = list }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Education Record")
            }
        }
    ) { padding ->
    Column(Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
        Text(text = "Education", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (records.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No education records yet") }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(records) { e ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Record #${e.recordId}")
                            Text("Child ID: ${e.childId}")
                            Text("School: ${e.schoolName}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                title = { Text("Add Education Record") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") })
                        OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text("School name") })
                        OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text("Grade (optional)") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val cid = childId.text.toIntOrNull()
                        if (cid != null && school.text.isNotBlank()) {
                            scope.launch {
                                db.educationRecordDao().insert(
                                    EducationRecordEntity(
                                        childId = cid,
                                        schoolName = school.text,
                                        grade = grade.text.ifBlank { null }
                                    )
                                )
                                showCreate = false
                                childId = TextFieldValue("")
                                school = TextFieldValue("")
                                grade = TextFieldValue("")
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
