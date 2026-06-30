package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.*
import com.example.adoption_and_childcare.viewmodel.ChildProfileViewModel

/**
 * High-detail "Child 360° Profile" screen.
 * 
 * @param child The child entity to display.
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for managing child profile state.
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

    val medicalRecords by viewModel.medicalRecords.collectAsStateWithLifecycle(initialValue = emptyList())
    val educationRecords by viewModel.educationRecords.collectAsStateWithLifecycle(initialValue = emptyList())
    val documents by viewModel.documents.collectAsStateWithLifecycle(initialValue = emptyList())
    val siblingLinks by viewModel.siblings.collectAsStateWithLifecycle(initialValue = emptyList())
    val siblingChildren by viewModel.siblingChildren.collectAsStateWithLifecycle(initialValue = emptyList())

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
                    IconButton(onClick = { /* Export */ }) { Icon(Icons.Default.PictureAsPdf, contentDescription = stringResource(R.string.child_profile_export)) }
                    IconButton(onClick = { /* Share */ }) { Icon(Icons.Default.Share, contentDescription = stringResource(R.string.child_profile_share)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { scaffoldPadding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues = scaffoldPadding)) {
            ProfileHeader(child)

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF4CAF50),
                edgePadding = 16.dp
            ) {
                for ((index, tabTitle) in tabs.withIndex()) {
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tabTitle, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
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

/**
 * Header component displaying child's basic identity information.
 * 
 * @param child The child entity.
 */
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
                    AsyncImage(model = child.photoUrl, contentDescription = stringResource(R.string.child_profile_photo_desc), modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.ChildCare, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF4CAF50))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${child.firstName} ${child.lastName}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.child_case_number_format, child.caseNumber ?: stringResource(R.string.child_unassigned)), color = Color(0xFF4CAF50))
                Badge(containerColor = if (child.currentStatus == stringResource(R.string.status_active)) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)) {
                    Text(child.currentStatus ?: stringResource(R.string.status_active), color = if (child.currentStatus == stringResource(R.string.status_active)) Color(0xFF2E7D32) else Color(0xFFC62828))
                }
            }
        }
    }
}

/**
 * Summary tab displaying vitals, location, and legal status.
 * 
 * @param child The child entity.
 */
