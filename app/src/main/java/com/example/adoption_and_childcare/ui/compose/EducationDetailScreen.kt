package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.viewmodel.EducationViewModel

/**
 * Screen displaying detailed education record information.
 *
 * @param recordId The ID of the education record to display.
 * @param onBack Callback to navigate back to the previous screen.
 * @param viewModel The ViewModel handling education record data and logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationDetailScreen(
    recordId: Int,
    onBack: () -> Unit,
    viewModel: EducationViewModel = hiltViewModel()
) {
    val records by viewModel.educationRecords.collectAsStateWithLifecycle(initialValue = emptyList())
    val record = records.find { it.recordId == recordId }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.module_education)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3F51B5),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        /** @param padding The padding values provided by the Scaffold. */
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF3F51B5))
            }
        } else if (record == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.search_na))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(64.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFF3F51B5)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(text = record.schoolName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(text = "Grade: ${record.grade ?: "N/A"}", color = Color(0xFF3F51B5))
                        }
                    }
                }

                DetailSectionEdu(stringResource(R.string.detail_enrollment), Icons.Default.CalendarToday) {
                    EduDetailRow("Date of Enrollment", record.enrollmentDate ?: "N/A")
                    EduDetailRow("Date of Exit", record.exitDate ?: "Still Enrolled")
                }

                DetailSectionEdu(stringResource(R.string.detail_performance), Icons.Default.Assessment) {
                    Text(text = record.performance ?: "No performance notes available.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 8.dp))
                }

                if (!record.specialNeeds.isNullOrBlank()) {
                    DetailSectionEdu(stringResource(R.string.profile_special_needs), Icons.Default.Info) {
                        Text(text = record.specialNeeds, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

/**
 * A section in the education detail screen.
 *
 * @param title The title of the section.
 * @param icon The icon to display next to the title.
 * @param content The composable content of the section.
 */
@Composable
private fun DetailSectionEdu(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF3F51B5), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

/**
 * A row displaying a label and value in the detail screen.
 *
 * @param label The label of the row.
 * @param value The value of the row.
 */
@Composable
private fun EduDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
