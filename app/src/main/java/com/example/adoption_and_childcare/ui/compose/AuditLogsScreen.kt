package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.AuditLogEntity
import com.example.adoption_and_childcare.data.repository.AuditLogRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for viewing system audit logs.
 *
 * @param onBack Callback invoked when the user navigates back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditLogsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    // Audit logs are read-only and fetched via sync pull endpoint
    var logs by remember { mutableStateOf<List<AuditLogEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        db.auditLogDao().observeAll().collectLatest { list ->
            logs = list
        }
    }
    
    // Fetch from API (via sync pull)
    LaunchedEffect(Unit) {
        fetchAuditLogsFromApi(db, scope) { loading, error ->
            isLoading = loading
            errorMessage = error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audit Logs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (logs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No audit logs found", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(logs) { log ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Action: ${log.action}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Table: ${log.tableName}", style = MaterialTheme.typography.bodySmall)
                                Text("Record ID: ${log.recordId}", style = MaterialTheme.typography.bodySmall)
                                Text("Changed by User: ${log.changedBy}", style = MaterialTheme.typography.bodySmall)
                                Text("Time: ${log.changedAt}", style = MaterialTheme.typography.bodySmall)
                                log.oldData?.let {
                                    Text("Old Data: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                }
                                log.newData?.let {
                                    Text("New Data: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper function to fetch audit logs from API via sync pull.
 */
private fun fetchAuditLogsFromApi(
    db: AppDatabase,
    scope: kotlinx.coroutines.CoroutineScope,
    onLoading: (Boolean, String?) -> Unit
) {
    scope.launch {
        onLoading(true, null)
        try {
            // Audit logs are fetched via sync pull endpoint
            // For now, just load from local DB
            onLoading(false, null)
        } catch (e: Exception) {
            onLoading(false, "Failed to fetch audit logs: ${e.message}")
        }
    }
}
