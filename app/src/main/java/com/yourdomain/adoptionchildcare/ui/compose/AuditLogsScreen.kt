package com.yourdomain.adoptionchildcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AuditLogsScreen() {
    var logs by remember { mutableStateOf(listOf(
        "User A logged in",
        "User B updated profile",
        "User C deleted a document",
        "User D changed permissions"
    )) }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Audit Logs", style = MaterialTheme.typography.headlineMedium)
        if (logs.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                Text("No logs available")
            }
        } else {
            logs.forEach { log ->
                Card(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                        Text(log, modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }
        }
    }
}


