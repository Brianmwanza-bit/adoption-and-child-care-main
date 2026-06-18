package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.viewmodel.SOSViewModel

/**
 * Screen displaying emergency contacts and a global SOS trigger.
 *
 * @param onBack Callback for navigating back to the previous screen.
 * @param viewModel The ViewModel handling SOS logic and contacts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(onBack: () -> Unit, viewModel: SOSViewModel = hiltViewModel()) {
    val contacts by viewModel.emergencyContacts.collectAsState()

    val policeKey = stringResource(R.string.key_police)
    val fireKey = stringResource(R.string.key_fire)
    val hospitalKey = stringResource(R.string.key_hospital)
    val cpsKey = stringResource(R.string.key_cps)
    val name1Key = stringResource(R.string.key_emergency1_name)
    val phone1Key = stringResource(R.string.key_emergency1_phone)
    val name2Key = stringResource(R.string.key_emergency2_name)
    val phone2Key = stringResource(R.string.key_emergency2_phone)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.sos_title), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.sos_back_desc),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFB71C1C))
            )
        }
    ) { padding -> // padding: The padding provided by Scaffold
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                EmergencySectionTitle(stringResource(R.string.sos_section_services))
            }

            item {
                val policePhone = contacts[policeKey] ?: "999"
                EmergencyContactItem(
                    name = stringResource(R.string.sos_police),
                    phone = policePhone,
                    icon = Icons.Default.LocalPolice,
                    onCall = { viewModel.placeCall(policePhone) }
                )
            }

            item {
                val firePhone = contacts[fireKey] ?: "999"
                EmergencyContactItem(
                    name = stringResource(R.string.sos_fire),
                    phone = firePhone,
                    icon = Icons.Default.LocalFireDepartment,
                    onCall = { viewModel.placeCall(firePhone) }
                )
            }

            item {
                val hospitalPhone = contacts[hospitalKey] ?: "999"
                EmergencyContactItem(
                    name = stringResource(R.string.sos_hospital),
                    phone = hospitalPhone,
                    icon = Icons.Default.LocalHospital,
                    onCall = { viewModel.placeCall(hospitalPhone) }
                )
            }

            item {
                val cpsPhone = contacts[cpsKey] ?: ""
                EmergencyContactItem(
                    name = stringResource(R.string.sos_cps),
                    phone = cpsPhone,
                    icon = Icons.Default.ChildCare,
                    onCall = { viewModel.placeCall(cpsPhone) }
                )
            }

            item {
                EmergencySectionTitle(stringResource(R.string.sos_section_personal))
            }

            item {
                val name1 = contacts[name1Key] ?: stringResource(R.string.sos_contact1_default)
                val phone1 = contacts[phone1Key] ?: ""
                EmergencyContactItem(
                    name = name1,
                    phone = phone1,
                    icon = Icons.Default.Person,
                    onCall = { if (phone1.isNotBlank()) viewModel.placeCall(phone1) }
                )
            }

            item {
                val name2 = contacts[name2Key] ?: stringResource(R.string.sos_contact2_default)
                val phone2 = contacts[phone2Key] ?: ""
                EmergencyContactItem(
                    name = name2,
                    phone = phone2,
                    icon = Icons.Default.Person,
                    onCall = { if (phone2.isNotBlank()) viewModel.placeCall(phone2) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.triggerSOS() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.sos_trigger_btn), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Displays a title for a section in the emergency contacts list.
 *
 * @param title The text to display as the section title.
 */
@Composable
fun EmergencySectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

/**
 * An item in the emergency contact list representing a single service or person.
 *
 * @param name The name of the emergency contact or service.
 * @param phone The phone number associated with the contact.
 * @param icon The icon to represent the contact.
 * @param onCall Callback invoked when the call button is clicked.
 */
@Composable
fun EmergencyContactItem(name: String, phone: String, icon: ImageVector, onCall: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFFF5F5F5)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color(0xFFB71C1C))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(phone.ifBlank { stringResource(R.string.sos_not_set) }, color = Color.Gray, fontSize = 14.sp)
            }

            IconButton(
                onClick = onCall,
                enabled = phone.isNotBlank(),
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.Call, contentDescription = stringResource(R.string.sos_call_desc), tint = Color.White)
            }
        }
    }
}
