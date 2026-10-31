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
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.DatabaseInitializer
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.ui.compose.ActionItemsScreen
import com.example.adoption_and_childcare.ui.compose.AdoptionApplicationsScreen
import com.example.adoption_and_childcare.ui.compose.AftercarePlansScreen
import com.example.adoption_and_childcare.ui.compose.AnalyticsScreen
import com.example.adoption_and_childcare.ui.compose.AppPermissionsScreen
import com.example.adoption_and_childcare.ui.compose.AuditLogsScreen
import com.example.adoption_and_childcare.ui.compose.BackgroundChecksScreen
import com.example.adoption_and_childcare.ui.compose.BehaviorAssessmentsScreen
import com.example.adoption_and_childcare.ui.compose.BottomNavBar
import com.example.adoption_and_childcare.ui.compose.BudgetAllocationsScreen
import com.example.adoption_and_childcare.ui.compose.CameraScreen
import com.example.adoption_and_childcare.ui.compose.CaseActivitiesScreen
import com.example.adoption_and_childcare.ui.compose.CaseApprovalsScreen
import com.example.adoption_and_childcare.ui.compose.CaseDeadlinesScreen
import com.example.adoption_and_childcare.ui.compose.CaseInboxScreen
import com.example.adoption_and_childcare.ui.compose.CaseReportsScreen
import com.example.adoption_and_childcare.ui.compose.CaseUrgencyFlagsScreen
import com.example.adoption_and_childcare.ui.compose.ChildServicesReferralsScreen
import com.example.adoption_and_childcare.ui.compose.ChildrenListScreen
import com.example.adoption_and_childcare.ui.compose.ConsentRecordsScreen
import com.example.adoption_and_childcare.ui.compose.CountiesScreen
import com.example.adoption_and_childcare.ui.compose.CountyOfficesScreen
import com.example.adoption_and_childcare.ui.compose.CourtCasesScreen
import com.example.adoption_and_childcare.ui.compose.CriticalDatesScreen
import com.example.adoption_and_childcare.ui.compose.DashboardPreferencesScreen
import com.example.adoption_and_childcare.ui.compose.DashboardScreenModern
import com.example.adoption_and_childcare.ui.compose.DocumentsScreen
import com.example.adoption_and_childcare.ui.compose.DonorFundingScreen
import com.example.adoption_and_childcare.ui.compose.DrawerHeader
import com.example.adoption_and_childcare.ui.compose.EducationScreen
import com.example.adoption_and_childcare.ui.compose.EmergencyActiveBanner
import com.example.adoption_and_childcare.ui.compose.EmergencyEventsScreen
import com.example.adoption_and_childcare.ui.compose.FamiliesScreen
import com.example.adoption_and_childcare.ui.compose.FinanceScreen
import com.example.adoption_and_childcare.ui.compose.FosterFamilyTrainingScreen
import com.example.adoption_and_childcare.ui.compose.FosterMatchesScreen
import com.example.adoption_and_childcare.ui.compose.FosterTasksScreen
import com.example.adoption_and_childcare.ui.compose.GlobalDocumentStorageScreen
import com.example.adoption_and_childcare.ui.compose.GreenHeader
import com.example.adoption_and_childcare.ui.compose.GuardiansScreen
import com.example.adoption_and_childcare.ui.compose.HelpAboutScreen
import com.example.adoption_and_childcare.ui.compose.HomeStudiesScreen
import com.example.adoption_and_childcare.ui.compose.InterCountyTransfersScreen
import com.example.adoption_and_childcare.ui.compose.InvestigationsScreen
import com.example.adoption_and_childcare.ui.compose.LoadingAnimationScreen
import com.example.adoption_and_childcare.ui.compose.LoginScreen
import com.example.adoption_and_childcare.ui.compose.MapScreen
import com.example.adoption_and_childcare.ui.compose.MedicalDetailScreen
import com.example.adoption_and_childcare.ui.compose.MedicalScreen
import com.example.adoption_and_childcare.ui.compose.ModernLandingPage
import com.example.adoption_and_childcare.ui.compose.NotificationsScreen
import com.example.adoption_and_childcare.ui.compose.OrganizationPartnersScreen
import com.example.adoption_and_childcare.ui.compose.PermanencyPlansScreen
import com.example.adoption_and_childcare.ui.compose.PlacementCompatibilityEngineScreen
import com.example.adoption_and_childcare.ui.compose.PlacementDisruptionsScreen
import com.example.adoption_and_childcare.ui.compose.PlacementsScreen
import com.example.adoption_and_childcare.ui.compose.RecentActivityScreen
import com.example.adoption_and_childcare.ui.compose.ReferralsScreen
import com.example.adoption_and_childcare.ui.compose.RegisterScreen
import com.example.adoption_and_childcare.ui.compose.ReportsGeneratedScreen
import com.example.adoption_and_childcare.ui.compose.RiskAssessmentCenterScreen
import com.example.adoption_and_childcare.ui.compose.SOSScreen
import com.example.adoption_and_childcare.ui.compose.SearchScreen
import com.example.adoption_and_childcare.ui.compose.ServicePlansScreen
import com.example.adoption_and_childcare.ui.compose.ServiceProvidersScreen
import com.example.adoption_and_childcare.ui.compose.SettingsScreen
import com.example.adoption_and_childcare.ui.compose.ShortcutsScreen
import com.example.adoption_and_childcare.ui.compose.SiblingsScreen
import com.example.adoption_and_childcare.ui.compose.TasksScreen
import com.example.adoption_and_childcare.ui.compose.TermsAndConditionsScreen
import com.example.adoption_and_childcare.ui.compose.UserManagementScreen
import com.example.adoption_and_childcare.ui.compose.UserRolesScreen
import com.example.adoption_and_childcare.ui.compose.VaccinationRecordsScreen
import com.example.adoption_and_childcare.ui.compose.VisitationSchedulesScreen
import com.example.adoption_and_childcare.ui.compose.WelcomeScreen
import com.example.adoption_and_childcare.ui.compose.WelfareIncidentsScreen
import com.example.adoption_and_childcare.ui.compose.WorkerLocationScreen
import com.example.adoption_and_childcare.ui.compose.WorkerMessagesScreen
import com.example.adoption_and_childcare.ui.compose.WorkloadDashboardScreen
import com.example.adoption_and_childcare.ui.theme.AdoptionChildcareTheme
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel
import com.example.adoption_and_childcare.viewmodel.SOSState
import com.example.adoption_and_childcare.viewmodel.SOSViewModel
import com.example.adoption_and_childcare.viewmodel.SyncViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Constants used throughout the MainActivity.
 */
