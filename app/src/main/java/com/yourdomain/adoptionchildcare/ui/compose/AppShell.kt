package com.yourdomain.adoptionchildcare.ui.compose

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.*

@Composable
fun GreenHeader(
    profilePhotoUri: Uri?,
    username: String,
    role: String,
    onLogout: () -> Unit,
    onImagePicker: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile photo placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (profilePhotoUri != null) {
                    AsyncImage(
                        model = profilePhotoUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "\uD83D\uDC64",
                        fontSize = 30.sp
                    )
                }
                
                // + button for image picker
                IconButton(
                    onClick = onImagePicker,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .background(
                            Color(0xFF9C27B0),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Photo",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Username and Role
            Text(
                text = username,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = role,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )

            // Logout button
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BlueFooter(
    version: String = "v1.0.0",
    isSynced: Boolean = true,
    onHelpClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2196F3)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = version,
                color = Color.White,
                fontSize = 12.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sync indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            if (isSynced) Color.Green else Color.Red,
                            CircleShape
                        )
                )

                TextButton(onClick = onHelpClick) {
                    Text(
                        text = "Help/About",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// Simple data holder mirroring DashboardScreen cards
private data class QuickCard(
    val title: String,
    val icon: ImageVector,
    val count: Int,
    val summary: String,
    val color: Color
)

@Composable
fun QuickAccessCardsPane(
    role: String,
    modifier: Modifier = Modifier
) {
    val cards = remember {
        listOf(
            QuickCard("Children", Icons.Default.ChildCare, 24, "Last added: Sarah (Age 8)", Color(0xFF4CAF50)),
            QuickCard("Families", Icons.Default.FamilyRestroom, 12, "Active families", Color(0xFF2196F3)),
            QuickCard("Placements", Icons.Default.Home, 18, "Current placements", Color(0xFF9C27B0)),
            QuickCard("Tasks", Icons.Default.Assignment, 8, "Upcoming tasks", Color(0xFFFF9800)),
            QuickCard("Documents", Icons.Default.Description, 45, "Recent uploads", Color(0xFF607D8B)),
            QuickCard("Reports & Cases", Icons.Default.Assessment, 6, "Active cases", Color(0xFFE91E63)),
            QuickCard("Finance", Icons.Default.AttachMoney, 32, "Recent transactions", Color(0xFF4CAF50)),
            QuickCard("Background Checks", Icons.Default.Security, 4, "Pending checks", Color(0xFFFF5722)),
            QuickCard("Education", Icons.Default.School, 15, "Education records", Color(0xFF3F51B5))
        )
    }

    // Determine which cards are daily-priority by role
    val dailySet = remember(role) {
        when (role) {
            "Admin" -> cards.map { it.title }.toSet()
            "Case Worker" -> setOf("Children", "Reports & Cases", "Background Checks", "Tasks", "Documents")
            "Foster Parent" -> setOf("Tasks", "Documents", "Education", "Medical", "Families")
            "Social Worker" -> setOf("Children", "Reports & Cases", "Tasks", "Documents", "Background Checks")
            else -> cards.map { it.title }.toSet()
        }
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = true
        ) {
            items(cards) { card ->
                val enabled = dailySet.contains(card.title)
                QuickAccessCardItem(card = card, enabled = enabled)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun QuickAccessCardItem(card: QuickCard, enabled: Boolean) {
    val alpha = if (enabled) 1f else 0.45f
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = alpha)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = card.icon,
                    contentDescription = card.title,
                    tint = card.color.copy(alpha = alpha),
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = card.count.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = card.color.copy(alpha = alpha)
                )
            }
            Text(
                text = card.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
            )
            Text(
                text = card.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f * alpha)
            )
        }
    }
}

@Composable
fun BlueFooter(
    version: String = "v1.0.0",
    isSynced: Boolean = true,
    onHelpClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2196F3)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = version,
                color = Color.White,
                fontSize = 12.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sync indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            if (isSynced) Color.Green else Color.Red,
                            CircleShape
                        )
                )

                TextButton(onClick = onHelpClick) {
                    Text(
                        text = "Help/About",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
