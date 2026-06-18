package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.*
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

private fun todayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

// ==================== INVESTIGATIONS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestigationsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<InvestigationEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.investigationDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Investigations", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No investigations recorded", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { inv ->
                        val statusColor = when (inv.status) { "open" -> Color(0xFFF44336); "closed" -> Color(0xFF4CAF50); else -> Color(0xFFFF9800) }
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Search, null, tint = statusColor)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(inv.caseNumber ?: "Investigation #${inv.investigationId}", fontWeight = FontWeight.Bold)
                                        Text(inv.investigationType ?: "General", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) {
                                        Text(inv.status ?: "open", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("Opened: ${inv.openedDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                inv.closedDate?.let { Text("Closed: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                inv.allegation?.let { Spacer(Modifier.height(4.dp)); Text(it, style = MaterialTheme.typography.bodySmall) }
                                inv.findings?.let { Text("Findings: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2196F3)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== SERVICE PLANS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicePlansScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<ServicePlanEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.servicePlanDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Service Plans", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.AutoMirrored.Filled.Assignment, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No service plans", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { plan ->
                        val statusColor = when (plan.status) { "active" -> Color(0xFF4CAF50); "completed" -> Color(0xFF2196F3); else -> Color(0xFFFF9800) }
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.Assignment, null, tint = statusColor)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(plan.planName, fontWeight = FontWeight.Bold)
                                        Text("Child #${plan.childId} • Started: ${plan.startDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) {
                                        Text(plan.status ?: "active", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor)
                                    }
                                }
                                plan.goalsSummary?.let { Spacer(Modifier.height(4.dp)); Text(it, style = MaterialTheme.typography.bodySmall) }
                                plan.nextReviewDate?.let { Text("Next Review: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFF9800)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== VISITATION SCHEDULES ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitationSchedulesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<VisitationScheduleEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.visitationScheduleDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Visitation Schedules", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Event, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No visitation schedules", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { v ->
                        val statusColor = when (v.status) { "scheduled" -> Color(0xFF2196F3); "completed" -> Color(0xFF4CAF50); "cancelled" -> Color(0xFFF44336); else -> Color.Gray }
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, null, tint = statusColor)
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(v.visitorName, fontWeight = FontWeight.Bold)
                                    Text("${v.visitorRelationship ?: "Visitor"} • ${v.visitationDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    v.location?.let { Text("Location: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        v.startTime?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                        v.endTime?.let { Text("- $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                    }
                                }
                                Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) {
                                    Text(v.status ?: "scheduled", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== REFERRALS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<ReferralEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.referralDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Referrals", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SwapHoriz, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No referrals", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { r ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SwapHoriz, null, tint = Color(0xFF9C27B0))
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(r.referralType ?: "Referral", fontWeight = FontWeight.Bold)
                                    Text("To: ${r.referredTo ?: "TBD"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    r.reason?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                    r.referralDate?.let { Text("Date: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                    r.outcome?.let { Text("Outcome: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== AFTERCARE PLANS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AftercarePlansScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<AftercarePlanEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.aftercarePlanDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Aftercare Plans", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocalHospital, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No aftercare plans", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { plan ->
                        val statusColor = when (plan.status) { "active" -> Color(0xFF4CAF50); "completed" -> Color(0xFF2196F3); else -> Color(0xFFFF9800) }
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalHospital, null, tint = statusColor)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(plan.planName, fontWeight = FontWeight.Bold)
                                        Text("Child #${plan.childId} • From: ${plan.startDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) {
                                        Text(plan.status ?: "active", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor)
                                    }
                                }
                                plan.housingArrangement?.let { Text("Housing: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                plan.educationEmployment?.let { Text("Education: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                plan.supportServices?.let { Text("Services: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== CHILD SERVICES REFERRALS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildServicesReferralsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<ChildServicesReferralEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.childServicesReferralDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Service Referrals", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocalActivity, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No service referrals", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { r ->
                        val statusColor = when (r.status) { "pending" -> Color(0xFFFF9800); "active" -> Color(0xFF4CAF50); "completed" -> Color(0xFF2196F3); else -> Color.Gray }
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalActivity, null, tint = statusColor)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(r.serviceType, fontWeight = FontWeight.Bold)
                                        Text("Child #${r.childId} • Ref: ${r.referralDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        r.frequency?.let { Text("Frequency: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                    }
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) {
                                        Text(r.status ?: "pending", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor)
                                    }
                                }
                                r.costEstimate?.let { Text("Est. Cost: $${it}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50)) }
                            }
                        }
                    }
                }
            }
        }
    }
}
