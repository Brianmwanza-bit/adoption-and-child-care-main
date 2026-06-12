package com.example.adoption_and_childcare

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.DatabaseInitializer
import com.example.adoption_and_childcare.ui.compose.*
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel
import com.example.adoption_and_childcare.viewmodel.SOSState
import com.example.adoption_and_childcare.viewmodel.SOSViewModel
import com.example.adoption_and_childcare.viewmodel.SyncViewModel
import com.yourdomain.adoptionchildcare.BuildConfig
import com.yourdomain.adoptionchildcare.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch

/**
 * Constants used throughout the MainActivity.
 */
@Suppress("HardcodedStringLiteral")
private object MainActivityConstants {
    const val DEFAULT_USER = "John Doe"
    const val DEFAULT_ROLE = "Admin"
    const val SYNC_PREFS = "sync_prefs"
    const val LAST_SYNCED_KEY = "last_synced_at"
    const val IMAGE_MIME_TYPE = "image/*"
    const val ONBOARDING_TRANSITION_LABEL = "onboarding_transition"
    
    // Routes
    const val DASHBOARD_ROUTE = "dashboard"
    const val CHILDREN_LIST_ROUTE = "children_list"
    const val FAMILIES_ROUTE = "families"
    const val ADOPTION_APPS_ROUTE = "adoption_applications"
    const val HOME_STUDIES_ROUTE = "home_studies"
    const val DOCUMENTS_ROUTE = "documents"
    const val PLACEMENTS_ROUTE = "placements"
    const val REPORTS_ROUTE = "reports"
    const val EDUCATION_ROUTE = "education"
    const val MEDICAL_ROUTE = "medical"
    const val FINANCE_ROUTE = "finance"
    const val SETTINGS_ROUTE = "settings"
    const val MAP_ROUTE = "map"
    const val BACKGROUND_CHECKS_ROUTE = "background_checks"
    const val USER_MANAGEMENT_ROUTE = "user_management"
    const val NOTIFICATIONS_ROUTE = "notifications"
    const val AUDIT_LOGS_ROUTE = "audit_logs"
    const val SEARCH_ROUTE = "search"
    const val CAMERA_ROUTE = "camera"
    const val USER_ROLES_ROUTE = "user_roles"
    const val ANALYTICS_ROUTE = "analytics"
    const val GUARDIANS_ROUTE = "guardians"
    const val COURT_CASES_ROUTE = "court_cases"
    const val FOSTER_TASKS_ROUTE = "foster_tasks"
    const val FOSTER_MATCHES_ROUTE = "foster_matches"
    const val TASKS_ROUTE = "tasks"
    const val ACTION_ITEMS_ROUTE = "action_items"
    const val RISK_ASSESSMENTS_ROUTE = "risk_assessments"
    const val PERMANENCY_PLANS_ROUTE = "permanency_plans"
    const val CASE_ACTIVITIES_ROUTE = "case_activities"
    const val CASE_DEADLINES_ROUTE = "case_deadlines"
    const val CASE_APPROVALS_ROUTE = "case_approvals"
    const val CASE_URGENCY_FLAGS_ROUTE = "case_urgency_flags"
    const val CRITICAL_DATES_ROUTE = "critical_dates"
    const val WORKLOAD_ROUTE = "workload"
    const val WORKER_MESSAGES_ROUTE = "worker_messages"
    const val PLACEMENT_COMPATIBILITY_ROUTE = "placement_compatibility"
    const val DASHBOARD_PREFERENCES_ROUTE = "dashboard_preferences"
    
    const val SYNC_THRESHOLD_SECONDS = 15 * 60
}

/**
 * Routes for the application navigation.
 *
 * @property route The string representation of the route.
 */
