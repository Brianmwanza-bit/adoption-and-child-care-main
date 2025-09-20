package com.adoptionapp.ui.compose

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
import com.adoptionapp.data.db.AppDatabase
import com.adoptionapp.data.db.entities.CaseReportEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CaseReportsScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var reports by remember { mutableStateOf<List<CaseReportEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var userId by remember { mutableStateOf(TextFieldValue("")) }
    var date by remember { mutableStateOf(TextFieldValue("")) }
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.caseReportDao().observeAll().collectLatest { list -> reports = list }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Report")
            }
        }
    ) { padding ->
    Column(Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
        Text(text = "Reports & Cases", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (reports.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No reports yet") }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reports) { r ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Report #${r.reportId}")
                            Text("Child ID: ${r.childId}")
                            r.reportTitle.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                title = { Text("Add Case Report") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") })
                        OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") })
                        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Report date (YYYY-MM-DD)") })
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val cid = childId.text.toIntOrNull()
                        val uid = userId.text.toIntOrNull()
                        if (cid != null && uid != null && date.text.isNotBlank() && title.text.isNotBlank() && content.text.isNotBlank()) {
                            scope.launch {
                                db.caseReportDao().insert(
                                    CaseReportEntity(
                                        childId = cid,
                                        userId = uid,
                                        reportDate = date.text,
                                        reportTitle = title.text,
                                        content = content.text
                                    )
                                )
                                showCreate = false
                                childId = TextFieldValue("")
                                userId = TextFieldValue("")
                                date = TextFieldValue("")
                                title = TextFieldValue("")
                                content = TextFieldValue("")
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
