package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.entities.CaseActivityEntity
import com.example.adoption_and_childcare.viewmodel.CaseToolsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseActivityFeedScreen(onBack: () -> Unit, viewModel: CaseToolsViewModel = hiltViewModel()) {
    val activities by viewModel.caseActivities.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Case Activity Feed") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.FilterList, "Filter") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { }, containerColor = Color(0xFF4CAF50), contentColor = Color.White) {
                Icon(Icons.Default.Mic, "Voice Note")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (activities.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No recent activities logged.", color = Color.Gray) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    items(activities) { activity ->
                        ActivityTimelineItem(activity)
                    }
                }
            }
        }
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
