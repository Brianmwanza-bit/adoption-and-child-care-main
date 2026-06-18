package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * High-detail In-App Map Screen using Google Maps.
 * 
 * Features interactive markers for children, families, and facilities,
 * and a distance analysis tool for placement stability.
 * 
 * @param onBack Callback for navigating back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(onBack: () -> Unit) {
    val nairobi = LatLng(-1.286389, 36.817223)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(nairobi, 11f)
    }

    var selectedLocationName by remember { mutableStateOf<String?>(null) }
    var showDistanceTool by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Case Management Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDistanceTool = !showDistanceTool }) {
                        Icon(
                            if (showDistanceTool) Icons.Default.Straighten else Icons.Default.Map,
                            contentDescription = "Distance Tool"
                        )
                    }
                    IconButton(onClick = { /* Refresh Markers */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Google Map View
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = false
                )
            ) {
                // Mock Children Markers
                Marker(
                    state = rememberMarkerState(position = LatLng(-1.2921, 36.8219)),
                    title = "Child: John Doe",
                    snippet = "Case #1024 - Active",
                    onClick = {
                        selectedLocationName = "John Doe"
                        false
                    }
                )

                Marker(
                    state = rememberMarkerState(position = LatLng(-1.2750, 36.8010)),
                    title = "Child: Jane Smith",
                    snippet = "Case #1025 - Pending",
                    onClick = {
                        selectedLocationName = "Jane Smith"
                        false
                    }
                )

                // Mock Family Markers
                Marker(
                    state = rememberMarkerState(position = LatLng(-1.3000, 36.7800)),
                    title = "Family: Kamau Family",
                    snippet = "Licensed Foster Home",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    onClick = {
                        selectedLocationName = "Kamau Family"
                        false
                    }
                )
            }

            // Overlay UI
            
            // Layer Toggles
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LayerToggle("Children", true, Color(0xFFE91E63))
                LayerToggle("Foster Homes", true, Color(0xFF2196F3))
                LayerToggle("Schools", false, Color(0xFFFF9800))
            }

            // Distance Tool UI
            if (showDistanceTool) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Straighten, contentDescription = null, tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Placement Stability Analysis", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Distance from School of Origin to Kamau Family Home: 4.2 km",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Estimated commute time: 12 mins",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { 0.9f },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            "STABILITY RATING: HIGH",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Selected Location Info
            selectedLocationName?.let { name ->
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .width(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("View Details", style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50))
                        }
                        IconButton(onClick = { selectedLocationName = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LayerToggle(label: String, isEnabled: Boolean, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isEnabled) color else Color.White,
        border = if (isEnabled) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isEnabled) Color.White else color, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                label,
                color = if (isEnabled) Color.White else Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
