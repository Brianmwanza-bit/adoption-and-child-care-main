package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yourdomain.adoptionchildcare.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.entities.WorkloadTrackingEntity
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel

/**
 * Screen that displays caseworker workload and wellness metrics.
 *
 * @param onBack Callback function to navigate back to the previous screen.
 * @param viewModel ViewModel for managing workload data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkloadWellnessScreen(onBack: () -> Unit, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val workload by viewModel.workload.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.workload_wellness_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { 
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            stringResource(R.string.workload_wellness_back_desc)
                        ) 
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF673AB7), 
                    titleContentColor = Color.White, 
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Summary Card
            Card(
                modifier = Modifier.padding(16.dp), 
                shape = RoundedCornerShape(16.dp), 
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.workload_wellness_current_bandwidth), 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF512DA8)
                        )
                        Text(
                            stringResource(R.string.workload_wellness_capacity_used), 
                            style = MaterialTheme.typography.bodySmall, 
                            color = Color(0xFF512DA8)
                        )
                    }
                    Box(
                        Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White), 
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(progress = { 0.85f }, color = Color(0xFF673AB7))
                    }
                }
            }
            
            Text(
                stringResource(R.string.workload_wellness_history), 
                modifier = Modifier.padding(horizontal = 16.dp), 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold
            )
            
            workload.forEach { workloadEntry ->
                WorkloadEntryItem(workloadEntry)
            }
        }
    }
}

/**
 * Item view for a single workload tracking entry.
 *
 * @param entry The workload tracking entity to display.
 */
@Composable
private fun WorkloadEntryItem(entry: WorkloadTrackingEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), 
        shape = RoundedCornerShape(12.dp), 
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(entry.trackingDate, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        stringResource(R.string.workload_wellness_active_cases, entry.totalActiveCases), 
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.workload_wellness_urgent_flags, entry.casesWithUrgentFlags), 
                        color = Color.Red, 
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        stringResource(R.string.workload_wellness_time_logged), 
                        style = MaterialTheme.typography.labelSmall, 
                        color = Color.Gray
                    )
                    Text("${entry.timeLoggedHours}h", fontWeight = FontWeight.Bold, color = Color(0xFF673AB7))
                }
            }
        }
    }
}
