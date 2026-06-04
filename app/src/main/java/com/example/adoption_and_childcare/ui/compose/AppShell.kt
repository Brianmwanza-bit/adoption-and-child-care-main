package com.example.adoption_and_childcare.ui.compose

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yourdomain.adoptionchildcare.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable that displays the header content within the navigation drawer.
 *
 * @param profilePhotoUri The [Uri] of the user's profile photo, if available.
 * @param username The name of the currently logged-in user.
 * @param role The role assigned to the current user (e.g., "Admin", "Case Worker").
 * @param onLogout Callback invoked when the logout button is clicked.
 */
@Composable
fun DrawerHeader(
    profilePhotoUri: Uri?,
    username: String,
    role: String,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50))
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (profilePhotoUri != null) {
                AsyncImage(
                    model = profilePhotoUri,
                    contentDescription = stringResource(R.string.profile_photo_desc),
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("\uD83D\uDC64", fontSize = 24.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = username,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = role,
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextButton(
            onClick = onLogout,
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.logout_label))
        }
    }
}

/**
 * A simplified top header bar for screens that use a navigation drawer.
 *
 * @param profilePhotoUri The [Uri] of the user's profile photo.
 * @param username The name of the currently logged-in user.
 * @param role The role assigned to the current user.
 * @param onImagePicker Callback invoked when the profile image placeholder is clicked.
 * @param onMenuClick Callback invoked when the navigation menu icon is clicked.
 */
@Composable
fun GreenHeader(
    profilePhotoUri: Uri?,
    username: String,
    role: String,
    onImagePicker: () -> Unit,
    onMenuClick: () -> Unit = {}
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
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu_desc), tint = Color.White)
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = username,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = role,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onImagePicker() },
                contentAlignment = Alignment.Center
            ) {
                if (profilePhotoUri != null) {
                    AsyncImage(
                        model = profilePhotoUri,
                        contentDescription = stringResource(R.string.profile_photo_desc),
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("\uD83D\uDC64", fontSize = 16.sp)
                }
            }
        }
    }
}

/**
 * Enhanced footer composable displaying app information, sync status, and important resources.
 *
 * @param version The current version string of the application.
 * @param isSynced Boolean flag indicating if the data is currently synchronized.
 * @param onHelpClick Callback invoked when the Help/About button is clicked.
 * @param onEmergencyClick Callback invoked when the Emergency button is clicked.
 * @param onPrivacyClick Callback invoked when the Privacy Policy button is clicked.
 * @param onTermsClick Callback invoked when the Terms of Service button is clicked.
 * @param onAgencyContactClick Callback invoked when the Agency Contact button is clicked.
 * @param onReportAbuseClick Callback invoked when the Report Abuse button is clicked.
 */
@Composable
fun BlueFooter(
    version: String = "v1.0.0",
    isSynced: Boolean = true,
    onHelpClick: () -> Unit = {},
    onEmergencyClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onAgencyContactClick: () -> Unit = {},
    onReportAbuseClick: () -> Unit = {}
) {
    val lastSyncTime = remember { 
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) 
    }
    val systemStatus = "Operational"
    val dataUsage = "2.4 MB"
    
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Primary footer row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Version and basic info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = version,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // Sync indicator with last sync time
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (isSynced) Color(0xFF4CAF50) else Color.Red,
                                    CircleShape
                                )
                        )
                        Text(
                            text = if (isSynced) "Synced $lastSyncTime" else "Sync Failed",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
                
                // System status
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = systemStatus,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
            
            // Secondary row with important links and emergency contact
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emergency contact button (highlighted)
                Button(
                    onClick = onEmergencyClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Emergency: 911",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Quick links row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Report Abuse button
                    Button(
                        onClick = onReportAbuseClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Report,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Report Abuse",
                            fontSize = 10.sp
                        )
                    }
                    
                    // Agency Contact
                    TextButton(
                        onClick = onAgencyContactClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            Icons.Default.ContactPhone,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Agency",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                    
                    // Privacy Policy
                    TextButton(
                        onClick = onPrivacyClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "Privacy",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                    
                    // Terms of Service
                    TextButton(
                        onClick = onTermsClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "Terms",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                    
                    // Help/About
                    TextButton(
                        onClick = onHelpClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.help_about_label),
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            // Tertiary row with additional info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Data usage and regulatory info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Storage,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Data: $dataUsage",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 9.sp
                        )
                    }
                    
                    Text(
                        text = "•",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp
                    )
                    
                    Text(
                        text = "CPS Compliant",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Additional resources
                Text(
                    text = "Regulatory Resources",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 9.sp
                )
            }
        }
    }
}

