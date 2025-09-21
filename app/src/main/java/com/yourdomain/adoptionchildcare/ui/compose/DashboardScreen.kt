package com.yourdomain.adoptionchildcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourdomain.adoptionchildcare.viewmodel.NotificationsViewModel
import com.yourdomain.adoptionchildcare.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DashboardCard(
    val title: String,
    val icon: ImageVector,
    val count: Int,
    val summary: String,
    val color: Color,
    val route: String? = null
)

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit = {}, notificationsViewModel: NotificationsViewModel = viewModel()) {
    val loading by notificationsViewModel.loading.collectAsState()
    val error by notificationsViewModel.error.collectAsState()
    val unreadCount by notificationsViewModel.unreadCount.collectAsState()

    val context = LocalContext.current

    var childrenCount by remember { mutableStateOf(0) }
    var familiesCount by remember { mutableStateOf(0) }
    var adoptionAppsCount by remember { mutableStateOf(0) }
    var homeStudiesCount by remember { mutableStateOf(0) }
    var documentsCount by remember { mutableStateOf(0) }
    var placementsCount by remember { mutableStateOf(0) }
    var reportsCount by remember { mutableStateOf(0) }
    var educationCount by remember { mutableStateOf(0) }
    var medicalCount by remember { mutableStateOf(0) }
    var financeCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        notificationsViewModel.loadNotifications()
        val db = AppDatabase.getInstance(context)
        withContext(Dispatchers.IO) {
            val c = db.childDao().count()
            val f = db.familyDao().count()
            val aa = db.adoptionApplicationDao().count()
            val hs = db.homeStudyDao().count()
            val d = db.documentDao().count()
            val p = db.placementDao().count()
            val r = db.caseReportDao().count()
            val e = db.educationRecordDao().count()
            val m = db.medicalRecordDao().count()
            val fin = db.moneyRecordDao().count()
            withContext(Dispatchers.Main) {
                childrenCount = c
                familiesCount = f
                adoptionAppsCount = aa
                homeStudiesCount = hs
                documentsCount = d
                placementsCount = p
                reportsCount = r
                educationCount = e
                medicalCount = m
                financeCount = fin
            }
        }
    }

    val dashboardCards = listOf(
        DashboardCard("Children", Icons.Default.ChildCare, childrenCount, "Total children", Color(0xFF4CAF50), route = "children_list"),
        DashboardCard("Families", Icons.Default.FamilyRestroom, familiesCount, "Registered families", Color(0xFF2196F3), route = "families"),
        DashboardCard("Adoption Applications", Icons.Default.Folder, adoptionAppsCount, "Applications submitted", Color(0xFF9C27B0), route = "adoption_applications"),
        DashboardCard("Home Studies", Icons.Default.AssignmentTurnedIn, homeStudiesCount, "Home studies", Color(0xFFFF9800), route = "home_studies"),
        DashboardCard("Documents", Icons.Default.Description, documentsCount, "Files uploaded", Color(0xFF607D8B), route = "documents"),
        DashboardCard("Placements", Icons.Default.Home, placementsCount, "Active placements", Color(0xFF8BC34A), route = "placements"),
        DashboardCard("Reports & Cases", Icons.Default.Assessment, reportsCount, "Case reports", Color(0xFFE91E63), route = "reports"),
        DashboardCard("Education", Icons.Default.School, educationCount, "Education records", Color(0xFF3F51B5), route = "education"),
        DashboardCard("Medical", Icons.Default.LocalHospital, medicalCount, "Medical records", Color(0xFFF44336), route = "medical"),
        DashboardCard("Finance", Icons.Default.AttachMoney, financeCount, "Transactions", Color(0xFF4CAF50), route = "finance")
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
                    DashboardCardItem(card = card, onClick = {
                        card.route?.let { onNavigate(it) }
                    })
                }
            }
        }
    }
}

@Composable
fun DashboardCardItem(card: DashboardCard, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
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
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(enabled = card.route != null) { onClick() }
            )
            
            Text(
                text = card.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
