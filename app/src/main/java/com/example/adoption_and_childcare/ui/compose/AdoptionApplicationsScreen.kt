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
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import com.example.adoption_and_childcare.data.repository.AdoptionApplicationRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionApplicationsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val repository = remember { AdoptionApplicationRepositoryImpl(db.adoptionApplicationDao(), apiService) }
    
    var apps by remember { mutableStateOf<List<AdoptionApplicationEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreate by remember { mutableStateOf(false) }
    var familyId by remember { mutableStateOf(TextFieldValue("")) }
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var status by remember { mutableStateOf(TextFieldValue("Pending")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }

    // Load from local DB
    LaunchedEffect(Unit) {
        db.adoptionApplicationDao().observeAll().collectLatest { list ->
            apps = list
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
                title = { Text("Adoption Applications") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Application")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
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
                                db.adoptionApplicationDao().insertWithSync(
                                    AdoptionApplicationEntity(
                                        familyId = fam,
                                        childId = child,
                                        status = status.text.ifBlank { "Pending" },
                                        notes = notes.text.ifBlank { null }
                                    ),
                                    db.syncQueueDao()
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

/**
 * Helper function to fetch adoption applications from API.
 */
private fun fetchFromApi(
    repository: AdoptionApplicationRepositoryImpl,
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
            onLoading(false, "Failed to fetch adoption applications: ${e.message}")
        }
    }
}