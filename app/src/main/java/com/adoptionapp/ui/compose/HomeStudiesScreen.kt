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
import com.adoptionapp.data.db.entities.HomeStudyEntity
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeStudiesScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var studies by remember { mutableStateOf<List<HomeStudyEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.homeStudyDao().observeAll().collectLatest { list ->
            studies = list
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Home Studies", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (studies.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No home studies yet")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(studies) { hs ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Home Study #${hs.homeStudyId}")
                            Text("Family ID: ${hs.familyId}")
                            hs.result?.let { Text("Result: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
}
