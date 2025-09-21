package com.yourdomain.adoptionchildcare.ui.compose

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
import com.yourdomain.adoptionchildcare.data.db.AppDatabase
import com.yourdomain.adoptionchildcare.data.db.entities.AdoptionApplicationEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AdoptionApplicationsScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var apps by remember { mutableStateOf<List<AdoptionApplicationEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var familyId by remember { mutableStateOf(TextFieldValue("")) }
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var status by remember { mutableStateOf(TextFieldValue("Pending")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.adoptionApplicationDao().observeAll().collectLatest { list ->
            apps = list
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Application")
            }
        }
    ) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
        Text(text = "Adoption Applications", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (apps.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No applications yet")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(apps) { a ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Application #${a.applicationId}")
                            Text("Family ID: ${a.familyId}")
                            a.status?.let { Text("Status: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                title = { Text("Add Application") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text("Family ID") })
                        OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID (optional)") })
                        OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") })
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val fam = familyId.text.toIntOrNull()
                        val child = childId.text.toIntOrNull()
                        if (fam != null) {
                            scope.launch {
                                db.adoptionApplicationDao().insert(
                                    AdoptionApplicationEntity(
                                        familyId = fam,
                                        childId = child,
                                        status = status.text.ifBlank { "Pending" },
                                        notes = notes.text.ifBlank { null }
                                    )
                                )
                                showCreate = false
                                familyId = TextFieldValue("")
                                childId = TextFieldValue("")
                                status = TextFieldValue("Pending")
                                notes = TextFieldValue("")
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
