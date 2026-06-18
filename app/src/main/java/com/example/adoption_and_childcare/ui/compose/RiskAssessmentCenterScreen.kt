package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.entities.RiskAssessmentEntity
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel

/**
 * High-detail "Risk Assessment Center" screen.
 * 
 * Provides a specialized interface for tracking and conducting safety assessments.
 * 
 * @param onBack Callback for navigating back.
 * @param viewModel ViewModel for case tools.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskAssessmentCenterScreen(
    onBack: () -> Unit,
    viewModel: CaseToolsViewModel = hiltViewModel()
) {
    val assessments by viewModel.riskAssessments.collectAsState()
    
    // UI State for "Conduct Assessment" Dialog
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Risk Assessment Center") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Help/Info */ }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE91E63),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFE91E63),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Conduct Assessment")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Safety Overview Section
            SafetyOverviewHeader(assessments)

            // Assessment List
            Text(
                "Recent Assessments",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (assessments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No assessments conducted yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(assessments) { assessment ->
                        AssessmentCardModern(assessment)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        // This would be a full form in a real app
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Risk Assessment") },
            text = { Text("Would you like to start a new safety assessment for an active case?") },
            confirmButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Begin") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SafetyOverviewHeader(assessments: List<RiskAssessmentEntity>) {
    val averageScore = if (assessments.isNotEmpty()) assessments.map { it.safetyScore ?: 0 }.average().toInt() else 0
    val riskCount = assessments.count { it.riskLevel?.lowercase() == "critical" || it.riskLevel?.lowercase() == "high" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Agency Safety Score", style = MaterialTheme.typography.labelMedium, color = Color(0xFF880E4F))
                Text("$averageScore / 100", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                Text("$riskCount High Risk Cases", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            
            // Gauge Placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { averageScore / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFE91E63),
                    trackColor = Color(0xFFFFEBEE),
                    strokeWidth = 8.dp
                )
                Text("${averageScore}%", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun AssessmentCardModern(assessment: RiskAssessmentEntity) {
    val riskColor = when (assessment.riskLevel?.lowercase()) {
        "critical" -> Color(0xFFC62828)
        "high" -> Color(0xFFE91E63)
        "medium" -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, riskColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Risk Level Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(riskColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = riskColor)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Child ID: ${assessment.childId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Assessed on: ${assessment.assessmentDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(assessment.riskLevel?.uppercase() ?: "LOW", riskColor)
                    Text(
                        "Score: ${assessment.safetyScore}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
private fun StatusChip(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
