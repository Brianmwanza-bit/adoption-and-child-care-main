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
import com.adoptionapp.data.db.entities.AdoptionApplicationEntity
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AdoptionApplicationsScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var apps by remember { mutableStateOf<List<AdoptionApplicationEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.adoptionApplicationDao().observeAll().collectLatest { list ->
            apps = list
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Adoption Applications", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (apps.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No applications yet")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(apps) { a ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Application #${a.applicationId}")
                            Text("Family ID: ${a.familyId}")
                            a.status?.let { Text("Status: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
}
