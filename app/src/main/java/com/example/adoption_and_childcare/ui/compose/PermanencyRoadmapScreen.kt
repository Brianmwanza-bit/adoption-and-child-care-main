package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.entities.PermanencyPlanEntity
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel
import com.example.adoption_and_childcare.R

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
    onBack: () -> Unit = {},
    viewModel: CaseToolsViewModel = hiltViewModel()
) {
    val plans by viewModel.permanencyPlans.collectAsState()
    val planFormat = stringResource(R.string.permanency_plans_plan_format)
    val childIdLabel = stringResource(R.string.permanency_plans_child_id_label)
    val statusDraft = stringResource(R.string.permanency_plans_status_draft)
    val primaryGoalLabel = stringResource(R.string.permanency_plans_primary_goal)
    val secondaryGoalLabel = stringResource(R.string.permanency_plans_secondary_goal)
    val notSet = stringResource(R.string.permanency_plans_not_set)
    val naVal = stringResource(R.string.permanency_plans_na)
    val startDateLabel = stringResource(R.string.permanency_plans_start_date)
    val nextReviewLabel = stringResource(R.string.permanency_plans_next_review)
    val percentCompleteFormat = stringResource(R.string.permanency_plans_percent_complete)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.permanency_roadmap_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export Roadmap */ }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = stringResource(R.string.finance_download_desc))
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
                    Text(stringResource(R.string.permanency_plans_empty), color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(plans) { plan ->
                        PlanRoadmapCard(
                            plan = plan,
                            planFormat = planFormat,
                            childIdLabel = childIdLabel,
                            statusDraft = statusDraft,
                            primaryGoalLabel = primaryGoalLabel,
                            secondaryGoalLabel = secondaryGoalLabel,
                            notSet = notSet,
                            naVal = naVal,
                            startDateLabel = startDateLabel,
                            nextReviewLabel = nextReviewLabel,
                            percentCompleteFormat = percentCompleteFormat
                        )
                    }
                }
            }
        }
    }
}

/**
 * A card representing a permanency plan roadmap entry.
 *
 * @param plan The plan data.
 * @param planFormat Format for plan number.
 * @param childIdLabel Label for child ID.
 * @param statusDraft Default status.
 * @param primaryGoalLabel Label for primary goal.
 * @param secondaryGoalLabel Label for secondary goal.
 * @param notSet Placeholder for missing goal.
 * @param naVal Placeholder for missing data.
 * @param startDateLabel Label for start date.
 * @param nextReviewLabel Label for review date.
 * @param percentCompleteFormat Format for percentage.
 */
@Composable
private fun PlanRoadmapCard(
    plan: PermanencyPlanEntity,
    planFormat: String,
    childIdLabel: String,
    statusDraft: String,
    primaryGoalLabel: String,
    secondaryGoalLabel: String,
    notSet: String,
    naVal: String,
    startDateLabel: String,
    nextReviewLabel: String,
    percentCompleteFormat: String
) {
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
                    Text(planFormat.format(plan.planNumber ?: naVal), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(childIdLabel.format(plan.childId), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                StatusBadgeModern(plan.status ?: statusDraft, Color(0xFF2196F3))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Goal Visual
            GoalProgressView(primaryGoalLabel, plan.primaryGoal ?: notSet, 0.65f, Color(0xFF4CAF50), percentCompleteFormat)
            
            if (plan.concurrentPlanning) {
                Spacer(modifier = Modifier.height(12.dp))
                GoalProgressView(secondaryGoalLabel, plan.secondaryGoal ?: notSet, 0.30f, Color(0xFFFF9800), percentCompleteFormat)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // Milestones / Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DateInfoBox(startDateLabel, plan.startDate ?: naVal, Icons.Default.Event)
                DateInfoBox(nextReviewLabel, plan.reviewDate ?: naVal, Icons.Default.Update)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* View Details */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD), contentColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.permanency_plans_view_full), fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * View displaying a goal and its progress bar.
 *
 * @param label The goal label.
 * @param goal The goal description.
 * @param progress Progress percentage (0.0 to 1.0).
 * @param color Theme color.
 * @param percentFormat Progress text format.
 */
@Composable
private fun GoalProgressView(label: String, goal: String, progress: Float, color: Color, percentFormat: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
            Text(percentFormat.format((progress * 100).toInt()), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Text(goal, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
    }
}

/**
 * Simple box showing a date and icon.
 *
 * @param label The date label.
 * @param date The date string.
 * @param icon The icon to show.
 */
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

/**
 * Modern status badge.
 *
 * @param label Badge text.
 * @param color Badge color.
 */
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