/**
 * Data class representing a quick access card on the dashboard.
 *
 * @property title The title displayed on the card.
 * @property icon The icon [ImageVector] representing the card's purpose.
 * @property count The numerical value or count shown on the card.
 * @property summary a brief descriptive text for the card.
 * @property color The primary theme color for this card.
 */
private data class QuickCard(
    val title: String,
    val icon: ImageVector,
    val count: Int,
    val summary: String,
    val color: Color
)

/**
 * A pane containing a grid of quick access cards tailored to the user's role.
 *
 * @param role The role of the current user, used to prioritize cards.
 * @param modifier The [Modifier] to be applied to this composable.
 */
@Composable
fun QuickAccessCardsPane(
    role: String,
    modifier: Modifier = Modifier
) {
    val cards = listOf(
        QuickCard(stringResource(R.string.card_children), Icons.Default.ChildCare, 24, stringResource(R.string.card_children_summary), Color(0xFF4CAF50)),
        QuickCard(stringResource(R.string.card_families), Icons.Default.FamilyRestroom, 12, stringResource(R.string.card_families_summary), Color(0xFF2196F3)),
        QuickCard(stringResource(R.string.card_placements), Icons.Default.Home, 18, stringResource(R.string.card_placements_summary), Color(0xFF9C27B0)),
        QuickCard(stringResource(R.string.card_tasks), Icons.Default.Assignment, 8, stringResource(R.string.card_tasks_summary), Color(0xFFFF9800)),
        QuickCard(stringResource(R.string.card_documents), Icons.Default.Description, 45, stringResource(R.string.card_documents_summary), Color(0xFF607D8B)),
        QuickCard(stringResource(R.string.card_reports), Icons.Default.Assessment, 6, stringResource(R.string.card_reports_summary), Color(0xFFE91E63)),
        QuickCard(stringResource(R.string.card_finance), Icons.Default.AttachMoney, 32, stringResource(R.string.card_finance_summary), Color(0xFF4CAF50)),
        QuickCard(stringResource(R.string.card_bg_checks), Icons.Default.Security, 4, stringResource(R.string.card_bg_checks_summary), Color(0xFFFF5722)),
        QuickCard(stringResource(R.string.card_education), Icons.Default.School, 15, stringResource(R.string.card_education_summary), Color(0xFF3F51B5))
    )

    // Determine which cards are daily-priority by role
    val dailySet = remember(role) {
        when (role) {
            "Admin" -> cards.map { it.title }.toSet()
            "Case Worker" -> setOf(
                "Children",
                "Reports & Cases",
                "Background Checks",
                "Tasks",
                "Documents"
            )
            "Foster Parent" -> setOf(
                "Tasks",
                "Documents",
                "Education",
                "Medical",
                "Families"
            )
            "Social Worker" -> setOf(
                "Children",
                "Reports & Cases",
                "Tasks",
                "Documents",
                "Background Checks"
            )
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

/**
 * An individual item in the [QuickAccessCardsPane].
 *
 * @param card The [QuickCard] data to display.
 * @param enabled Whether this card is enabled/highlighted for the current user.
 */
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