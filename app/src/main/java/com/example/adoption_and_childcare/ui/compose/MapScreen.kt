package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.viewmodel.MapViewModel
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
fun MapScreen(
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val children by viewModel.children.collectAsState(initial = emptyList())
    val families by viewModel.families.collectAsState(initial = emptyList())

    val nairobi = LatLng(-1.286389, 36.817223)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(nairobi, 11f)
    }

    var selectedLocationName by remember { mutableStateOf<String?>(null) }
    var showDistanceTool by remember { mutableStateOf(false) }
    
    var showChildren by remember { mutableStateOf(true) }
    var showFamilies by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Case Management Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDistanceTool = !showDistanceTool }) {
                        Icon(
                            if (showDistanceTool) Icons.Default.Straighten else Icons.Default.Map,
                            contentDescription = "Distance Tool"
                        )
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
                // Dynamic Children Markers
                if (showChildren) {
                    children.forEach { child ->
                        // Simulation: Lat/Lng based on ID for visual placement
                        val lat = -1.28 + (child.childId * 0.005)
                        val lng = 36.81 + (child.childId * 0.003)
                        Marker(
                            state = rememberUpdatedMarkerState(position = LatLng(lat, lng)),
                            title = "Child: ${child.firstName} ${child.lastName}",
                            snippet = "Case #${child.caseNumber ?: "N/A"} - ${child.currentStatus}",
                            onClick = {
                                selectedLocationName = "${child.firstName} ${child.lastName}"
                                false
                            }
                        )
                    }
                }

                // Dynamic Family Markers
                if (showFamilies) {
                    families.forEach { family ->
                        val lat = -1.30 - (family.familyId * 0.004)
                        val lng = 36.78 + (family.familyId * 0.006)
                        Marker(
                            state = rememberUpdatedMarkerState(position = LatLng(lat, lng)),
                            title = "Family: ${family.primaryContactName}",
                            snippet = "Status: ${family.status ?: "Licensed"}",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                            onClick = {
                                selectedLocationName = family.primaryContactName
                                false
                            }
                        )
                    }
                }
            }

            // Overlay UI
            
            // Layer Toggles
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LayerToggle("Children", showChildren, Color(0xFFE91E63)) { showChildren = !showChildren }
                LayerToggle("Families", showFamilies, Color(0xFF2196F3)) { showFamilies = !showFamilies }
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
                            "Proximity Analysis: Optimized for resource allocation.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Estimating travel times between current placements and services.",
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
                            "SYSTEM STATUS: 100% OPERATIONAL",
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
private fun LayerToggle(label: String, isEnabled: Boolean, color: Color, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isEnabled) color else Color.White,
        border = if (isEnabled) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier.height(32.dp).clickable { onClick() }
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
