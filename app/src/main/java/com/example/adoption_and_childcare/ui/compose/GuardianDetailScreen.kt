package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.GuardianEntity
import com.example.adoption_and_childcare.data.db.entities.ChildEntity

/**
 * Screen displaying detailed information about a guardian.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianDetailScreen(
    guardian: GuardianEntity,
    child: ChildEntity?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guardian Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF009688),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)), shape = RoundedCornerShape(16.dp)) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(64.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFF009688)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SupervisorAccount, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = "${guardian.firstName} ${guardian.lastName}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(text = "Relationship: ${guardian.relationship}", color = Color(0xFF00796B))
                    }
                }
            }

            DetailSectionGua(stringResource(R.string.detail_contact_info), Icons.Default.Phone) {
                GuaDetailRow("Phone Number", guardian.phone ?: "Not set")
            }

            DetailSectionGua(stringResource(R.string.detail_child_info), Icons.Default.ChildCare) {
                if (child != null) {
                    GuaDetailRow("Child Name", "${child.firstName} ${child.lastName}")
                    GuaDetailRow("Case Number", child.caseNumber ?: "N/A")
                } else {
                    Text(stringResource(R.string.search_na), color = Color.Gray)
                }
            }

            DetailSectionGua(stringResource(R.string.detail_legal_status), Icons.Default.Gavel) {
                GuaDetailRow("Verification Status", guardian.verificationStatus ?: "Pending")
            }
        }
    }
}

@Composable
private fun DetailSectionGua(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF009688), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF009688))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

@Composable
private fun GuaDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
