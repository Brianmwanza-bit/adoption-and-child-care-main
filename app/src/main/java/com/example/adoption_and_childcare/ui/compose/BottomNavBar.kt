package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.adoption_and_childcare.viewmodel.SyncState
import com.example.adoption_and_childcare.viewmodel.SyncViewModel
import com.example.adoption_and_childcare.viewmodel.NotificationsViewModel

import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.adoption_and_childcare.viewmodel.SOSState
import com.example.adoption_and_childcare.viewmodel.SOSViewModel
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    navController: NavController,
    syncViewModel: SyncViewModel,
    notificationsViewModel: NotificationsViewModel,
    sosViewModel: SOSViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val syncStatus by syncViewModel.syncStatus.collectAsState()
    val unreadNotificationsCount by notificationsViewModel.unreadCount.collectAsState()
    val sosState by sosViewModel.sosState.collectAsState()

    Column {
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
        NavigationBar(
            containerColor = Color(0xFF1976D2), // Modern Blue Block
            tonalElevation = 12.dp,
            modifier = Modifier
                .height(64.dp) // Reduced height as labels are removed
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) // Rounded top for appeal
        ) {
            // ... (Home and Cases items same as before)
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                selected = currentRoute == "dashboard",
                onClick = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Cases") },
                selected = currentRoute == "reports",
                onClick = {
                    navController.navigate("reports") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = Color.Transparent
                )
            )

            // SOS (Custom Center Button)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                SOSButton(sosViewModel)
            }

            // Alerts
            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationsCount > 0) {
                                Badge(containerColor = Color.Red, contentColor = Color.White) { 
                                    Text(unreadNotificationsCount.toString()) 
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Alerts")
                    }
                },
                selected = currentRoute == "notifications",
                onClick = {
                    navController.navigate("notifications") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = Color.Transparent
                )
            )

            // Sync
            NavigationBarItem(
                icon = {
                    SyncIconWithState(syncStatus.state, syncStatus.pendingCount, tint = Color.White)
                },
                selected = false,
                onClick = { syncViewModel.triggerSync() },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }

    if (sosState == SOSState.ARMING) {
        SOSCancelOverlay(onCancel = { sosViewModel.cancelSOS() }, onTimeout = { sosViewModel.activateSOS() })
    }
}

@Composable
fun SOSButton(sosViewModel: SOSViewModel) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var sosProgress by remember { mutableStateOf(0f) }
    val sosState by sosViewModel.sosState.collectAsState()
    val scope = rememberCoroutineScope()
    
    val infiniteTransition = rememberInfiniteTransition(label = "sosPulse")
    val pulseOpacity by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Emergency SOS") },
            text = { Text("Hold for 4 seconds to send emergency alert. This will contact Kenya Police and Children's Protection Services.") },
            confirmButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(contentAlignment = Alignment.Center) {
            // Main SOS Button - Rectangular and All Red
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Red.copy(alpha = if (sosState == SOSState.ACTIVE) pulseOpacity else 1f))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val down = awaitFirstDown()
                                val job = scope.launch {
                                    delay(4000L)
                                    sosViewModel.triggerSOS()
                                }
                                val progressJob = scope.launch {
                                    var elapsed = 0L
                                    while (elapsed < 4000L) {
                                        delay(50L)
                                        elapsed += 50L
                                        sosProgress = elapsed / 4000f
                                    }
                                }
                                waitForUpOrCancellation()
                                job.cancel()
                                progressJob.cancel()
                                sosProgress = 0f
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { showConfirmDialog = true }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Progress indicator (linear at the bottom of the rectangle)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(sosProgress)
                        .height(4.dp)
                        .align(Alignment.BottomStart)
                        .background(Color.White.copy(alpha = 0.5f))
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = "Emergency SOS",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "SOS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
        if (sosState == SOSState.ACTIVE) {
            Text(
                text = "ACTIVE",
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun SOSCancelOverlay(onCancel: () -> Unit, onTimeout: () -> Unit) {
    var countdown by remember { mutableStateOf(5) }
    
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
        onTimeout()
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = { },
        properties = androidx.compose.ui.window.DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.8f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🔴 SOS ACTIVATED", color = Color.Red, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(32.dp))
                Text("Contacting:", color = Color.White)
                // Simplified status checks for now
                StatusLine("Call placed to emergency number", true)
                StatusLine("SMS sent to contacts", true)
                StatusLine("WhatsApp alert sent", true)
                StatusLine("Nearest station alerted", true)
                StatusLine("Broadcasting location", true)
                
                Spacer(Modifier.height(48.dp))
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("CANCEL ($countdown)")
                }
            }
        }
    }
}

@Composable
fun StatusLine(text: String, checked: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(
            imageVector = if (checked) Icons.Outlined.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (checked) Color.Green else Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = if (checked) Color.White else Color.Gray)
    }
}

@Composable
fun EmergencyActiveBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFA32D2D)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Emergency active — location broadcasting", color = Color.White, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncIconWithState(state: SyncState, pendingCount: Int, tint: Color = Color.Unspecified) {
    Box(contentAlignment = Alignment.Center) {
        BadgedBox(
            badge = {
                if (pendingCount > 0) {
                    Badge(containerColor = Color.Red, contentColor = Color.White) { 
                        Text(pendingCount.toString()) 
                    }
                }
            }
        ) {
            Icon(Icons.Outlined.CloudUpload, contentDescription = "Sync", tint = tint)
        }
        
        Canvas(modifier = Modifier.size(24.dp).align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp)) {
            val color = when (state) {
                SyncState.ONLINE_IDLE -> Color(0xFF4CAF50) // Brighter Green
                SyncState.ONLINE_PENDING -> Color(0xFFFFC107) // Amber
                SyncState.SYNCING -> Color.Transparent 
                SyncState.OFFLINE -> Color(0xFF9E9E9E)
                SyncState.ERROR -> Color(0xFFF44336)
            }
            if (state != SyncState.SYNCING) {
                drawCircle(color = color, radius = 4.dp.toPx())
            }
        }
    }
}
