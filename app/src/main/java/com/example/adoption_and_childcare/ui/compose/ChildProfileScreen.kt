package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.*
import com.example.adoption_and_childcare.viewmodel.ChildProfileViewModel

/**
 * High-detail "Child 360° Profile" screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildProfileScreen(
    child: ChildEntity,
    onBack: () -> Unit,
    viewModel: ChildProfileViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.profile_summary),
        stringResource(R.string.route_siblings),
        stringResource(R.string.profile_med_records),
        stringResource(R.string.profile_edu_records),
        stringResource(R.string.profile_docs)
    )

    val medicalRecords by viewModel.medicalRecords.collectAsState()
    val educationRecords by viewModel.educationRecords.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val siblingLinks by viewModel.siblings.collectAsState()
    val siblingChildren by viewModel.siblingChildren.collectAsState()

    LaunchedEffect(child.childId) {
        viewModel.loadData(child.childId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export */ }) { Icon(Icons.Default.PictureAsPdf, contentDescription = "Export") }
                    IconButton(onClick = { /* Share */ }) { Icon(Icons.Default.Share, contentDescription = "Share") }
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ProfileHeader(child)

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF4CAF50),
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> SummaryTab(child)
                    1 -> SiblingsTab(child, siblingLinks, siblingChildren)
                    2 -> MedicalListTab(medicalRecords)
                    3 -> EducationListTab(educationRecords)
                    4 -> DocumentsListTab(documents)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(child: ChildEntity) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White).border(2.dp, Color(0xFF4CAF50), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (child.photoUrl != null) {
                    AsyncImage(model = child.photoUrl, contentDescription = "Profile Photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.ChildCare, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF4CAF50))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${child.firstName} ${child.lastName}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "Case #${child.caseNumber ?: "UNASSIGNED"}", color = Color(0xFF4CAF50))
                Badge(containerColor = if (child.currentStatus == "Active") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)) {
                    Text(child.currentStatus ?: "Active", color = if (child.currentStatus == "Active") Color(0xFF2E7D32) else Color(0xFFC62828))
                }
            }
        }
    }
}

@Composable
private fun SummaryTab(child: ChildEntity) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (!child.traumaNotes.isNullOrBlank() || !child.allergies.isNullOrBlank()) {
            AlertCard(
                title = "CRITICAL ALERTS",
                items = listOfNotNull(
                    child.traumaNotes?.let { "Trauma: $it" },
                    child.allergies?.let { "Allergy: $it" }
                ),
                color = Color(0xFFE91E63)
            )
        }

        DetailSectionModern(stringResource(R.string.profile_vitals), Icons.Default.Badge) {
            DetailRowModern("Full Name", "${child.firstName} ${child.middleName ?: ""} ${child.lastName}")
            DetailRowModern("DOB", child.dateOfBirth ?: "Not Specified")
            DetailRowModern("Gender", child.gender ?: "Not Specified")
            DetailRowModern("Nationality", child.nationality ?: "Not Specified")
            DetailRowModern("Blood Type", child.bloodType ?: "Not Specified")
            DetailRowModern("Birth Cert #", child.birthCertificateNo ?: "N/A")
            DetailRowModern("Primary Physician", child.primaryPhysician ?: "Not Assigned")
        }

        DetailSectionModern(stringResource(R.string.profile_location), Icons.Default.LocationOn) {
            DetailRowModern("Current County", child.currentCounty ?: "Not Specified")
            DetailRowModern("Place of Birth", child.placeOfBirth ?: "Not Specified")
            DetailRowModern("County of Origin", child.county ?: "Not Specified")
        }

        // Emancipation Info
        if (child.isEmancipated) {
            DetailSectionModern(stringResource(R.string.detail_legal_status), Icons.Default.Gavel) {
                DetailRowModern("Status", "EMANCIPATED")
                DetailRowModern("Date", child.emancipationDate ?: "N/A")
                DetailRowModern("Reason", child.emancipationReason ?: "Not provided")
            }
        }

        if (!child.specialNeeds.isNullOrBlank()) {
            DetailSectionModern(stringResource(R.string.profile_special_needs), Icons.Default.Accessibility) {
                Text(child.specialNeeds, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun SiblingsTab(child: ChildEntity, links: List<SiblingEntity>, children: List<ChildEntity>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.profile_siblings_map), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth().height(200.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                SiblingTreeMap(childName = child.firstName, siblingsCount = children.size)
            }
        }
        Text(stringResource(R.string.profile_known_siblings), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        if (children.isEmpty()) {
            Text("No siblings recorded.", color = Color.Gray, modifier = Modifier.padding(8.dp))
        } else {
            children.forEach { sib ->
                val link = links.find { it.siblingChildId == sib.childId }
                SiblingItem(sib.firstName + " " + sib.lastName, "Age: ${sib.dateOfBirth ?: "N/A"}", "Relation: ${link?.relationshipType ?: "Sibling"}")
            }
        }
    }
}

@Composable
private fun MedicalListTab(records: List<MedicalRecordEntity>) {
    if (records.isEmpty()) {
        EmptyState(Icons.Default.LocalHospital, "No medical records found")
    } else {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            records.forEach { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(record.visitDate, fontWeight = FontWeight.Bold)
                            if (record.isImmunization) Badge { Text("Immunization") }
                        }
                        Text("Hospital: ${record.hospitalName ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                        Text("Doctor: ${record.doctorName ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Diagnosis: ${record.diagnosis ?: "N/A"}", fontWeight = FontWeight.Medium)
                        Text("Treatment: ${record.treatment ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun EducationListTab(records: List<EducationRecordEntity>) {
    if (records.isEmpty()) {
        EmptyState(Icons.Default.School, "No education records found")
    } else {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            records.forEach { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(record.schoolName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Grade: ${record.grade ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                        Text("Enrolled: ${record.enrollmentDate ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                        if (record.performance != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text("Performance: ${record.performance}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentsListTab(docs: List<DocumentEntity>) {
    if (docs.isEmpty()) {
        EmptyState(Icons.Default.Description, "No documents found")
    } else {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            docs.forEach { doc ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(doc.fileName, fontWeight = FontWeight.Bold)
                            Text(doc.documentType, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SiblingTreeMap(childName: String, siblingsCount: Int) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val center = size.center
            val radius = 30.dp.toPx()
            val distance = 70.dp.toPx()
            drawCircle(Color(0xFF2196F3), radius, center)
            repeat(siblingsCount) { i ->
                val angle = (i * 360f / siblingsCount) * (Math.PI / 180f).toFloat()
                val target = center.copy(
                    x = center.x + distance * Math.cos(angle.toDouble()).toFloat(),
                    y = center.y + distance * Math.sin(angle.toDouble()).toFloat()
                )
                drawLine(Color.LightGray, center, target, 2.dp.toPx())
                drawCircle(Color(0xFF4CAF50), radius * 0.7f, target)
            }
        }
        Text("You", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
    }
}

@Composable
private fun SiblingItem(name: String, age: String, relation: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFE3F2FD), CircleShape), contentAlignment = Alignment.Center) {
                Text(name.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(name, fontWeight = FontWeight.Bold)
                Text("$age • $relation", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun EmptyState(icon: ImageVector, message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = Color.Gray)
        }
    }
}

@Composable
private fun DetailSectionModern(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

@Composable
private fun DetailRowModern(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AlertCard(title: String, items: List<String>, color: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)), border = BorderStroke(1.dp, color)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
            items.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = color, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item, style = MaterialTheme.typography.bodySmall, color = color)
                }
            }
        }
    }
}
