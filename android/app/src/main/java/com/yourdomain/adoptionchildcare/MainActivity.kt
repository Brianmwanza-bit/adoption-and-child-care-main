package com.yourdomain.adoptionchildcare

import android.os.Bundle
import com.yourdomain.adoptionchildcare.config.AppConfig
import com.yourdomain.adoptionchildcare.Logger
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.gridlayout.widget.GridLayout
import com.yourdomain.adoptionchildcare.SyncManager
import com.yourdomain.adoptionchildcare.TokenManager

// --- Jetpack Compose Navigation and Dashboard Skeletons ---
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDialogState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.VerifiedUser
import com.adoptionapp.viewmodel.FamilyProfileViewModel
import com.adoptionapp.viewmodel.FosterTasksViewModel
import com.adoptionapp.viewmodel.BackgroundChecksViewModel
import com.adoptionapp.viewmodel.DocumentsViewModel
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.ui.graphics.Color
import com.adoptionapp.ui.compose.DashboardScreen
import com.adoptionapp.ui.compose.AnalyticsScreen
import com.adoptionapp.ui.compose.NotificationsScreen
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.graphics.Bitmap
import androidx.compose.ui.graphics.ImageDecoder
import androidx.compose.ui.graphics.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Build
import androidx.compose.ui.graphics.asAndroidBitmap
import com.adoptionapp.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Activity for the Adoption and Child Care Application
 * 
 * This activity serves as the primary entry point for the app and provides:
 * - User authentication (login/register)
 * - Dashboard with key features
 * - Navigation to different modules
 * - Real-time data synchronization
 * 
 * Features:
 * - Secure user authentication
 * - Child records management
 * - Guardian profiles
 * - Document management
 * - Case tracking
 * - Background checks
 * - Placement monitoring
 * - Medical records
 * - Education tracking
 * - Financial management
 * - Analytics and reporting
 * 
 * @author Adoption App Team
 * @version 1.0
 */
class MainActivity : AppCompatActivity() {
    private lateinit var mainContainer: ConstraintLayout
    private lateinit var featuresGrid: GridLayout
    private lateinit var bottomNav: LinearLayout
    private lateinit var loginModal: FrameLayout
    private lateinit var registerModal: FrameLayout
    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var signInButton: Button
    private lateinit var loginError: TextView
    private lateinit var registerRole: EditText
    private lateinit var registerEmail: EditText
    private lateinit var registerButton: Button
    private lateinit var registerError: TextView
    private lateinit var registerSuccess: TextView

    private val userRepository by lazy {
        // TODO: Replace with actual DAO and API instances as needed
        UserRepository(/* userDao = */ TODO(), /* userApi = */ TODO(), /* syncManager = */ TODO())
    }
    private val uiScope = CoroutineScope(Dispatchers.Main)

