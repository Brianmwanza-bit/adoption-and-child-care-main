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
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.data.db.DatabaseInitializer
import com.example.adoption_and_childcare.ui.compose.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.adoption_and_childcare.viewmodel.SyncViewModel
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel
import com.example.adoption_and_childcare.viewmodel.SOSViewModel
import com.example.adoption_and_childcare.viewmodel.SOSState
import androidx.work.*
import com.example.adoption_and_childcare.data.sync.SyncWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

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
    USER_ROLES("user_roles", "User Roles"),
    GUARDIANS("guardians", "Guardians"),
    COURT_CASES("court_cases", "Court Cases"),
    FOSTER_TASKS("foster_tasks", "Foster Tasks"),
    FOSTER_MATCHES("foster_matches", "Foster Matches")
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

            val syncViewModel: SyncViewModel = hiltViewModel()
            val notificationsViewModel: NotificationsViewModel = hiltViewModel()
            val sosViewModel: SOSViewModel = hiltViewModel()

            // Feature E7 — SOS Active State UI Banner
            val sosState by sosViewModel.sosState.collectAsState()

            // Feature S5 — Auto-sync on App Foreground
            val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        val lastSynced = context.getSharedPreferences("sync_prefs", android.content.Context.MODE_PRIVATE)
                            .getLong("last_synced_at", 0L)
                        val now = System.currentTimeMillis() / 1000
                        if (now - lastSynced > 15 * 60) {
                            syncViewModel.triggerSync()
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            var isLoggedIn by remember { mutableStateOf<Boolean>(isLoggedInState) }
            var onboardingStep by remember { mutableStateOf(0) } // 0: Landing, 1: Terms, 2: Permissions
            var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
            var currentUser by remember { mutableStateOf<String>(if (isLoggedInState) session.getUsername() ?: "John Doe" else "John Doe") }
            var currentRole by remember { mutableStateOf<String>(if (isLoggedInState) session.getRole() ?: "Admin" else "Admin") }

            // Initialize database with sample data
            LaunchedEffect(Unit) {
                DatabaseInitializer.initializeDatabase(context)
                
                // Schedule periodic background sync
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "PeriodicSync",
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest
                )
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
                            }
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                Column {
                                    if (sosState == SOSState.ACTIVE) {
                                        EmergencyActiveBanner()
                                    }
                                    GreenHeader(
                                        profilePhotoUri = profilePhotoUri,
                                        username = currentUser,
                                        role = currentRole,
                                        onImagePicker = { imagePickerLauncher.launch("image/*") },
                                        onMenuClick = {
                                            scope.launch { drawerState.open() }
                                        }
                                    )
                                }
                            },
                            bottomBar = {
                                BottomNavBar(
                                    navController = navController,
                                    syncViewModel = syncViewModel,
                                    notificationsViewModel = notificationsViewModel,
                                    sosViewModel = sosViewModel
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
        composable(AppRoute.CHILDREN_LIST.route) { 
            ChildrenListScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.FAMILIES.route) { 
            FamiliesScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.ADOPTION_APPS.route) { 
            AdoptionApplicationsScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.HOME_STUDIES.route) { 
            HomeStudiesScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.DOCUMENTS.route) { 
            DocumentsScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.PLACEMENTS.route) { 
            PlacementsScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.REPORTS.route) { 
            CaseReportsScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.EDUCATION.route) { 
            EducationScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.MEDICAL.route) { 
            MedicalScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.FINANCE.route) { 
            FinanceScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.SETTINGS.route) { 
            SettingsScreen() 
        }
        composable(AppRoute.MAP.route) { MapScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.BACKGROUND_CHECKS.route) { BackgroundChecksScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.USER_MANAGEMENT.route) { UserManagementScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.NOTIFICATIONS.route) { NotificationsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.AUDIT_LOGS.route) { AuditLogsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.SEARCH.route) { 
            SearchScreen(onBack = { navController.popBackStack() }) 
        }
        composable(AppRoute.CAMERA.route) { CameraScreen() }
        composable(AppRoute.USER_ROLES.route) { UserRolesScreen(onBack = { navController.popBackStack() }) }
        composable("analytics") { AnalyticsScreen(onBack = { navController.popBackStack() }) }
        composable("guardians") { GuardiansScreen(onBack = { navController.popBackStack() }) }
        composable("court_cases") { CourtCasesScreen(onBack = { navController.popBackStack() }) }
        composable("foster_tasks") { FosterTasksScreen(onBack = { navController.popBackStack() }) }
        composable("foster_matches") { FosterMatchesScreen(onBack = { navController.popBackStack() }) }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
