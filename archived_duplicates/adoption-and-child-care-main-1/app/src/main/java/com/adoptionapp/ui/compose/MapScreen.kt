package com.adoptionapp.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
// Google Maps Compose imports
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.loadLocations()
    }
    val familyLocations by viewModel.familyLocations.collectAsState()
    val userLocations by viewModel.userLocations.collectAsState()
    val loading by viewModel.loading.collectAsState(initial = false)
    val error by viewModel.error.collectAsState(initial = null)
    var matchResults by remember { mutableStateOf(listOf<String>()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Family and User Locations Map", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        if (loading) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
        // GoogleMap Compose
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPositionState().position.copy(target = LatLng(0.0, 0.0), zoom = 2f)
        }
        Box(Modifier.height(300.dp).fillMaxWidth()) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState
            ) {
                familyLocations.forEach { loc ->
                    Marker(
                        state = MarkerState(position = LatLng(loc.latitude, loc.longitude)),
                        title = "Family ${loc.family_id}",
                        snippet = "Family Location"
                    )
                }
                userLocations.forEach { loc ->
                    Marker(
                        state = MarkerState(position = LatLng(loc.latitude, loc.longitude)),
                        title = "User ${loc.user_id}",
                        snippet = "Role: ${loc.role}"
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            // Placeholder matching logic: match each family to a user by index
            matchResults = familyLocations.zip(userLocations) { fam, user ->
                "Family ${fam.family_id} matched to User ${user.user_id} (${user.role})"
            }
        }) {
            Text("Suggest Matches")
        }
        Spacer(Modifier.height(8.dp))
        if (matchResults.isNotEmpty()) {
            Text("Suggested Matches:", style = MaterialTheme.typography.titleMedium)
            matchResults.forEach { result ->
                Text(result)
                }
            }
        }
    }
} 