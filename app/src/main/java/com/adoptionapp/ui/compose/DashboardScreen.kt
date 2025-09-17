package com.adoptionapp.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptionapp.viewmodel.NotificationsViewModel

data class DashboardCard(
    val title: String,
    val icon: ImageVector,
    val count: Int,
    val summary: String,
    val color: Color
)

@Composable
fun DashboardScreen(notificationsViewModel: NotificationsViewModel = viewModel()) {
    val loading by notificationsViewModel.loading.collectAsState()
    val error by notificationsViewModel.error.collectAsState()
    val unreadCount by notificationsViewModel.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        notificationsViewModel.loadNotifications()
    }

    val dashboardCards = listOf(
        DashboardCard(
            title = "Children",
            icon = Icons.Default.ChildCare,
            count = 24,
            summary = "Last added: Sarah (Age 8)",
            color = Color(0xFF4CAF50)
        ),
        DashboardCard(
            title = "Families",
            icon = Icons.Default.FamilyRestroom,
            count = 12,
            summary = "Active families",
            color = Color(0xFF2196F3)
        ),
        DashboardCard(
            title = "Placements",
            icon = Icons.Default.Home,
            count = 18,
            summary = "Current placements",
            color = Color(0xFF9C27B0)
        ),
        DashboardCard(
            title = "Tasks",
            icon = Icons.Default.Assignment,
            count = 8,
            summary = "Upcoming tasks",
            color = Color(0xFFFF9800)
        ),
        DashboardCard(
            title = "Documents",
            icon = Icons.Default.Description,
            count = 45,
            summary = "Recent uploads",
            color = Color(0xFF607D8B)
        ),
        DashboardCard(
            title = "Reports & Cases",
            icon = Icons.Default.Assessment,
            count = 6,
            summary = "Active cases",
            color = Color(0xFFE91E63)
        ),
        DashboardCard(
            title = "Finance",
            icon = Icons.Default.AttachMoney,
            count = 32,
            summary = "Recent transactions",
            color = Color(0xFF4CAF50)
        ),
        DashboardCard(
            title = "Background Checks",
            icon = Icons.Default.Security,
            count = 4,
            summary = "Pending checks",
            color = Color(0xFFFF5722)
        ),
        DashboardCard(
            title = "Education",
            icon = Icons.Default.School,
            count = 15,
            summary = "Education records",
            color = Color(0xFF3F51B5)
        ),
        DashboardCard(
            title = "Medical",
            icon = Icons.Default.LocalHospital,
            count = 7,
            summary = "Next follow-ups",
            color = Color(0xFFF44336)
        )
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (loading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dashboardCards) { card ->
                    DashboardCardItem(card = card)
                }
            }
        }
    }
}

@Composable
fun DashboardCardItem(card: DashboardCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = card.icon,
                    contentDescription = card.title,
                    tint = card.color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = card.count.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = card.color
                )
            }
            
            Text(
                text = card.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = card.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}