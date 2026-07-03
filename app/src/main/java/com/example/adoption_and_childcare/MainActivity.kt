package com.example.adoption_and_childcare

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.ui.compose.*
import com.example.adoption_and_childcare.ui.theme.AdoptionChildcareTheme
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel
import com.example.adoption_and_childcare.viewmodel.SOSState
import com.example.adoption_and_childcare.viewmodel.SOSViewModel
import com.example.adoption_and_childcare.viewmodel.SyncViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Constants used throughout the MainActivity.
 */
@Suppress("HardcodedStringLiteral") // Suppressed as these are internal route and key constants
internal object MainActivityConstants {
    const val SYNC_PREFS = "sync_prefs"
    const val LAST_SYNCED_KEY = "last_synced_at"
    const val ONBOARDING_TRANSITION_LABEL = "onboarding_transition"
    const val LOG_TAG = "MainActivity"
    
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
    const val VACCINATION_RECORDS_ROUTE = "vaccination_records"
    const val BEHAVIOR_ASSESSMENTS_ROUTE = "behavior_assessments"
    const val WELFARE_INCIDENTS_ROUTE = "welfare_incidents"
    const val SIBLINGS_ROUTE = "siblings"
    const val CONSENT_RECORDS_ROUTE = "consent_records"
    const val INVESTIGATIONS_ROUTE = "investigations"
    const val SERVICE_PLANS_ROUTE = "service_plans"
    const val VISITATION_SCHEDULES_ROUTE = "visitation_schedules"
    const val REFERRALS_ROUTE = "referrals"
    const val AFTERCARE_PLANS_ROUTE = "aftercare_plans"
    const val CHILD_SERVICES_REFERRALS_ROUTE = "child_services_referrals"
    const val ORG_PARTNERS_ROUTE = "org_partners"
    const val SERVICE_PROVIDERS_ROUTE = "service_providers"
    const val DONOR_FUNDING_ROUTE = "donor_funding"
    const val BUDGET_ALLOCATIONS_ROUTE = "budget_allocations"
    const val COUNTIES_ROUTE = "counties"
    const val COUNTY_OFFICES_ROUTE = "county_offices"
    const val PLACEMENT_DISRUPTIONS_ROUTE = "placement_disruptions"
    const val FOSTER_TRAINING_ROUTE = "foster_training"
    const val REPORTS_GENERATED_ROUTE = "reports_generated"
    const val EMERGENCY_EVENTS_ROUTE = "emergency_events"
    const val DOCUMENT_STORAGE_ROUTE = "document_storage"
    const val INTER_COUNTY_TRANSFERS_ROUTE = "inter_county_transfers"
    const val WORKER_LOCATIONS_ROUTE = "worker_locations"
    const val CASE_INBOX_ROUTE = "case_inbox"
    const val RECENT_ACTIVITY_ROUTE = "recent_activity"
    const val SHORTCUTS_ROUTE = "shortcuts"
    const val PERMISSIONS_ROUTE = "permissions"
    const val HELP_ABOUT_ROUTE = "help_about"
    const val SOS_EMERGENCY_ROUTE = "sos_emergency"
    
    const val SYNC_THRESHOLD_SECONDS = 15 * 60
}

/**
 * Routes for the application navigation.
 *
 * @param routeVal The string representation of the route.
 */
