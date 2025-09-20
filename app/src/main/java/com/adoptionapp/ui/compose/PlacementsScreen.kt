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
import com.adoptionapp.data.db.entities.PlacementEntity
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PlacementsScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<PlacementEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.placementDao().observeAll().collectLatest { list -> items = list }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Placements", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No placements yet") }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { p ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Placement #${p.placementId}")
                            Text("Child ID: ${p.childId}")
                            p.placementType?.let { Text("Type: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
}