    /**
     * Initialize the main activity and set up the user interface
     * 
     * This method:
     * - Sets up the main layout and UI components
     * - Initializes the database and repositories
     * - Sets up authentication and user session
     * - Configures navigation and event handlers
     * - Starts background sync processes
     * 
     * @param savedInstanceState The saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Log app startup
        Logger.logStartup("MainActivity", "Initializing")
        
        try {
        setContentView(R.layout.MainActivity)
        
        Logger.logStartup("MainActivity", "Layout set successfully")
        
        // Initialize UI components
        initializeUI()
        
        // Initialize database and repositories
        initializeDatabase()
        
        // Set up authentication
        setupAuthentication()
        
        // Configure navigation
        setupNavigation()
        
        // Start background sync
        startBackgroundSync()
        
        Logger.logStartup("MainActivity", "Initialization complete")
        
        } catch (e: Exception) {
            Logger.logError("MainActivity", e, "Failed to initialize main activity")
            ErrorHandler.handleException(e)
        }

        mainContainer = findViewById(R.id.main_container)
        featuresGrid = findViewById(R.id.featuresGrid)
        bottomNav = findViewById(R.id.bottomNav)
        loginModal = findViewById(R.id.loginModal)
        registerModal = findViewById(R.id.registerModal)
        loginUsername = findViewById(R.id.loginUsername)
        loginPassword = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)
        signInButton = findViewById(R.id.signInButton)
        loginError = findViewById(R.id.loginError)
        registerRole = findViewById(R.id.registerRole)
        registerEmail = findViewById(R.id.registerEmail)
        registerButton = findViewById(R.id.registerButton)
        registerError = findViewById(R.id.registerError)
        registerSuccess = findViewById(R.id.registerSuccess)

        // Show login modal on start
        showLoginModal()
        setupListeners()
        populateFeaturesGrid()
        // Schedule periodic sync for children
        SyncManager.scheduleChildrenSync(this)

        // --- Jetpack Compose Navigation and Dashboard Skeletons ---
        setContent {
            MainNavHost(userRole = "admin") // TODO: Replace with real role logic
        }
    }

    private fun showLoginModal() {
        loginModal.visibility = View.VISIBLE
        registerModal.visibility = View.GONE
        mainContainer.visibility = View.GONE
        loginError.visibility = View.GONE
    }

    private fun showDashboard() {
        loginModal.visibility = View.GONE
        registerModal.visibility = View.GONE
        mainContainer.visibility = View.VISIBLE
    }

    private fun showRegisterModal() {
        loginModal.visibility = View.GONE
        registerModal.visibility = View.VISIBLE
        mainContainer.visibility = View.GONE
        registerError.visibility = View.GONE
        registerSuccess.visibility = View.GONE
    }

    private fun setupListeners() {
        loginButton.setOnClickListener {
            val username = loginUsername.text.toString().trim()
            val password = loginPassword.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                loginError.text = "Please enter both username and password."
                loginError.visibility = View.VISIBLE
            } else {
                uiScope.launch {
                    try {
                        val response = userRepository.login(this@MainActivity, username, password)
                        if (response.isSuccessful && response.body()?.success == true) {
                    showDashboard()
                            onLoginSuccess(response.body()?.token ?: "")
                } else {
                            loginError.text = response.body()?.let { it.toString() } ?: "Invalid credentials."
                    loginError.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        loginError.text = "Login failed: ${e.localizedMessage}"
                        loginError.visibility = View.VISIBLE
                    }
                }
            }
        }
        signInButton.setOnClickListener {
            showRegisterModal()
        }
        registerButton.setOnClickListener {
            val role = registerRole.text.toString().trim()
            val email = registerEmail.text.toString().trim()
            val username = loginUsername.text.toString().trim()
            val password = loginPassword.text.toString()
            if (role.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                registerError.text = "Please fill all fields."
                registerError.visibility = View.VISIBLE
                registerSuccess.visibility = View.GONE
            } else {
                uiScope.launch {
                    try {
                        val response = userRepository.register(this@MainActivity, username, password, email, role)
                        if (response.isSuccessful && response.body()?.success == true) {
                registerError.visibility = View.GONE
                registerSuccess.text = "Registration successful! You can now log in."
                registerSuccess.visibility = View.VISIBLE
                        } else {
                            registerError.text = response.body()?.let { it.toString() } ?: "Registration failed."
                            registerError.visibility = View.VISIBLE
                            registerSuccess.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        registerError.text = "Registration failed: ${e.localizedMessage}"
                        registerError.visibility = View.VISIBLE
                        registerSuccess.visibility = View.GONE
                    }
                }
            }
        }
        // Hide modals when clicking outside (optional, not implemented here)
    }

    private fun populateFeaturesGrid() {
        val features = listOf(
            "Court", "Children", "PLACEMENT", "Medical", "Guardia", "Case Reports",
            "Money", "Education", "User", "Audit Logs", "Permissions", "User Permissions", "Documents"
        )
        featuresGrid.removeAllViews()
        for (feature in features) {
            val btn = Button(this)
            btn.text = feature
            btn.setOnClickListener {
                Toast.makeText(this, "$feature clicked", Toast.LENGTH_SHORT).show()
            }
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(8, 8, 8, 8)
            btn.layoutParams = params
            featuresGrid.addView(btn)
        }
    }

    // Example: Call this after successful login
    fun onLoginSuccess(jwtToken: String) {
        TokenManager.saveToken(this, jwtToken)
        // Optionally trigger immediate sync
        SyncManager.scheduleChildrenSync(this)
    }
}

@Composable
fun MainNavHost(userRole: String) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf("dashboard") }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == "dashboard",
                    onClick = { selectedTab = "dashboard"; navController.navigate("dashboard") },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = selectedTab == "analytics",
                    onClick = { selectedTab = "analytics"; navController.navigate("analytics") },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Analytics") },
                    label = { Text("Analytics") }
                )
                NavigationBarItem(
                    selected = selectedTab == "notifications",
                    onClick = { selectedTab = "notifications"; navController.navigate("notifications") },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications") },
                    label = { Text("Notifications") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") { DashboardScreen() }
            composable("analytics") { AnalyticsScreen() }
            composable("notifications") { NotificationsScreen() }
            composable("children") { ChildrenScreen() }
            composable("placements") { PlacementsScreen() }
            composable("documents") { DocumentsScreen() }
            composable("foster_tasks") { FosterTasksScreen() }
            composable("family_profiles") { FamilyProfilesScreen() }
            composable("background_checks") { BackgroundChecksScreen() }
        }
    }
}

@Composable
fun LoginScreen(navController: androidx.navigation.NavHostController) {
    // TODO: Implement login UI and logic
    Text("Login Screen (Compose)")
}

@Composable
fun AdminDashboardScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Admin Dashboard", fontSize = 22.sp)
        Spacer(Modifier.height(16.dp))
        // Quick stats
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Dashboard, contentDescription = null)
                Text("System Health")
                Text("OK", color = Color.Green)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Notifications, contentDescription = null)
                Text("Pending Approvals")
                Text("3", color = Color.Red)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.BarChart, contentDescription = null)
                Text("Recent Logins")
                Text("12")
            }
        }
        Spacer(Modifier.height(24.dp))
        // Widgets
        Text("Audit Logs", fontSize = 16.sp)
        Box(Modifier.fillMaxWidth().height(80.dp).background(Color.LightGray))
        Spacer(Modifier.height(16.dp))
        Text("User Management", fontSize = 16.sp)
        Box(Modifier.fillMaxWidth().height(80.dp).background(Color.LightGray))
    }
}

data class AnalyticsSummary(
    val userCount: Int,
    val familyCount: Int,
    val taskCount: Int,
    val matchCount: Int,
    val backgroundCheckCount: Int
)

@Composable
fun NotificationScreen(notifications: List<String>) {
    Column(Modifier.padding(16.dp)) {
        Text("Notifications", style = MaterialTheme.typography.titleLarge)
        notifications.forEach { msg ->
            Text(msg)
        }
    }
}

@Composable
fun CaseWorkerDashboardScreen(navController: androidx.navigation.NavHostController) {
    Text("Case Worker Dashboard (Compose)")
}

@Composable
fun FamilyDashboardScreen(navController: androidx.navigation.NavHostController) {
    Text("Family Dashboard (Compose)")
}

@Composable
fun MapScreen(familyLocations: List<LatLng>, caseWorkerLocations: List<LatLng>) {
    val cameraPositionState = rememberCameraPositionState()
    GoogleMap(cameraPositionState = cameraPositionState) {
        familyLocations.forEach { location ->
            Marker(position = location, title = "Family")
        }
        caseWorkerLocations.forEach { location ->
            Marker(position = location, title = "Case Worker")
        }
    }
}

@Composable
fun BackgroundCheckStatus(status: String) {
    Text("Background Check Status: $status")
}

@Composable
fun ChildrenScreen(viewModel: ChildViewModel = viewModel()) {
    val children by viewModel.children.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editChild by remember { mutableStateOf<Child?>(null) }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { viewModel.syncChildren() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync")
                }
            }
            LazyColumn(Modifier.weight(1f)) {
                items(children) { child ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { editChild = child; showDialog = true }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(child.name, fontSize = 18.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.deleteChild(child) }) {
                            Icon(painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { editChild = null; showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Child")
        }
        if (showDialog) {
            ChildDialog(
                child = editChild,
                onDismiss = { showDialog = false },
                onSave = { child ->
                    if (editChild == null) viewModel.addChild(child) else viewModel.updateChild(child)
                    showDialog = false
                },
                photoBitmap = photoBitmap,
                onPhotoChange = { photoBitmap = it }
            )
        }
    }
}

@Composable
fun ChildDialog(
    child: Child?,
    onDismiss: () -> Unit,
    onSave: (Child) -> Unit,
    photoBitmap: Bitmap?,
    onPhotoChange: (Bitmap?) -> Unit
) {
    var name by remember { mutableStateOf(child?.name ?: "") }
    var age by remember { mutableStateOf(child?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(child?.gender ?: "") }
    var dob by remember { mutableStateOf(child?.dateOfBirth ?: "") }
    var medicalHistory by remember { mutableStateOf(child?.medicalHistory ?: "") }
    var specialNeeds by remember { mutableStateOf(child?.specialNeeds ?: "") }
    var status by remember { mutableStateOf(child?.status ?: "Available") }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            onPhotoChange(bitmap)
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (child == null) "Add Child" else "Edit Child") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") })
                OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") })
                OutlinedTextField(value = dob, onValueChange = { dob = it }, label = { Text("Date of Birth") })
                OutlinedTextField(value = medicalHistory, onValueChange = { medicalHistory = it }, label = { Text("Medical History") })
                OutlinedTextField(value = specialNeeds, onValueChange = { specialNeeds = it }, label = { Text("Special Needs") })
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") })
                Spacer(Modifier.height(8.dp))
                if (photoBitmap != null) {
                    Image(bitmap = photoBitmap.asImageBitmap(), contentDescription = "Photo", modifier = Modifier.size(64.dp))
                }
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Pick Photo")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val childObj = Child(
                    id = child?.id ?: 0,
                    name = name,
                    age = age.toIntOrNull() ?: 0,
                    gender = gender,
                    dateOfBirth = dob,
                    medicalHistory = medicalHistory,
                    specialNeeds = specialNeeds,
                    photoUrl = child?.photoUrl ?: "",
                    status = status,
                    createdAt = child?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                // Convert Bitmap to ByteArray
                val photoBytes = photoBitmap?.let {
                    val stream = java.io.ByteArrayOutputStream()
                    it.asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
                    stream.toByteArray()
                }
                // Call new ViewModel method for photo upload
                val viewModel: ChildViewModel = viewModel()
                viewModel.addChildWithPhoto(childObj, photoBytes)
                onSave(childObj)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PlacementsScreen(viewModel: PlacementViewModel = viewModel()) {
    val placements by viewModel.placements.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editPlacement by remember { mutableStateOf<Placement?>(null) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { viewModel.syncPlacements() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync")
                }
            }
            LazyColumn(Modifier.weight(1f)) {
                items(placements) { placement ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { editPlacement = placement; showDialog = true }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(placement.placementType + " - " + placement.status, fontSize = 18.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.deletePlacement(placement) }) {
                            Icon(painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { editPlacement = null; showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Placement")
        }
        if (showDialog) {
            PlacementDialog(
                placement = editPlacement,
                onDismiss = { showDialog = false },
                onSave = { placement ->
                    if (editPlacement == null) viewModel.addPlacement(placement) else viewModel.updatePlacement(placement)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun PlacementDialog(
    placement: Placement?,
    onDismiss: () -> Unit,
    onSave: (Placement) -> Unit
) {
    var childId by remember { mutableStateOf(placement?.childId?.toString() ?: "") }
    var guardianId by remember { mutableStateOf(placement?.guardianId?.toString() ?: "") }
    var placementDate by remember { mutableStateOf(placement?.placementDate ?: "") }
    var placementType by remember { mutableStateOf(placement?.placementType ?: "Foster Care") }
    var status by remember { mutableStateOf(placement?.status ?: "Active") }
    var notes by remember { mutableStateOf(placement?.notes ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (placement == null) "Add Placement" else "Edit Placement") },
        text = {
            Column {
                OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") })
                OutlinedTextField(value = guardianId, onValueChange = { guardianId = it }, label = { Text("Guardian ID") })
                OutlinedTextField(value = placementDate, onValueChange = { placementDate = it }, label = { Text("Placement Date") })
                OutlinedTextField(value = placementType, onValueChange = { placementType = it }, label = { Text("Placement Type") })
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val placementObj = Placement(
                    id = placement?.id ?: 0,
                    childId = childId.toIntOrNull() ?: 0,
                    guardianId = guardianId.toIntOrNull() ?: 0,
                    placementDate = placementDate,
                    placementType = placementType,
                    status = status,
                    notes = notes,
                    createdAt = placement?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                onSave(placementObj)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DocumentsScreen(viewModel: DocumentsViewModel = viewModel()) {
    val documents by viewModel.documents.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editDocument by remember { mutableStateOf<DocumentsEntity?>(null) }
    var selectedFileName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadDocuments() }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { viewModel.loadDocuments() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync")
                }
            }
            LazyColumn(Modifier.weight(1f)) {
                items(documents) { document ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { editDocument = document; showDialog = true }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(document.file_name + " - " + (document.document_type), fontSize = 16.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.deleteDocument(document) }) {
                            Icon(painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { editDocument = null; showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Document")
        }
        if (showDialog) {
            DocumentDialog(
                document = editDocument,
                onDismiss = { showDialog = false },
                onSave = { doc ->
                    if (editDocument == null) viewModel.addDocument(doc) else viewModel.updateDocument(doc)
                    showDialog = false
                },
                selectedFileName = selectedFileName,
                onFilePick = { selectedFileName = it }
            )
        }
    }
}

@Composable
fun DocumentDialog(
    document: DocumentsEntity?,
    onDismiss: () -> Unit,
    onSave: (DocumentsEntity) -> Unit,
    selectedFileName: String,
    onFilePick: (String) -> Unit
) {
    var childId by remember { mutableStateOf(document?.child_id?.toString() ?: "") }
    var documentType by remember { mutableStateOf(document?.document_type ?: "") }
    var fileName by remember { mutableStateOf(document?.file_name ?: "") }
    var fileType by remember { mutableStateOf(document?.file_type ?: "") }
    var fileSize by remember { mutableStateOf(document?.file_size?.toString() ?: "") }
    var filePath by remember { mutableStateOf(document?.file_path ?: "") }
    var description by remember { mutableStateOf(document?.description ?: "") }
    var uploadedBy by remember { mutableStateOf(document?.uploaded_by?.toString() ?: "") }
    val context = LocalContext.current
    var fileBytes by remember { mutableStateOf<ByteArray?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "selected_file"
            onFilePick(fileName)
            // Read file as ByteArray
            val inputStream = context.contentResolver.openInputStream(uri)
            fileBytes = inputStream?.readBytes()
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (document == null) "Add Document" else "Edit Document") },
        text = {
            Column {
                OutlinedTextField(value = childId, onValueChange = { childId = it }, label = { Text("Child ID") })
                OutlinedTextField(value = documentType, onValueChange = { documentType = it }, label = { Text("Document Type") })
                OutlinedTextField(value = fileName, onValueChange = { fileName = it }, label = { Text("File Name") })
                OutlinedTextField(value = fileType, onValueChange = { fileType = it }, label = { Text("File Type") })
                OutlinedTextField(value = fileSize, onValueChange = { fileSize = it }, label = { Text("File Size") })
                OutlinedTextField(value = filePath, onValueChange = { filePath = it }, label = { Text("File Path") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(value = uploadedBy, onValueChange = { uploadedBy = it }, label = { Text("Uploaded By") })
                Spacer(Modifier.height(8.dp))
                Button(onClick = { launcher.launch("*/*") }) {
                    Text(if (selectedFileName.isEmpty()) "Pick File/Photo" else selectedFileName)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val docObj = DocumentsEntity(
                    document_id = document?.document_id ?: 0,
                    document_type = documentType,
                    description = description,
                    fileBlob = fileBytes
                )
                // Call new ViewModel method for file upload
                val viewModel: DocumentsViewModel = viewModel()
                viewModel.insertWithFile(docObj, fileBytes)
                onSave(docObj)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun FosterTasksScreen(viewModel: FosterTasksViewModel = viewModel()) {
    val tasks by viewModel.tasks.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editTask by remember { mutableStateOf<FosterTasksEntity?>(null) }

    LaunchedEffect(Unit) { viewModel.loadTasks() }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { viewModel.loadTasks() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync")
                }
            }
            LazyColumn(Modifier.weight(1f)) {
                items(tasks) { task ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { editTask = task; showDialog = true }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text((task.description ?: "") + " - " + (task.status ?: ""), fontSize = 16.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.deleteTask(task) }) {
                            Icon(painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { editTask = null; showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
        if (showDialog) {
            FosterTaskDialog(
                task = editTask,
                onDismiss = { showDialog = false },
                onSave = { t ->
                    if (editTask == null) viewModel.addTask(t) else viewModel.updateTask(t)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun FosterTaskDialog(
    task: FosterTasksEntity?,
    onDismiss: () -> Unit,
    onSave: (FosterTasksEntity) -> Unit
) {
    var familyId by remember { mutableStateOf(task?.family_id?.toString() ?: "") }
    var caseWorkerId by remember { mutableStateOf(task?.case_worker_id?.toString() ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var status by remember { mutableStateOf(task?.status ?: "") }
    var createdAt by remember { mutableStateOf(task?.created_at ?: "") }
    var dueDate by remember { mutableStateOf(task?.due_date ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Add Task" else "Edit Task") },
        text = {
            Column {
                OutlinedTextField(value = familyId, onValueChange = { familyId = it }, label = { Text("Family ID") })
                OutlinedTextField(value = caseWorkerId, onValueChange = { caseWorkerId = it }, label = { Text("Case Worker ID") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") })
                OutlinedTextField(value = createdAt, onValueChange = { createdAt = it }, label = { Text("Created At") })
                OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val taskObj = FosterTasksEntity(
                    task_id = task?.task_id ?: 0,
                    family_id = familyId.toIntOrNull() ?: 0,
                    case_worker_id = caseWorkerId.toIntOrNull(),
                    description = description,
                    status = status,
                    created_at = createdAt,
                    due_date = dueDate
                )
                onSave(taskObj)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun FamilyProfilesScreen(viewModel: FamilyProfileViewModel = viewModel()) {
    val profiles by viewModel.profiles.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editProfile by remember { mutableStateOf<FamilyProfileEntity?>(null) }

    LaunchedEffect(Unit) { viewModel.loadProfiles() }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { viewModel.loadProfiles() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync")
                }
            }
            LazyColumn(Modifier.weight(1f)) {
                items(profiles) { profile ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { editProfile = profile; showDialog = true }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text((profile.address ?: "") + " - " + (profile.household_size?.toString() ?: ""), fontSize = 16.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.deleteProfile(profile) }) {
                            Icon(painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { editProfile = null; showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Profile")
        }
        if (showDialog) {
            FamilyProfileDialog(
                profile = editProfile,
                onDismiss = { showDialog = false },
                onSave = { p ->
                    if (editProfile == null) viewModel.addProfile(p) else viewModel.updateProfile(p)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun FamilyProfileDialog(
    profile: FamilyProfileEntity?,
    onDismiss: () -> Unit,
    onSave: (FamilyProfileEntity) -> Unit
) {
    var userId by remember { mutableStateOf(profile?.user_id?.toString() ?: "") }
    var address by remember { mutableStateOf(profile?.address ?: "") }
    var householdSize by remember { mutableStateOf(profile?.household_size?.toString() ?: "") }
    var notes by remember { mutableStateOf(profile?.notes ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (profile == null) "Add Profile" else "Edit Profile") },
        text = {
            Column {
                OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") })
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
                OutlinedTextField(value = householdSize, onValueChange = { householdSize = it }, label = { Text("Household Size") })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val profileObj = FamilyProfileEntity(
                    family_id = profile?.family_id ?: 0,
                    user_id = userId.toIntOrNull() ?: 0,
                    address = address,
                    household_size = householdSize.toIntOrNull(),
                    notes = notes
                )
                onSave(profileObj)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun BackgroundChecksScreen(viewModel: BackgroundChecksViewModel = viewModel()) {
    val checks by viewModel.checks.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editCheck by remember { mutableStateOf<BackgroundChecksEntity?>(null) }

    LaunchedEffect(Unit) { viewModel.loadChecks() }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { viewModel.loadChecks() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync")
                }
            }
            LazyColumn(Modifier.weight(1f)) {
                items(checks) { check ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { editCheck = check; showDialog = true }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text((check.status ?: "") + " - " + (check.result ?: ""), fontSize = 16.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.deleteCheck(check) }) {
                            Icon(painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { editCheck = null; showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Check")
        }
        if (showDialog) {
            BackgroundCheckDialog(
                check = editCheck,
                onDismiss = { showDialog = false },
                onSave = { c ->
                    if (editCheck == null) viewModel.addCheck(c) else viewModel.updateCheck(c)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun BackgroundCheckDialog(
    check: BackgroundChecksEntity?,
    onDismiss: () -> Unit,
    onSave: (BackgroundChecksEntity) -> Unit
) {
    var userId by remember { mutableStateOf(check?.user_id?.toString() ?: "") }
    var status by remember { mutableStateOf(check?.status ?: "") }
    var result by remember { mutableStateOf(check?.result ?: "") }
    var requestedAt by remember { mutableStateOf(check?.requested_at ?: "") }
    var completedAt by remember { mutableStateOf(check?.completed_at ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (check == null) "Add Check" else "Edit Check") },
        text = {
            Column {
                OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") })
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") })
                OutlinedTextField(value = result, onValueChange = { result = it }, label = { Text("Result") })
                OutlinedTextField(value = requestedAt, onValueChange = { requestedAt = it }, label = { Text("Requested At") })
                OutlinedTextField(value = completedAt, onValueChange = { completedAt = it }, label = { Text("Completed At") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val checkObj = BackgroundChecksEntity(
                    check_id = check?.check_id ?: 0,
                    user_id = userId.toIntOrNull() ?: 0,
                    status = status,
                    result = result,
                    requested_at = requestedAt,
                    completed_at = completedAt
                )
                onSave(checkObj)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AnalyticsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Analytics Dashboard", fontSize = 22.sp)
        Spacer(Modifier.height(16.dp))
        // Placeholder summary stats
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Children")
                Text("123", fontSize = 20.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Families")
                Text("45", fontSize = 20.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Placements")
                Text("67", fontSize = 20.sp)
            }
        }
        Spacer(Modifier.height(24.dp))
        // Placeholder for charts/graphs
        Text("Role Breakdown (Pie Chart)", fontSize = 16.sp)
        Box(Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray))
        Spacer(Modifier.height(16.dp))
        Text("Recent Activity (Bar Chart)", fontSize = 16.sp)
        Box(Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray))
    }
}

@Composable
fun NotificationsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Notifications", fontSize = 22.sp)
        Spacer(Modifier.height(16.dp))
        // Placeholder notification list
        LazyColumn {
            items(10) { idx ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Notification #$idx", fontSize = 16.sp)
                            Text("This is a sample notification.", fontSize = 12.sp)
                        }
                        Button(onClick = { /* Mark as read */ }) { Text("Read") }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDashboardScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Admin Dashboard", fontSize = 22.sp)
        Spacer(Modifier.height(16.dp))
        // Quick stats
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Dashboard, contentDescription = null)
                Text("System Health")
                Text("OK", color = Color.Green)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Notifications, contentDescription = null)
                Text("Pending Approvals")
                Text("3", color = Color.Red)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.BarChart, contentDescription = null)
                Text("Recent Logins")
                Text("12")
            }
        }
        Spacer(Modifier.height(24.dp))
        // Widgets
        Text("Audit Logs", fontSize = 16.sp)
        Box(Modifier.fillMaxWidth().height(80.dp).background(Color.LightGray))
        Spacer(Modifier.height(16.dp))
        Text("User Management", fontSize = 16.sp)
        Box(Modifier.fillMaxWidth().height(80.dp).background(Color.LightGray))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainNavHost(userRole = "admin")
} 