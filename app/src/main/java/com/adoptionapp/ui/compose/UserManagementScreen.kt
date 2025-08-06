package com.adoptionapp.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptionapp.viewmodel.UserManagementViewModel
import com.adoptionapp.UsersEntity

@Composable
fun UserManagementScreen(viewModel: UserManagementViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }
    val users by viewModel.users.collectAsState()
    val loading by viewModel.loading.collectAsState(initial = false)
    val error by viewModel.error.collectAsState(initial = null)
    var showDialog by remember { mutableStateOf(false) }
    var editUser by remember { mutableStateOf<UsersEntity?>(null) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    fun openDialog(user: UsersEntity?) {
        editUser = user
        username = user?.username ?: ""
        password = ""
        role = user?.role ?: ""
        showDialog = true
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
                            Button(onClick = { openDialog(user) }, modifier = Modifier.padding(end = 8.dp)) {
                                Text("Edit")
                            }
                            Button(onClick = { viewModel.deleteUser(user.user_id) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
            Button(onClick = { openDialog(null) }, modifier = Modifier.align(Alignment.End)) {
                Text("Add User")
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editUser == null) "Add User" else "Edit User") },
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
                    if (username.isNotBlank() && (editUser != null || password.isNotBlank()) && role.isNotBlank()) {
                        val user = UsersEntity(
                            user_id = editUser?.user_id ?: 0,
                            username = username,
                            password = password,
                            role = role
                        )
                        viewModel.addOrUpdateUser(user)
                        showDialog = false
                        LaunchedEffect(Unit) {
                            snackbarHostState.showSnackbar(if (editUser == null) "User added" else "User updated")
                        }
                    }
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 