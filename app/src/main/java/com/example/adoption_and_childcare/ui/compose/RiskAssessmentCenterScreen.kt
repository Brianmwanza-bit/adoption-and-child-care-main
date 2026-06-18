package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
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
                title = { Text(stringResource(R.string.risk_assessment_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.risk_assessment_back))
                    }
                },
                actions = {
                    IconButton(onClick = { /* Help/Info */ }) {
                        Icon(Icons.Default.Info, contentDescription = stringResource(R.string.risk_assessment_info))
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
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.risk_assessment_conduct))
            }
        }
    ) { scPadding -> // scPadding: The padding provided by Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scPadding)
        ) {
            // Safety Overview Section
            SafetyOverviewHeader(assessments)

            // Assessment List
            Text(
                stringResource(R.string.risk_assessment_recent_header),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (assessments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.risk_assessment_empty), color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(assessments) { itemAssessment ->
                        AssessmentCardModern(itemAssessment)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        // This would be a full form in a real app
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.risk_assessment_dialog_title)) },
            text = { Text(stringResource(R.string.risk_assessment_dialog_text)) },
            confirmButton = {
                TextButton(onClick = { showAddDialog = false }) { Text(stringResource(R.string.risk_assessment_begin)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text(stringResource(R.string.risk_assessment_cancel)) }
            }
        )
    }
}

/**
 * Header section displaying the safety score and high-risk case count.
 *
 * @param assessments The list of risk assessments to calculate the overview from.
 */
@Composable
private fun SafetyOverviewHeader(assessments: List<RiskAssessmentEntity>) {
    val criticalKey = stringResource(R.string.risk_assessment_level_critical)
    val highKey = stringResource(R.string.risk_assessment_level_high)
    val averageScore = if (assessments.isNotEmpty()) assessments.map { it.safetyScore ?: 0 }.average().toInt() else 0
    val riskCount = assessments.count { it.riskLevel?.lowercase() == criticalKey || it.riskLevel?.lowercase() == highKey }

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
                Text(stringResource(R.string.risk_assessment_agency_score), style = MaterialTheme.typography.labelMedium, color = Color(0xFF880E4F))
                Text("$averageScore / 100", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                Text(stringResource(R.string.risk_assessment_high_risk_cases, riskCount), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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

/**
 * A modern card displaying details for a specific risk assessment.
 *
 * @param assessment The risk assessment entity to display.
 */
@Composable
private fun AssessmentCardModern(assessment: RiskAssessmentEntity) {
    val criticalKey = stringResource(R.string.risk_assessment_level_critical)
    val highKey = stringResource(R.string.risk_assessment_level_high)
    val mediumKey = stringResource(R.string.risk_assessment_level_medium)
    val lowKey = stringResource(R.string.risk_assessment_level_low)

    val riskColor = when (assessment.riskLevel?.lowercase()) {
        criticalKey -> Color(0xFFC62828)
        highKey -> Color(0xFFE91E63)
        mediumKey -> Color(0xFFFF9800)
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
                    text = stringResource(R.string.risk_assessment_child_id_label, assessment.childId),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.risk_assessment_date_label, assessment.assessmentDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(assessment.riskLevel?.uppercase() ?: lowKey.uppercase(), riskColor)
                    Text(
                        stringResource(R.string.risk_assessment_score_label, assessment.safetyScore ?: 0),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

/**
 * A small chip displaying a status label with a background color.
 *
 * @param label The text to display in the chip.
 * @param color The theme color to apply to the text and background.
 */
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
