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
import com.adoptionapp.data.db.entities.FamilyEntity
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FamiliesScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var families by remember { mutableStateOf<List<FamilyEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.familyDao().observeAll().collectLatest { list ->
            families = list
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
    }
}
