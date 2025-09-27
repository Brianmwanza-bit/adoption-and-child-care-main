package com.adoptionapp.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptionapp.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel = viewModel()) {
    LaunchedEffect(Unit) { viewModel.loadNotifications() }
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Notifications", modifier = Modifier.align(Alignment.CenterHorizontally).semantics { contentDescription = "Notifications Title" })
        if (loading) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        } else if (notifications.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                Text("No notifications.")
            }
        } else {
            Text("Unread: $unreadCount", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { showConfirm = true }, modifier = Modifier.align(Alignment.End)) {
                Text("Mark All as Read")
            }
            notifications.forEach { notification ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(vertical = 4.dp)
                        .clickable { if (!notification.is_read) viewModel.markAsRead(notification.notification_id) }
                        .semantics { contentDescription = if (notification.is_read) "Read Notification" else "Unread Notification" },
                    colors = if (notification.is_read) CardDefaults.cardColors() else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                        Text(notification.message, modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }
        }
    }
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Mark All as Read?") },
            text = { Text("Are you sure you want to mark all notifications as read?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.markAllAsRead()
                    showConfirm = false
                }) { Text("Yes") }
            },
            dismissButton = {
                Button(onClick = { showConfirm = false }) { Text("No") }
            }
        )
    }
} 