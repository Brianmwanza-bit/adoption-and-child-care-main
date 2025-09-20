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
import com.adoptionapp.data.db.entities.CaseReportEntity
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CaseReportsScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var reports by remember { mutableStateOf<List<CaseReportEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.caseReportDao().observeAll().collectLatest { list -> reports = list }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Reports & Cases", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (reports.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No reports yet") }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reports) { r ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Report #${r.reportId}")
                            Text("Child ID: ${r.childId}")
                            r.reportTitle.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
}