private enum class AppRoute(val routeVal: String) {
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
    DASHBOARD_PREFERENCES(MainActivityConstants.DASHBOARD_PREFERENCES_ROUTE),
    VACCINATION_RECORDS(MainActivityConstants.VACCINATION_RECORDS_ROUTE),
    BEHAVIOR_ASSESSMENTS(MainActivityConstants.BEHAVIOR_ASSESSMENTS_ROUTE),
    WELFARE_INCIDENTS(MainActivityConstants.WELFARE_INCIDENTS_ROUTE),
    SIBLINGS(MainActivityConstants.SIBLINGS_ROUTE),
    CONSENT_RECORDS(MainActivityConstants.CONSENT_RECORDS_ROUTE),
    INVESTIGATIONS(MainActivityConstants.INVESTIGATIONS_ROUTE),
    SERVICE_PLANS(MainActivityConstants.SERVICE_PLANS_ROUTE),
    VISITATION_SCHEDULES(MainActivityConstants.VISITATION_SCHEDULES_ROUTE),
    REFERRALS(MainActivityConstants.REFERRALS_ROUTE),
    AFTERCARE_PLANS(MainActivityConstants.AFTERCARE_PLANS_ROUTE),
    CHILD_SERVICES_REFERRALS(MainActivityConstants.CHILD_SERVICES_REFERRALS_ROUTE),
    ORG_PARTNERS(MainActivityConstants.ORG_PARTNERS_ROUTE),
    SERVICE_PROVIDERS(MainActivityConstants.SERVICE_PROVIDERS_ROUTE),
    DONOR_FUNDING(MainActivityConstants.DONOR_FUNDING_ROUTE),
    BUDGET_ALLOCATIONS(MainActivityConstants.BUDGET_ALLOCATIONS_ROUTE),
    COUNTIES(MainActivityConstants.COUNTIES_ROUTE),
    COUNTY_OFFICES(MainActivityConstants.COUNTY_OFFICES_ROUTE),
    PLACEMENT_DISRUPTIONS(MainActivityConstants.PLACEMENT_DISRUPTIONS_ROUTE),
    FOSTER_TRAINING(MainActivityConstants.FOSTER_TRAINING_ROUTE),
    REPORTS_GENERATED(MainActivityConstants.REPORTS_GENERATED_ROUTE),
    EMERGENCY_EVENTS(MainActivityConstants.EMERGENCY_EVENTS_ROUTE),
    DOCUMENT_STORAGE(MainActivityConstants.DOCUMENT_STORAGE_ROUTE),
    INTER_COUNTY_TRANSFERS(MainActivityConstants.INTER_COUNTY_TRANSFERS_ROUTE),
    WORKER_LOCATIONS(MainActivityConstants.WORKER_LOCATIONS_ROUTE),
    CASE_INBOX(MainActivityConstants.CASE_INBOX_ROUTE),
    RECENT_ACTIVITY(MainActivityConstants.RECENT_ACTIVITY_ROUTE),
    SHORTCUTS(MainActivityConstants.SHORTCUTS_ROUTE),
    PERMISSIONS(MainActivityConstants.PERMISSIONS_ROUTE),
    HELP_ABOUT(MainActivityConstants.HELP_ABOUT_ROUTE),
    SOS_EMERGENCY(MainActivityConstants.SOS_EMERGENCY_ROUTE)
}

