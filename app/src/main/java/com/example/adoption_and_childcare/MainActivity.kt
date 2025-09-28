package com.example.adoption_and_childcare

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.adoption_and_childcare.data.db.DatabaseInitializer
import com.example.adoption_and_childcare.ui.compose.AdoptionApplicationsScreen
import com.example.adoption_and_childcare.ui.compose.AnalyticsScreen
import com.example.adoption_and_childcare.ui.compose.AuditLogsScreen
import com.example.adoption_and_childcare.ui.compose.BackgroundChecksScreen
import com.example.adoption_and_childcare.ui.compose.CameraScreen
import com.example.adoption_and_childcare.ui.compose.CaseReportsScreen
import com.example.adoption_and_childcare.ui.compose.ChildrenListScreen
import com.example.adoption_and_childcare.ui.compose.DashboardScreen
import com.example.adoption_and_childcare.ui.compose.DocumentsScreen
import com.example.adoption_and_childcare.ui.compose.EducationScreen
import com.example.adoption_and_childcare.ui.compose.FamiliesScreen
import com.example.adoption_and_childcare.ui.compose.FinanceScreen
import com.example.adoption_and_childcare.ui.compose.GreenHeader
import com.example.adoption_and_childcare.ui.compose.HomeStudiesScreen
import com.example.adoption_and_childcare.ui.compose.LoginScreen
import com.example.adoption_and_childcare.ui.compose.MapScreen
import com.example.adoption_and_childcare.ui.compose.MedicalScreen
import com.example.adoption_and_childcare.ui.compose.NotificationsScreen
import com.example.adoption_and_childcare.ui.compose.PlacementsScreen
import com.example.adoption_and_childcare.ui.compose.SearchScreen
import com.example.adoption_and_childcare.ui.compose.SettingsScreen
import com.example.adoption_and_childcare.ui.compose.UserManagementScreen
import com.example.adoption_and_childcare.ui.compose.UserRolesScreen
import com.example.adoption_and_childcare.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint

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

            // Use Boolean.not() instead of ! operator
            when (isLoggedIn.not()) {
                true -> {
                    LoginScreen(
                        onLoginSuccess = {
                            // Refresh from session saved by LoginScreen
                            isLoggedIn = session.isLoggedIn()
                            currentUser = session.getUsername() ?: currentUser
                            currentRole = session.getRole() ?: currentRole
                        }
                    )
                }
                false -> {
                    Scaffold(
                        topBar = {
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
                        },
                        bottomBar = {
                            com.example.adoption_and_childcare.ui.compose.BlueFooter(
                                version = "1.0.0",
                                isSynced = true,
                                onHelpClick = { /* TODO: Show help dialog */ }
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