@Suppress("HardcodedStringLiteral")
internal object MainActivityConstants {
    /** Route: Dashboard. */
    const val ROUTE_DASHBOARD = "dashboard"
    /** Route: Children List. */
    const val ROUTE_CHILDREN_LIST = "children_list"
    /** Route: Families. */
    const val ROUTE_FAMILIES = "families"
    /** Route: Adoption Applications. */
    const val ROUTE_ADOPTION_APPS = "adoption_applications"
    /** Route: Home Studies. */
    const val ROUTE_HOME_STUDIES = "home_studies"
    /** Route: Documents. */
    const val ROUTE_DOCUMENTS = "documents"
    /** Route: Placements. */
    const val ROUTE_PLACEMENTS = "placements"
    /** Route: Reports. */
    const val ROUTE_REPORTS = "reports"
    /** Route: Education. */
    const val ROUTE_EDUCATION = "education"
    /** Route: Medical. */
    const val ROUTE_MEDICAL = "medical"
    /** Route: Finance. */
    const val ROUTE_FINANCE = "finance"
    /** Route: Settings. */
    const val ROUTE_SETTINGS = "settings"
    /** Route: Map. */
    const val ROUTE_MAP = "map"
    /** Route: Background Checks. */
    const val ROUTE_BACKGROUND_CHECKS = "background_checks"
    /** Route: User Management. */
    const val ROUTE_USER_MANAGEMENT = "user_management"
    /** Route: Notifications. */
    const val ROUTE_NOTIFICATIONS = "notifications"
    /** Route: Audit Logs. */
    const val ROUTE_AUDIT_LOGS = "audit_logs"
    /** Route: Search. */
    const val ROUTE_SEARCH = "search"
    /** Route: Camera. */
    const val ROUTE_CAMERA = "camera"
    /** Route: User Roles. */
    const val ROUTE_USER_ROLES = "user_roles"
    /** Route: Analytics. */
    const val ROUTE_ANALYTICS = "analytics"
    /** Route: Guardians. */
    const val ROUTE_GUARDIANS = "guardians"
    /** Route: Court Cases. */
    const val ROUTE_COURT_CASES = "court_cases"
    /** Route: Foster Tasks. */
    const val ROUTE_FOSTER_TASKS = "foster_tasks"
    /** Route: Foster Matches. */
    const val ROUTE_FOSTER_MATCHES = "foster_matches"
    /** Route: Tasks. */
    const val ROUTE_TASKS = "tasks"
    /** Route: Action Items. */
    const val ROUTE_ACTION_ITEMS = "action_items"
    /** Route: Risk Assessments. */
    const val ROUTE_RISK_ASSESSMENTS = "risk_assessments"
    /** Route: Permanency Plans. */
    const val ROUTE_PERMANENCY_PLANS = "permanency_plans"
    /** Route: Case Activities. */
    const val ROUTE_CASE_ACTIVITIES = "case_activities"
    /** Route: Case Deadlines. */
    const val ROUTE_CASE_DEADLINES = "case_deadlines"
    /** Route: Case Approvals. */
    const val ROUTE_CASE_APPROVALS = "case_approvals"
    /** Route: Case Urgency Flags. */
    const val ROUTE_CASE_URGENCY_FLAGS = "case_urgency_flags"
    /** Route: Critical Dates. */
    const val ROUTE_CRITICAL_DATES = "critical_dates"
    /** Route: Workload. */
    const val ROUTE_WORKLOAD = "workload"
    /** Route: Worker Messages. */
    const val ROUTE_WORKER_MESSAGES = "worker_messages"
    /** Route: Placement Compatibility. */
    const val ROUTE_PLACEMENT_COMPATIBILITY = "placement_compatibility"
    /** Route: Dashboard Preferences. */
    const val ROUTE_DASHBOARD_PREFERENCES = "dashboard_preferences"
    /** Route: Vaccination Records. */
    const val ROUTE_VACCINATION_RECORDS = "vaccination_records"
    /** Route: Behavior Assessments. */
    const val ROUTE_BEHAVIOR_ASSESSMENTS = "behavior_assessments"
    /** Route: Welfare Incidents. */
    const val ROUTE_WELFARE_INCIDENTS = "welfare_incidents"
    /** Route: Siblings. */
    const val ROUTE_SIBLINGS = "siblings"
    /** Route: Consent Records. */
    const val ROUTE_CONSENT_RECORDS = "consent_records"
    /** Route: Investigations. */
    const val ROUTE_INVESTIGATIONS = "investigations"
    /** Route: Service Plans. */
    const val ROUTE_SERVICE_PLANS = "service_plans"
    /** Route: Visitation Schedules. */
    const val ROUTE_VISITATION_SCHEDULES = "visitation_schedules"
    /** Route: Referrals. */
    const val ROUTE_REFERRALS = "referrals"
    /** Route: Aftercare Plans. */
    const val ROUTE_AFTERCARE_PLANS = "aftercare_plans"
    /** Route: Child Services Referrals. */
    const val ROUTE_CHILD_SERVICES_REFERRALS = "child_services_referrals"
    /** Route: Organization Partners. */
    const val ROUTE_ORG_PARTNERS = "org_partners"
    /** Route: Service Providers. */
    const val ROUTE_SERVICE_PROVIDERS = "service_providers"
    /** Route: Donor Funding. */
    const val ROUTE_DONOR_FUNDING = "donor_funding"
    /** Route: Budget Allocations. */
    const val ROUTE_BUDGET_ALLOCATIONS = "budget_allocations"
    /** Route: Counties. */
    const val ROUTE_COUNTIES = "counties"
    /** Route: County Offices. */
    const val ROUTE_COUNTY_OFFICES = "county_offices"
    /** Route: Placement Disruptions. */
    const val ROUTE_PLACEMENT_DISRUPTIONS = "placement_disruptions"
    /** Route: Foster Training. */
    const val ROUTE_FOSTER_TRAINING = "foster_training"
    /** Route: Reports Generated. */
    const val ROUTE_REPORTS_GENERATED = "reports_generated"
    /** Route: Emergency Events. */
    const val ROUTE_EMERGENCY_EVENTS = "emergency_events"
    /** Route: Document Storage. */
    const val ROUTE_DOCUMENT_STORAGE = "document_storage"
    /** Route: Inter-county Transfers. */
    const val ROUTE_INTER_COUNTY_TRANSFERS = "inter_county_transfers"
    /** Route: Worker Locations. */
    const val ROUTE_WORKER_LOCATIONS = "worker_locations"
    /** Route: Case Inbox. */
    const val ROUTE_CASE_INBOX = "case_inbox"
    /** Route: Recent Activity. */
    const val ROUTE_RECENT_ACTIVITY = "recent_activity"
    /** Route: Shortcuts. */
    const val ROUTE_SHORTCUTS = "shortcuts"
    /** Route: Permissions. */
    const val ROUTE_PERMISSIONS = "permissions"
    /** Route: Help/About. */
    const val ROUTE_HELP_ABOUT = "help_about"
    /** Route: SOS Emergency. */
    const val ROUTE_SOS_EMERGENCY = "sos_emergency"
}

