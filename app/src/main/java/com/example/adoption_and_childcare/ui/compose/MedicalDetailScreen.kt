package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.viewmodel.MedicalViewModel

/**
 * Screen displaying detailed medical record information.
 * 
 * @param recordId The unique identifier of the medical record to display.
 * @param onBack Callback for navigating back.
 * @param viewModel ViewModel for handling medical record operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalDetailScreen(
    recordId: Int,
    onBack: () -> Unit,
    viewModel: MedicalViewModel = hiltViewModel()
) {
    val records by viewModel.medicalRecords.collectAsState(initial = emptyList())
    val record = records.find { it.recordId == recordId }
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.medical_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.search_back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF44336),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFF44336))
            }
        } else if (record == null) {
            Box(Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.medical_record_not_found))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF44336)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.medical_record_id, record.recordId),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.medical_child_id, record.childId),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFF44336)
                            )
                        }
                    }
                }

                // Details Sections
                DetailSection(stringResource(R.string.medical_visit_info), Icons.Default.Event) {
                    MedicalRecordDetailRow(stringResource(R.string.medical_label_date), record.visitDate)
                    val doctor = record.doctorName
                    if (doctor != null) {
                        MedicalRecordDetailRow(stringResource(R.string.medical_label_doctor), doctor)
                    }
                    val hospital = record.hospitalName
                    if (hospital != null) {
                        MedicalRecordDetailRow(stringResource(R.string.medical_label_hospital), hospital)
                    }
                }

                DetailSection(stringResource(R.string.medical_diagnosis_treatment), Icons.Default.MedicalServices) {
                    MedicalRecordDetailRow(stringResource(R.string.medical_label_diagnosis), record.diagnosis ?: stringResource(R.string.search_na))
                    MedicalRecordDetailRow(stringResource(R.string.medical_label_treatment), record.treatment ?: stringResource(R.string.search_na))
                    val medications = record.medications
                    if (medications != null) {
                        MedicalRecordDetailRow(stringResource(R.string.medical_label_medications), medications)
                    }
                }

                if (record.isImmunization) {
                    DetailSection(stringResource(R.string.medical_immunization), Icons.Default.Vaccines) {
                        MedicalRecordDetailRow(stringResource(R.string.medical_label_type), record.immunizationType ?: stringResource(R.string.search_na))
                    }
                }

                val followUp = record.followUpDate
                if (followUp != null) {
                    DetailSection(stringResource(R.string.medical_follow_up), Icons.Default.Update) {
                        MedicalRecordDetailRow(stringResource(R.string.medical_label_scheduled_date), followUp)
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* Export */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.medical_export_pdf))
                    }
                    OutlinedButton(
                        onClick = { /* Share */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.medical_share))
                    }
                }
            }
        }
    }
}

/**
 * A reusable section for medical record details.
 *
 * @param title The title of the section.
 * @param icon The icon to display next to the title.
 * @param content The composable content of the section.
 */
@Composable
private fun DetailSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
            content()
        }
    }
}

/**
 * A reusable row for displaying a label and its corresponding value.
 *
 * @param label The label to display.
 * @param value The value to display.
 */
@Composable
private fun MedicalRecordDetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}
