package com.adoptionapp.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<String>()) }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Search", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Query") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { results = if (query.isBlank()) emptyList() else listOf("Result for '$query'") }) {
            Text("Search")
        }
        if (results.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) { Text("No results") }
        } else {
            results.forEach { r ->
                Card(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) { Text(r, modifier = Modifier.padding(start = 16.dp)) }
                }
            }
        }
    }
}


