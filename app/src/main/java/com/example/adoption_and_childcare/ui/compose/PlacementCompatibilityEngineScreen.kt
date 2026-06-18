package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.PlacementCompatibilityEntity
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel

/**
 * Screen for analyzing and displaying placement compatibility scores between children and families.
 *
 * @param onBack Callback for navigating back to the previous screen.
 * @param viewModel ViewModel providing access to compatibility analysis data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementCompatibilityEngineScreen(onBack: () -> Unit, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val records by viewModel.compatibilityRecords.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.compatibility_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.compatibility_back)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF009688), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding -> // padding: the scaffold padding
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(Modifier.fillMaxWidth().background(Color(0xFF009688)).padding(16.dp)) {
                Text(stringResource(R.string.compatibility_analysis_header), color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }
            
            if (records.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.compatibility_no_records), color = Color.Gray) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(records) { record -> // record: a compatibility record
                        CompatibilityAnalysisCard(record)
                    }
                }
            }
        }
    }
}

/**
 * Card displaying detailed compatibility scores for a specific child-family match.
 *
 * @param record The compatibility entity containing scores and metadata.
 */
@Composable
private fun CompatibilityAnalysisCard(record: PlacementCompatibilityEntity) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(stringResource(R.string.compatibility_match_score), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("${record.compatibilityScore?.toInt()}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF009688))
                }
                Surface(shape = CircleShape, color = Color(0xFFE0F2F1)) {
                    Text(
                        stringResource(R.string.compatibility_id_format, record.childId, record.familyId),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF004D40)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            ScoreProgressRow(stringResource(R.string.compatibility_medical_support), record.medicalNeedsSupport ?: 0.0)
            ScoreProgressRow(stringResource(R.string.compatibility_behavioral_support), record.behavioralNeedsSupport ?: 0.0)
            ScoreProgressRow(stringResource(R.string.compatibility_emotional_capacity), record.emotionalSupportCapacity ?: 0.0)
            ScoreProgressRow(stringResource(R.string.compatibility_cultural_fit), record.culturalFitScore ?: 0.0)
            
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            
            val notesText = record.notes ?: stringResource(R.string.compatibility_no_notes)
            Text(stringResource(R.string.compatibility_notes_format, notesText), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

/**
 * Reusable row for displaying a specific compatibility category with a progress bar.
 *
 * @param label The category name.
 * @param score The numerical score (0-100).
 */
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