/**
 * Main activity of the application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Database instance.
     */
    @Inject
    lateinit var database: dagger.Lazy<AppDatabase>

    /**
     * Entry point of the activity.
     *
     * @param savedInstanceState The saved state of the activity.
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
                    val observer = LifecycleEventObserver { _: Any, event: Lifecycle.Event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            val lastSynced = context.getSharedPreferences(context.getString(R.string.prefs_sync), 0)
                                .getLong(context.getString(R.string.key_last_synced_at), 0L)
                            val now = System.currentTimeMillis() / 1000
                            if ((now - lastSynced) > (15 * 60)) {
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
                ) { uri: Uri? -> 
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
                        Crossfade(targetState = onboardingStep, label = stringResource(R.string.label_onboarding_transition)) { step: Int ->
                            when (step) {
                                0 -> WelcomeScreen { onboardingStep = if (session.isLoggedIn()) 5 else 3 }
                                1 -> ModernLandingPage(
                                        onGetStarted = { 
                                            onboardingStep = if (hasLoggedInBefore) 3 else 2 
                                        }, 
                                        onLogin = { onboardingStep = 3 }
                                    )
                                2 -> TermsAndConditionsScreen(onAccept = { onboardingStep = 3 })
                                3 -> LoginScreen(
                                        onLoginSuccess = { user ->
                                            currentUser = user.username
                                            currentRole = user.role
                                            val savedPhotoStr = session.getProfilePhotoUri()
                                            if (!savedPhotoStr.isNullOrBlank()) {
                                                try {
                                                    profilePhotoUri = Uri.parse(savedPhotoStr)
                                                } catch (_: Exception) {}
                                            }
                                            isLoadingAfterLogin = true
                                        },
                                        onNavigateToRegister = {
                                            registrationSuccessMessage = ""
                                            onboardingStep = 4
                                        },
                                        successMessage = registrationSuccessMessage
                                    )
                                4 -> RegisterScreen(
                                        onRegisterSuccess = { message: String ->
                                            registrationSuccessMessage = message
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
                                if (canAccessRoute(MainActivityConstants.ROUTE_CHILDREN_LIST, currentRole, adminRoleVal, supervisorRoleVal, caseworkerRoleVal, socialWorkerRoleVal, guardianRoleVal)) {
                                        NavigationDrawerItem(
                                            icon = { Icon(Icons.Default.ChildCare, contentDescription = null) },
                                            label = { Text(stringResource(R.string.module_children)) },
                                            selected = currentRoute(navController) == MainActivityConstants.ROUTE_CHILDREN_LIST,
                                            onClick = {
                                                navController.navigate(MainActivityConstants.ROUTE_CHILDREN_LIST)
                                                scope.launch { drawerState.close() }
                                            },
                                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                        )
                                    }
                                    
                                    if (canAccessRoute(MainActivityConstants.ROUTE_FAMILIES, currentRole, adminRoleVal, supervisorRoleVal, caseworkerRoleVal, socialWorkerRoleVal, guardianRoleVal)) {
                                        NavigationDrawerItem(
                                            icon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null) },
                                            label = { Text(stringResource(R.string.module_families)) },
                                            selected = currentRoute(navController) == MainActivityConstants.ROUTE_FAMILIES,
                                            onClick = {
                                                navController.navigate(MainActivityConstants.ROUTE_FAMILIES)
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
                                        
                                        if (canAccessRoute(MainActivityConstants.ROUTE_USER_MANAGEMENT, currentRole, adminRoleVal, supervisorRoleVal, caseworkerRoleVal, socialWorkerRoleVal, guardianRoleVal)) {
                                            NavigationDrawerItem(
                                                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                                                label = { Text(stringResource(R.string.user_management_title)) },
                                                selected = currentRoute(navController) == MainActivityConstants.ROUTE_USER_MANAGEMENT,
                                                onClick = {
                                                    navController.navigate(MainActivityConstants.ROUTE_USER_MANAGEMENT)
                                                    scope.launch { drawerState.close() }
                                                },
                                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                            )
                                        }
                                        
                                        if (canAccessRoute(MainActivityConstants.ROUTE_USER_ROLES, currentRole, adminRoleVal, supervisorRoleVal, caseworkerRoleVal, socialWorkerRoleVal, guardianRoleVal)) {
                                            NavigationDrawerItem(
                                                icon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                                label = { Text(stringResource(R.string.dashboard_label_user_roles)) },
                                                selected = currentRoute(navController) == MainActivityConstants.ROUTE_USER_ROLES,
                                                onClick = {
                                                    navController.navigate(MainActivityConstants.ROUTE_USER_ROLES)
                                                    scope.launch { drawerState.close() }
                                                },
                                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                            )
                                        }
                                    }

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                        label = { Text(stringResource(R.string.action_settings)) },
                                        selected = currentRoute(navController) == MainActivityConstants.ROUTE_SETTINGS,
                                        onClick = {
                                            navController.navigate(MainActivityConstants.ROUTE_SETTINGS)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )

                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.Inbox, contentDescription = null) },
                                        label = { Text(stringResource(R.string.title_case_inbox)) },
                                        selected = currentRoute(navController) == MainActivityConstants.ROUTE_CASE_INBOX,
                                        onClick = {
                                            navController.navigate(MainActivityConstants.ROUTE_CASE_INBOX)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                                        label = { Text(stringResource(R.string.title_recent_activity)) },
                                        selected = currentRoute(navController) == MainActivityConstants.ROUTE_RECENT_ACTIVITY,
                                        onClick = {
                                            navController.navigate(MainActivityConstants.ROUTE_RECENT_ACTIVITY)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.PushPin, contentDescription = null) },
                                        label = { Text(stringResource(R.string.title_shortcuts)) },
                                        selected = currentRoute(navController) == MainActivityConstants.ROUTE_SHORTCUTS,
                                        onClick = {
                                            navController.navigate(MainActivityConstants.ROUTE_SHORTCUTS)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                        label = { Text(stringResource(R.string.title_permissions)) },
                                        selected = currentRoute(navController) == MainActivityConstants.ROUTE_PERMISSIONS,
                                        onClick = {
                                            navController.navigate(MainActivityConstants.ROUTE_PERMISSIONS)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )

                                    NavigationDrawerItem(
                                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                                        label = { Text(stringResource(R.string.help_about_label)) },
                                        selected = currentRoute(navController) == MainActivityConstants.ROUTE_HELP_ABOUT,
                                        onClick = {
                                            navController.navigate(MainActivityConstants.ROUTE_HELP_ABOUT)
                                            scope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )
                                }
                            }
                        }
                    ) {
                        Scaffold(
                            containerColor = Color(0xFFF8F9FA),
                            topBar = {
                                Column {
                                    if (sosState == SOSState.ACTIVE) {
                                        EmergencyActiveBanner()
                                    }
                                    GreenHeader(
                                        profilePhotoUri = profilePhotoUri,
                                        searchQuery = headerSearchQuery,
                                        onSearchQueryChange = { query: String ->
                                            headerSearchQuery = query
                                            val currentPath = navController.currentDestination?.route
                                            if (query.isNotEmpty() && currentPath != MainActivityConstants.ROUTE_SEARCH) {
                                                navController.navigate(MainActivityConstants.ROUTE_SEARCH)
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
                            }
                        ) { scaffoldPadding: PaddingValues ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(scaffoldPadding)
                            ) {
                                AppNavHost(
                                    navController = navController,
                                    headerSearchQuery = headerSearchQuery,
                                    onSearchQueryChange = { updatedQuery: String ->
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
    val logTag = stringResource(R.string.tag_main_activity)

    NavHost(navController = navController, startDestination = MainActivityConstants.ROUTE_DASHBOARD, modifier = modifier) {
        composable(MainActivityConstants.ROUTE_DASHBOARD) { _: NavBackStackEntry ->
            DashboardScreenModern(
                userRole = userRole,
                onNavigate = { destination: String ->
                    try {
                        navController.navigate(destination)
                    } catch (e: Exception) {
                        Log.e(logTag, "Navigation error: ${e.message}")
                    }
                }
            )
        }
        composable(MainActivityConstants.ROUTE_CHILDREN_LIST) { _: NavBackStackEntry -> ChildrenListScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_FAMILIES) { _: NavBackStackEntry -> FamiliesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_ADOPTION_APPS) { _: NavBackStackEntry -> AdoptionApplicationsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_HOME_STUDIES) { _: NavBackStackEntry -> HomeStudiesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_DOCUMENTS) { _: NavBackStackEntry -> DocumentsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_PLACEMENTS) { _: NavBackStackEntry -> PlacementsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_REPORTS) { _: NavBackStackEntry -> CaseReportsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_EDUCATION) { _: NavBackStackEntry -> EducationScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_MEDICAL) { _: NavBackStackEntry -> 
            val medicalDetailPathPattern = stringResource(R.string.nav_medical_detail_path)
            val itemsCountPrefix = stringResource(R.string.dashboard_items_count).take(3)
            MedicalScreen(
                onBack = { navController.popBackStack() },
                onMedicalRecordClick = { id: Int ->
                    val medicalDetailPath = if (navMedicalDetailTemplate.contains(itemsCountPrefix)) {
                        String.format(navMedicalDetailTemplate, id)
                    } else {
                        String.format(medicalDetailPathPattern, MainActivityConstants.ROUTE_MEDICAL, id)
                    }
                    navController.navigate(medicalDetailPath) 
                }
            ) 
        }
        composable(navMedicalDetailRoute) { entry: NavBackStackEntry ->
            val recordId = entry.arguments?.getString("recordId")?.toIntOrNull() ?: 0
            MedicalDetailScreen(
                recordId = recordId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(MainActivityConstants.ROUTE_FINANCE) { _: NavBackStackEntry -> FinanceScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_SETTINGS) { _: NavBackStackEntry ->
            SettingsScreen(
                onBack = {
                    navController.navigate(MainActivityConstants.ROUTE_DASHBOARD) {
                        popUpTo(MainActivityConstants.ROUTE_DASHBOARD) { inclusive = true }
                    }
                }
            )
        }
        composable(MainActivityConstants.ROUTE_MAP) { _: NavBackStackEntry -> MapScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_SOS_EMERGENCY) { _: NavBackStackEntry -> SOSScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_BACKGROUND_CHECKS) { _: NavBackStackEntry -> BackgroundChecksScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_USER_MANAGEMENT) { _: NavBackStackEntry -> UserManagementScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_NOTIFICATIONS) { _: NavBackStackEntry -> NotificationsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_AUDIT_LOGS) { _: NavBackStackEntry -> AuditLogsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_SEARCH) { _: NavBackStackEntry ->
            SearchScreen(
                query = headerSearchQuery,
                onQueryChange = onSearchQueryChange,
                onBack = { navController.popBackStack() }
            )
        }
        composable(MainActivityConstants.ROUTE_CAMERA) { _: NavBackStackEntry -> CameraScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_USER_ROLES) { _: NavBackStackEntry -> UserRolesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_ANALYTICS) { _: NavBackStackEntry -> AnalyticsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_GUARDIANS) { _: NavBackStackEntry -> GuardiansScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_COURT_CASES) { _: NavBackStackEntry -> CourtCasesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_FOSTER_TASKS) { _: NavBackStackEntry -> FosterTasksScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_FOSTER_MATCHES) { _: NavBackStackEntry -> FosterMatchesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_TASKS) { _: NavBackStackEntry -> TasksScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_ACTION_ITEMS) { _: NavBackStackEntry -> ActionItemsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_RISK_ASSESSMENTS) { _: NavBackStackEntry -> RiskAssessmentCenterScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_PERMANENCY_PLANS) { _: NavBackStackEntry -> PermanencyPlansScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_CASE_ACTIVITIES) { _: NavBackStackEntry -> CaseActivitiesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_CASE_DEADLINES) { _: NavBackStackEntry -> CaseDeadlinesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_CASE_APPROVALS) { _: NavBackStackEntry -> CaseApprovalsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_CASE_URGENCY_FLAGS) { _: NavBackStackEntry -> CaseUrgencyFlagsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_CRITICAL_DATES) { _: NavBackStackEntry -> CriticalDatesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_WORKLOAD) { _: NavBackStackEntry -> WorkloadDashboardScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_WORKER_MESSAGES) { _: NavBackStackEntry -> WorkerMessagesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_PLACEMENT_COMPATIBILITY) { _: NavBackStackEntry -> PlacementCompatibilityEngineScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_DASHBOARD_PREFERENCES) { _: NavBackStackEntry -> DashboardPreferencesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_VACCINATION_RECORDS) { _: NavBackStackEntry -> VaccinationRecordsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_BEHAVIOR_ASSESSMENTS) { _: NavBackStackEntry -> BehaviorAssessmentsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_WELFARE_INCIDENTS) { _: NavBackStackEntry -> WelfareIncidentsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_SIBLINGS) { _: NavBackStackEntry -> SiblingsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_CONSENT_RECORDS) { _: NavBackStackEntry -> ConsentRecordsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_INVESTIGATIONS) { _: NavBackStackEntry -> InvestigationsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_SERVICE_PLANS) { _: NavBackStackEntry -> ServicePlansScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_VISITATION_SCHEDULES) { _: NavBackStackEntry -> VisitationSchedulesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_REFERRALS) { _: NavBackStackEntry -> ReferralsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_AFTERCARE_PLANS) { _: NavBackStackEntry -> AftercarePlansScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_CHILD_SERVICES_REFERRALS) { _: NavBackStackEntry -> ChildServicesReferralsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_ORG_PARTNERS) { _: NavBackStackEntry -> OrganizationPartnersScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_SERVICE_PROVIDERS) { _: NavBackStackEntry -> ServiceProvidersScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_DONOR_FUNDING) { _: NavBackStackEntry -> DonorFundingScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_BUDGET_ALLOCATIONS) { _: NavBackStackEntry -> BudgetAllocationsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_COUNTIES) { _: NavBackStackEntry -> CountiesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_COUNTY_OFFICES) { _: NavBackStackEntry -> CountyOfficesScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_PLACEMENT_DISRUPTIONS) { _: NavBackStackEntry -> PlacementDisruptionsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_FOSTER_TRAINING) { _: NavBackStackEntry -> FosterFamilyTrainingScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_REPORTS_GENERATED) { _: NavBackStackEntry -> ReportsGeneratedScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_EMERGENCY_EVENTS) { _: NavBackStackEntry -> EmergencyEventsScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_DOCUMENT_STORAGE) { _: NavBackStackEntry -> GlobalDocumentStorageScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_INTER_COUNTY_TRANSFERS) { _: NavBackStackEntry -> InterCountyTransfersScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_WORKER_LOCATIONS) { _: NavBackStackEntry -> WorkerLocationScreen(onBack = { navController.popBackStack() }) }
        composable(MainActivityConstants.ROUTE_CASE_INBOX) { _: NavBackStackEntry -> CaseInboxScreen() }
        composable(MainActivityConstants.ROUTE_RECENT_ACTIVITY) { _: NavBackStackEntry -> RecentActivityScreen() }
        composable(MainActivityConstants.ROUTE_SHORTCUTS) { _: NavBackStackEntry -> ShortcutsScreen() }
        composable(MainActivityConstants.ROUTE_PERMISSIONS) { _: NavBackStackEntry -> AppPermissionsScreen() }
        composable(MainActivityConstants.ROUTE_HELP_ABOUT) { _: NavBackStackEntry -> HelpAboutScreen() }
    }
}

/**
 * Check if current user can access a specific route.
 *
 * @param route The destination route string.
 * @param userRole The role of the current user.
 * @param adminRole String value for admin role.
 * @param supervisorRole String value for supervisor role.
 * @param caseworkerRole String value for caseworker role.
 * @param socialWorkerRole String value for social worker role.
 * @param guardianRole String value for guardian role.
 * @return True if access is granted, false otherwise.
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
        MainActivityConstants.ROUTE_DASHBOARD,
        MainActivityConstants.ROUTE_SETTINGS,
        MainActivityConstants.ROUTE_NOTIFICATIONS,
        MainActivityConstants.ROUTE_SEARCH,
        MainActivityConstants.ROUTE_CAMERA,
        MainActivityConstants.ROUTE_CASE_INBOX,
        MainActivityConstants.ROUTE_RECENT_ACTIVITY,
        MainActivityConstants.ROUTE_SHORTCUTS,
        MainActivityConstants.ROUTE_PERMISSIONS,
        MainActivityConstants.ROUTE_HELP_ABOUT,
        MainActivityConstants.ROUTE_CASE_URGENCY_FLAGS,
        MainActivityConstants.ROUTE_RISK_ASSESSMENTS,
        MainActivityConstants.ROUTE_CASE_DEADLINES,
        MainActivityConstants.ROUTE_CHILDREN_LIST,
        MainActivityConstants.ROUTE_FAMILIES,
        MainActivityConstants.ROUTE_PLACEMENTS,
        MainActivityConstants.ROUTE_ADOPTION_APPS,
        MainActivityConstants.ROUTE_ACTION_ITEMS
    )

    return when (userRole) {
        adminRole, supervisorRole -> true
        
        caseworkerRole -> route !in listOf(
            MainActivityConstants.ROUTE_USER_MANAGEMENT,
            MainActivityConstants.ROUTE_USER_ROLES,
            MainActivityConstants.ROUTE_AUDIT_LOGS
        )

        socialWorkerRole -> route in commonRoutes + listOf(
            MainActivityConstants.ROUTE_CHILDREN_LIST,
            MainActivityConstants.ROUTE_FAMILIES,
            MainActivityConstants.ROUTE_DOCUMENTS,
            MainActivityConstants.ROUTE_REPORTS,
            MainActivityConstants.ROUTE_HOME_STUDIES,
            MainActivityConstants.ROUTE_PLACEMENTS,
            MainActivityConstants.ROUTE_EDUCATION,
            MainActivityConstants.ROUTE_MEDICAL,
            MainActivityConstants.ROUTE_COURT_CASES,
            MainActivityConstants.ROUTE_FOSTER_MATCHES,
            MainActivityConstants.ROUTE_CASE_ACTIVITIES,
            MainActivityConstants.ROUTE_CASE_DEADLINES
        )

        guardianRole -> route in commonRoutes + listOf(
            MainActivityConstants.ROUTE_CHILDREN_LIST,
            MainActivityConstants.ROUTE_DOCUMENTS,
            MainActivityConstants.ROUTE_EDUCATION,
            MainActivityConstants.ROUTE_MEDICAL,
            MainActivityConstants.ROUTE_FOSTER_TASKS,
            MainActivityConstants.ROUTE_VACCINATION_RECORDS,
            MainActivityConstants.ROUTE_BEHAVIOR_ASSESSMENTS,
            MainActivityConstants.ROUTE_VISITATION_SCHEDULES
        )

        else -> route in commonRoutes
    }
}

/**
 * Returns the current route.
 *
 * @param navController The navigation controller.
 * @return The route string or null.
 */
@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
