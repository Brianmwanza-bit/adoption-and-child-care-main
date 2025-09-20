package com.adoptionapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adoptionapp.ui.compose.*
import com.adoptionapp.data.session.SessionManager
import com.adoptionapp.data.db.DatabaseInitializer

private enum class AppRoute(val route: String, val label: String) {
    LOGIN("login", "Login"),
    DASHBOARD("dashboard", "Dashboard"),
    CHILDREN_LIST("children_list", "Children"),
    FAMILIES("families", "Families"),
    ADOPTION_APPS("adoption_applications", "Adoption Applications"),
    HOME_STUDIES("home_studies", "Home Studies"),
    DOCUMENTS("documents", "Documents"),
    PLACEMENTS("placements", "Placements"),
    REPORTS("reports", "Reports & Cases"),
    EDUCATION("education", "Education"),
    MEDICAL("medical", "Medical"),
    FINANCE("finance", "Finance"),
    SETTINGS("settings", "Settings"),
    MAP("map", "Map"),
    BACKGROUND_CHECKS("background_checks", "Background Checks"),
    USER_MANAGEMENT("user_management", "User Management"),
    NOTIFICATIONS("notifications", "Notifications"),
    AUDIT_LOGS("audit_logs", "Audit Logs"),
    SEARCH("search", "Search"),
    CAMERA("camera", "Camera"),
    USER_ROLES("user_roles", "User Roles")
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val session = remember { SessionManager(context) }
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            var drawerOpen by remember { mutableStateOf(false) }
            var isLoggedIn by remember { mutableStateOf(session.isLoggedIn()) }
            var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
            var currentUser by remember { mutableStateOf(if (session.isLoggedIn()) session.username() else "John Doe") }
            var currentRole by remember { mutableStateOf(if (session.isLoggedIn()) session.role() else "Admin") }

