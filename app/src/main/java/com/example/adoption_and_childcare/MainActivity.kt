package com.example.adoption_and_childcare

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.adoption_and_childcare.data.db.DatabaseInitializer
import com.example.adoption_and_childcare.ui.compose.*
import com.example.adoption_and_childcare.data.session.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val session = remember { SessionManager(context) }
            val isLoggedInState = session.isLoggedIn()

            var isLoggedIn by remember { mutableStateOf<Boolean>(isLoggedInState) }
            var onboardingStep by remember { mutableStateOf(0) } // 0: Landing, 1: Terms, 2: Permissions
            var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
            var currentUser by remember { mutableStateOf<String>(if (isLoggedInState) session.getUsername() ?: "John Doe" else "John Doe") }
            var currentRole by remember { mutableStateOf<String>(if (isLoggedInState) session.getRole() ?: "Admin" else "Admin") }

            // Initialize database with sample data
            LaunchedEffect(Unit) {
                DatabaseInitializer.initializeDatabase(context)
            }

            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                profilePhotoUri = uri
            }

            Crossfade(targetState = isLoggedIn, label = "main_transition") { loggedIn ->
                if (!loggedIn) {
                    Crossfade(targetState = onboardingStep, label = "onboarding_transition") { step ->
                        when (step) {
                            0 -> {
                                WelcomeScreen(
                                    onExploreClicked = { onboardingStep = 1 },
                                    onLoginClicked = { onboardingStep = 4 } // Step 4 is Login directly
                                )
                            }
                            1 -> {
                                ModernLandingPage(
                                    onGetStarted = { onboardingStep = 2 },
                                    onLogin = { onboardingStep = 4 }
                                )
                            }
                            2 -> {
                                TermsAndConditionsScreen(onAccept = { onboardingStep = 3 })
                            }
                            3 -> {
                                PermissionsScreen(onContinue = { onboardingStep = 4 })
                            }
                            else -> {
                                LoginScreen(
                                    onLoginSuccess = {
                                        isLoggedIn = session.isLoggedIn()
                                        currentUser = session.getUsername() ?: currentUser
                                        currentRole = session.getRole() ?: currentRole
                                    }
                                )
                            }
                        }
                    }
                } else {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                DrawerHeader(
                                    profilePhotoUri = profilePhotoUri,
                                    username = currentUser,
                                    role = currentRole,
                                    onLogout = {
                                        session.clearSession()
                                        isLoggedIn = false
                                        scope.launch { drawerState.close() }
                                    }
                                )
                                Spacer(Modifier.height(12.dp))
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                                    label = { Text("Dashboard") },
                                    selected = currentRoute(navController) == AppRoute.DASHBOARD.route,
                                    onClick = {
                                        navController.navigate(AppRoute.DASHBOARD.route) {
                                            popUpTo(AppRoute.DASHBOARD.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.ChildCare, contentDescription = null) },
                                    label = { Text("Children") },
                                    selected = currentRoute(navController) == AppRoute.CHILDREN_LIST.route,
                                    onClick = {
                                        navController.navigate(AppRoute.CHILDREN_LIST.route)
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null) },
                                    label = { Text("Families") },
                                    selected = currentRoute(navController) == AppRoute.FAMILIES.route,
                                    onClick = {
                                        navController.navigate(AppRoute.FAMILIES.route)
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    label = { Text("Settings") },
                                    selected = currentRoute(navController) == AppRoute.SETTINGS.route,
                                    onClick = {
                                        navController.navigate(AppRoute.SETTINGS.route)
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                                Divider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                                    label = { Text("Applications") },
                                    selected = currentRoute(navController) == AppRoute.ADOPTION_APPS.route,
                                    onClick = {
                                        navController.navigate(AppRoute.ADOPTION_APPS.route)
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Description, contentDescription = null) },
                                    label = { Text("Documents") },
                                    selected = currentRoute(navController) == AppRoute.DOCUMENTS.route,
                                    onClick = {
                                        navController.navigate(AppRoute.DOCUMENTS.route)
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                                    label = { Text("Reports") },
                                    selected = currentRoute(navController) == AppRoute.REPORTS.route,
                                    onClick = {
                                        navController.navigate(AppRoute.REPORTS.route)
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                                    label = { Text("Notifications") },
                                    selected = currentRoute(navController) == AppRoute.NOTIFICATIONS.route,
                                    onClick = {
                                        navController.navigate(AppRoute.NOTIFICATIONS.route)
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                                // Add more items as needed
                            }
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                GreenHeader(
                                    profilePhotoUri = profilePhotoUri,
                                    username = currentUser,
                                    role = currentRole,
                                    onImagePicker = { imagePickerLauncher.launch("image/*") },
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    }
                                )
                            },
                            bottomBar = {
                                com.example.adoption_and_childcare.ui.compose.BlueFooter(
                                    version = "1.0.0",
                                    isSynced = true,
                                    onHelpClick = { /* TODO: Show help dialog */ },
                                    onEmergencyClick = { /* TODO: Handle emergency call */ },
                                    onPrivacyClick = { /* TODO: Show privacy policy */ },
                                    onTermsClick = { /* TODO: Show terms of service */ },
                                    onAgencyContactClick = { /* TODO: Show agency contact info */ },
                                    onReportAbuseClick = { /* TODO: Show report abuse form */ }
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
                            }
                        }
                    }
                }
            }
            }
        }
    }

@Composable
private fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = AppRoute.DASHBOARD.route, modifier = modifier) {
        composable(AppRoute.DASHBOARD.route) {
            val current = currentRoute(navController)
            DashboardScreen(
                onNavigate = { route ->
                    if (current != route) {
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
        // Add AnalyticsScreen to navigation
        composable("analytics") { AnalyticsScreen() }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}