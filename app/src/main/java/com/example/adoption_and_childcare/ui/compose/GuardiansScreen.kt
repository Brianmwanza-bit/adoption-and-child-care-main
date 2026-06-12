package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.GuardianEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for managing guardians of children.
 *
 * @param onBack Callback invoked when the user navigates back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardiansScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var guardians by remember { mutableStateOf<List<GuardianEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }

    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var relationship by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.guardianDao().observeAll().collectLatest { list ->
            guardians = list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guardians") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Guardian")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (guardians.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No guardians found", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(guardians) { guardian ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${guardian.firstName} ${guardian.lastName}", style = MaterialTheme.typography.titleMedium)
                                    Text("Child ID: ${guardian.childId}", style = MaterialTheme.typography.bodySmall)
                                    Text("Relationship: ${guardian.relationship}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    guardian.phone?.let { Text("Phone: $it", style = MaterialTheme.typography.bodySmall) }
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        db.guardianDao().deleteById(guardian.guardianId)
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
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
            title = { Text("Add Guardian") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") })
                    OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") })
                    OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") })
                    OutlinedTextField(value = relationship, onValueChange = { relationship = it }, label = { Text("Relationship") })
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val cid = childId.text.toIntOrNull()
                    if (cid != null && firstName.text.isNotBlank() && lastName.text.isNotBlank()) {
                        scope.launch {
                            db.guardianDao().insert(
                                GuardianEntity(
                                    childId = cid,
                                    firstName = firstName.text,
                                    lastName = lastName.text,
                                    relationship = relationship.text,
                                    phone = phone.text.ifBlank { null }
                                )
                            )
                            showCreate = false
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
