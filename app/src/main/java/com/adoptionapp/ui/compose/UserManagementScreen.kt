package com.adoptionapp.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptionapp.viewmodel.UserManagementViewModel
import com.adoptionapp.entity.UsersEntity

@Composable
fun UserManagementScreen(viewModel: UserManagementViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }
    val users by viewModel.users.collectAsState()
    val loading by viewModel.loading.collectAsState(initial = false)
    val error by viewModel.error.collectAsState(initial = null)
    var showRegistrationDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editUser by remember { mutableStateOf<UsersEntity?>(null) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    fun openEditDialog(user: UsersEntity?) {
        editUser = user
        username = user?.username ?: ""
        password = ""
        role = user?.role ?: ""
        showEditDialog = true
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("User Management", style = MaterialTheme.typography.headlineMedium)
        if (loading) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            users.forEach { user ->
                Card(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("User: ${user.username} (${user.role})")
                        Row {
                            Button(onClick = { openEditDialog(user) }, modifier = Modifier.padding(end = 8.dp)) {
                                Text("Edit")
                            }
                            Button(onClick = { viewModel.deleteUser(user.user_id) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
            Button(onClick = { showRegistrationDialog = true }, modifier = Modifier.align(Alignment.End)) {
                Text("Add User")
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
    
    // Registration Dialog
    if (showRegistrationDialog) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RegistrationScreen(
                onRegistrationComplete = {
                    showRegistrationDialog = false
                    LaunchedEffect(Unit) {
                        snackbarHostState.showSnackbar("User registered successfully!")
                    }
                },
                onCancel = { showRegistrationDialog = false },
                viewModel = viewModel
            )
        }
    }
    
    // Edit Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit User") },
            text = {
                Column {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") }
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Role") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (username.isNotBlank() && password.isNotBlank() && role.isNotBlank()) {
                        val user = UsersEntity(
                            user_id = editUser?.user_id ?: 0,
                            username = username,
                            password = password,
                            role = role
                        )
                        viewModel.addOrUpdateUser(user)
                        showEditDialog = false
                        LaunchedEffect(Unit) {
                            snackbarHostState.showSnackbar("User updated")
                        }
                    }
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 