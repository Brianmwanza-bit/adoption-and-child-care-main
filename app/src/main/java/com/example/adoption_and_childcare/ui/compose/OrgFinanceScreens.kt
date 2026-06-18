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

// ==================== ORGANIZATION PARTNERS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationPartnersScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<OrganizationPartnerEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.organizationPartnerDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Organization Partners", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Business, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No partners registered", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { p ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Business, null, tint = Color(0xFF2196F3))
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(p.partnerName, fontWeight = FontWeight.Bold)
                                        Text(p.partnerType ?: "Partner", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    if (p.isActive) {
                                        Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = Color(0xFF4CAF50).copy(alpha = 0.1f)) {
                                            Text("Active", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50))
                                        }
                                    }
                                }
                                p.contactPerson?.let { Text("Contact: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                p.phone?.let { Text("Phone: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                p.email?.let { Text("Email: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                p.servicesProvided?.let { Text("Services: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                p.mouExpiry?.let { Text("MOU Expires: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFF9800)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== SERVICE PROVIDERS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceProvidersScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<ServiceProviderEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.serviceProviderDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Service Providers", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MiscellaneousServices, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No service providers", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { p ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MiscellaneousServices, null, tint = Color(0xFF9C27B0))
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(p.providerName, fontWeight = FontWeight.Bold)
                                        Text(p.providerType ?: "Provider", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    if (p.isActive) {
                                        Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = Color(0xFF4CAF50).copy(alpha = 0.1f)) {
                                            Text("Active", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50))
                                        }
                                    }
                                }
                                p.contactPerson?.let { Text("Contact: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                p.phone?.let { Text("Phone: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                p.county?.let { Text("County: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                p.contractEnd?.let { Text("Contract Ends: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFF9800)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== DONOR FUNDING ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorFundingScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<DonorFundingEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.donorFundingDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Donor Funding", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VolunteerActivism, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No donor funding records", color = Color.Gray)
                    }
                }
            } else {
                val total = items.sumOf { it.amount }
                Card(Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f))) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachMoney, null, tint = Color(0xFF4CAF50))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Total Funding", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text("$${String.format("%,.2f", total)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            Text("${items.size} donation(s)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { d ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VolunteerActivism, null, tint = Color(0xFF4CAF50))
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(d.donorName, fontWeight = FontWeight.Bold)
                                    Text(d.donorType ?: "Donor", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text("Date: ${d.donationDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    d.purpose?.let { Text("Purpose: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                    d.referenceNumber?.let { Text("Ref: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                }
                                Text("$${String.format("%,.2f", d.amount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== BUDGET ALLOCATIONS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetAllocationsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<BudgetAllocationEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.budgetAllocationDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Budget Allocations", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AccountBalance, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No budget allocations", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { b ->
                        val allocated = b.allocatedAmount ?: 0.0
                        val utilized = b.utilizedAmount ?: 0.0
                        val percent = if (allocated > 0) ((utilized / allocated) * 100).toInt() else 0
                        val barColor = if (percent > 90) Color(0xFFF44336) else if (percent > 70) Color(0xFFFF9800) else Color(0xFF4CAF50)
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountBalance, null, tint = Color(0xFF607D8B))
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(b.category ?: "General", fontWeight = FontWeight.Bold)
                                        Text(b.financialYear ?: "Current Year", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(progress = { percent / 100f }, modifier = Modifier.fillMaxWidth().height(8.dp), color = barColor, trackColor = Color.LightGray.copy(alpha = 0.3f))
                                Spacer(Modifier.height(4.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Allocated: $${String.format("%,.0f", allocated)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text("Used: $${String.format("%,.0f", utilized)} ($percent%)", style = MaterialTheme.typography.bodySmall, color = barColor)
                                }
                                b.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== COUNTIES ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountiesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<CountyEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.countyDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("Counties", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocationCity, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No counties registered", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { c ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationCity, null, tint = Color(0xFF3F51B5))
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(c.countyName, fontWeight = FontWeight.Bold)
                                    c.countyCode?.let { Text("Code: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                    c.region?.let { Text("Region: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                }
                                if (c.isActive) {
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = Color(0xFF4CAF50).copy(alpha = 0.1f)) {
                                        Text("Active", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50))
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

// ==================== COUNTY OFFICES ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountyOfficesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<CountyOfficeEntity>>(emptyList()) }

    LaunchedEffect(Unit) { db.countyOfficeDao().observeAll().collectLatest { items = it } }

    SimpleBackScreen("County Offices", onBack) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Apartment, null, Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No county offices", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { o ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Apartment, null, tint = Color(0xFF795548))
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(o.officeName, fontWeight = FontWeight.Bold)
                                        Text("${o.county}${o.subCounty?.let { " - $it" } ?: ""}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    if (o.isActive) {
                                        Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = Color(0xFF4CAF50).copy(alpha = 0.1f)) {
                                            Text("Active", Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50))
                                        }
                                    }
                                }
                                o.headOfficerName?.let { Text("Head: $it (${o.headOfficerTitle ?: "Officer"})", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                o.phone?.let { Text("Phone: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                o.email?.let { Text("Email: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                                o.address?.let { Text("Address: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                            }
                        }
                    }
                }
            }
        }
    }
}
