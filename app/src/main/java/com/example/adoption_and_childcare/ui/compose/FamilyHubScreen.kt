package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.data.db.entities.BackgroundCheckEntity
import com.example.adoption_and_childcare.viewmodel.FamilyHubViewModel

/**
 * High-detail "Family & Provider Hub" screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyHubScreen(
    family: FamilyEntity,
    onBack: () -> Unit,
    viewModel: FamilyHubViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.family_hub_tab_overview),
        stringResource(R.string.family_hub_tab_members),
        stringResource(R.string.family_hub_tab_checks),
        stringResource(R.string.family_hub_tab_licensing),
        stringResource(R.string.family_hub_tab_placements)
    )

    val placedChildren by viewModel.placedChildren.collectAsState()
    val bgChecks by viewModel.backgroundChecks.collectAsState()

    LaunchedEffect(family.familyId) {
        viewModel.loadData(family.familyId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.family_hub_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.family_hub_back_desc))
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export */ }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = stringResource(R.string.family_hub_export_desc))
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            FamilyHeaderSection(family)

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF2196F3),
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> FamilyOverviewTab(family)
                    1 -> HouseholdMembersTab(family, placedChildren)
                    2 -> BackgroundChecksTab(bgChecks)
                    3 -> LicensingTab(family)
                    4 -> FamilyPlacementsTab(placedChildren)
                }
            }
        }
    }
}

@Composable
private fun FamilyHeaderSection(family: FamilyEntity) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White).border(2.dp, Color(0xFF2196F3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF2196F3))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = family.primaryContactName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "${family.city ?: "N/A"}, ${family.county ?: "N/A"}", color = Color(0xFF2196F3))
                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFE8F5E9), modifier = Modifier.padding(top = 4.dp)) {
                    Text(text = family.licenseStatus ?: "Pending", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32))
                }
            }
        }
    }
}

@Composable
private fun FamilyOverviewTab(family: FamilyEntity) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionBlue(stringResource(R.string.family_hub_section_contact), Icons.Default.ContactPhone) {
            DetailRowBlue(stringResource(R.string.family_hub_label_email), family.email ?: "N/A")
            DetailRowBlue(stringResource(R.string.family_hub_label_phone), family.phone ?: "N/A")
            DetailRowBlue(stringResource(R.string.family_hub_label_national_id), family.nationalIdNo ?: "N/A")
            DetailRowBlue(stringResource(R.string.family_hub_label_secondary_contact), family.secondaryContactName ?: "None")
        }
        DetailSectionBlue(stringResource(R.string.family_hub_section_address), Icons.Default.LocationOn) {
            DetailRowBlue("Street", family.address ?: "N/A")
            DetailRowBlue("City", family.city ?: "N/A")
            DetailRowBlue("County", family.county ?: "N/A")
            DetailRowBlue("Sub-County", family.subCounty ?: "N/A")
        }
    }
}

@Composable
private fun HouseholdMembersTab(family: FamilyEntity, children: List<ChildEntity>) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.family_primary_members), fontWeight = FontWeight.Bold)
        MemberItem(family.primaryContactName, stringResource(R.string.family_hub_primary_contact), true)
        family.secondaryContactName?.let { MemberItem(it, stringResource(R.string.family_hub_secondary_contact), true) }
        
        if (children.isNotEmpty()) {
            Text(stringResource(R.string.family_placed_children), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            children.forEach { child ->
                MemberItem("${child.firstName} ${child.lastName}", stringResource(R.string.family_hub_minor_foster), false)
            }
        }
    }
}

@Composable
private fun BackgroundChecksTab(checks: List<BackgroundCheckEntity>) {
    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.family_hub_bg_check_status), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (checks.isEmpty()) {
            Text("No background checks on record.")
        } else {
            checks.forEach { check ->
                CheckStatusCard("System Check", "User #${check.userId}", check.status ?: "Pending", if (check.status == "Completed") Color(0xFF4CAF50) else Color(0xFFFF9800))
            }
        }
    }
}

@Composable
private fun LicensingTab(family: FamilyEntity) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionBlue(stringResource(R.string.family_license_info), Icons.Default.Verified) {
            DetailRowBlue("License #", family.licenseNumber ?: "N/A")
            DetailRowBlue("Status", family.licenseStatus ?: "N/A")
            DetailRowBlue("Issue Date", family.licenseIssueDate ?: "N/A")
            DetailRowBlue("Expiry Date", family.licenseExpirationDate ?: "N/A")
        }

        if (family.licenseStatus?.contains("Renewal", ignoreCase = true) == true) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                border = BorderStroke(1.dp, Color(0xFFFF9800))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("LICENSE RENEWAL PENDING", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FamilyPlacementsTab(children: List<ChildEntity>) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.family_capacity), fontWeight = FontWeight.Bold)
        LinearProgressIndicator(progress = { 0.5f }, modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)), color = Color(0xFF2196F3))
        
        Text(stringResource(R.string.family_placed_children), fontWeight = FontWeight.Bold)
        if (children.isEmpty()) {
            Text("No children currently placed.")
        } else {
            children.forEach { child ->
                PlacementMiniItem("${child.firstName} ${child.lastName}", child.currentStatus ?: "Active")
            }
        }
    }
}

@Composable
private fun DetailSectionBlue(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF2196F3), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

@Composable
private fun DetailRowBlue(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MemberItem(name: String, role: String, isAdult: Boolean) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(if (isAdult) Color(0xFFE3F2FD) else Color(0xFFF1F8E9), CircleShape), contentAlignment = Alignment.Center) {
                Icon(if (isAdult) Icons.Default.Person else Icons.Default.ChildCare, null, tint = if (isAdult) Color(0xFF2196F3) else Color(0xFF4CAF50))
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
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(checkType, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(person, fontWeight = FontWeight.Bold)
            }
            Badge(containerColor = color.copy(alpha = 0.2f), contentColor = color) { Text(status) }
        }
    }
}

@Composable
private fun PlacementMiniItem(name: String, date: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, fontWeight = FontWeight.Medium)
            Text(date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
