package com.adoptionapp.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.adoptionapp.data.db.AppDatabase
import com.adoptionapp.data.db.entities.UserEntity
import com.adoptionapp.data.session.AppSettings
import com.adoptionapp.data.session.SessionManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val settings = remember { AppSettings(context) }
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf(TextFieldValue(session.username())) }
    var email by remember { mutableStateOf(TextFieldValue(session.email())) }
    var role by remember { mutableStateOf(session.role()) }

    var notificationsEnabled by remember { mutableStateOf(settings.notificationsEnabled) }
    var wifiOnlySync by remember { mutableStateOf(settings.wifiOnlySync) }

    val roles = listOf("Admin", "Case Worker", "Foster Parent", "Social Worker", "Supervisor")
    var showRoleMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)

        // Profile section
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Profile", style = MaterialTheme.typography.titleMedium)
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
                                    // Update session to reflect changes
                                    session.saveSession(updated)
                                }
                            }
                        }
                    }) {
                        Text("Save Profile")
                    }
                }
            }
        }

        // App settings section
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "App Preferences", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Enable notifications", modifier = Modifier.weight(1f))
                    Switch(checked = notificationsEnabled, onCheckedChange = {
                        notificationsEnabled = it
                        settings.notificationsEnabled = it
                    })
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Wi‑Fi only sync", modifier = Modifier.weight(1f))
                    Switch(checked = wifiOnlySync, onCheckedChange = {
                        wifiOnlySync = it
                        settings.wifiOnlySync = it
                    })
                }
            }
        }
    }
}
