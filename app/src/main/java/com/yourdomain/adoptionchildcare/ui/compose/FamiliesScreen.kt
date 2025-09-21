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
import com.yourdomain.adoptionchildcare.data.db.entities.FamilyEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun FamiliesScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var families by remember { mutableStateOf<List<FamilyEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var primary by remember { mutableStateOf(TextFieldValue("")) }
    var secondary by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var city by remember { mutableStateOf(TextFieldValue("")) }
    var state by remember { mutableStateOf(TextFieldValue("")) }
    var country by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.familyDao().observeAll().collectLatest { list ->
            families = list
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Family")
            }
        }
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(padding)
    ) {
        Text(text = "Families", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (families.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No families yet")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(families) { family ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(text = family.primaryContactName)
                            val details = listOfNotNull(family.city, family.state, family.country).joinToString(", ")
                            if (details.isNotBlank()) Text(details, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                title = { Text("Add Family") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = primary, onValueChange = { primary = it }, label = { Text("Primary contact name") })
                        OutlinedTextField(value = secondary, onValueChange = { secondary = it }, label = { Text("Secondary contact (optional)") })
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email (optional)") })
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone (optional)") })
                        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City (optional)") })
                        OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State (optional)") })
                        OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("Country (optional)") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (primary.text.isNotBlank()) {
                            scope.launch {
                                db.familyDao().insert(
                                    FamilyEntity(
                                        primaryContactName = primary.text,
                                        secondaryContactName = secondary.text.ifBlank { null },
                                        email = email.text.ifBlank { null },
                                        phone = phone.text.ifBlank { null },
                                        city = city.text.ifBlank { null },
                                        state = state.text.ifBlank { null },
                                        country = country.text.ifBlank { null }
                                    )
                                )
                                showCreate = false
                                primary = TextFieldValue("")
                                secondary = TextFieldValue("")
                                email = TextFieldValue("")
                                phone = TextFieldValue("")
                                city = TextFieldValue("")
                                state = TextFieldValue("")
                                country = TextFieldValue("")
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
