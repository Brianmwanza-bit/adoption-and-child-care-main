package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity

/**
 * High-detail "Family & Provider Hub" screen.
 * 
 * Provides a comprehensive view of family data, household members, and licensing status.
 * 
 * @param family The family entity to display.
 * @param onBack Callback for navigating back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyHubScreen(
    family: FamilyEntity,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Members", "Checks", "Licensing", "Placements")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family & Provider Hub") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export */ }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export")
                    }
                    IconButton(onClick = { /* Contact */ }) {
                        Icon(Icons.Default.Phone, contentDescription = "Call")
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
            // Family Header
            FamilyHeaderSection(family)

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF2196F3),
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> FamilyOverviewTab(family)
                    1 -> HouseholdMembersTab()
                    2 -> BackgroundChecksTab()
                    3 -> LicensingTab()
                    4 -> FamilyPlacementsTab()
                }
            }
        }
    }
}

@Composable
private fun FamilyHeaderSection(family: FamilyEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color(0xFF2196F3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF2196F3))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = family.primaryContactName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${family.city ?: ""}, ${family.county ?: ""}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2196F3)
                )
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Licensed Provider",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}

@Composable
private fun FamilyOverviewTab(family: FamilyEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailSectionBlue("Contact Information", Icons.Default.ContactPhone) {
            DetailRowBlue("Email", family.email ?: "Not Specified")
            DetailRowBlue("Phone", family.phone ?: "Not Specified")
            DetailRowBlue("National ID", family.nationalIdNo ?: "Not Specified")
            DetailRowBlue("Secondary Contact", family.secondaryContactName ?: "None")
        }

        DetailSectionBlue("Address", Icons.Default.LocationOn) {
            DetailRowBlue("Street", family.address ?: "Not Specified")
            DetailRowBlue("City", family.city ?: "Not Specified")
            DetailRowBlue("County", family.county ?: "Not Specified")
            DetailRowBlue("Country", family.country ?: "Not Specified")
        }
    }
}

@Composable
private fun HouseholdMembersTab() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Current Residents", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        MemberItem("John Doe", "Primary Contact", true)
        MemberItem("Mary Doe", "Secondary Contact", true)
        MemberItem("Kevin Doe", "Minor (Biological)", false)
        MemberItem("Sarah Smith", "Minor (Foster)", false)
    }
}

@Composable
private fun BackgroundChecksTab() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Background Check Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        CheckStatusCard("Criminal Background", "John Doe", "CLEARED", Color(0xFF4CAF50))
        CheckStatusCard("Criminal Background", "Mary Doe", "CLEARED", Color(0xFF4CAF50))
        CheckStatusCard("Child Abuse Registry", "John Doe", "CLEARED", Color(0xFF4CAF50))
        CheckStatusCard("Child Abuse Registry", "Mary Doe", "PENDING", Color(0xFFFF9800))
    }
}

@Composable
private fun LicensingTab() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionBlue("License Details", Icons.Default.Verified) {
            DetailRowBlue("Status", "Fully Licensed")
            DetailRowBlue("License Number", "FL-2024-8891")
            DetailRowBlue("Issue Date", "2024-01-10")
            DetailRowBlue("Expiration Date", "2025-01-10")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Renewal due in 60 days", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FamilyPlacementsTab() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Placement Capacity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("2 of 3 spots filled", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
        }
        
        LinearProgressIndicator(
            progress = { 0.66f },
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
            color = Color(0xFF2196F3),
            trackColor = Color(0xFFE3F2FD)
        )

        Text("Currently Placed Children", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        
        PlacementMiniItem("Sarah Smith", "Since 2024-03-01")
        PlacementMiniItem("Timmy Jones", "Since 2024-05-20")
    }
}

// Reusable Components

@Composable
private fun DetailSectionBlue(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
private fun DetailRowBlue(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MemberItem(name: String, role: String, isAdult: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(if (isAdult) Color(0xFFE3F2FD) else Color(0xFFF1F8E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isAdult) Icons.Default.Person else Icons.Default.ChildCare,
                    contentDescription = null,
                    tint = if (isAdult) Color(0xFF2196F3) else Color(0xFF4CAF50)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(name, fontWeight = FontWeight.Bold)
                Text(role, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun CheckStatusCard(checkType: String, person: String, status: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(checkType, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(person, fontWeight = FontWeight.Bold)
            }
            Badge(containerColor = color.copy(alpha = 0.2f), contentColor = color) {
                Text(status)
            }
        }
    }
}

@Composable
private fun PlacementMiniItem(name: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, fontWeight = FontWeight.Medium)
            Text(date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
