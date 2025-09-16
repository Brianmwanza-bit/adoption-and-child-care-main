import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.List
@Composable
fun AppShell(viewModel: AuthViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isAdmin = currentUser?.role == "admin"
    val menuItems = listOf(
        "Dashboard" to Icons.Default.Dashboard,
        "Children" to Icons.Default.People,
        "Families & Placements" to Icons.Default.Assignment,
        "Tasks" to Icons.Default.List,
        "Documents" to Icons.Default.Folder,
        "Reports & Cases" to Icons.Default.Report,
        "Finance" to Icons.Default.Money,
        "Profile & Settings" to Icons.Default.Settings,
        "Logout" to Icons.Default.Logout
    )
    var selectedMenu by remember { mutableStateOf("Dashboard") }

    val currentUser by viewModel.currentUser.collectAsState()
    val profilePhoto by viewModel.profilePhoto.collectAsState()
    var showImagePicker by remember { mutableStateOf(false) }
    var showProfilePanel by remember { mutableStateOf(false) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .background(Color.White)
            ) {
                // User profile block
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .clickable { showProfilePanel = true },
                    contentAlignment = Alignment.Center
                ) {
                    Box {
                        if (profilePhoto != null) {
                            Image(
                                painter = rememberAsyncImagePainter(profilePhoto),
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Photo", color = Color.DarkGray)
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Photo",
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.BottomEnd)
                                .clickable { showImagePicker = true },
                            tint = Color(0xFF388E3C)
                        )
                    }
                }
                if (currentUser != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(currentUser!!.username, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(currentUser!!.role, fontSize = 14.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                menuItems.forEach { (label, icon) ->
                    if (label == "Profile & Settings" && isAdmin) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            NavigationDrawerItem(
                                label = { Text(label) },
                                selected = selectedMenu == label,
                                onClick = {
                                    selectedMenu = label
                                    scope.launch { drawerState.close() }
                                },
                                icon = { Icon(icon, contentDescription = label) },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = Color(0xFF388E3C).copy(alpha = 0.1f),
                                    selectedIconColor = Color(0xFF388E3C),
                                    selectedTextColor = Color(0xFF388E3C)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            // Admin-only nested items
                            NavigationDrawerItem(
                                label = { Text("Audit Logs") },
                                selected = selectedMenu == "Audit Logs",
                                onClick = {
                                    selectedMenu = "Audit Logs"
                                    scope.launch { drawerState.close() }
                                },
                                icon = { Icon(Icons.Default.VerifiedUser, contentDescription = "Audit Logs") },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = Color(0xFF388E3C).copy(alpha = 0.1f),
                                    selectedIconColor = Color(0xFF388E3C),
                                    selectedTextColor = Color(0xFF388E3C)
                                ),
                                modifier = Modifier.fillMaxWidth().padding(start = 24.dp)
                            )
                            NavigationDrawerItem(
                                label = { Text("Permissions") },
                                selected = selectedMenu == "Permissions",
                                onClick = {
                                    selectedMenu = "Permissions"
                                    scope.launch { drawerState.close() }
                                },
                                icon = { Icon(Icons.Default.VerifiedUser, contentDescription = "Permissions") },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = Color(0xFF388E3C).copy(alpha = 0.1f),
                                    selectedIconColor = Color(0xFF388E3C),
                                    selectedTextColor = Color(0xFF388E3C)
                                ),
                                modifier = Modifier.fillMaxWidth().padding(start = 24.dp)
                            )
                        }
                    } else if (label != "Audit Logs" && label != "Permissions") {
                        NavigationDrawerItem(
                            label = { Text(label) },
                            selected = selectedMenu == label,
                            onClick = {
                                selectedMenu = label
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color(0xFF388E3C).copy(alpha = 0.1f),
                                selectedIconColor = Color(0xFF388E3C),
                                selectedTextColor = Color(0xFF388E3C)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    Text(selectedMenu, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF388E3C))
                }
            },
            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when {
                        showProfilePanel -> ProfileSettingsPanel(viewModel) // To be implemented
                        selectedMenu == "Dashboard" -> DashboardScreen(viewModel)
                        // TODO: Add other screens for menu items
                        else -> DashboardScreen(viewModel)
                    }
                }
                if (showImagePicker) {
                    // TODO: Implement image picker and update profile photo
                    showImagePicker = false
                }
            }
        )
    }
}
package com.adoptionapp.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.adoptionapp.viewmodel.AuthViewModel

