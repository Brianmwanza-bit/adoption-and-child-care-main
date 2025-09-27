package com.adoptionapp.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptionapp.viewmodel.BackgroundChecksViewModel
import com.adoptionapp.BackgroundChecksEntity

@Composable
fun BackgroundChecksScreen(viewModel: BackgroundChecksViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.loadChecks()
    }
    val checks by viewModel.checks.observeAsState(emptyList())
    val loading by viewModel.loading.collectAsState(initial = false)
    val error by viewModel.error.collectAsState(initial = null)
    var showDialog by remember { mutableStateOf(false) }
    var editCheck by remember { mutableStateOf<BackgroundChecksEntity?>(null) }
    var userId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    fun openDialog(check: BackgroundChecksEntity?) {
        editCheck = check
        userId = check?.user_id?.toString() ?: ""
        status = check?.status ?: ""
        result = check?.result ?: ""
        showDialog = true
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Background Checks", style = MaterialTheme.typography.headlineMedium)
        if (loading) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            checks.forEach { check ->
                Card(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Check: ${check.check_id}, User: ${check.user_id}, Status: ${check.status}")
                        Row {
                            Button(onClick = { openDialog(check) }, modifier = Modifier.padding(end = 8.dp)) {
                                Text("Edit")
                            }
                            Button(onClick = { viewModel.deleteCheck(check.check_id) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
            Button(onClick = { openDialog(null) }, modifier = Modifier.align(Alignment.End)) {
                Text("Trigger New Check")
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editCheck == null) "Trigger Background Check" else "Edit Background Check") },
            text = {
                Column {
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = { Text("User ID") },
                        enabled = editCheck == null
                    )
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status") }
                    )
                    OutlinedTextField(
                        value = result,
                        onValueChange = { result = it },
                        label = { Text("Result") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (userId.isNotBlank() && status.isNotBlank()) {
                        val check = BackgroundChecksEntity(
                            check_id = editCheck?.check_id ?: 0,
                            user_id = userId.toIntOrNull() ?: 0,
                            status = status,
                            result = if (result.isBlank()) null else result,
                            requested_at = editCheck?.requested_at,
                            completed_at = editCheck?.completed_at
                        )
                        if (editCheck == null) {
                            viewModel.addCheck(check)
                            LaunchedEffect(Unit) { snackbarHostState.showSnackbar("Background check triggered!") }
                        } else {
                            viewModel.updateCheck(check)
                            LaunchedEffect(Unit) { snackbarHostState.showSnackbar("Background check updated!") }
                        }
                        showDialog = false
                        userId = ""
                        status = ""
                        result = ""
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