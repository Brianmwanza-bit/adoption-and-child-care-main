package com.adoptionapp.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.adoptionapp.data.db.AppDatabase
import com.adoptionapp.data.db.entities.ChildEntity
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChildrenListScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var children by remember { mutableStateOf<List<ChildEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.childDao().observeAll().collectLatest { list ->
            children = list
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
    }
}
