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
 * @param child The related child entity, if available.
 * @param family The related family/provider entity, if available.
 * @param onBack Callback for back navigation.
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
    ) { scaffoldPadding ->
        /** @param scaffoldPadding The padding values provided by the Scaffold. */
        PlacementScreenContent(
            padding = scaffoldPadding,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            tabs = tabs,
            placement = placement,
            child = child,
            family = family
        )
    }
}

/**
 * Main content area of the placement profile screen.
 * 
 * @param padding The inner padding provided by the Scaffold to avoid overlapping with top bar.
 * @param selectedTab The index of the currently selected tab.
 * @param onTabSelected Callback when a tab is selected.
 * @param tabs The list of tab titles.
 * @param placement The placement entity.
 * @param child The related child entity.
 * @param family The related family entity.
 */
@Composable
private fun PlacementScreenContent(
    padding: PaddingValues,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    placement: PlacementEntity,
    child: ChildEntity?,
    family: FamilyEntity?
) {
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        PlacementHeader(placement, child)

        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Color(0xFF8BC34A),
            edgePadding = 16.dp
        ) {
            for ((index, tabTitle) in tabs.withIndex()) {
                PlacementProfileTab(
                    index = index,
                    title = tabTitle,
                    isSelected = selectedTab == index,
                    onClick = { onTabSelected(index) }
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

/**
 * A single tab in the placement profile tab row.
 * 
 * @param index The zero-based index of the tab.
 * @param title The localized title text for the tab.
 * @param isSelected Whether the tab is currently selected.
 * @param onClick Callback when the tab is clicked.
 */
@Composable
private fun PlacementProfileTab(
    index: Int,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = { Text(title) }
    )
}

/**
 * Header component for the placement profile.
 * 
 * @param placement The placement entity.
 * @param child The related child entity.
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
                Text(text = stringResource(R.string.placement_id_format, placement.placementId.toString()), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = child?.let { "${it.firstName} ${it.lastName}" } ?: stringResource(R.string.placement_unknown_child))
                Badge(containerColor = if (placement.isCurrent) Color(0xFFE8F5E9) else Color.LightGray) {
                    Text(if (placement.isCurrent) stringResource(R.string.placement_status_current_caps) else stringResource(R.string.placement_status_past_caps), color = if (placement.isCurrent) Color(0xFF2E7D32) else Color.DarkGray)
                }
            }
        }
    }
}

/**
 * Tab displaying general placement overview details.
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
 * Tab displaying identity information for the child in this placement.
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
 * Tab displaying information for the family/provider hosting this placement.
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
 * Tab displaying historical activity logs for the placement.
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
 * A section for displaying grouped placement details.
 * 
 * @param title The title of the section.
 * @param icon The icon representing the section.
 * @param content The composable content to display within the section.
 */
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

/**
 * A row for displaying a single placement detail label and value.
 * 
 * @param label The descriptive label for the detail.
 * @param value The value associated with the label.
 */
@Composable
private fun DetailRowGreen(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
