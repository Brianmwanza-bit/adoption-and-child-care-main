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

    val roles = listOf("Admin", "Case Worker", "Foster Parent", "Social Worker", "Supervisor", "Staff")
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
                    settings.apiBaseUrl = "http://192.168.43.197:50000/"
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
