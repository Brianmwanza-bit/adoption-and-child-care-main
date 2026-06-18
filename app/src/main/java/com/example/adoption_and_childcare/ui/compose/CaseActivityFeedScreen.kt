package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.entities.CaseActivityEntity
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel

/**
 * Screen displaying a timeline of activities related to case management.
 * 
 * Users can view a historical feed of visits, court hearings, and other
 * case-related events, and log new activities.
 * 
 * @param onBack Callback for navigating back.
 * @param viewModel ViewModel for managing case activity data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseActivityFeedScreen(onBack: () -> Unit, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val activities by viewModel.caseActivities.collectAsState()
    val lookupSettings by viewModel.lookupSettings.collectAsState()
    
    var showCreate by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Visit") }
    var notes by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val activityTypes = remember(lookupSettings) {
        lookupSettings.find { it.settingKey == "case_activity_types" }
            ?.settingValue?.split(",") ?: listOf("Visit", "Court", "Medical", "Education", "Other")
    }
    var showTypeDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.activity_feed_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.activity_feed_back_desc)) }
                },
                actions = {
                    IconButton(onClick = { /* Implement real filter logic */ }) { Icon(Icons.Default.FilterList, stringResource(R.string.activity_feed_filter_desc)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50), 
                    titleContentColor = Color.White, 
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }, containerColor = Color(0xFF4CAF50), contentColor = Color.White) {
                Icon(Icons.Default.Add, stringResource(R.string.activity_feed_add_desc))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (activities.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Spacer(Modifier.height(16.dp))
                        Text(stringResource(R.string.activity_feed_no_activities), color = Color.Gray) 
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    items(activities) { activity ->
                        ActivityTimelineItem(activity)
                    }
                }
            }
        }
    }

    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text(stringResource(R.string.activity_feed_add_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.activity_feed_field_title)) }, modifier = Modifier.fillMaxWidth())
                    ExposedDropdownMenuBox(expanded = showTypeDropdown, onExpandedChange = { showTypeDropdown = !showTypeDropdown }) {
                        OutlinedTextField(
                            value = type,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.activity_feed_field_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showTypeDropdown, onDismissRequest = { showTypeDropdown = false }) {
                            activityTypes.forEach { t ->
                                DropdownMenuItem(text = { Text(t) }, onClick = {
                                    type = t
                                    showTypeDropdown = false
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text(stringResource(R.string.activity_feed_field_location)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.activity_feed_field_notes)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (title.isNotBlank()) {
                        viewModel.saveCaseActivity(
                            CaseActivityEntity(
                                caseId = 1, // Default for now, should be passed in real scenario
                                activityType = type,
                                activityDate = System.currentTimeMillis().toString(), // Should use a proper date picker
                                title = title,
                                notes = notes,
                                location = location.ifBlank { null }
                            )
                        )
                        showCreate = false
                        title = ""
                        notes = ""
                        location = ""
                    }
                }) { Text(stringResource(R.string.activity_feed_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.activity_feed_cancel)) }
            }
        )
    }
}

@Composable
private fun ActivityTimelineItem(activity: CaseActivityEntity) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
            Box(Modifier.width(2.dp).weight(1f).background(Color.LightGray))
        }
        Spacer(Modifier.width(16.dp))
        Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(activity.activityType, style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    Text(activity.activityDate ?: "", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Text(activity.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(activity.notes ?: "", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                if (activity.location != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Text(activity.location, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}
