package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.session.AppSettings
import com.example.adoption_and_childcare.data.session.SessionManager
import kotlinx.coroutines.launch

/**
 * Screen for managing application settings and user profile.
 *
 * @param onBack Callback to navigate back to the home/dashboard screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val settings = remember { AppSettings(context) }
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var username by remember { mutableStateOf(TextFieldValue(session.username())) }
    var email by remember { mutableStateOf(TextFieldValue(session.email())) }
    var role by remember { mutableStateOf(session.role()) }

    // App Preferences
    var notificationsEnabled by remember { mutableStateOf(settings.notificationsEnabled) }
    var wifiOnlySync by remember { mutableStateOf(settings.wifiOnlySync) }
    
    // Network/API Settings
    var apiBaseUrl by remember { mutableStateOf(TextFieldValue(settings.apiBaseUrl)) }
    var apiTimeout by remember { mutableStateOf(settings.apiTimeout.toString()) }
    var apiRetryCount by remember { mutableStateOf(settings.apiRetryCount.toString()) }
    
    // Sync Settings
    var syncInterval by remember { mutableStateOf(settings.syncIntervalHours.toString()) }
    var autoSyncEnabled by remember { mutableStateOf(settings.autoSyncEnabled) }
    
    // File Upload Settings
    var maxFileSize by remember { mutableStateOf(settings.maxFileSizeMB.toString()) }
    var uploadTimeout by remember { mutableStateOf(settings.uploadTimeout.toString()) }
    
    // Security Settings
    var sessionTimeout by remember { mutableStateOf(settings.sessionTimeoutMinutes.toString()) }
    var maxLoginAttempts by remember { mutableStateOf(settings.maxLoginAttempts.toString()) }
    
    // UI Settings
    var themeMode by remember { mutableStateOf(settings.themeMode) }
    var showThemeDropdown by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf(settings.language) }
    var showLanguageDropdown by remember { mutableStateOf(false) }
    
    // Database Settings
    var useLocalDatabase by remember { mutableStateOf(settings.useLocalDatabase) }
    var localDatabasePath by remember { mutableStateOf(TextFieldValue(settings.localDatabasePath ?: "")) }
    
    // Debug Settings
    var debugMode by remember { mutableStateOf(settings.debugMode) }
    var enableLogging by remember { mutableStateOf(settings.enableLogging) }
    
    // SOS Emergency Contacts
    var policeNumber by remember { mutableStateOf(TextFieldValue(settings.getSosContact("police"))) }
    var fireDepartmentNumber by remember { mutableStateOf(TextFieldValue(settings.getSosContact("fire"))) }
    var hospitalNumber by remember { mutableStateOf(TextFieldValue(settings.getSosContact("hospital"))) }
    var cpsNumber by remember { mutableStateOf(TextFieldValue(settings.getSosContact("cps"))) }
    var emsNumber by remember { mutableStateOf(TextFieldValue(settings.getSosContact("ems"))) }
    var emergencyContact1Name by remember { mutableStateOf(TextFieldValue(settings.getSosContact("emergency1_name"))) }
    var emergencyContact1Phone by remember { mutableStateOf(TextFieldValue(settings.getSosContact("emergency1_phone"))) }
    var emergencyContact2Name by remember { mutableStateOf(TextFieldValue(settings.getSosContact("emergency2_name"))) }
    var emergencyContact2Phone by remember { mutableStateOf(TextFieldValue(settings.getSosContact("emergency2_phone"))) }

    val roles = listOf("Admin", "Case Worker", "Guardian", "Social Worker", "Supervisor", "Staff")
    val themeModes = listOf("System", "Light", "Dark")
    val languages = listOf("English", "Swahili", "French", "Spanish")

    var showRoleMenu by remember { mutableStateOf(false) }
    var showSaveSuccessMessage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Home, contentDescription = "Return to Home")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile section
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Profile", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenuBox(expanded = showRoleMenu, onExpandedChange = { showRoleMenu = !showRoleMenu }) {
                        OutlinedTextField(
                            value = role,
                            onValueChange = { },
                            label = { Text("Role") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRoleMenu) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showRoleMenu, onDismissRequest = { showRoleMenu = false }) {
                            roles.forEach { r ->
                                DropdownMenuItem(text = { Text(r) }, onClick = {
                                    role = r
                                    showRoleMenu = false
                                })
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(onClick = {
                            scope.launch {
                                val uid = session.userId()
                                if (uid > 0) {
                                    val user = db.userDao().findById(uid)
                                    if (user != null) {
                                        val updated = user.copy(username = username.text, email = email.text, role = role)
                                        db.userDao().update(updated)
                                        session.saveSession(updated)
                                        showSaveSuccessMessage = true
                                    }
                                }
                            }
                        }) {
                            Text("Save Profile")
                        }
                    }
                    if (showSaveSuccessMessage) {
                        Text(
                            text = "Profile saved successfully!",
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Network & API Configuration
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Cloud, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Network & API", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    OutlinedTextField(
                        value = apiBaseUrl,
                        onValueChange = { apiBaseUrl = it },
                        label = { Text("API Base URL") },
                        placeholder = { Text("http://192.168.43.197:50000/") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = apiTimeout,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() }) {
                                apiTimeout = it
                                settings.apiTimeout = it.toIntOrNull() ?: 30
                            }
                        },
                        label = { Text("API Timeout (seconds)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = apiRetryCount,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() }) {
                                apiRetryCount = it
                                settings.apiRetryCount = it.toIntOrNull() ?: 3
                            }
                        },
                        label = { Text("Retry Count") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            settings.apiBaseUrl = apiBaseUrl.text
                            settings.apiTimeout = apiTimeout.toIntOrNull() ?: 30
                            settings.apiRetryCount = apiRetryCount.toIntOrNull() ?: 3
                            showSaveSuccessMessage = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Network Settings")
                    }
                }
            }

            // Sync Configuration
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sync Configuration", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    OutlinedTextField(
                        value = syncInterval,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() }) {
                                syncInterval = it
                                settings.syncIntervalHours = it.toLongOrNull() ?: 6L
                            }
                        },
                        label = { Text("Sync Interval (hours)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Auto Sync", modifier = Modifier.weight(1f))
                        Switch(
                            checked = autoSyncEnabled,
                            onCheckedChange = {
                                autoSyncEnabled = it
                                settings.autoSyncEnabled = it
                            }
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Notifications", modifier = Modifier.weight(1f))
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                                settings.notificationsEnabled = it
                            }
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Wi-Fi Only Sync", modifier = Modifier.weight(1f))
                        Switch(
                            checked = wifiOnlySync,
                            onCheckedChange = {
                                wifiOnlySync = it
                                settings.wifiOnlySync = it
                            }
                        )
                    }
                    Button(
                        onClick = {
                            settings.syncIntervalHours = syncInterval.toLongOrNull() ?: 6L
                            showSaveSuccessMessage = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Sync Settings")
                    }
                }
            }

            // File Upload Configuration
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "File Upload", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    OutlinedTextField(
                        value = maxFileSize,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() }) {
                                maxFileSize = it
                                settings.maxFileSizeMB = it.toLongOrNull() ?: 10
                            }
                        },
                        label = { Text("Max File Size (MB)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uploadTimeout,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() }) {
                                uploadTimeout = it
                                settings.uploadTimeout = it.toIntOrNull() ?: 60
                            }
                        },
                        label = { Text("Upload Timeout (seconds)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            settings.maxFileSizeMB = maxFileSize.toLongOrNull() ?: 10
                            settings.uploadTimeout = uploadTimeout.toIntOrNull() ?: 60
                            showSaveSuccessMessage = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Upload Settings")
                    }
                }
            }

            // Security Configuration
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Security", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    OutlinedTextField(
                        value = sessionTimeout,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() }) {
                                sessionTimeout = it
                                settings.sessionTimeoutMinutes = it.toIntOrNull() ?: 30
                            }
                        },
                        label = { Text("Session Timeout (minutes)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = maxLoginAttempts,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() }) {
                                maxLoginAttempts = it
                                settings.maxLoginAttempts = it.toIntOrNull() ?: 5
                            }
                        },
                        label = { Text("Max Login Attempts") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            settings.sessionTimeoutMinutes = sessionTimeout.toIntOrNull() ?: 30
                            settings.maxLoginAttempts = maxLoginAttempts.toIntOrNull() ?: 5
                            showSaveSuccessMessage = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Security Settings")
                    }
                }
            }

            // UI Preferences
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "UI Preferences", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    ExposedDropdownMenuBox(expanded = showThemeDropdown, onExpandedChange = { showThemeDropdown = !showThemeDropdown }) {
                        OutlinedTextField(
                            value = themeMode,
                            onValueChange = { },
                            label = { Text("Theme") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showThemeDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showThemeDropdown, onDismissRequest = { showThemeDropdown = false }) {
                            themeModes.forEach { mode ->
                                DropdownMenuItem(text = { Text(mode) }, onClick = {
                                    themeMode = mode
                                    settings.themeMode = mode
                                    showThemeDropdown = false
                                })
                            }
                        }
                    }
                    
                    ExposedDropdownMenuBox(expanded = showLanguageDropdown, onExpandedChange = { showLanguageDropdown = !showLanguageDropdown }) {
                        OutlinedTextField(
                            value = language,
                            onValueChange = { },
                            label = { Text("Language") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showLanguageDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showLanguageDropdown, onDismissRequest = { showLanguageDropdown = false }) {
                            languages.forEach { lang ->
                                DropdownMenuItem(text = { Text(lang) }, onClick = {
                                    language = lang
                                    settings.language = lang
                                    showLanguageDropdown = false
                                })
                            }
                        }
                    }
                }
            }

            // Database Configuration
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Database", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Use Local Database", modifier = Modifier.weight(1f))
                        Switch(
                            checked = useLocalDatabase,
                            onCheckedChange = {
                                useLocalDatabase = it
                                settings.useLocalDatabase = it
                            }
                        )
                    }
                    OutlinedTextField(
                        value = localDatabasePath,
                        onValueChange = { localDatabasePath = it },
                        label = { Text("Local Database Path (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            settings.useLocalDatabase = useLocalDatabase
                            settings.localDatabasePath = localDatabasePath.text.takeIf { it.isNotEmpty() }
                            showSaveSuccessMessage = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Database Settings")
                    }
                }
            }

            // SOS Emergency Contacts
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE53935))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "SOS Emergency Contacts", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Text(
                        text = "These contacts will be used in emergency situations. Fill in all available fields.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Emergency Services
                    Text(
                        text = "Emergency Services",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = policeNumber,
                        onValueChange = { policeNumber = it },
                        label = { Text("Police") },
                        placeholder = { Text("911 or local police number") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocalPolice, contentDescription = null) }
                    )
                    
                    OutlinedTextField(
                        value = fireDepartmentNumber,
                        onValueChange = { fireDepartmentNumber = it },
                        label = { Text("Fire Department") },
                        placeholder = { Text("911 or fire department number") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocalFireDepartment, contentDescription = null) }
                    )
                    
                    OutlinedTextField(
                        value = hospitalNumber,
                        onValueChange = { hospitalNumber = it },
                        label = { Text("Hospital / Emergency Room") },
                        placeholder = { Text("Local hospital number") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) }
                    )
                    
                    OutlinedTextField(
                        value = cpsNumber,
                        onValueChange = { cpsNumber = it },
                        label = { Text("Child Protective Services (CPS)") },
                        placeholder = { Text("CPS hotline number") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.ChildCare, contentDescription = null) }
                    )
                    
                    OutlinedTextField(
                        value = emsNumber,
                        onValueChange = { emsNumber = it },
                        label = { Text("Emergency Medical Transport (EMS)") },
                        placeholder = { Text("911 or EMS number") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Ambulance, contentDescription = null) }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Personal Emergency Contacts
                    Text(
                        text = "Personal Emergency Contacts",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "Emergency Contact 1",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    OutlinedTextField(
                        value = emergencyContact1Name,
                        onValueChange = { emergencyContact1Name = it },
                        label = { Text("Name") },
                        placeholder = { Text("Contact name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = emergencyContact1Phone,
                        onValueChange = { emergencyContact1Phone = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("Phone number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Emergency Contact 2",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    OutlinedTextField(
                        value = emergencyContact2Name,
                        onValueChange = { emergencyContact2Name = it },
                        label = { Text("Name") },
                        placeholder = { Text("Contact name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = emergencyContact2Phone,
                        onValueChange = { emergencyContact2Phone = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("Phone number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Button(
                        onClick = {
                            settings.setSosContact("police", policeNumber.text)
                            settings.setSosContact("fire", fireDepartmentNumber.text)
                            settings.setSosContact("hospital", hospitalNumber.text)
                            settings.setSosContact("cps", cpsNumber.text)
                            settings.setSosContact("ems", emsNumber.text)
                            settings.setSosContact("emergency1_name", emergencyContact1Name.text)
                            settings.setSosContact("emergency1_phone", emergencyContact1Phone.text)
                            settings.setSosContact("emergency2_name", emergencyContact2Name.text)
                            settings.setSosContact("emergency2_phone", emergencyContact2Phone.text)
                            showSaveSuccessMessage = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Emergency Contacts")
                    }
                }
            }

            // Debug Settings
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Debug", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Debug Mode", modifier = Modifier.weight(1f))
                        Switch(
                            checked = debugMode,
                            onCheckedChange = {
                                debugMode = it
                                settings.debugMode = it
                            }
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Enable Logging", modifier = Modifier.weight(1f))
                        Switch(
                            checked = enableLogging,
                            onCheckedChange = {
                                enableLogging = it
                                settings.enableLogging = it
                            }
                        )
                    }
                }
            }

            // Legal and Resources section
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Legal & Resources", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    ListItem(
                        headlineContent = { Text("Privacy Policy") },
                        leadingContent = { Icon(Icons.Default.PrivacyTip, contentDescription = null) },
                        modifier = Modifier.clickable { /* TODO: Open Privacy Policy */ }
                    )
                    ListItem(
                        headlineContent = { Text("Terms of Service") },
                        leadingContent = { Icon(Icons.Default.Gavel, contentDescription = null) },
                        modifier = Modifier.clickable { /* TODO: Open Terms */ }
                    )
                    ListItem(
                        headlineContent = { Text("Agency Contact Info") },
                        leadingContent = { Icon(Icons.Default.Business, contentDescription = null) },
                        modifier = Modifier.clickable { /* TODO: Open Contact */ }
                    )
                    ListItem(
                        headlineContent = { Text("Report Abuse Form") },
                        leadingContent = { Icon(Icons.Default.Report, contentDescription = null) },
                        modifier = Modifier.clickable { /* TODO: Open Report form */ },
                        colors = ListItemDefaults.colors(headlineColor = MaterialTheme.colorScheme.error)
                    )
                }
            }

            // System Info
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "System Information", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Text("API Endpoint: ${settings.apiBaseUrl}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("Database: ${if (settings.useLocalDatabase) "Local" else "Remote"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("Sync: ${if (settings.autoSyncEnabled) "Enabled" else "Disabled"} (${settings.syncIntervalHours}h interval)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("Theme: ${themeMode}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            // Reset Settings Button
            Button(
                onClick = {
                    // Reset to defaults
                    settings.apiBaseUrl = "http://10.0.2.2:50000/"
                    settings.apiTimeout = 30
                    settings.apiRetryCount = 3
                    settings.syncIntervalHours = 6
                    settings.autoSyncEnabled = true
                    settings.notificationsEnabled = true
                    settings.wifiOnlySync = false
                    settings.maxFileSizeMB = 10
                    settings.uploadTimeout = 60
                    settings.sessionTimeoutMinutes = 30
                    settings.maxLoginAttempts = 5
                    settings.themeMode = "system"
                    settings.language = "en"
                    settings.useLocalDatabase = true
                    settings.debugMode = false
                    settings.enableLogging = true
                    // Reset SOS contacts
                    settings.setSosContact("police", "911")
                    settings.setSosContact("fire", "911")
                    settings.setSosContact("hospital", "")
                    settings.setSosContact("cps", "")
                    settings.setSosContact("ems", "911")
                    settings.setSosContact("emergency1_name", "")
                    settings.setSosContact("emergency1_phone", "")
                    settings.setSosContact("emergency2_name", "")
                    settings.setSosContact("emergency2_phone", "")
                    showSaveSuccessMessage = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray
                )
            ) {
                Text("Reset to Default Settings")
            }
        }
    }

    if (showSaveSuccessMessage) {
        LaunchedEffect(showSaveSuccessMessage) {
            kotlinx.coroutines.delay(2000)
            showSaveSuccessMessage = false
        }
    }
}
