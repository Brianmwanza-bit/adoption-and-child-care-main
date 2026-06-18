package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Screen displaying the case inbox with messages and notifications.
 */
@Composable
fun CaseInboxScreen() {
    val context = LocalContext.current
    var messages by remember { mutableStateOf(emptyList<Int>()) }
    var loading by remember { mutableStateOf(true) }

    val msgNewApp = R.string.msg_new_application
    val msgDocVerified = R.string.msg_document_verified
    val msgHomeVisit = R.string.msg_home_visit_reminder
    val msgSystemUpdate = R.string.msg_system_update
    val msgSupervisor = R.string.msg_supervisor_message

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance(context)
            // Get unread notifications for the admin user (ID 1 for now)
            db.notificationDao().getUnreadNotificationCount(1)
            // Simulate fetching real titles from the DB
            val list = listOf(
                msgNewApp,
                msgDocVerified,
                msgHomeVisit,
                msgSystemUpdate,
                msgSupervisor
            )
            withContext(Dispatchers.Main) {
                messages = list
                loading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(R.string.title_case_inbox),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(messages.size) {
                    val msgResId = messages[it]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mail, contentDescription = null, tint = Color(0xFF2196F3))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = stringResource(msgResId), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen displaying recent activities within the application.
 */
@Composable
fun RecentActivityScreen() {
    val context = LocalContext.current
    var activities by remember { mutableStateOf(emptyList<Pair<Int, Int?>>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance(context)
            val logs = db.auditLogDao().count() // Just checking count for now
            // Simulate dynamic activity list based on DB state
            val list = listOf(
                R.string.activity_updated_child_records to logs,
                R.string.activity_uploaded_home_studies to null,
                R.string.activity_completed_bg_checks to null,
                R.string.activity_system_audit to null
            )
            withContext(Dispatchers.Main) {
                activities = list
                loading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(R.string.title_recent_activity),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(activities.size) {
                    val (resId, count) = activities[it]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(16.dp))
                            val text = if (count != null) {
                                stringResource(resId, count)
                            } else {
                                stringResource(resId)
                            }
                            Text(text = text, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen displaying shortcuts to frequently accessed records or reports.
 */
@Composable
fun ShortcutsScreen() {
    val shortcuts = listOf(
        R.string.shortcut_active_placement,
        R.string.shortcut_daily_reports,
        R.string.shortcut_training_docs,
        R.string.shortcut_urgent_bg_checks
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(R.string.title_shortcuts),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(shortcuts.size) {
                val shortcutResId = shortcuts[it]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PushPin, contentDescription = null, tint = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = stringResource(shortcutResId), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

/**
 * Screen for viewing and managing application permissions.
 */
@Composable
fun AppPermissionsScreen() {
    val scrollState = rememberScrollState()
    val permissions = listOf(
        R.string.perm_view_child_records to true,
        R.string.perm_edit_case_reports to true,
        R.string.perm_manage_finance to false,
        R.string.perm_export_audit_logs to false,
        R.string.perm_approve_apps to false
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
        Text(
            text = stringResource(R.string.title_permissions),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                for (index in permissions.indices) {
                    val (permResId, granted) = permissions[index]
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(permResId), style = MaterialTheme.typography.bodyLarge)
                        Switch(checked = granted, onCheckedChange = null, enabled = false)
                    }
                    if (index != permissions.size - 1) {
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

/**
 * Screen providing information about the application and support contacts.
 */
@Composable
fun HelpAboutScreen() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ChildCare,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.app_title_full),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.app_version_format, com.example.adoption_and_childcare.BuildConfig.VERSION_NAME),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.label_support),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.label_email_format, stringResource(R.string.support_email)))
                Text(text = stringResource(R.string.label_phone_format, stringResource(R.string.support_phone)))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.legal_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}
