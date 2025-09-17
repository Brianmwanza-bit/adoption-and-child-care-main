package com.adoptionapp.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserRolesScreen() {
    val roles = listOf("Admin", "Case Worker", "Supervisor", "Guest")
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("User Roles", style = MaterialTheme.typography.headlineMedium)
        roles.forEach { role ->
            Card(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    Text(role, modifier = Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}


