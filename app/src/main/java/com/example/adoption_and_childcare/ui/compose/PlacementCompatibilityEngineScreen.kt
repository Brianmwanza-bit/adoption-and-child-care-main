package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.entities.PlacementCompatibilityEntity
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementCompatibilityEngineScreen(onBack: () -> Unit, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val records by viewModel.compatibilityRecords.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compatibility Engine") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF009688), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(Modifier.fillMaxWidth().background(Color(0xFF009688)).padding(16.dp)) {
                Text("Matching Analysis: Comparing Child Needs vs. Provider Capacity", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }
            
            if (records.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No matching records generated.", color = Color.Gray) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(records) { record ->
                        CompatibilityAnalysisCard(record)
                    }
                }
            }
        }
    }
}

@Composable
private fun CompatibilityAnalysisCard(record: PlacementCompatibilityEntity) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("MATCH SCORE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("${record.compatibilityScore?.toInt()}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF009688))
                }
                Surface(shape = CircleShape, color = Color(0xFFE0F2F1)) {
                    Text("ID: ${record.childId} ↔ ${record.familyId}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF004D40))
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            ScoreProgressRow("Medical Support", record.medicalNeedsSupport ?: 0.0)
            ScoreProgressRow("Behavioral Support", record.behavioralNeedsSupport ?: 0.0)
            ScoreProgressRow("Emotional Capacity", record.emotionalSupportCapacity ?: 0.0)
            ScoreProgressRow("Cultural Fit", record.culturalFitScore ?: 0.0)
            
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            
            Text("Notes: ${record.notes ?: "No additional notes"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
private fun ScoreProgressRow(label: String, score: Double) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text("${score.toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(progress = { (score / 100.0).toFloat() }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = Color(0xFF009688), trackColor = Color(0xFFB2DFDB))
    }
}
