package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adoption_and_childcare.viewmodel.AnalyticsViewModel
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription

@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = viewModel()) {
    val error by viewModel.error.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val placementsOverTime by viewModel.placementsOverTime.collectAsState()
    val childrenByStatus by viewModel.childrenByStatus.collectAsState()
    var retried by remember { mutableStateOf(false) }
    LaunchedEffect(retried) {
        viewModel.loadAnalytics()
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Analytics", modifier = Modifier.align(Alignment.CenterHorizontally).semantics { contentDescription = "Analytics Title" })
        if (loading) {
            Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { retried = !retried }) { Text("Retry") }
                }
            }
        } else {
            Card(modifier = Modifier.fillMaxWidth().height(120.dp).semantics { contentDescription = "Placements Over Time" }) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (placementsOverTime.isNotEmpty()) {
                        Text("Placements Over Time: ${placementsOverTime.joinToString()}")
                    } else {
                        Text("No placement data.")
                    }
                }
            }
            Card(modifier = Modifier.fillMaxWidth().height(120.dp).semantics { contentDescription = "Children By Status" }) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (childrenByStatus.isNotEmpty()) {
                        Text("Children by Status: ${childrenByStatus.entries.joinToString { "${it.key}: ${it.value}" }}")
                    } else {
                        Text("No children status data.")
                    }
                }
            }
            Card(modifier = Modifier.fillMaxWidth().height(120.dp).semantics { contentDescription = "Summary Stats" }) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (summary != null && summary != "No data") {
                        Text("Summary: ${summary}")
                    } else {
                        Text("No summary data.")
                    }
                }
            }
        }
    }
}