            // Initialize database with sample data
            LaunchedEffect(Unit) {
                DatabaseInitializer.initializeDatabase(context)
            }

            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                profilePhotoUri = uri
            }

            if (!isLoggedIn) {
                LoginScreen(
                    onLoginSuccess = {
                        // Refresh from session saved by LoginScreen
                        isLoggedIn = session.isLoggedIn()
                        currentUser = session.username().ifBlank { currentUser }
                        currentRole = session.role().ifBlank { currentRole }
                    }
                )
            } else {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            // Green block at the very top of the left drawer
                            GreenHeader(
                                profilePhotoUri = profilePhotoUri,
                                username = currentUser,
                                role = currentRole,
                                onLogout = {
                                    session.clearSession()
                                    isLoggedIn = false
                                },
                                onImagePicker = { imagePickerLauncher.launch("image/*") }
                            )

                            // Drawer items (9) under the green block. Functions (routes) unchanged
                            val items = listOf(
                                Pair("Children", AppRoute.DASHBOARD),
                                Pair("Family", AppRoute.FAMILIES),
                                Pair("Document", AppRoute.DOCUMENTS),
                                Pair("Reports", AppRoute.REPORTS),
                                Pair("Users", AppRoute.USER_MANAGEMENT),
                                Pair("Settings", AppRoute.SETTINGS),
                                Pair("Background Checks", AppRoute.BACKGROUND_CHECKS),
                                Pair("Notifications", AppRoute.NOTIFICATIONS),
                                Pair("Map", AppRoute.MAP)
                            )
                            val currentRoute = currentRoute(navController)

                            items.forEach { (label, route) ->
                                // Respect admin-only visibility for Audit Logs
                                if (route == AppRoute.AUDIT_LOGS && currentRole != "Admin") return@forEach
                                NavigationDrawerItem(
                                    label = { Text(label) },
                                    selected = currentRoute == route.route,
                                    onClick = {
                                        if (currentRoute != route.route) {
                                            navController.navigate(route.route) {
                                                popUpTo(AppRoute.DASHBOARD.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    modifier = Modifier,
                                    colors = NavigationDrawerItemDefaults.colors()
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // You can add any static footer or version here if needed
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(currentScreenLabel(navController)) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        drawerOpen = !drawerOpen
                                        if (drawerOpen) drawerState.open() else drawerState.close()
                                    }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        if (currentRoute(navController) != AppRoute.SEARCH.route) {
                                            navController.navigate(AppRoute.SEARCH.route) {
                                                popUpTo(AppRoute.DASHBOARD.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Search,
                                            contentDescription = "Search"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            )
                        }
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            AppNavHost(
                                navController = navController,
                                modifier = Modifier.weight(1f)
                            )
                            BlueFooter(
                                onHelpClick = { /* TODO: Show help dialog */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerContent(navController: NavHostController, currentRole: String) {
    val items = listOf(
        AppRoute.DASHBOARD,
        AppRoute.MAP,
        AppRoute.BACKGROUND_CHECKS,
        AppRoute.USER_MANAGEMENT,
        AppRoute.NOTIFICATIONS,
        AppRoute.AUDIT_LOGS,
        AppRoute.SEARCH,
        AppRoute.CAMERA,
        AppRoute.USER_ROLES
    )
    val currentRoute = currentRoute(navController)
    
    items.forEach { item ->
        // Show admin-only items only for Admin role
        if (item == AppRoute.AUDIT_LOGS && currentRole != "Admin") {
            return@forEach
        }
        
        NavigationDrawerItem(
            label = { Text(item.label) },
            selected = currentRoute == item.route,
            onClick = {
                if (currentRoute != item.route) {
                    navController.navigate(item.route) {
                        popUpTo(AppRoute.DASHBOARD.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier,
            colors = NavigationDrawerItemDefaults.colors()
        )
    }
}

@Composable
private fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = AppRoute.DASHBOARD.route, modifier = modifier) {
        composable(AppRoute.DASHBOARD.route) {
            DashboardScreen(
                onNavigate = { route ->
                    if (currentRoute(navController) != route) {
                        navController.navigate(route) {
                            popUpTo(AppRoute.DASHBOARD.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
        composable(AppRoute.CHILDREN_LIST.route) { ChildrenListScreen() }
        composable(AppRoute.FAMILIES.route) { FamiliesScreen() }
        composable(AppRoute.ADOPTION_APPS.route) { AdoptionApplicationsScreen() }
        composable(AppRoute.HOME_STUDIES.route) { HomeStudiesScreen() }
        composable(AppRoute.DOCUMENTS.route) { DocumentsScreen() }
        composable(AppRoute.PLACEMENTS.route) { PlacementsScreen() }
        composable(AppRoute.REPORTS.route) { CaseReportsScreen() }
        composable(AppRoute.EDUCATION.route) { EducationScreen() }
        composable(AppRoute.MEDICAL.route) { MedicalScreen() }
        composable(AppRoute.FINANCE.route) { FinanceScreen() }
        composable(AppRoute.SETTINGS.route) { SettingsScreen() }
        composable(AppRoute.MAP.route) { MapScreen() }
        composable(AppRoute.BACKGROUND_CHECKS.route) { BackgroundChecksScreen() }
        composable(AppRoute.USER_MANAGEMENT.route) { UserManagementScreen() }
        composable(AppRoute.NOTIFICATIONS.route) { NotificationsScreen() }
        composable(AppRoute.AUDIT_LOGS.route) { AuditLogsScreen() }
        composable(AppRoute.SEARCH.route) { SearchScreen() }
        composable(AppRoute.CAMERA.route) { CameraScreen() }
        composable(AppRoute.USER_ROLES.route) { UserRolesScreen() }
    }
}

@Composable
private fun currentScreenLabel(navController: NavHostController): String {
    val route = currentRoute(navController)
    return AppRoute.values().firstOrNull { it.route == route }?.label ?: "Adoption App"
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}


