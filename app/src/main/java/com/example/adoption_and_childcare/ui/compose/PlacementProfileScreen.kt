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
import androidx.compose.material.icons.automirrored.filled.Notes
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
 *
 * @param placement The placement entity to display.
 * @param child The child associated with the placement.
 * @param family The family/provider associated with the placement.
 * @param onBack Callback invoked when the back button is clicked.
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
        stringResource(R.string.placement_activity_logs)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.placement_detail_title)) },
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
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            PlacementHeader(placement, child)

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF8BC34A),
                edgePadding = 16.dp
            ) {
                for (tabIndex in tabs.indices) {
                    val tabTitle = tabs[tabIndex]
                    Tab(
                        selected = selectedTab == tabIndex,
                        onClick = { selectedTab = tabIndex },
                        text = { Text(tabTitle) }
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

/**
 * Header section for the placement profile.
 *
 * @param placement The placement entity.
 * @param child The associated child entity.
 */
@Composable
private fun PlacementHeader(placement: PlacementEntity, child: ChildEntity?) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White).border(2.dp, Color(0xFF8BC34A), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Place, null, tint = Color(0xFF8BC34A))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = stringResource(R.string.placement_id_format, placement.placementId), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = child?.let { "${it.firstName} ${it.lastName}" } ?: stringResource(R.string.placement_unknown_child))
                Badge(containerColor = if (placement.isCurrent) Color(0xFFE8F5E9) else Color.LightGray) {
                    Text(
                        if (placement.isCurrent) stringResource(R.string.placement_status_current_caps) else stringResource(R.string.placement_status_past_caps),
                        color = if (placement.isCurrent) Color(0xFF2E7D32) else Color.DarkGray
                    )
                }
            }
        }
    }
}

/**
 * Tab content for placement overview details.
 *
 * @param placement The placement entity.
 */
@Composable
private fun PlacementOverviewTab(placement: PlacementEntity) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionGreen(stringResource(R.string.detail_basic_info), Icons.Default.Info) {
            DetailRowGreen(stringResource(R.string.placement_label_type), placement.placementType ?: stringResource(R.string.search_na))
            DetailRowGreen(stringResource(R.string.placements_label_start_date), placement.startDate)
            DetailRowGreen(stringResource(R.string.placements_label_end_date), placement.endDate ?: stringResource(R.string.placement_ongoing))
        }
        DetailSectionGreen(stringResource(R.string.detail_description), Icons.AutoMirrored.Filled.Notes) {
            Text(placement.notes ?: stringResource(R.string.placement_no_notes), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * Tab content for associated child information.
 *
 * @param child The child entity.
 */
@Composable
private fun PlacementChildTab(child: ChildEntity?) {
    if (child == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.search_na)) }
        return
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionGreen(stringResource(R.string.placement_child_identity), Icons.Default.Person) {
            DetailRowGreen(stringResource(R.string.placement_label_name), "${child.firstName} ${child.lastName}")
            DetailRowGreen(stringResource(R.string.placement_label_case_number), child.caseNumber ?: stringResource(R.string.search_na))
        }
    }
}

/**
 * Tab content for associated family/provider information.
 *
 * @param family The family entity.
 */
@Composable
private fun PlacementFamilyTab(family: FamilyEntity?) {
    if (family == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.search_na)) }
        return
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionGreen(stringResource(R.string.detail_provider_info), Icons.Default.Home) {
            DetailRowGreen(stringResource(R.string.placement_primary_contact), family.primaryContactName)
            DetailRowGreen(stringResource(R.string.placement_label_phone_field), family.phone ?: stringResource(R.string.search_na))
        }
    }
}

/**
 * Tab content for placement activity logs.
 */
@Composable
private fun PlacementLogsTab() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.placement_activity_history), fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(stringResource(R.string.placement_system_log), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(stringResource(R.string.placement_log_event_desc))
            }
        }
    }
}

/**
 * A styled detail section with a green header.
 *
 * @param sectionTitle The section title.
 * @param icon The section icon.
 * @param content The composable content for the section.
 */
@Composable
private fun DetailSectionGreen(sectionTitle: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(sectionTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF8BC34A))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

/**
 * A styled row for displaying label-value pairs in green theme.
 *
 * @param label The label text.
 * @param value The value text.
 */
@Composable
private fun DetailRowGreen(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