@Composable
private fun SummaryTab(child: ChildEntity) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (!child.traumaNotes.isNullOrBlank() || !child.allergies.isNullOrBlank()) {
            AlertCard(
                title = stringResource(R.string.alert_critical_title),
                items = listOfNotNull(
                    child.traumaNotes?.let { stringResource(R.string.alert_trauma_format, it) },
                    child.allergies?.let { stringResource(R.string.alert_allergy_format, it) }
                ),
                color = Color(0xFFE91E63)
            )
        }

        DetailSectionModern(stringResource(R.string.profile_vitals), Icons.Default.Badge) {
            DetailRowModern(stringResource(R.string.label_full_name), "${child.firstName} ${child.middleName ?: ""} ${child.lastName}")
            DetailRowModern(stringResource(R.string.label_dob), child.dateOfBirth ?: stringResource(R.string.label_not_specified))
            DetailRowModern(stringResource(R.string.label_gender), child.gender ?: stringResource(R.string.label_not_specified))
            DetailRowModern(stringResource(R.string.label_nationality), child.nationality ?: stringResource(R.string.label_not_specified))
            DetailRowModern(stringResource(R.string.label_blood_type), child.bloodType ?: stringResource(R.string.label_not_specified))
            DetailRowModern(stringResource(R.string.label_birth_cert), child.birthCertificateNo ?: stringResource(R.string.search_na))
            DetailRowModern(stringResource(R.string.label_primary_physician), child.primaryPhysician ?: stringResource(R.string.label_not_assigned))
        }

        DetailSectionModern(stringResource(R.string.profile_location), Icons.Default.LocationOn) {
            DetailRowModern(stringResource(R.string.label_current_county), child.currentCounty ?: stringResource(R.string.label_not_specified))
            DetailRowModern(stringResource(R.string.label_place_of_birth), child.placeOfBirth ?: stringResource(R.string.label_not_specified))
            DetailRowModern(stringResource(R.string.label_county_of_origin), child.county ?: stringResource(R.string.label_not_specified))
        }

        // Emancipation Info
        if (child.isEmancipated) {
            DetailSectionModern(stringResource(R.string.detail_legal_status), Icons.Default.Gavel) {
                DetailRowModern(stringResource(R.string.placements_label_status), stringResource(R.string.label_emancipated))
                DetailRowModern(stringResource(R.string.common_label_date), child.emancipationDate ?: stringResource(R.string.search_na))
                DetailRowModern(stringResource(R.string.common_label_reason), child.emancipationReason ?: stringResource(R.string.label_not_provided))
            }
        }

        if (!child.specialNeeds.isNullOrBlank()) {
            DetailSectionModern(stringResource(R.string.profile_special_needs), Icons.Default.Accessibility) {
                Text(child.specialNeeds, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

/**
 * Siblings tab displaying sibling connectivity and known siblings list.
 * 
 * @param child The current child entity.
 * @param links List of sibling relationship links.
 * @param children List of sibling child entities.
 */
@Composable
private fun SiblingsTab(child: ChildEntity, links: List<SiblingEntity>, children: List<ChildEntity>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.profile_siblings_map), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth().height(200.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                SiblingTreeMap(siblingsCount = children.size)
            }
        }
        Text(stringResource(R.string.profile_known_siblings), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        if (children.isEmpty()) {
            Text(stringResource(R.string.label_no_siblings), color = Color.Gray, modifier = Modifier.padding(8.dp))
        } else {
            for (sib in children) {
                val link = links.find { it.siblingChildId == sib.childId }
                SiblingItem(sib.firstName + " " + sib.lastName, stringResource(R.string.label_age_format, sib.dateOfBirth ?: stringResource(R.string.search_na)), stringResource(R.string.label_relation_format, link?.relationshipType ?: stringResource(R.string.label_relation_sibling)))
            }
        }
    }
}

/**
 * Medical tab displaying list of medical records.
 * 
 * @param records List of medical record entities.
 */
@Composable
private fun MedicalListTab(records: List<MedicalRecordEntity>) {
    if (records.isEmpty()) {
        EmptyState(Icons.Default.LocalHospital, stringResource(R.string.empty_medical_records))
    } else {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (record in records) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(record.visitDate, fontWeight = FontWeight.Bold)
                            if (record.isImmunization) Badge { Text(stringResource(R.string.label_immunization)) }
                        }
                        Text(stringResource(R.string.label_hospital_format, record.hospitalName ?: stringResource(R.string.search_na)), style = MaterialTheme.typography.bodySmall)
                        Text(stringResource(R.string.label_doctor_format, record.doctorName ?: stringResource(R.string.search_na)), style = MaterialTheme.typography.bodySmall)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(stringResource(R.string.label_diagnosis_format, record.diagnosis ?: stringResource(R.string.search_na)), fontWeight = FontWeight.Medium)
                        Text(stringResource(R.string.label_treatment_format, record.treatment ?: stringResource(R.string.search_na)), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

/**
 * Education tab displaying list of education records.
 * 
 * @param records List of education record entities.
 */
@Composable
private fun EducationListTab(records: List<EducationRecordEntity>) {
    if (records.isEmpty()) {
        EmptyState(Icons.Default.School, stringResource(R.string.empty_education_records))
    } else {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (record in records) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(record.schoolName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.label_grade_format, record.grade ?: stringResource(R.string.search_na)), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.label_enrolled_format, record.enrollmentDate ?: stringResource(R.string.search_na)), style = MaterialTheme.typography.bodySmall)
                        if (record.performance != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(stringResource(R.string.label_performance_format, record.performance), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Documents tab displaying list of child's documents.
 * 
 * @param docs List of document entities.
 */
@Composable
private fun DocumentsListTab(docs: List<DocumentEntity>) {
    if (docs.isEmpty()) {
        EmptyState(Icons.Default.Description, stringResource(R.string.empty_documents))
    } else {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (doc in docs) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null, tint = Color.Gray)
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

/**
 * A visualization component for sibling relationships.
 * 
 * @param siblingsCount The number of siblings to visualize.
 */
@Composable
private fun SiblingTreeMap(siblingsCount: Int) {
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
        Text(stringResource(R.string.label_you), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
    }
}

/**
 * Component for displaying an individual sibling item in a list.
 * 
 * @param name The sibling's name.
 * @param age The sibling's age.
 * @param relation The relationship type.
 */
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

/**
 * Component for displaying an empty state message with an icon.
 * 
 * @param icon The icon to display.
 * @param message The message to display.
 */
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

/**
 * A section for displaying grouped profile details.
 * 
 * @param title The title of the section.
 * @param icon The icon representing the section.
 * @param content The composable content to display within the section.
 */
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

/**
 * A row for displaying a single detail label and value.
 * 
 * @param label The descriptive label for the detail.
 * @param value The value associated with the label.
 */
@Composable
private fun DetailRowModern(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

/**
 * Card for displaying critical alerts or warnings.
 * 
 * @param title The title of the alert card.
 * @param items List of alert messages.
 * @param color The theme color for the alert.
 */
@Composable
private fun AlertCard(title: String, items: List<String>, color: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)), border = BorderStroke(1.dp, color)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
            for (item in items) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = color, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item, style = MaterialTheme.typography.bodySmall, color = color)
                }
            }
        }
    }
}