/**
 * Main activity of the application, handling navigation, state management, and main UI shell.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Database instance for the application, lazily injected via Hilt.
     */
    @Inject
    lateinit var database: dagger.Lazy<AppDatabase>

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AdoptionChildcareTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val session = remember { SessionManager(context) }
                val isLoggedInState = session.isLoggedIn()

                val syncViewModel: SyncViewModel = hiltViewModel()
                val notificationsViewModel: NotificationsViewModel = hiltViewModel()
                val sosViewModel: SOSViewModel = hiltViewModel()

                val sosState by sosViewModel.sosState.collectAsState()

                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { source: Any?, event: Lifecycle.Event -> // source: The lifecycle owner, event: The event
                        if (event == Lifecycle.Event.ON_RESUME) {
                            val lastSynced = context.getSharedPreferences(MainActivityConstants.SYNC_PREFS, 0)
                                .getLong(MainActivityConstants.LAST_SYNCED_KEY, 0L)
                            val now = System.currentTimeMillis() / 1000
                            if ((now - lastSynced) > MainActivityConstants.SYNC_THRESHOLD_SECONDS) {
                                syncViewModel.triggerSync()
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                val hasLoggedInBefore = remember { session.hasLoggedInBefore() }
                var isLoggedIn by remember { mutableStateOf(value = isLoggedInState) }
                var isLoadingAfterLogin by remember { mutableStateOf(value = false) }
                var onboardingStep by remember { mutableIntStateOf(if (isLoggedInState) 5 else if (hasLoggedInBefore) 1 else 0) }
                var registrationSuccessMessage by remember { mutableStateOf(value = "") }
                var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
                var headerSearchQuery by remember { mutableStateOf(value = "") }
                
                val guestLabel = stringResource(R.string.dashboard_guest)
                val adminRoleVal = stringResource(R.string.role_admin_val)
                val supervisorRoleVal = stringResource(R.string.role_supervisor_val)
                val caseworkerRoleVal = stringResource(R.string.role_case_worker_val)
                val socialWorkerRoleVal = stringResource(R.string.role_social_worker_val)
                val guardianRoleVal = stringResource(R.string.role_guardian_val)
                
                var currentUser by remember(isLoggedInState) { mutableStateOf(value = if (isLoggedInState) session.getUsername() ?: guestLabel else guestLabel) }
                var currentRole by remember(isLoggedInState) { mutableStateOf(value = if (isLoggedInState) session.getRole() ?: adminRoleVal else adminRoleVal) }

                LaunchedEffect(Unit) {
                    launch(Dispatchers.IO) {
                        DatabaseInitializer.initializeDatabase(database.get())
                    }
                }

                val imagePickerLauncher = rememberLauncherForActivityResult(
                    contract = PickVisualMedia()
                ) { uri -> // uri: The selected image URI
                    profilePhotoUri = uri
                }

                if (!isLoggedIn) {
                    if (isLoadingAfterLogin) {
                        LoadingAnimationScreen(
                            onComplete = {
                                isLoadingAfterLogin = false
                                isLoggedIn = true
                            },
                            username = currentUser
                        )
                    } else {
                        Crossfade(targetState = onboardingStep, label = MainActivityConstants.ONBOARDING_TRANSITION_LABEL) { step: Int -> // step: onboarding step index
                            when (step) {
                                0 -> WelcomeScreen { onboardingStep = 1 }
                                1 -> ModernLandingPage(
                                        onGetStarted = { 
                                            onboardingStep = if (hasLoggedInBefore) 3 else 2 
                                        }, 
                                        onLogin = { onboardingStep = 3 }
                                    )
                                2 -> TermsAndConditionsScreen(onAccept = { onboardingStep = 3 })
                                3 -> LoginScreen(
                                        onLoginSuccess = { user: com.example.adoption_and_childcare.data.db.entities.UserEntity -> // user: the logged-in user entity
                                            currentUser = user.username
                                            currentRole = user.role
                                            isLoadingAfterLogin = true
                                        },
                                        onNavigateToRegister = {
                                            registrationSuccessMessage = ""
                                            onboardingStep = 4
                                        },
                                        successMessage = registrationSuccessMessage
                                    )
                                4 -> RegisterScreen(
                                        onRegisterSuccess = { msg: String -> // msg: registration success message
                                            registrationSuccessMessage = msg
                                            onboardingStep = 3
                                        },
                                        onNavigateToLogin = {
                                            registrationSuccessMessage = ""
                                            onboardingStep = 3
                                        }
                                    )
                                else -> WelcomeScreen { onboardingStep = 1 }
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
                                
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                        .padding(bottom = 16.dp)
                                ) {
                                if (canAccessRoute(AppRoute.CHILDREN_LIST.routeVal, currentRole, adminRoleVal, supervisorRoleVal, caseworkerRoleVal, socialWorkerRoleVal, guardianRoleVal)) {
                                        NavigationDrawerItem(
                                            icon = { Icon(Icons.Default.ChildCare, contentDescription = null) },
                                            label = { Text(stringResource(R.string.module_children)) },
                                            selected = currentRoute(navController) == AppRoute.CHILDREN_LIST.routeVal,
                                            onClick = {
                                                navController.navigate(AppRoute.CHILDREN_LIST.routeVal)
                                                scope.launch { drawerState.close() }
                                            },
                                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                        )
                                    }
                                    
                                    if (canAccessRoute(AppRoute.FAMILIES.routeVal, currentRole, adminRoleVal, supervisorRoleVal, caseworkerRoleVal, socialWorkerRoleVal, guardianRoleVal)) {
                                        NavigationDrawerItem(
                                            icon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null) },
                                            label = { Text(stringResource(R.string.module_families)) },
                                            selected = currentRoute(navController) == AppRoute.FAMILIES.routeVal,
                                            onClick = {
                                                navController.navigate(AppRoute.FAMILIES.routeVal)
                                                scope.launch { drawerState.close() }
                                            },
                                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                        )
                                    }
                                    
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                                    
                                    if (currentRole == adminRoleVal || currentRole == supervisorRoleVal) {
                                        Text(
                                            stringResource(R.string.dashboard_administration),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)
                                        )
                                        
                                        if (canAccessRoute(AppRoute.USER_MANAGEMENT.routeVal, currentRole, adminRoleVal, supervisorRoleVal, caseworkerRoleVal, socialWorkerRoleVal, guardianRoleVal)) {
                                            NavigationDrawerItem(
                                                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                                                label = { Text(stringResource(R.string.user_management_title)) },
                                                selected = currentRoute(navController) == AppRoute.USER_MANAGEMENT.routeVal,
                                                onClick = {
                                                    navController.navigate(AppRoute.USER_MANAGEMENT.routeVal)
                                                    scope.launch { drawerState.close() }
                                                },
                                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                            )
                                        }
                                        
                                        if (canAccessRoute(AppRoute.USER_ROLES.routeVal, currentRole, adminRoleVal, supervisorRoleVal, caseworkerRoleVal, socialWorkerRoleVal, guardianRoleVal)) {
                                            NavigationDrawerItem(
                                                icon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                                label = { Text(stringResource(R.string.dashboard_label_user_roles)) },
                                                selected = currentRoute(navController) == AppRoute.USER_ROLES.routeVal,
                                                onClick = {
                                                    navController.navigate(AppRoute.USER_ROLES.routeVal)
                                                    scope.launch { drawerState.close() }
                                                },
                                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                            )
                                        }
                                    }

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                        label = { Text(stringResource(R.string.action_settings)) },
                                        selected = currentRoute(navController) == AppRoute.SETTINGS.routeVal,
                                        onClick = {
                                            navController.navigate(AppRoute.SETTINGS.routeVal)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )

                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.Inbox, contentDescription = null) },
                                        label = { Text(stringResource(R.string.title_case_inbox)) },
                                        selected = currentRoute(navController) == AppRoute.CASE_INBOX.routeVal,
                                        onClick = {
                                            navController.navigate(AppRoute.CASE_INBOX.routeVal)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                                        label = { Text(stringResource(R.string.title_recent_activity)) },
                                        selected = currentRoute(navController) == AppRoute.RECENT_ACTIVITY.routeVal,
                                        onClick = {
                                            navController.navigate(AppRoute.RECENT_ACTIVITY.routeVal)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.PushPin, contentDescription = null) },
                                        label = { Text(stringResource(R.string.title_shortcuts)) },
                                        selected = currentRoute(navController) == AppRoute.SHORTCUTS.routeVal,
                                        onClick = {
                                            navController.navigate(AppRoute.SHORTCUTS.routeVal)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                        label = { Text(stringResource(R.string.title_permissions)) },
                                        selected = currentRoute(navController) == AppRoute.PERMISSIONS.routeVal,
                                        onClick = {
                                            navController.navigate(AppRoute.PERMISSIONS.routeVal)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                                        label = { Text(stringResource(R.string.help_about_label)) },
                                        selected = currentRoute(navController) == AppRoute.HELP_ABOUT.routeVal,
                                        onClick = {
                                            navController.navigate(AppRoute.HELP_ABOUT.routeVal)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )
                                }

                                val versionDisplay = stringResource(
                                    R.string.app_version_format, 
                                    BuildConfig.VERSION_NAME,
                                    BuildConfig.VERSION_CODE
                                )
                                Text(
                                    text = versionDisplay,
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
                                        onSearchQueryChange = { query: String -> // query: the updated search text
                                            headerSearchQuery = query
                                            val currentPath = navController.currentDestination?.route
                                            if (query.isNotEmpty() && currentPath != AppRoute.SEARCH.routeVal) {
                                                navController.navigate(AppRoute.SEARCH.routeVal)
                                            }
                                        },
                                        onImagePicker = { 
                                            imagePickerLauncher.launch(
                                                PickVisualMediaRequest(PickVisualMedia.ImageOnly)
                                            ) 
                                        },
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
                        ) { scaffoldPadding: PaddingValues -> // scaffoldPadding: padding values from Scaffold
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(scaffoldPadding)
                            ) {
                                AppNavHost(
                                    navController = navController,
                                    headerSearchQuery = headerSearchQuery,
                                    onSearchQueryChange = { updatedQuery: String -> // updatedQuery: the new search query
                                        headerSearchQuery = updatedQuery 
                                    },
                                    userRole = currentRole,
                                    adminRole = adminRoleVal,
                                    supervisorRole = supervisorRoleVal,
                                    caseworkerRole = caseworkerRoleVal,
                                    socialWorkerRole = socialWorkerRoleVal,
                                    guardianRole = guardianRoleVal,
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

/**
 * Main navigation host for the application.
 *
 * Suppressed LongMethod and LongParameterList as this defines the core app navigation structure.
 *
 * @param navController The navigation controller instance.
 * @param headerSearchQuery The current search query from the header.
 * @param onSearchQueryChange Callback when the search query changes.
 * @param userRole The role of the current user.
 * @param adminRole String for admin role.
 * @param supervisorRole String for supervisor role.
 * @param caseworkerRole String for caseworker role.
 * @param socialWorkerRole String for social worker role.
 * @param guardianRole String for guardian role.
 * @param modifier The modifier to be applied to the NavHost.
 */
@Composable
@Suppress("LongMethod", "LongParameterList")
private fun AppNavHost(
    navController: NavHostController,
    headerSearchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    userRole: String,
    adminRole: String,
    supervisorRole: String,
    caseworkerRole: String,
    socialWorkerRole: String,
    guardianRole: String,
    modifier: Modifier = Modifier
) {
    val navMedicalDetailTemplate = stringResource(R.string.nav_medical_detail)
    val navMedicalDetailRoute = stringResource(R.string.nav_medical_detail_route)
    val deniedMsgTemplate = stringResource(R.string.error_access_denied)

    NavHost(navController = navController, startDestination = AppRoute.DASHBOARD.routeVal, modifier = modifier) {
        composable(AppRoute.DASHBOARD.routeVal) {
            val currentDest = navController.currentDestination?.route
            DashboardScreenModern(
                userRole = userRole,
                onNavigate = { destination: String -> // destination: target route string
                    if (currentDest != destination) {
                        if (canAccessRoute(destination, userRole, adminRole, supervisorRole, caseworkerRole, socialWorkerRole, guardianRole)) {
                            navController.navigate(destination) {
                                popUpTo(AppRoute.DASHBOARD.routeVal) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            val deniedMsg = String.format(deniedMsgTemplate, destination, userRole)
                            Log.d(MainActivityConstants.LOG_TAG, deniedMsg)
                        }
                    }
                }
            )
        }
        composable(AppRoute.CHILDREN_LIST.routeVal) { ChildrenListScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.FAMILIES.routeVal) { FamiliesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.ADOPTION_APPS.routeVal) { AdoptionApplicationsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.HOME_STUDIES.routeVal) { HomeStudiesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.DOCUMENTS.routeVal) { DocumentsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.PLACEMENTS.routeVal) { PlacementsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.REPORTS.routeVal) { CaseReportsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.EDUCATION.routeVal) { EducationScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.MEDICAL.routeVal) { 
            val medicalDetailPathPattern = stringResource(R.string.nav_medical_detail_path)
            val itemsCountPrefix = stringResource(R.string.dashboard_items_count).take(3)
            MedicalScreen(
                onBack = { navController.popBackStack() },
                onMedicalRecordClick = { id: Int -> // id: medical record unique identifier
                    val medicalDetailPath = if (navMedicalDetailTemplate.contains(itemsCountPrefix)) {
                        String.format(navMedicalDetailTemplate, id)
                    } else {
                        String.format(medicalDetailPathPattern, AppRoute.MEDICAL.routeVal, id)
                    }
                    navController.navigate(medicalDetailPath) 
                }
            ) 
        }
        composable(navMedicalDetailRoute) { navBackStackEntry: androidx.navigation.NavBackStackEntry -> // navBackStackEntry: current navigation state
            val recordId = navBackStackEntry.arguments?.getString("recordId")?.toIntOrNull() ?: 0
            MedicalDetailScreen(
                recordId = recordId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoute.FINANCE.routeVal) { FinanceScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.SETTINGS.routeVal) {
            SettingsScreen(
                onBack = {
                    navController.navigate(AppRoute.DASHBOARD.routeVal) {
                        popUpTo(AppRoute.DASHBOARD.routeVal) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.MAP.routeVal) { MapScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.SOS_EMERGENCY.routeVal) { SOSScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.BACKGROUND_CHECKS.routeVal) { BackgroundChecksScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.USER_MANAGEMENT.routeVal) { UserManagementScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.NOTIFICATIONS.routeVal) { NotificationsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.AUDIT_LOGS.routeVal) { AuditLogsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.SEARCH.routeVal) {
            SearchScreen(
                query = headerSearchQuery,
                onQueryChange = onSearchQueryChange,
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoute.CAMERA.routeVal) { CameraScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.USER_ROLES.routeVal) { UserRolesScreen(onBack = { navController.popBackStack() }) }
        // Add additional screens to navigation
        composable(AppRoute.ANALYTICS.routeVal) { AnalyticsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.GUARDIANS.routeVal) { GuardiansScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.COURT_CASES.routeVal) { CourtCasesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.FOSTER_TASKS.routeVal) { FosterTasksScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.FOSTER_MATCHES.routeVal) { FosterMatchesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.TASKS.routeVal) { TasksScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.ACTION_ITEMS.routeVal) { ActionItemsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.RISK_ASSESSMENTS.routeVal) { RiskAssessmentCenterScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.PERMANENCY_PLANS.routeVal) { PermanencyPlansScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.CASE_ACTIVITIES.routeVal) { CaseActivitiesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.CASE_DEADLINES.routeVal) { CaseDeadlinesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.CASE_APPROVALS.routeVal) { CaseApprovalsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.CASE_URGENCY_FLAGS.routeVal) { CaseUrgencyFlagsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.CRITICAL_DATES.routeVal) { CriticalDatesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.WORKLOAD.routeVal) { WorkloadDashboardScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.WORKER_MESSAGES.routeVal) { WorkerMessagesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.PLACEMENT_COMPATIBILITY.routeVal) { PlacementCompatibilityEngineScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.DASHBOARD_PREFERENCES.routeVal) { DashboardPreferencesScreen(onBack = { navController.popBackStack() }) }
        // New screens
        composable(AppRoute.VACCINATION_RECORDS.routeVal) { VaccinationRecordsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.BEHAVIOR_ASSESSMENTS.routeVal) { BehaviorAssessmentsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.WELFARE_INCIDENTS.routeVal) { WelfareIncidentsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.SIBLINGS.routeVal) { SiblingsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.CONSENT_RECORDS.routeVal) { ConsentRecordsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.INVESTIGATIONS.routeVal) { InvestigationsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.SERVICE_PLANS.routeVal) { ServicePlansScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.VISITATION_SCHEDULES.routeVal) { VisitationSchedulesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.REFERRALS.routeVal) { ReferralsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.AFTERCARE_PLANS.routeVal) { AftercarePlansScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.CHILD_SERVICES_REFERRALS.routeVal) { ChildServicesReferralsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.ORG_PARTNERS.routeVal) { OrganizationPartnersScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.SERVICE_PROVIDERS.routeVal) { ServiceProvidersScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.DONOR_FUNDING.routeVal) { DonorFundingScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.BUDGET_ALLOCATIONS.routeVal) { BudgetAllocationsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.COUNTIES.routeVal) { CountiesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.COUNTY_OFFICES.routeVal) { CountyOfficesScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.PLACEMENT_DISRUPTIONS.routeVal) { PlacementDisruptionsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.FOSTER_TRAINING.routeVal) { FosterFamilyTrainingScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.REPORTS_GENERATED.routeVal) { ReportsGeneratedScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.EMERGENCY_EVENTS.routeVal) { EmergencyEventsScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.DOCUMENT_STORAGE.routeVal) { GlobalDocumentStorageScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.INTER_COUNTY_TRANSFERS.routeVal) { InterCountyTransfersScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.WORKER_LOCATIONS.routeVal) { WorkerLocationScreen(onBack = { navController.popBackStack() }) }
        composable(AppRoute.CASE_INBOX.routeVal) { CaseInboxScreen() }
        composable(AppRoute.RECENT_ACTIVITY.routeVal) { RecentActivityScreen() }
        composable(AppRoute.SHORTCUTS.routeVal) { ShortcutsScreen() }
        composable(AppRoute.PERMISSIONS.routeVal) { AppPermissionsScreen() }
        composable(AppRoute.HELP_ABOUT.routeVal) { HelpAboutScreen() }
    }
}

/**
 * Check if current user can access a specific route based on their role.
 *
 * @param route The destination route to check.
 * @param userRole The role of the current user.
 * @param adminRole String for admin role.
 * @param supervisorRole String for supervisor role.
 * @param caseworkerRole String for caseworker role.
 * @param socialWorkerRole String for social worker role.
 * @param guardianRole String for guardian role.
 * @return True if the user has permission to access the route.
 */
internal fun canAccessRoute(
    route: String,
    userRole: String,
    adminRole: String,
    supervisorRole: String,
    caseworkerRole: String,
    socialWorkerRole: String,
    guardianRole: String
): Boolean {
    val commonRoutes = listOf(
        MainActivityConstants.DASHBOARD_ROUTE,
        MainActivityConstants.SETTINGS_ROUTE,
        MainActivityConstants.NOTIFICATIONS_ROUTE,
        MainActivityConstants.SEARCH_ROUTE,
        MainActivityConstants.CAMERA_ROUTE,
        MainActivityConstants.CASE_INBOX_ROUTE,
        MainActivityConstants.RECENT_ACTIVITY_ROUTE,
        MainActivityConstants.SHORTCUTS_ROUTE,
        MainActivityConstants.PERMISSIONS_ROUTE,
        MainActivityConstants.HELP_ABOUT_ROUTE
    )

    return when (userRole) {
        adminRole, supervisorRole -> true
        
        caseworkerRole -> route !in listOf(
            MainActivityConstants.USER_MANAGEMENT_ROUTE,
            MainActivityConstants.USER_ROLES_ROUTE,
            MainActivityConstants.AUDIT_LOGS_ROUTE
        )

        socialWorkerRole -> route in commonRoutes + listOf(
            MainActivityConstants.CHILDREN_LIST_ROUTE,
            MainActivityConstants.FAMILIES_ROUTE,
            MainActivityConstants.DOCUMENTS_ROUTE,
            MainActivityConstants.REPORTS_ROUTE,
            MainActivityConstants.HOME_STUDIES_ROUTE,
            MainActivityConstants.PLACEMENTS_ROUTE,
            MainActivityConstants.EDUCATION_ROUTE,
            MainActivityConstants.MEDICAL_ROUTE,
            MainActivityConstants.COURT_CASES_ROUTE,
            MainActivityConstants.FOSTER_MATCHES_ROUTE,
            MainActivityConstants.CASE_ACTIVITIES_ROUTE,
            MainActivityConstants.CASE_DEADLINES_ROUTE
        )

        guardianRole -> route in commonRoutes + listOf(
            MainActivityConstants.CHILDREN_LIST_ROUTE,
            MainActivityConstants.DOCUMENTS_ROUTE,
            MainActivityConstants.EDUCATION_ROUTE,
            MainActivityConstants.MEDICAL_ROUTE,
            MainActivityConstants.FOSTER_TASKS_ROUTE,
            MainActivityConstants.VACCINATION_RECORDS_ROUTE,
            MainActivityConstants.BEHAVIOR_ASSESSMENTS_ROUTE,
            MainActivityConstants.VISITATION_SCHEDULES_ROUTE
        )

        else -> route in commonRoutes
    }
}

/**
 * Returns the current route in the navigation stack.
 *
 * @param navController The navigation controller instance.
 * @return The current route string or null if not available.
 */
@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
