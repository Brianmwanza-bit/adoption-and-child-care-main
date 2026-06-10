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
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for managing system users.
 *
 * @param onBack Callback invoked when the user navigates back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var users by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var role by remember { mutableStateOf("Staff") }
    val roles = listOf("Admin", "Case Worker", "Foster Parent", "Social Worker", "Supervisor", "Staff")
    var showRoleDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.userDao().observeAll().collectLatest { list ->
            users = list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (users.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No users found", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(users) { user ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(user.username, style = MaterialTheme.typography.titleMedium)
                                    Text("Role: ${user.role}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    user.email?.let { Text("Email: $it", style = MaterialTheme.typography.bodySmall) }
                                    Text("Status: ${if (user.isActive) "Active" else "Inactive"}", style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        db.userDao().deleteById(user.userId)
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
            title = { Text("Add User") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                    ExposedDropdownMenuBox(expanded = showRoleDropdown, onExpandedChange = { showRoleDropdown = !showRoleDropdown }) {
                        OutlinedTextField(
                            value = role,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Role") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRoleDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showRoleDropdown, onDismissRequest = { showRoleDropdown = false }) {
                            roles.forEach { r ->
                                DropdownMenuItem(text = { Text(r) }, onClick = {
                                    role = r
                                    showRoleDropdown = false
                                })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (username.text.isNotBlank()) {
                        scope.launch {
                            db.userDao().insert(
                                UserEntity(
                                    username = username.text,
                                    email = email.text.ifBlank { null },
                                    role = role,
                                    passwordHash = "sample_hash", // In a real app, this should be handled properly
                                    isActive = true
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
