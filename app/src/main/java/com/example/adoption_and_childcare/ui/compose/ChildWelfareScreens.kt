package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

// ==================== VACCINATION RECORDS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationRecordsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var records by remember { mutableStateOf<List<VaccinationRecordEntity>>(emptyList()) }
    var showAdd by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.vaccinationRecordDao().observeAll().collectLatest { records = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaccination Records") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, "Add") }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (records.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Vaccines, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No vaccination records", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(records) { r ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Vaccines, null, tint = Color(0xFF4CAF50))
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(r.vaccineName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("Dose ${r.doseNumber ?: "?"} • ${r.administrationDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text(r.facilityName ?: r.administeredBy ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                                Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = if (r.status == "completed") Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFFFF9800).copy(alpha = 0.1f)) {
                                    Text(r.status ?: "pending", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = if (r.status == "completed") Color(0xFF4CAF50) else Color(0xFFFF9800))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showAdd) {
        var name by remember { mutableStateOf("") }
        var date by remember { mutableStateOf(today()) }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Add Vaccination") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Vaccine Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(date, { date = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(onClick = {
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    db.vaccinationRecordDao().insert(VaccinationRecordEntity(childId = 1, vaccineName = name, administrationDate = date))
                }
                showAdd = false
            }) { Text("Add") } },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } }
        )
    }
}

// ==================== BEHAVIOR ASSESSMENTS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BehaviorAssessmentsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<ChildBehaviorAssessmentEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.childBehaviorAssessmentDao().observeAll().collectLatest { items = it }
    }

    SimpleBackScreen("Behavior Assessments", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Psychology, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No behavior assessments", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { a ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Psychology, null, tint = Color(0xFF9C27B0))
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text("Assessment on ${a.assessmentDate}", fontWeight = FontWeight.Bold)
                                        Text(a.assessmentTool ?: "Standard Tool", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    a.overallScore?.let { ScoreChip("Overall", it, Color(0xFF2196F3)) }
                                    a.behavioralScore?.let { ScoreChip("Behavior", it, Color(0xFFFF9800)) }
                                    a.emotionalScore?.let { ScoreChip("Emotional", it, Color(0xFFE91E63)) }
                                }
                                a.strengths?.let { Text("Strengths: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50), modifier = Modifier.padding(top = 4.dp)) }
                                a.challenges?.let { Text("Challenges: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF44336), modifier = Modifier.padding(top = 2.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreChip(label: String, score: Int, color: Color) {
    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = color.copy(alpha = 0.1f)) {
        Text("$label: $score", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = color)
    }
}

// ==================== WELFARE INCIDENTS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelfareIncidentsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<ChildWelfareIncidentEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.childWelfareIncidentDao().observeAll().collectLatest { items = it }
    }

    SimpleBackScreen("Welfare Incidents", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ReportProblem, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No welfare incidents", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { inc ->
                        val sevColor = when (inc.severity) { "high" -> Color(0xFFF44336); "medium" -> Color(0xFFFF9800); else -> Color(0xFF4CAF50) }
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ReportProblem, null, tint = sevColor)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(inc.incidentType ?: "Incident", fontWeight = FontWeight.Bold)
                                        Text(inc.incidentDate, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = sevColor.copy(alpha = 0.1f)) {
                                        Text(inc.severity ?: "low", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = sevColor)
                                    }
                                }
                                inc.description?.let { Spacer(Modifier.height(4.dp)); Text(it, style = MaterialTheme.typography.bodySmall) }
                                if (inc.policeInvolved) { Spacer(Modifier.height(4.dp)); Text("Police involved: ${inc.policeReportNo ?: "Yes"}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF44336)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== SIBLINGS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiblingsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<SiblingEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.siblingDao().observeAll().collectLatest { items = it }
    }

    SimpleBackScreen("Sibling Relationships", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FamilyRestroom, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No sibling relationships recorded", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { s ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FamilyRestroom, null, tint = Color(0xFF2196F3))
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Child #${s.childId} ↔ Child #${s.siblingChildId}", fontWeight = FontWeight.Bold)
                                    Text(s.relationshipType ?: "Full Sibling", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    s.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    if (s.samePlacement) Text("Same Home", style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50))
                                    if (s.contactAllowed) Text("Contact OK", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2196F3))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== CONSENT RECORDS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentRecordsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<ConsentRecordEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.consentRecordDao().observeAll().collectLatest { items = it }
    }

    SimpleBackScreen("Consent Records", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No consent records", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { c ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (c.isValid) Icons.Default.CheckCircle else Icons.Default.Cancel, null, tint = if (c.isValid) Color(0xFF4CAF50) else Color(0xFFF44336))
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(c.consentType, fontWeight = FontWeight.Bold)
                                    Text("By: ${c.providedBy ?: "N/A"} (${c.relationshipToChild ?: ""})", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text("Date: ${c.consentDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    c.expiryDate?.let { Text("Expires: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFF9800)) }
                                }
                                Text(if (c.isValid) "Valid" else "Revoked", style = MaterialTheme.typography.labelMedium, color = if (c.isValid) Color(0xFF4CAF50) else Color(0xFFF44336))
                            }
                        }
                    }
                }
            }
        }
    }
}
