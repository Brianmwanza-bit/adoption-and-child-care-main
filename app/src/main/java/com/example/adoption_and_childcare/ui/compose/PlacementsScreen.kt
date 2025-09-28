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
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun PlacementsScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.Companion.getInstance(context) }
    var items by remember { mutableStateOf<List<PlacementEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var startDate by remember { mutableStateOf(TextFieldValue("")) }
    var type by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.placementDao().observeAll().collectLatest { list -> items = list }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Placement")
            }
        }
    ) { padding ->
    Column(Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
        Text(text = "Placements", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No placements yet") }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { p ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Placement #${p.placementId}")
                            Text("Child ID: ${p.childId}")
                            p.placementType?.let { Text("Type: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                title = { Text("Add Placement") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") })
                        OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start date (YYYY-MM-DD)") })
                        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Placement type (optional)") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val cid = childId.text.toIntOrNull()
                        if (cid != null && startDate.text.isNotBlank()) {
                            scope.launch {
                                db.placementDao().insert(
                                    PlacementEntity(
                                        childId = cid,
                                        startDate = startDate.text,
                                        placementType = type.text.ifBlank { null }
                                    )
                                )
                                showCreate = false
                                childId = TextFieldValue("")
                                startDate = TextFieldValue("")
                                type = TextFieldValue("")
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
