package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.adoption_and_childcare.data.db.entities.PermanencyPlanEntity
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel

/**
 * High-detail "Permanency Roadmap" screen.
 * 
 * Provides a visual timeline and status tracking for long-term placement goals.
 * 
 * @param onBack Callback for navigating back.
 * @param viewModel ViewModel for case tools.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermanencyRoadmapScreen(
    onBack: () -> Unit,
    viewModel: CaseToolsViewModel = hiltViewModel()
) {
    val plans by viewModel.permanencyPlans.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permanency Roadmap") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export Roadmap */ }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (plans.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No permanency plans found.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(plans) { plan ->
                        PlanRoadmapCard(plan)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanRoadmapCard(plan: PermanencyPlanEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("PLAN #${plan.planNumber ?: "N/A"}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("Child ID: ${plan.childId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                StatusBadgeModern(plan.status ?: "draft", Color(0xFF2196F3))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Goal Visual
            GoalProgressView("PRIMARY GOAL", plan.primaryGoal ?: "Not Set", 0.65f, Color(0xFF4CAF50))
            
            if (plan.concurrentPlanning) {
                Spacer(modifier = Modifier.height(12.dp))
                GoalProgressView("SECONDARY GOAL", plan.secondaryGoal ?: "Not Set", 0.30f, Color(0xFFFF9800))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // Milestones / Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DateInfoBox("Start Date", plan.startDate ?: "N/A", Icons.Default.Event)
                DateInfoBox("Next Review", plan.reviewDate ?: "N/A", Icons.Default.Update)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* View Details */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD), contentColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Full Case Roadmap", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GoalProgressView(label: String, goal: String, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
            Text("${(progress * 100).toInt()}% COMPLETE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Text(goal, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun DateInfoBox(label: String, date: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(date, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatusBadgeModern(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = label.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