@Composable
fun MainEntryScreen(viewModel: AuthViewModel) {
    val isRegistered by viewModel.isRegistered.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val error by viewModel.error.collectAsState()
    val profilePhoto by viewModel.profilePhoto.collectAsState()
    var showImagePicker by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("") }
        var showLoginDialog by remember { mutableStateOf(false) }
        var loginInput by remember { mutableStateOf("") }
        var loginPassword by remember { mutableStateOf("") }
                .fillMaxWidth()
                .height(80.dp)
                // Purple accent color
                val primaryColor = Color(0xFF8E24AA)
        ) {
            Text("Welcome", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Profile photo section (now between header and buttons)
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box {
                if (profilePhoto != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profilePhoto),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Photo", color = Color.DarkGray)
                    }
                }
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Photo",
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .clickable { showImagePicker = true },
                    tint = Color(0xFF388E3C)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Registration/Login buttons
        if (!isRegistered) {
            Button(
                onClick = {
                    viewModel.register(username, email, password, role, profilePhoto)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                                tint = primaryColor
            }
        }
        Button(
            onClick = {
                viewModel.login(email, password)
            },
            Button(
                onClick = {
                    showLoginDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Login", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
            }
            if (showLoginDialog) {
                AlertDialog(
                    onDismissRequest = { showLoginDialog = false },
                    title = { Text("Login") },
                    text = {
                        Column {
                            Text("Enter Username or Email")
                            TextField(
                                value = loginInput,
                                onValueChange = { loginInput = it },
                                label = { Text("Username or Email") },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text("Password") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation()
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            showLoginDialog = false
                            viewModel.loginWithUsernameOrEmail(loginInput, loginPassword)
                        }) {
                            Text("Login")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showLoginDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        if (showImagePicker) {
            // TODO: Implement image picker and update profile photo
            showImagePicker = false
        }
    }
}

@Composable
fun DashboardScreen(viewModel: AuthViewModel) {
    // Responsive grid of dashboard cards
    val dashboardCards = listOf(
        DashboardCardData("Children", Icons.Default.People, viewModel.getChildrenCount(), viewModel.getLastChildSummary()),
        DashboardCardData("Families", Icons.Default.Assignment, viewModel.getFamilyCount(), viewModel.getLastFamilySummary()),
        DashboardCardData("Placements", Icons.Default.Assignment, viewModel.getPlacementsSummary(), viewModel.getPlacementsDetail()),
        DashboardCardData("Tasks", Icons.Default.List, viewModel.getNextTaskSummary(), viewModel.getTaskDetail()),
        DashboardCardData("Documents", Icons.Default.Folder, viewModel.getDocumentsSummary(), viewModel.getLastDocumentType()),
        DashboardCardData("Reports & Cases", Icons.Default.Report, viewModel.getReportsCasesSummary(), viewModel.getLastReportCase()),
        DashboardCardData("Finance", Icons.Default.Money, viewModel.getFinanceSummary(), viewModel.getLastTransaction()),
        DashboardCardData("Background Checks", Icons.Default.VerifiedUser, viewModel.getPendingChecksCount(), viewModel.getBackgroundCheckDetail()),
        DashboardCardData("Education", Icons.Default.Assignment, viewModel.getEducationSummary(), viewModel.getLastEducationUpdate()),
        DashboardCardData("Medical", Icons.Default.Assignment, viewModel.getMedicalSummary(), viewModel.getNextFollowUp())
    )
    var refreshing by remember { mutableStateOf(false) }
    var selectedEntity by remember { mutableStateOf<String?>(null) }
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dashboard", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF388E3C))
            IconButton(onClick = { refreshing = true }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF388E3C))
            }
        }
        if (selectedEntity == null) {
            val columns = if (/* check screen width for tablet */ false) 2 else 1
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(dashboardCards) { card ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable { selectedEntity = card.title },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(card.icon, contentDescription = card.title, tint = Color(0xFF388E3C), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable { selectedEntity = card.title }
                                    .graphicsLayer {
                                        alpha = 0.85f
                                        shadowElevation = 12.dp.toPx()
                                    }
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.6f),
                                                Color(0xFFB39DDB).copy(alpha = 0.4f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(card.icon, contentDescription = card.title, tint = primaryColor, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(card.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Text(card.primaryMetric, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryColor)
                                        Text(card.secondarySummary, fontSize = 14.sp, color = Color.Gray)
                                    }
                                }
                            }
    var searchQuery by remember { mutableStateOf("") }
    var showFilter by remember { mutableStateOf(false) }
    val items = when (entity) {
        "Children" -> viewModel.searchChildren(searchQuery)
        // TODO: Add other entity search calls
        else -> emptyList<Any>()
    }
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showFilter = true }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
            }
        }
        // List items
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items) { item ->
                // TODO: Render compact card for item
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = 0.85f
                            shadowElevation = 8.dp.toPx()
                        }
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.6f),
                                    Color(0xFFB39DDB).copy(alpha = 0.4f)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    // TODO: Show 2-3 key fields for item
                    Text(item.toString(), modifier = Modifier.padding(16.dp))
                }
            }
        }
        // FAB for create form
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            FloatingActionButton(onClick = { /* TODO: Open create form */ }, containerColor = Color(0xFF1976D2)) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
            }
        }
        if (showFilter) {
            // TODO: Show filter panel
            showFilter = false
        }
    }
}
}

data class DashboardCardData(
    val title: String,
    val icon: ImageVector,
    val primaryMetric: String,
    val secondarySummary: String
)
}
