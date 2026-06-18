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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.session.AppSettings
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.viewmodel.SettingsViewModel
import com.yourdomain.adoptionchildcare.R
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

// Internal Constants to avoid hardcoded strings and lint warnings
private const val DEFAULT_API_BASE_URL = "http://10.0.2.2:50000/"
private const val EMERGENCY_911 = "911"

/**
 * Screen for managing application settings and user profile.
 * Room DB is primary storage, Remote API is secondary (live sync).
 *
 * @param onBack Callback to navigate back to the home/dashboard screen.
 * @param viewModel ViewModel for settings management provided via Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val settings = remember { AppSettings(context) }
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val roomSettings by viewModel.settingsFlow.collectAsState()

    // Key Resources
    val keyPoliceStr = stringResource(R.string.key_police)
    val keyFireStr = stringResource(R.string.key_fire)
    val keyHospitalStr = stringResource(R.string.key_hospital)
    val keyCpsStr = stringResource(R.string.key_cps)
    val keyEmsStr = stringResource(R.string.key_ems)
    val keyEmergency1NameStr = stringResource(R.string.key_emergency1_name)
    val keyEmergency1PhoneStr = stringResource(R.string.key_emergency1_phone)
    val keyEmergency2NameStr = stringResource(R.string.key_emergency2_name)
    val keyEmergency2PhoneStr = stringResource(R.string.key_emergency2_phone)
    val valThemeSystemStr = stringResource(R.string.val_theme_system)
    val valLangEnStr = stringResource(R.string.val_lang_en)

    // Category and Key Resources for Room Sync
    val catNetworkStr = stringResource(R.string.cat_network)
    val catSyncStr = stringResource(R.string.cat_sync)
    val catUploadStr = stringResource(R.string.cat_upload)
    val catSecurityStr = stringResource(R.string.cat_security)
    val catUiStr = stringResource(R.string.cat_ui)
    val catDatabaseStr = stringResource(R.string.cat_database)
    val catSosStr = stringResource(R.string.cat_sos)
    val catDebugStr = stringResource(R.string.cat_debug)

    val keyApiUrlStr = stringResource(R.string.key_api_url)
    val keyAutoSyncStr = stringResource(R.string.key_auto_sync)
    val keyNotifEnabledStr = stringResource(R.string.key_notif_enabled)
    val keyWifiSyncStr = stringResource(R.string.key_wifi_sync)
    val keySyncIntervalStr = stringResource(R.string.key_sync_interval)
    val keyMaxSizeStr = stringResource(R.string.key_max_size)
    val keySessionTimeoutStr = stringResource(R.string.key_session_timeout)
    val keyThemeStr = stringResource(R.string.key_theme)
    val keyLangStr = stringResource(R.string.key_lang)
    val keyLocalDbStr = stringResource(R.string.key_local_db)
    val keyDbPathStr = stringResource(R.string.key_db_path)
    val keyDebugStr = stringResource(R.string.key_debug)
    val keyLoggingStr = stringResource(R.string.key_logging)

    var usernameState by remember { mutableStateOf(TextFieldValue(session.username())) }
    var emailState by remember { mutableStateOf(TextFieldValue(session.email())) }
    var roleState by remember { mutableStateOf(session.role()) }

    // App Preferences
    var notificationsEnabledState by remember { mutableStateOf(settings.notificationsEnabled) }
    var wifiOnlySyncState by remember { mutableStateOf(settings.wifiOnlySync) }
    
    // Network/API Settings
    var apiBaseUrlState by remember { mutableStateOf(TextFieldValue(settings.apiBaseUrl)) }
    var apiTimeoutState by remember { mutableStateOf(settings.apiTimeout.toString()) }
    var apiRetryCountState by remember { mutableStateOf(settings.apiRetryCount.toString()) }
    
    // Sync Settings
    var syncIntervalState by remember { mutableStateOf(settings.syncIntervalHours.toString()) }
    var autoSyncEnabledState by remember { mutableStateOf(settings.autoSyncEnabled) }
    
    // File Upload Settings
    var maxFileSizeState by remember { mutableStateOf(settings.maxFileSizeMB.toString()) }
    var uploadTimeoutState by remember { mutableStateOf(settings.uploadTimeout.toString()) }
    
    // Security Settings
    var sessionTimeoutState by remember { mutableStateOf(settings.sessionTimeoutMinutes.toString()) }
    var maxLoginAttemptsState by remember { mutableStateOf(settings.maxLoginAttempts.toString()) }
    
    // UI Settings
    var themeModeState by remember { mutableStateOf(settings.themeMode) }
    var showThemeDropdownState by remember { mutableStateOf(false) }
    var languageState by remember { mutableStateOf(settings.language) }
    var showLanguageDropdownState by remember { mutableStateOf(false) }
    
    // Database Settings
    var useLocalDatabaseState by remember { mutableStateOf(settings.useLocalDatabase) }
    var localDatabasePathState by remember { mutableStateOf(TextFieldValue(settings.localDatabasePath ?: "")) }
    
    // Debug Settings
    var debugModeState by remember { mutableStateOf(settings.debugMode) }
    var enableLoggingState by remember { mutableStateOf(settings.enableLogging) }
    
    // SOS Emergency Contacts
    var policeNumberState by remember { mutableStateOf(TextFieldValue(settings.getSosContact(keyPoliceStr))) }
    var fireDepartmentNumberState by remember { mutableStateOf(TextFieldValue(settings.getSosContact(keyFireStr))) }
    var hospitalNumberState by remember { mutableStateOf(TextFieldValue(settings.getSosContact(keyHospitalStr))) }
    var cpsNumberState by remember { mutableStateOf(TextFieldValue(settings.getSosContact(keyCpsStr))) }
    var emsNumberState by remember { mutableStateOf(TextFieldValue(settings.getSosContact(keyEmsStr))) }
    var emergencyContact1NameState by remember { mutableStateOf(TextFieldValue(settings.getSosContact(keyEmergency1NameStr))) }
    var emergencyContact1PhoneState by remember { mutableStateOf(TextFieldValue(settings.getSosContact(keyEmergency1PhoneStr))) }
    var emergencyContact2NameState by remember { mutableStateOf(TextFieldValue(settings.getSosContact(keyEmergency2NameStr))) }
    var emergencyContact2PhoneState by remember { mutableStateOf(TextFieldValue(settings.getSosContact(keyEmergency2PhoneStr))) }

    val rolesList = listOf(
        stringResource(R.string.role_admin),
        stringResource(R.string.role_case_worker),
        stringResource(R.string.role_guardian_short),
        stringResource(R.string.role_social_worker),
        stringResource(R.string.role_supervisor),
        stringResource(R.string.role_staff)
    )
    val themeModesList = listOf(
        stringResource(R.string.theme_system),
        stringResource(R.string.theme_light),
        stringResource(R.string.theme_dark)
    )
    val languagesList = listOf(
        stringResource(R.string.lang_english),
        stringResource(R.string.lang_swahili),
        stringResource(R.string.lang_french),
        stringResource(R.string.lang_spanish)
    )

    var showRoleMenuState by remember { mutableStateOf(false) }
    var showSaveSuccessMessageState by remember { mutableStateOf(false) }

    // Sync UI from Room Primary Storage
    LaunchedEffect(roomSettings) {
        roomSettings.forEach { settingItem ->
            when (settingItem.settingKey) {
                keyApiUrlStr -> {
                    apiBaseUrlState = TextFieldValue(settingItem.settingValue ?: DEFAULT_API_BASE_URL)
                    settings.apiBaseUrl = settingItem.settingValue ?: DEFAULT_API_BASE_URL
                }
                keyAutoSyncStr -> {
                    autoSyncEnabledState = settingItem.settingValue?.toBoolean() ?: true
                    settings.autoSyncEnabled = autoSyncEnabledState
                }
                keyNotifEnabledStr -> {
                    notificationsEnabledState = settingItem.settingValue?.toBoolean() ?: true
                    settings.notificationsEnabled = notificationsEnabledState
                }
                keyWifiSyncStr -> {
                    wifiOnlySyncState = settingItem.settingValue?.toBoolean() ?: false
                    settings.wifiOnlySync = wifiOnlySyncState
                }
                keySyncIntervalStr -> {
                    syncIntervalState = settingItem.settingValue ?: "6"
                    settings.syncIntervalHours = syncIntervalState.toLongOrNull() ?: 6L
                }
                keyThemeStr -> {
                    themeModeState = settingItem.settingValue ?: valThemeSystemStr
                    settings.themeMode = themeModeState
                }
                keyLangStr -> {
                    languageState = settingItem.settingValue ?: valLangEnStr
                    settings.language = languageState
                }
                keyPoliceStr -> policeNumberState = TextFieldValue(settingItem.settingValue ?: "")
                keyFireStr -> fireDepartmentNumberState = TextFieldValue(settingItem.settingValue ?: "")
                keyHospitalStr -> hospitalNumberState = TextFieldValue(settingItem.settingValue ?: "")
                keyCpsStr -> cpsNumberState = TextFieldValue(settingItem.settingValue ?: "")
                keyEmsStr -> emsNumberState = TextFieldValue(settingItem.settingValue ?: "")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Home, contentDescription = stringResource(R.string.settings_back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
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
                        Text(text = stringResource(R.string.settings_profile_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    OutlinedTextField(
                        value = usernameState,
                        onValueChange = { usernameState = it },
                        label = { Text(stringResource(R.string.settings_username_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = emailState,
                        onValueChange = { emailState = it },
                        label = { Text(stringResource(R.string.settings_email_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenuBox(expanded = showRoleMenuState, onExpandedChange = { showRoleMenuState = !showRoleMenuState }) {
                        OutlinedTextField(
                            value = roleState,
                            onValueChange = { },
                            label = { Text(stringResource(R.string.settings_role_label)) },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRoleMenuState) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showRoleMenuState, onDismissRequest = { showRoleMenuState = false }) {
                            rolesList.forEach { roleOption ->
                                DropdownMenuItem(text = { Text(roleOption) }, onClick = {
                                    roleState = roleOption
                                    showRoleMenuState = false
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
                                        val updated = user.copy(username = usernameState.text, email = emailState.text, role = roleState)
                                        db.userDao().update(updated)
                                        session.saveSession(updated)
                                        showSaveSuccessMessageState = true
                                    }
                                }
                            }
                        }) {
                            Text(stringResource(R.string.settings_save_profile))
                        }
                    }
                    if (showSaveSuccessMessageState) {
                        Text(
                            text = stringResource(R.string.settings_profile_saved),
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
                        Text(text = stringResource(R.string.settings_network_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    OutlinedTextField(
                        value = apiBaseUrlState,
                        onValueChange = { apiBaseUrlState = it },
                        label = { Text(stringResource(R.string.settings_api_url_label)) },
                        placeholder = { Text(stringResource(R.string.settings_api_url_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = apiTimeoutState,
                        onValueChange = { timeoutText ->
                            if (timeoutText.all { it.isDigit() }) { apiTimeoutState = timeoutText }
                        },
                        label = { Text(stringResource(R.string.settings_api_timeout_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = apiRetryCountState,
                        onValueChange = { retryCountText ->
                            if (retryCountText.all { it.isDigit() }) { apiRetryCountState = retryCountText }
                        },
                        label = { Text(stringResource(R.string.settings_retry_count_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            settings.apiBaseUrl = apiBaseUrlState.text
                            settings.apiTimeout = apiTimeoutState.toIntOrNull() ?: 30
                            settings.apiRetryCount = apiRetryCountState.toIntOrNull() ?: 3
                            viewModel.saveSetting(keyApiUrlStr, apiBaseUrlState.text, catNetworkStr)
                            showSaveSuccessMessageState = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.settings_save_network))
                    }
                }
            }

            // Sync Configuration
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.settings_sync_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    OutlinedTextField(
                        value = syncIntervalState,
                        onValueChange = { intervalText ->
                            if (intervalText.all { it.isDigit() }) { syncIntervalState = intervalText }
                        },
                        label = { Text(stringResource(R.string.settings_sync_interval_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_auto_sync_label), modifier = Modifier.weight(1f))
                        Switch(
                            checked = autoSyncEnabledState,
                            onCheckedChange = { isAutoSyncEnabled ->
                                autoSyncEnabledState = isAutoSyncEnabled
                                settings.autoSyncEnabled = isAutoSyncEnabled
                                viewModel.saveSetting(keyAutoSyncStr, isAutoSyncEnabled.toString(), catSyncStr)
                            }
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_notifications_label), modifier = Modifier.weight(1f))
                        Switch(
                            checked = notificationsEnabledState,
                            onCheckedChange = { isNotifEnabled ->
                                notificationsEnabledState = isNotifEnabled
                                settings.notificationsEnabled = isNotifEnabled
                                viewModel.saveSetting(keyNotifEnabledStr, isNotifEnabled.toString(), catSyncStr)
                            }
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_wifi_only_label), modifier = Modifier.weight(1f))
                        Switch(
                            checked = wifiOnlySyncState,
                            onCheckedChange = { isWifiOnly ->
                                wifiOnlySyncState = isWifiOnly
                                settings.wifiOnlySync = isWifiOnly
                                viewModel.saveSetting(keyWifiSyncStr, isWifiOnly.toString(), catSyncStr)
                            }
                        )
                    }
                    Button(
                        onClick = {
                            settings.syncIntervalHours = syncIntervalState.toLongOrNull() ?: 6L
                            viewModel.saveSetting(keySyncIntervalStr, syncIntervalState, catSyncStr)
                            showSaveSuccessMessageState = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.settings_save_sync))
                    }
                }
            }

            // File Upload Configuration
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.settings_upload_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    OutlinedTextField(
                        value = maxFileSizeState,
                        onValueChange = { maxFileSizeText ->
                            if (maxFileSizeText.all { it.isDigit() }) { maxFileSizeState = maxFileSizeText }
                        },
                        label = { Text(stringResource(R.string.settings_max_file_size_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uploadTimeoutState,
                        onValueChange = { uploadTimeoutText ->
                            if (uploadTimeoutText.all { it.isDigit() }) { uploadTimeoutState = uploadTimeoutText }
                        },
                        label = { Text(stringResource(R.string.settings_upload_timeout_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            settings.maxFileSizeMB = maxFileSizeState.toLongOrNull() ?: 10
                            settings.uploadTimeout = uploadTimeoutState.toIntOrNull() ?: 60
                            viewModel.saveSetting(keyMaxSizeStr, maxFileSizeState, catUploadStr)
                            showSaveSuccessMessageState = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.settings_save_upload))
                    }
                }
            }

            // Security Configuration
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.settings_security_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    OutlinedTextField(
                        value = sessionTimeoutState,
                        onValueChange = { sessionTimeoutText ->
                            if (sessionTimeoutText.all { it.isDigit() }) { sessionTimeoutState = sessionTimeoutText }
                        },
                        label = { Text(stringResource(R.string.settings_session_timeout_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = maxLoginAttemptsState,
                        onValueChange = { maxLoginText ->
                            if (maxLoginText.all { it.isDigit() }) { maxLoginAttemptsState = maxLoginText }
                        },
                        label = { Text(stringResource(R.string.settings_max_login_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            settings.sessionTimeoutMinutes = sessionTimeoutState.toIntOrNull() ?: 30
                            settings.maxLoginAttempts = maxLoginAttemptsState.toIntOrNull() ?: 5
                            viewModel.saveSetting(keySessionTimeoutStr, sessionTimeoutState, catSecurityStr)
                            showSaveSuccessMessageState = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.settings_save_security))
                    }
                }
            }

            // UI Preferences
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.settings_ui_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    ExposedDropdownMenuBox(expanded = showThemeDropdownState, onExpandedChange = { showThemeDropdownState = it }) {
                        OutlinedTextField(
                            value = themeModeState,
                            onValueChange = { },
                            label = { Text(stringResource(R.string.settings_theme_label)) },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showThemeDropdownState) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showThemeDropdownState, onDismissRequest = { showThemeDropdownState = false }) {
                            themeModesList.forEach { themeOptionItem ->
                                DropdownMenuItem(text = { Text(themeOptionItem) }, onClick = {
                                    themeModeState = themeOptionItem
                                    settings.themeMode = themeOptionItem
                                    viewModel.saveSetting(keyThemeStr, themeOptionItem, catUiStr)
                                    showThemeDropdownState = false
                                })
                            }
                        }
                    }
                    
                    ExposedDropdownMenuBox(expanded = showLanguageDropdownState, onExpandedChange = { showLanguageDropdownState = it }) {
                        OutlinedTextField(
                            value = languageState,
                            onValueChange = { },
                            label = { Text(stringResource(R.string.settings_language_label)) },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showLanguageDropdownState) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = showLanguageDropdownState, onDismissRequest = { showLanguageDropdownState = false }) {
                            languagesList.forEach { languageOptionItem ->
                                DropdownMenuItem(text = { Text(languageOptionItem) }, onClick = {
                                    languageState = languageOptionItem
                                    settings.language = languageOptionItem
                                    viewModel.saveSetting(keyLangStr, languageOptionItem, catUiStr)
                                    showLanguageDropdownState = false
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
                        Text(text = stringResource(R.string.settings_database_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_use_local_db), modifier = Modifier.weight(1f))
                        Switch(
                            checked = useLocalDatabaseState,
                            onCheckedChange = { isLocalDbEnabled ->
                                useLocalDatabaseState = isLocalDbEnabled
                                settings.useLocalDatabase = isLocalDbEnabled
                                viewModel.saveSetting(keyLocalDbStr, isLocalDbEnabled.toString(), catDatabaseStr)
                            }
                        )
                    }
                    OutlinedTextField(
                        value = localDatabasePathState,
                        onValueChange = { localDatabasePathState = it },
                        label = { Text(stringResource(R.string.settings_local_db_path_label)) },
                        placeholder = { Text(stringResource(R.string.settings_local_db_path_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            settings.useLocalDatabase = useLocalDatabaseState
                            settings.localDatabasePath = localDatabasePathState.text.takeIf { it.isNotEmpty() }
                            viewModel.saveSetting(keyDbPathStr, localDatabasePathState.text, catDatabaseStr)
                            showSaveSuccessMessageState = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.settings_save_db))
                    }
                }
            }

            // SOS Emergency Contacts
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE53935))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.settings_sos_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Text(
                        text = stringResource(R.string.settings_sos_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Emergency Services
                    Text(
                        text = stringResource(R.string.settings_emergency_services),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = policeNumberState,
                        onValueChange = { policeNumberState = it },
                        label = { Text(stringResource(R.string.settings_police_label)) },
                        placeholder = { Text(stringResource(R.string.settings_police_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocalPolice, contentDescription = null) }
                    )
                    
                    OutlinedTextField(
                        value = fireDepartmentNumberState,
                        onValueChange = { fireDepartmentNumberState = it },
                        label = { Text(stringResource(R.string.settings_fire_label)) },
                        placeholder = { Text(stringResource(R.string.settings_fire_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocalFireDepartment, contentDescription = null) }
                    )
                    
                    OutlinedTextField(
                        value = hospitalNumberState,
                        onValueChange = { hospitalNumberState = it },
                        label = { Text(stringResource(R.string.settings_hospital_label)) },
                        placeholder = { Text(stringResource(R.string.settings_hospital_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) }
                    )
                    
                    OutlinedTextField(
                        value = cpsNumberState,
                        onValueChange = { cpsNumberState = it },
                        label = { Text(stringResource(R.string.settings_cps_label)) },
                        placeholder = { Text(stringResource(R.string.settings_cps_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.ChildCare, contentDescription = null) }
                    )
                    
                    OutlinedTextField(
                        value = emsNumberState,
                        onValueChange = { emsNumberState = it },
                        label = { Text(stringResource(R.string.settings_ems_label)) },
                        placeholder = { Text(stringResource(R.string.settings_ems_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null) }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Personal Emergency Contacts
                    Text(
                        text = stringResource(R.string.settings_personal_contacts),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = stringResource(R.string.settings_contact_1),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    OutlinedTextField(
                        value = emergencyContact1NameState,
                        onValueChange = { emergencyContact1NameState = it },
                        label = { Text(stringResource(R.string.settings_name_label)) },
                        placeholder = { Text(stringResource(R.string.settings_name_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = emergencyContact1PhoneState,
                        onValueChange = { emergencyContact1PhoneState = it },
                        label = { Text(stringResource(R.string.settings_phone_label)) },
                        placeholder = { Text(stringResource(R.string.settings_phone_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = stringResource(R.string.settings_contact_2),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    OutlinedTextField(
                        value = emergencyContact2NameState,
                        onValueChange = { emergencyContact2NameState = it },
                        label = { Text(stringResource(R.string.settings_name_label)) },
                        placeholder = { Text(stringResource(R.string.settings_name_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = emergencyContact2PhoneState,
                        onValueChange = { emergencyContact2PhoneState = it },
                        label = { Text(stringResource(R.string.settings_phone_label)) },
                        placeholder = { Text(stringResource(R.string.settings_phone_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Button(
                        onClick = {
                            settings.setSosContact(keyPoliceStr, policeNumberState.text)
                            settings.setSosContact(keyFireStr, fireDepartmentNumberState.text)
                            settings.setSosContact(keyHospitalStr, hospitalNumberState.text)
                            settings.setSosContact(keyCpsStr, cpsNumberState.text)
                            settings.setSosContact(keyEmsStr, emsNumberState.text)
                            settings.setSosContact(keyEmergency1NameStr, emergencyContact1NameState.text)
                            settings.setSosContact(keyEmergency1PhoneStr, emergencyContact1PhoneState.text)
                            settings.setSosContact(keyEmergency2NameStr, emergencyContact2NameState.text)
                            settings.setSosContact(keyEmergency2PhoneStr, emergencyContact2PhoneState.text)
                            
                            viewModel.saveSetting(keyPoliceStr, policeNumberState.text, catSosStr)
                            viewModel.saveSetting(keyFireStr, fireDepartmentNumberState.text, catSosStr)
                            viewModel.saveSetting(keyHospitalStr, hospitalNumberState.text, catSosStr)
                            viewModel.saveSetting(keyCpsStr, cpsNumberState.text, catSosStr)
                            viewModel.saveSetting(keyEmsStr, emsNumberState.text, catSosStr)
                            viewModel.saveSetting(keyEmergency1NameStr, emergencyContact1NameState.text, catSosStr)
                            viewModel.saveSetting(keyEmergency1PhoneStr, emergencyContact1PhoneState.text, catSosStr)
                            viewModel.saveSetting(keyEmergency2NameStr, emergencyContact2NameState.text, catSosStr)
                            viewModel.saveSetting(keyEmergency2PhoneStr, emergencyContact2PhoneState.text, catSosStr)
                            
                            showSaveSuccessMessageState = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.settings_save_sos))
                    }
                }
            }

            // Debug Settings
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.settings_debug_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_debug_mode), modifier = Modifier.weight(1f))
                        Switch(
                            checked = debugModeState,
                            onCheckedChange = { isDebugEnabled ->
                                debugModeState = isDebugEnabled
                                settings.debugMode = isDebugEnabled
                                viewModel.saveSetting(keyDebugStr, isDebugEnabled.toString(), catDebugStr)
                            }
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_enable_logging), modifier = Modifier.weight(1f))
                        Switch(
                            checked = enableLoggingState,
                            onCheckedChange = { isLoggingEnabled ->
                                enableLoggingState = isLoggingEnabled
                                settings.enableLogging = isLoggingEnabled
                                viewModel.saveSetting(keyLoggingStr, isLoggingEnabled.toString(), catDebugStr)
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
                        Text(text = stringResource(R.string.settings_legal_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_privacy_policy)) },
                        leadingContent = { Icon(Icons.Default.PrivacyTip, contentDescription = null) },
                        modifier = Modifier.clickable { /* TODO: Open Privacy Policy */ }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_terms_service)) },
                        leadingContent = { Icon(Icons.Default.Gavel, contentDescription = null) },
                        modifier = Modifier.clickable { /* TODO: Open Terms */ }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_agency_info)) },
                        leadingContent = { Icon(Icons.Default.Business, contentDescription = null) },
                        modifier = Modifier.clickable { /* TODO: Open Contact */ }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_report_abuse)) },
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
                        Text(text = stringResource(R.string.settings_info_section), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Text(
                        text = stringResource(R.string.settings_info_api, settings.apiBaseUrl),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = stringResource(
                            R.string.settings_info_db,
                            if (settings.useLocalDatabase) stringResource(R.string.settings_db_local) else stringResource(R.string.settings_db_remote)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = stringResource(
                            R.string.settings_info_sync,
                            if (settings.autoSyncEnabled) stringResource(R.string.settings_enabled) else stringResource(R.string.settings_disabled),
                            settings.syncIntervalHours
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = stringResource(R.string.settings_info_theme, themeModeState),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Reset Settings Button
            Button(
                onClick = {
                    // Reset to defaults
                    settings.apiBaseUrl = DEFAULT_API_BASE_URL
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
                    settings.themeMode = valThemeSystemStr
                    settings.language = valLangEnStr
                    settings.useLocalDatabase = true
                    settings.debugMode = false
                    settings.enableLogging = true
                    // Reset SOS contacts
                    settings.setSosContact(keyPoliceStr, EMERGENCY_911)
                    settings.setSosContact(keyFireStr, EMERGENCY_911)
                    settings.setSosContact(keyHospitalStr, "")
                    settings.setSosContact(keyCpsStr, "")
                    settings.setSosContact(keyEmsStr, EMERGENCY_911)
                    settings.setSosContact(keyEmergency1NameStr, "")
                    settings.setSosContact(keyEmergency1PhoneStr, "")
                    settings.setSosContact(keyEmergency2NameStr, "")
                    settings.setSosContact(keyEmergency2PhoneStr, "")
                    
                    viewModel.resetSettings()

                    showSaveSuccessMessageState = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray
                )
            ) {
                Text(stringResource(R.string.settings_reset_button))
            }
        }
    }

    if (showSaveSuccessMessageState) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2.seconds)
            showSaveSuccessMessageState = false
        }
    }
}
