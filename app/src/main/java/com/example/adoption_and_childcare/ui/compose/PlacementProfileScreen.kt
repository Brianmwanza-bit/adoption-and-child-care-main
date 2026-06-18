package com.example.adoption_and_childcare.ui.compose

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
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity

/**
 * High-detail "Placement 360° Profile" screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementProfileScreen(
    placement: PlacementEntity,
    child: ChildEntity?,
    family: FamilyEntity?,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.detail_placement_overview),
        stringResource(R.string.detail_child_info),
        stringResource(R.string.detail_provider_info),
        "Activity Logs"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Placement 360° Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8BC34A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            PlacementHeader(placement, child)

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF8BC34A),
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
                    0 -> PlacementOverviewTab(placement)
                    1 -> PlacementChildTab(child)
                    2 -> PlacementFamilyTab(family)
                    3 -> PlacementLogsTab()
                }
            }
        }
    }
}

@Composable
private fun PlacementHeader(placement: PlacementEntity, child: ChildEntity?) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White).border(2.dp, Color(0xFF8BC34A), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Place, null, tint = Color(0xFF8BC34A))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Placement #${placement.placementId}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = child?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Child")
                Badge(containerColor = if (placement.isCurrent) Color(0xFFE8F5E9) else Color.LightGray) {
                    Text(if (placement.isCurrent) "CURRENT" else "PAST", color = if (placement.isCurrent) Color(0xFF2E7D32) else Color.DarkGray)
                }
            }
        }
    }
}

@Composable
private fun PlacementOverviewTab(p: PlacementEntity) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionGreen(stringResource(R.string.detail_basic_info), Icons.Default.Info) {
            DetailRowGreen("Placement Type", p.placementType ?: "N/A")
            DetailRowGreen("Start Date", p.startDate)
            DetailRowGreen("End Date", p.endDate ?: "Ongoing")
        }
        DetailSectionGreen(stringResource(R.string.detail_description), Icons.Default.Notes) {
            Text(p.notes ?: "No notes available.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PlacementChildTab(child: ChildEntity?) {
    if (child == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.search_na)) }
        return
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionGreen("Child Identity", Icons.Default.Person) {
            DetailRowGreen("Name", "${child.firstName} ${child.lastName}")
            DetailRowGreen("Case #", child.caseNumber ?: "N/A")
        }
    }
}

@Composable
private fun PlacementFamilyTab(family: FamilyEntity?) {
    if (family == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.search_na)) }
        return
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionGreen(stringResource(R.string.detail_provider_info), Icons.Default.Home) {
            DetailRowGreen("Primary Contact", family.primaryContactName)
            DetailRowGreen("Phone", family.phone ?: "N/A")
        }
    }
}

@Composable
private fun PlacementLogsTab() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Activity History", fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("System Log", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Placement event recorded in historical database.")
            }
        }
    }
}

@Composable
private fun DetailSectionGreen(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF8BC34A))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

@Composable
private fun DetailRowGreen(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
