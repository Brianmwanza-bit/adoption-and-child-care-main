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
import com.yourdomain.adoptionchildcare.data.db.entities.ChildEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ChildrenListScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var children by remember { mutableStateOf<List<ChildEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var gender by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.childDao().observeAll().collectLatest { list ->
            children = list
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Child")
            }
        }
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(padding)
    ) {
        Text(text = "Children", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (children.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No children yet")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(children) { child ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(text = listOfNotNull(child.firstName, child.middleName, child.lastName).joinToString(" "))
                            val details = buildString {
                                child.gender?.let { append("Gender: $it  ") }
                                child.dateOfBirth?.let { append("DOB: $it") }
                            }
                            if (details.isNotBlank()) Text(text = details, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                title = { Text("Add Child") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First name") })
                        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last name") })
                        OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender (optional)") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (firstName.text.isNotBlank() && lastName.text.isNotBlank()) {
                            scope.launch {
                                db.childDao().insert(
                                    ChildEntity(
                                        firstName = firstName.text,
                                        lastName = lastName.text,
                                        gender = gender.text.ifBlank { null }
                                    )
                                )
                                showCreate = false
                                firstName = TextFieldValue("")
                                lastName = TextFieldValue("")
                                gender = TextFieldValue("")
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