private enum class AppRoute(val route: String) {
    DASHBOARD(MainActivityConstants.DASHBOARD_ROUTE),
    CHILDREN_LIST(MainActivityConstants.CHILDREN_LIST_ROUTE),
    FAMILIES(MainActivityConstants.FAMILIES_ROUTE),
    ADOPTION_APPS(MainActivityConstants.ADOPTION_APPS_ROUTE),
    HOME_STUDIES(MainActivityConstants.HOME_STUDIES_ROUTE),
    DOCUMENTS(MainActivityConstants.DOCUMENTS_ROUTE),
    PLACEMENTS(MainActivityConstants.PLACEMENTS_ROUTE),
    REPORTS(MainActivityConstants.REPORTS_ROUTE),
    EDUCATION(MainActivityConstants.EDUCATION_ROUTE),
    MEDICAL(MainActivityConstants.MEDICAL_ROUTE),
    FINANCE(MainActivityConstants.FINANCE_ROUTE),
    SETTINGS(MainActivityConstants.SETTINGS_ROUTE),
    MAP(MainActivityConstants.MAP_ROUTE),
    BACKGROUND_CHECKS(MainActivityConstants.BACKGROUND_CHECKS_ROUTE),
    USER_MANAGEMENT(MainActivityConstants.USER_MANAGEMENT_ROUTE),
    NOTIFICATIONS(MainActivityConstants.NOTIFICATIONS_ROUTE),
    AUDIT_LOGS(MainActivityConstants.AUDIT_LOGS_ROUTE),
    SEARCH(MainActivityConstants.SEARCH_ROUTE),
    CAMERA(MainActivityConstants.CAMERA_ROUTE),
    USER_ROLES(MainActivityConstants.USER_ROLES_ROUTE),
    ANALYTICS(MainActivityConstants.ANALYTICS_ROUTE),
    GUARDIANS(MainActivityConstants.GUARDIANS_ROUTE),
    COURT_CASES(MainActivityConstants.COURT_CASES_ROUTE),
    FOSTER_TASKS(MainActivityConstants.FOSTER_TASKS_ROUTE),
    FOSTER_MATCHES(MainActivityConstants.FOSTER_MATCHES_ROUTE),
    TASKS(MainActivityConstants.TASKS_ROUTE),
    ACTION_ITEMS(MainActivityConstants.ACTION_ITEMS_ROUTE),
    RISK_ASSESSMENTS(MainActivityConstants.RISK_ASSESSMENTS_ROUTE),
    PERMANENCY_PLANS(MainActivityConstants.PERMANENCY_PLANS_ROUTE),
    CASE_ACTIVITIES(MainActivityConstants.CASE_ACTIVITIES_ROUTE),
    CASE_DEADLINES(MainActivityConstants.CASE_DEADLINES_ROUTE),
    CASE_APPROVALS(MainActivityConstants.CASE_APPROVALS_ROUTE),
    CASE_URGENCY_FLAGS(MainActivityConstants.CASE_URGENCY_FLAGS_ROUTE),
    CRITICAL_DATES(MainActivityConstants.CRITICAL_DATES_ROUTE),
    WORKLOAD(MainActivityConstants.WORKLOAD_ROUTE),
    WORKER_MESSAGES(MainActivityConstants.WORKER_MESSAGES_ROUTE),
    PLACEMENT_COMPATIBILITY(MainActivityConstants.PLACEMENT_COMPATIBILITY_ROUTE),
    DASHBOARD_PREFERENCES(MainActivityConstants.DASHBOARD_PREFERENCES_ROUTE)
}

