package com.example.adoption_and_childcare.ui.compose

import android.net.Uri
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
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.yourdomain.adoptionchildcare.R

/**
 * High-detail "Child 360° Profile" screen.
 * 
 * Provides a comprehensive view of all data related to a child, organized into tabs.
 * 
 * @param child The child entity to display.
 * @param onBack Callback for navigating back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildProfileScreen(
    child: ChildEntity,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Summary", "Siblings", "Medical", "Education", "Documents")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Child 360° Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export PDF */ }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export")
                    }
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
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
            // Header Section
            ProfileHeader(child)

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF4CAF50),
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
                    0 -> SummaryTab(child)
                    1 -> SiblingsTab(child)
                    2 -> MedicalTab(child)
                    3 -> EducationTab(child)
                    4 -> DocumentsTab(child)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(child: ChildEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Photo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color(0xFF4CAF50), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (child.photoUrl != null) {
                    AsyncImage(
                        model = child.photoUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.ChildCare, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF4CAF50))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${child.firstName} ${child.lastName}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Case #${child.caseNumber ?: "UNASSIGNED"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF4CAF50)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (child.currentStatus == "Active") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                ) {
                    Text(
                        text = child.currentStatus ?: "Active",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (child.currentStatus == "Active") Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryTab(child: ChildEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Trauma & Allergy Alerts (Flash Alerts)
        AlertCard(
            title = "CRITICAL ALERTS",
            items = listOf(
                "Trauma: Sensitive to loud noises",
                "Allergy: Peanut Allergy (Severe)"
            ),
            color = Color(0xFFE91E63)
        )

        // Identity Section
        DetailSectionModern("Identity & Vitals", Icons.Default.Badge) {
            DetailRowModern("Full Name", "${child.firstName} ${child.middleName ?: ""} ${child.lastName}")
            DetailRowModern("DOB", child.dateOfBirth ?: "Not Specified")
            DetailRowModern("Gender", child.gender ?: "Not Specified")
            DetailRowModern("Nationality", child.nationality ?: "Not Specified")
        }

        // Documents Vault Section
        DetailSectionModern("Vital Documents Vault", Icons.Default.VerifiedUser) {
            DocumentStatusRow("Birth Certificate", true)
            DocumentStatusRow("Social Security Card", true)
            DocumentStatusRow("Passport", false)
            DocumentStatusRow("Immunization Record", true)
        }

        // Location Section
        DetailSectionModern("Placement Location", Icons.Default.LocationOn) {
            DetailRowModern("Current County", child.currentCounty ?: "Not Specified")
            DetailRowModern("County of Origin", child.county ?: "Not Specified")
        }
    }
}

@Composable
private fun SiblingsTab(child: ChildEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Sibling Connectivity Map", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        // Visual Tree/Map
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                SiblingTreeMap(childName = child.firstName ?: "Current Child")
            }
        }

        Text("Known Siblings", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        
        // Mock Sibling List
        SiblingItem("Jane Doe", "Age: 12", "Placed: Foster Home #42")
        SiblingItem("Mark Doe", "Age: 4", "Placed: Kinship Care (Aunt Mary)")
    }
}

@Composable
private fun MedicalTab(child: ChildEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailSectionModern("Medical Overview", Icons.Default.LocalHospital) {
            DetailRowModern("Blood Type", "O+")
            DetailRowModern("Primary Physician", "Dr. Smith (City Hospital)")
            DetailRowModern("Last Physical", "2024-05-15")
        }

        // Immunization Progress
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Immunization Progress", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = { 0.8f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFFE8F5E9)
            )
            Text("8 of 10 Required Vaccinations Completed", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        // Medications
        DetailSectionModern("Active Medications", Icons.Default.Medication) {
            DetailRowModern("Amoxicillin", "250mg - 2x daily")
            DetailRowModern("Vitamin D", "1000 IU - 1x daily")
        }
    }
}

@Composable
private fun EducationTab(child: ChildEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailSectionModern("School Information", Icons.Default.School) {
            DetailRowModern("Current School", "Green Valley Elementary")
            DetailRowModern("Grade Level", "4th Grade")
            DetailRowModern("School of Origin", "Yes (Stability Maintained)")
        }

        // IEP Progress
        DetailSectionModern("IEP / 504 Goals", Icons.Default.AssignmentTurnedIn) {
            GoalTrackerRow("Reading Proficiency", 0.75f)
            GoalTrackerRow("Math Fundamentals", 0.60f)
            GoalTrackerRow("Social Interaction", 0.90f)
        }
    }
}

@Composable
private fun DocumentsTab(child: ChildEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Document Repository", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        DocumentCategoryItem("Court Orders", 3, Color(0xFF795548))
        DocumentCategoryItem("Medical Records", 8, Color(0xFFF44336))
        DocumentCategoryItem("Educational Reports", 5, Color(0xFF2196F3))
        DocumentCategoryItem("Case Worker Notes", 12, Color(0xFF607D8B))
    }
}

// Reusable Components

@Composable
private fun DetailSectionModern(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
private fun DetailRowModern(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AlertCard(title: String, items: List<String>, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            items.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item, style = MaterialTheme.typography.bodySmall, color = color)
                }
            }
        }
    }
}

@Composable
private fun DocumentStatusRow(name: String, isAvailable: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, style = MaterialTheme.typography.bodyMedium)
        Icon(
            imageVector = if (isAvailable) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isAvailable) Color(0xFF4CAF50) else Color(0xFFC62828),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SiblingItem(name: String, age: String, placement: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFE3F2FD), CircleShape), contentAlignment = Alignment.Center) {
                Text(name.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text("$age • $placement", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun SiblingTreeMap(childName: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val center = size.center
            val radius = 30.dp.toPx()
            val siblingDistance = 80.dp.toPx()
            
            // Draw lines first so they are behind circles
            val siblings = listOf(
                androidx.compose.ui.geometry.Offset(center.x - siblingDistance, center.y + siblingDistance),
                androidx.compose.ui.geometry.Offset(center.x + siblingDistance, center.y + siblingDistance),
                androidx.compose.ui.geometry.Offset(center.x, center.y - siblingDistance)
            )
            
            siblings.forEach { siblingOffset ->
                drawLine(
                    color = Color.LightGray,
                    start = center,
                    end = siblingOffset,
                    strokeWidth = 2.dp.toPx()
                )
            }
            
            // Main Child
            drawCircle(
                color = Color(0xFF2196F3),
                radius = radius,
                center = center
            )
            
            // Siblings
            siblings.forEach { siblingOffset ->
                drawCircle(
                    color = Color(0xFF4CAF50),
                    radius = radius * 0.8f,
                    center = siblingOffset
                )
            }
        }
        
        // Simple labels for the nodes
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("You", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun GoalTrackerRow(label: String, progress: Float) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = Color(0xFF2196F3),
            trackColor = Color(0xFFE3F2FD)
        )
    }
}

@Composable
private fun DocumentCategoryItem(name: String, count: Int, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Folder, contentDescription = null, tint = color)
                Spacer(modifier = Modifier.width(12.dp))
                Text(name, style = MaterialTheme.typography.bodyLarge)
            }
            Badge(containerColor = color.copy(alpha = 0.2f), contentColor = color) {
                Text("$count files")
            }
        }
    }
}
