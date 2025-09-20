package com.adoptionapp.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.adoptionapp.data.db.AppDatabase
import com.adoptionapp.data.db.entities.DocumentEntity
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DocumentsScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var docs by remember { mutableStateOf<List<DocumentEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.documentDao().observeAll().collectLatest { list ->
            docs = list
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Documents", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (docs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No documents yet") }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(docs) { d ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(d.fileName)
                            d.documentType?.let { Text("Type: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
}