/**
 * Main activity of the application, handling navigation, state management, and main UI shell.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @javax.inject.Inject
    lateinit var database: AppDatabase

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied.
     */
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
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        val lastSynced = context.getSharedPreferences(MainActivityConstants.SYNC_PREFS, Context.MODE_PRIVATE)
                            .getLong(MainActivityConstants.LAST_SYNCED_KEY, 0L)
                        val now = System.currentTimeMillis() / 1000
                        if (now - lastSynced > MainActivityConstants.SYNC_THRESHOLD_SECONDS) {
                            syncViewModel.triggerSync()
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            var isLoggedIn by remember { mutableStateOf(isLoggedInState) }
            var isLoadingAfterLogin by remember { mutableStateOf(false) }
            var onboardingStep by remember { mutableIntStateOf(0) } // 0: Welcome, 1: Landing, 2: Terms, 3: Login
            var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
            var headerSearchQuery by remember { mutableStateOf("") }
            var currentUser by remember { mutableStateOf(if (isLoggedInState) session.getUsername() ?: MainActivityConstants.DEFAULT_USER else MainActivityConstants.DEFAULT_USER) }
            var currentRole by remember { mutableStateOf(if (isLoggedInState) session.getRole() ?: MainActivityConstants.DEFAULT_ROLE else MainActivityConstants.DEFAULT_ROLE) }

            // Initialize database with sample data
            LaunchedEffect(Unit) {
                DatabaseInitializer.initializeDatabase(database)
            }

            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { selectedUri ->
                profilePhotoUri = selectedUri
            }

            if (!isLoggedIn) {
                if (isLoadingAfterLogin) {
                    // Show luminous green loading animation
                    LoadingAnimationScreen(
                        onComplete = {
                            isLoadingAfterLogin = false
                            isLoggedIn = true
                        },
                        username = currentUser
                    )
                } else {
                    androidx.compose.animation.Crossfade(targetState = onboardingStep, label = MainActivityConstants.ONBOARDING_TRANSITION_LABEL) { currentStepIndex ->
                        when (currentStepIndex) {
                            0 -> {
                                WelcomeScreen(
                                    onExploreClicked = { onboardingStep = 1 }
                                )
                            }
                            1 -> {
                                ModernLandingPage(
                                    onGetStarted = { onboardingStep = 2 },
                                    onLogin = { onboardingStep = 3 }
                                )
                            }
                            2 -> {
                                TermsAndConditionsScreen(onAccept = { onboardingStep = 3 })
                            }
                            3 -> {
                                LoginScreen(
                                    onLoginSuccess = { userEntity ->
                                        currentUser = userEntity.username
                                        currentRole = userEntity.role
                                        isLoadingAfterLogin = true
                                    },
                                    onNavigateToRegister = { onboardingStep = 4 }
                                )
                            }
                            4 -> {
                                RegisterScreen(
                                    onRegisterSuccess = { onboardingStep = 3 },
                                    onNavigateToLogin = { onboardingStep = 3 }
                                )
                            }
                            else -> {
                                WelcomeScreen(
                                    onExploreClicked = { onboardingStep = 1 }
                                )
                            }
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
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                            ) {
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
                            }
                            
                            // Main Navigation Items (above divider)
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.ChildCare, contentDescription = null) },
                                label = { Text(stringResource(R.string.module_children)) },
                                selected = currentRoute(navController) == AppRoute.CHILDREN_LIST.route,
                                onClick = {
                                    navController.navigate(AppRoute.CHILDREN_LIST.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null) },
                                label = { Text(stringResource(R.string.module_families)) },
                                selected = currentRoute(navController) == AppRoute.FAMILIES.route,
                                onClick = {
                                    navController.navigate(AppRoute.FAMILIES.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                            Text(
                                "Administration",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                                label = { Text("User Management") },
                                selected = currentRoute(navController) == AppRoute.USER_MANAGEMENT.route,
                                onClick = {
                                    navController.navigate(AppRoute.USER_MANAGEMENT.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                label = { Text("User Roles") },
                                selected = currentRoute(navController) == AppRoute.USER_ROLES.route,
                                onClick = {
                                    navController.navigate(AppRoute.USER_ROLES.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Shield, contentDescription = null) },
                                label = { Text("Background Checks") },
                                selected = currentRoute(navController) == AppRoute.BACKGROUND_CHECKS.route,
                                onClick = {
                                    navController.navigate(AppRoute.BACKGROUND_CHECKS.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.History, contentDescription = null) },
                                label = { Text("Audit Logs") },
                                selected = currentRoute(navController) == AppRoute.AUDIT_LOGS.route,
                                onClick = {
                                    navController.navigate(AppRoute.AUDIT_LOGS.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                label = { Text(stringResource(R.string.action_settings)) },
                                selected = currentRoute(navController) == AppRoute.SETTINGS.route,
                                onClick = {
                                    navController.navigate(AppRoute.SETTINGS.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                            Text(
                                "Case Tools",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Shield, contentDescription = null) },
                                label = { Text("Risk Assessments") },
                                selected = currentRoute(navController) == AppRoute.RISK_ASSESSMENTS.route,
                                onClick = {
                                    navController.navigate(AppRoute.RISK_ASSESSMENTS.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Map, contentDescription = null) },
                                label = { Text("Permanency Plans") },
                                selected = currentRoute(navController) == AppRoute.PERMANENCY_PLANS.route,
                                onClick = {
                                    navController.navigate(AppRoute.PERMANENCY_PLANS.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.History, contentDescription = null) },
                                label = { Text("Case Activities") },
                                selected = currentRoute(navController) == AppRoute.CASE_ACTIVITIES.route,
                                onClick = {
                                    navController.navigate(AppRoute.CASE_ACTIVITIES.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                                label = { Text("Deadlines") },
                                selected = currentRoute(navController) == AppRoute.CASE_DEADLINES.route,
                                onClick = {
                                    navController.navigate(AppRoute.CASE_DEADLINES.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                                label = { Text("Approvals") },
                                selected = currentRoute(navController) == AppRoute.CASE_APPROVALS.route,
                                onClick = {
                                    navController.navigate(AppRoute.CASE_APPROVALS.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.PriorityHigh, contentDescription = null) },
                                label = { Text("Urgency Flags") },
                                selected = currentRoute(navController) == AppRoute.CASE_URGENCY_FLAGS.route,
                                onClick = {
                                    navController.navigate(AppRoute.CASE_URGENCY_FLAGS.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Event, contentDescription = null) },
                                label = { Text("Critical Dates") },
                                selected = currentRoute(navController) == AppRoute.CRITICAL_DATES.route,
                                onClick = {
                                    navController.navigate(AppRoute.CRITICAL_DATES.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.CompareArrows, contentDescription = null) },
                                label = { Text("Compatibility") },
                                selected = currentRoute(navController) == AppRoute.PLACEMENT_COMPATIBILITY.route,
                                onClick = {
                                    navController.navigate(AppRoute.PLACEMENT_COMPATIBILITY.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                            Text(
                                "My Workspace",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.TaskAlt, contentDescription = null) },
                                label = { Text("My Tasks") },
                                selected = currentRoute(navController) == AppRoute.TASKS.route,
                                onClick = {
                                    navController.navigate(AppRoute.TASKS.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Checklist, contentDescription = null) },
                                label = { Text("Action Items") },
                                selected = currentRoute(navController) == AppRoute.ACTION_ITEMS.route,
                                onClick = {
                                    navController.navigate(AppRoute.ACTION_ITEMS.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Message, contentDescription = null) },
                                label = { Text("Messages") },
                                selected = currentRoute(navController) == AppRoute.WORKER_MESSAGES.route,
                                onClick = {
                                    navController.navigate(AppRoute.WORKER_MESSAGES.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.DonutLarge, contentDescription = null) },
                                label = { Text("Workload") },
                                selected = currentRoute(navController) == AppRoute.WORKLOAD.route,
                                onClick = {
                                    navController.navigate(AppRoute.WORKLOAD.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Tune, contentDescription = null) },
                                label = { Text("Dashboard Prefs") },
                                selected = currentRoute(navController) == AppRoute.DASHBOARD_PREFERENCES.route,
                                onClick = {
                                    navController.navigate(AppRoute.DASHBOARD_PREFERENCES.route)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                            
                            // Push version to bottom
                            Spacer(Modifier.weight(1f))
                            
                            // App Version at bottom
                            Text(
                                text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
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
                                    searchQuery = headerSearchQuery,
                                    onSearchQueryChange = { updatedQueryText ->
                                        headerSearchQuery = updatedQueryText
                                        // Use navController directly to check route
                                        val currentRouteStr = navController.currentDestination?.route
                                        if (updatedQueryText.isNotEmpty() && currentRouteStr != AppRoute.SEARCH.route) {
                                            navController.navigate(AppRoute.SEARCH.route)
                                        }
                                    },
                                    onImagePicker = { imagePickerLauncher.launch(MainActivityConstants.IMAGE_MIME_TYPE) },
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
                    ) { scaffoldPaddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(scaffoldPaddingValues)
                        ) {
                            AppNavHost(
                                navController = navController,
                                headerSearchQuery = headerSearchQuery,
                                onSearchQueryChange = { newQuery -> headerSearchQuery = newQuery },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Main navigation host for the application.
 *
 * @param navController The navigation controller.
 * @param headerSearchQuery The current search query string.
 * @param onSearchQueryChange Callback when the search query is modified.
 * @param modifier The modifier to apply to this layout.
 */
@Composable
private fun AppNavHost(
    navController: NavHostController,
    headerSearchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = AppRoute.DASHBOARD.route, modifier = modifier) {
        composable(AppRoute.DASHBOARD.route) {
            val currentDest = navController.currentDestination?.route
            DashboardScreenModern(
                onNavigate = { destinationRouteStr ->
                    if (currentDest != destinationRouteStr) {
                        navController.navigate(destinationRouteStr) {
                            popUpTo(AppRoute.DASHBOARD.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
        composable(AppRoute.CHILDREN_LIST.route) { ChildrenListScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.FAMILIES.route) { FamiliesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.ADOPTION_APPS.route) { AdoptionApplicationsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.HOME_STUDIES.route) { HomeStudiesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.DOCUMENTS.route) { DocumentsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.PLACEMENTS.route) { PlacementsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.REPORTS.route) { CaseReportsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.EDUCATION.route) { EducationScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.MEDICAL.route) { MedicalScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.FINANCE.route) { FinanceScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.SETTINGS.route) {
            SettingsScreen(
                onBack = {
                    navController.navigate(AppRoute.DASHBOARD.route) {
                        popUpTo(AppRoute.DASHBOARD.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.MAP.route) { MapScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.BACKGROUND_CHECKS.route) { BackgroundChecksScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.USER_MANAGEMENT.route) { UserManagementScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.NOTIFICATIONS.route) { NotificationsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.AUDIT_LOGS.route) { AuditLogsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.SEARCH.route) {
            SearchScreen(
                query = headerSearchQuery,
                onQueryChange = onSearchQueryChange,
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoute.CAMERA.route) { CameraScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.USER_ROLES.route) { UserRolesScreen(onBack = { navController.popBackStack() }) }
        // Add additional screens to navigation
        composable(MainActivityConstants.ANALYTICS_ROUTE) { AnalyticsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.GUARDIANS_ROUTE) { GuardiansScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.COURT_CASES_ROUTE) { CourtCasesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.FOSTER_TASKS_ROUTE) { FosterTasksScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.FOSTER_MATCHES_ROUTE) { FosterMatchesScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.TASKS.route) { TasksScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.ACTION_ITEMS.route) { ActionItemsScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.RISK_ASSESSMENTS.route) { RiskAssessmentsScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.PERMANENCY_PLANS.route) { PermanencyPlansScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.CASE_ACTIVITIES.route) { CaseActivitiesScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.CASE_DEADLINES.route) { CaseDeadlinesScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.CASE_APPROVALS.route) { CaseApprovalsScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.CASE_URGENCY_FLAGS.route) { CaseUrgencyFlagsScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.CRITICAL_DATES.route) { CriticalDatesScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.WORKLOAD.route) { WorkloadDashboardScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.WORKER_MESSAGES.route) { WorkerMessagesScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.PLACEMENT_COMPATIBILITY.route) { PlacementCompatibilityScreen(onBack = { navController.popBackStack() }) }
        // composable(AppRoute.DASHBOARD_PREFERENCES.route) { DashboardPreferencesScreen(onBack = { navController.popBackStack() }) }
    }
}

/**
 * Check if current user can access a specific route based on their role.
 *
 * @param route The route to check access for.
 * @param userRole The role of the user.
 * @return True if the user can access the route, false otherwise.
 */
@Suppress("HardcodedStringLiteral", "unused")
internal fun canAccessRoute(route: String, userRole: String): Boolean {
    return when (userRole) {
        "Admin", "Case Worker", "Supervisor" -> true // Full access to all routes
        "Social Worker" -> route in listOf(
            "dashboard", "children_list", "families", "documents", 
            "reports", "home_studies", "placements", "settings"
        )
        "Foster Parent" -> route in listOf(
            "dashboard", "children_list", "documents", "education", "medical", "settings"
        )
        else -> route in listOf("dashboard", "children_list", "documents", "settings") // Limited access
    }
}

/**
 * Returns the current route in the navigation stack.
 *
 * @param navController The navigation controller.
 * @return The current route string, or null if not available.
 */
@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
