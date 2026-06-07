package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeStudiesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var studies by remember { mutableStateOf<List<HomeStudyEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var familyId by remember { mutableStateOf(TextFieldValue("")) }
    var result by remember { mutableStateOf(TextFieldValue("")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.homeStudyDao().observeAll().collectLatest { list ->
            studies = list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Studies") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Home Study")
            }
        }
    ) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
        if (studies.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No home studies yet")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(studies) { hs ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Home Study #${hs.homeStudyId}")
                            Text("Family ID: ${hs.familyId}")
                            hs.result?.let { Text("Result: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                title = { Text("Add Home Study") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text("Family ID") })
                        OutlinedTextField(value = result, onValueChange = { result = it }, label = { Text("Result (optional)") })
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") })
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
                                        notes = notes.text.ifBlank { null }
                                    ),
                                    db.syncQueueDao()
                                )
                                showCreate = false
                                familyId = TextFieldValue("")
                                result = TextFieldValue("")
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
