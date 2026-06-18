package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
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

// ==================== PLACEMENT DISRUPTIONS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementDisruptionsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<PlacementDisruptionEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.placementDisruptionDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Placement Disruptions", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WarningAmber, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No placement disruptions", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { d ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WarningAmber, null, tint = Color(0xFFF44336))
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(d.disruptionType ?: "Disruption", fontWeight = FontWeight.Bold)
                                        Text("Child #${d.childId} • ${d.disruptionDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }
                                d.reason?.let { Spacer(Modifier.height(4.dp)); Text("Reason: $it", style = MaterialTheme.typography.bodySmall) }
                                d.childBehaviorFactor?.let { Text("Child Factor: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                d.familyFactor?.let { Text("Family Factor: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                if (d.reunificationAttempted) { Text("Reunification attempted", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50)) }
                                d.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== FOSTER FAMILY TRAINING ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FosterFamilyTrainingScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<FosterFamilyTrainingEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.fosterFamilyTrainingDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Foster Family Training", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.School, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No training records", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { t ->
                        val statusColor = when (t.status) { "completed" -> Color(0xFF4CAF50); "scheduled" -> Color(0xFF2196F3); "cancelled" -> Color(0xFFF44336); else -> Color(0xFFFF9800) }
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.School, null, tint = statusColor)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(t.trainingName, fontWeight = FontWeight.Bold)
                                        Text("Family #${t.familyId} • ${t.trainingDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        t.trainerName?.let { Text("Trainer: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                    }
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) {
                                        Text(t.status ?: "scheduled", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor)
                                    }
                                }
                                if (t.certificateIssued) {
                                    Spacer(Modifier.height(4.dp))
                                    Text("Certificate: ${t.certificateNumber ?: "Issued"}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                                }
                                t.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== REPORTS GENERATED ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsGeneratedScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<ReportGeneratedEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.reportGeneratedDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Generated Reports", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PictureAsPdf, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No generated reports", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { r ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PictureAsPdf, null, tint = Color(0xFFE91E63))
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(r.reportName, fontWeight = FontWeight.Bold)
                                    Text(r.reportType ?: "Report", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    r.generatedDate?.let { Text("Generated: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        r.fileSize?.let { Text("${it / 1024}KB", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                        r.downloadCount?.let { Text("Downloads: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                    }
                                }
                                if (!r.isDeleted) {
                                    Icon(Icons.Default.Download, null, tint = Color(0xFF2196F3))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== EMERGENCY EVENTS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyEventsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<EmergencyEventEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.emergencyEventDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Emergency Events", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Sos, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No emergency events", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { e ->
                        val statusColor = when (e.status) { "open" -> Color(0xFFF44336); "resolved" -> Color(0xFF4CAF50); else -> Color(0xFFFF9800) }
                        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (e.status == "open") Color(0xFFFFEBEE) else Color.White)) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Sos, null, tint = statusColor)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(e.eventType ?: "Emergency", fontWeight = FontWeight.Bold)
                                        Text(e.eventDate ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) {
                                        Text(e.status ?: "open", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor)
                                    }
                                }
                                e.description?.let { Spacer(Modifier.height(4.dp)); Text(it, style = MaterialTheme.typography.bodySmall) }
                                e.actionTaken?.let { Text("Action: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== GLOBAL DOCUMENT STORAGE ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalDocumentStorageScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<GlobalDocumentStorageEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.globalDocumentStorageDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Document Storage", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CloudUpload, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No stored documents", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { d ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                val icon = when {
                                    d.mimeType?.startsWith("image") == true -> Icons.Default.Image
                                    d.mimeType?.contains("pdf") == true -> Icons.Default.PictureAsPdf
                                    else -> Icons.AutoMirrored.Filled.InsertDriveFile
                                }
                                Icon(icon, null, tint = Color(0xFF607D8B))
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(d.documentName, fontWeight = FontWeight.Bold)
                                    Text(d.documentCategory ?: d.documentType ?: "Document", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        d.fileSize?.let { Text("${it / 1024}KB", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                        Text("v${d.version ?: 1}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    d.uploadedAt?.let { Text("Uploaded: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                }
                                if (d.isPublic) {
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = Color(0xFF2196F3).copy(alpha = 0.1f)) {
                                        Text("Public", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF2196F3))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== INTER-COUNTY TRANSFERS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterCountyTransfersScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<InterCountyTransferEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.interCountyTransferDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Inter-County Transfers", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SwapHoriz, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No inter-county transfers", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { t ->
                        val statusColor = when (t.transferStatus) { "pending" -> Color(0xFFFF9800); "completed" -> Color(0xFF4CAF50); "rejected" -> Color(0xFFF44336); else -> Color.Gray }
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.SwapHoriz, null, tint = statusColor)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("Child #${t.childId}", fontWeight = FontWeight.Bold)
                                        Text("${t.fromCounty ?: "?"} → ${t.toCounty ?: "?"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        Text("Date: ${t.transferDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) {
                                        Text(t.transferStatus ?: "pending", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor)
                                    }
                                }
                                t.reason?.let { Spacer(Modifier.height(4.dp)); Text("Reason: $it", style = MaterialTheme.typography.bodySmall) }
                                if (t.documentsTransferred) { Text("Documents transferred", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== WORKER LOCATION TRACKING ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerLocationScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<WorkerLocationTrackingEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.workerLocationTrackingDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Worker Locations", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocationOn, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No location data", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { loc ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, tint = Color(0xFF4CAF50))
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Worker #${loc.userId}", fontWeight = FontWeight.Bold)
                                    Text("%.4f, %.4f".format(loc.latitude, loc.longitude), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    loc.accuracy?.let { Text("Accuracy: ${it}m", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(loc.trackingTime, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    loc.activityType?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = Color(0xFF2196F3)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
