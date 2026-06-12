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
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var records by remember { mutableStateOf<List<EducationRecordEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<EducationRecordEntity?>(null) }
    
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var school by remember { mutableStateOf(TextFieldValue("")) }
    var grade by remember { mutableStateOf(TextFieldValue("")) }
    var enrollmentDate by remember { mutableStateOf(TextFieldValue("")) }
    var performance by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.educationRecordDao().observeAll().collectLatest { list -> records = list }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Education Records") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Education Record")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
            if (records.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No education records yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(records) { e ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text(e.schoolName, style = MaterialTheme.typography.titleMedium)
                                    Text("Child ID: ${e.childId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    e.grade?.let { Text("Grade: $it", style = MaterialTheme.typography.bodySmall) }
                                    e.enrollmentDate?.let { Text("Enrollment: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                    e.performance?.let { Text("Performance: $it", style = MaterialTheme.typography.bodySmall) }
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedRecord = e
                                        showEditDialog = true
                                        childId = TextFieldValue(e.childId.toString())
                                        school = TextFieldValue(e.schoolName)
                                        grade = TextFieldValue(e.grade ?: "")
                                        enrollmentDate = TextFieldValue(e.enrollmentDate ?: "")
                                        performance = TextFieldValue(e.performance ?: "")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedRecord = e
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
                    title = { Text("Add Education Record") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") }, singleLine = true)
                            OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text("School name") }, singleLine = true)
                            OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text("Grade") }, singleLine = true)
                            OutlinedTextField(value = enrollmentDate, onValueChange = { enrollmentDate = it }, label = { Text("Enrollment Date (YYYY-MM-DD)") }, singleLine = true)
                            OutlinedTextField(value = performance, onValueChange = { performance = it }, label = { Text("Performance (optional)") }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val cid = childId.text.toIntOrNull()
                            if (cid != null && school.text.isNotBlank()) {
                                scope.launch {
                                    db.educationRecordDao().insertWithSync(
                                        EducationRecordEntity(
                                            childId = cid,
                                            schoolName = school.text,
                                            grade = grade.text.ifBlank { null },
                                            enrollmentDate = enrollmentDate.text.ifBlank { null },
                                            performance = performance.text.ifBlank { null }
                                        ),
                                        db.syncQueueDao()
                                    )
                                    showCreate = false
                                    childId = TextFieldValue("")
                                    school = TextFieldValue("")
                                    grade = TextFieldValue("")
                                    enrollmentDate = TextFieldValue("")
                                    performance = TextFieldValue("")
                                }
                            }
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreate = false }) { Text("Cancel") }
                    }
                )
            }

            if (showEditDialog && selectedRecord != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit Education Record") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") }, singleLine = true, enabled = false)
                            OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text("School name") }, singleLine = true)
                            OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text("Grade") }, singleLine = true)
                            OutlinedTextField(value = enrollmentDate, onValueChange = { enrollmentDate = it }, label = { Text("Enrollment Date") }, singleLine = true)
                            OutlinedTextField(value = performance, onValueChange = { performance = it }, label = { Text("Performance") }, singleLine = true)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (school.text.isNotBlank()) {
                                scope.launch {
                                    val updated = selectedRecord!!.copy(
                                        schoolName = school.text,
                                        grade = grade.text.ifBlank { null },
                                        enrollmentDate = enrollmentDate.text.ifBlank { null },
                                        performance = performance.text.ifBlank { null }
                                    )
                                    db.educationRecordDao().updateWithSync(updated, db.syncQueueDao())
                                    showEditDialog = false
                                    selectedRecord = null
                                }
                            }
                        }) { Text("Update") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false; selectedRecord = null }) { Text("Cancel") }
                    }
                )
            }

            if (showDeleteDialog && selectedRecord != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false; selectedRecord = null },
                    title = { Text("Delete Education Record") },
                    text = { Text("Are you sure you want to delete the record for ${selectedRecord!!.schoolName}?") },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                db.educationRecordDao().deleteByIdWithSync(selectedRecord!!.recordId, db.syncQueueDao())
                                showDeleteDialog = false
                                selectedRecord = null
                            }
                        }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false; selectedRecord = null }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}
