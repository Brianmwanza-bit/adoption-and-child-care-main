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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adoptionapp.ui.compose.*

private enum class AppRoute(val route: String, val label: String) {
    LOGIN("login", "Login"),
    DASHBOARD("dashboard", "Dashboard"),
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
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            var drawerOpen by remember { mutableStateOf(false) }
            var isLoggedIn by remember { mutableStateOf(false) }
            var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
            var currentUser by remember { mutableStateOf("John Doe") }
            var currentRole by remember { mutableStateOf("Admin") }

            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                profilePhotoUri = uri
            }

            if (!isLoggedIn) {
                LoginScreen(
                    onLoginSuccess = { isLoggedIn = true },
                    onImagePicker = { imagePickerLauncher.launch("image/*") }
                )
            } else {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            DrawerContent(navController, currentRole)
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
                            GreenHeader(
                                profilePhotoUri = profilePhotoUri,
                                username = currentUser,
                                role = currentRole,
                                onLogout = { isLoggedIn = false },
                                onImagePicker = { imagePickerLauncher.launch("image/*") }
                            )
                            
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
        composable(AppRoute.DASHBOARD.route) { DashboardScreen() }
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


