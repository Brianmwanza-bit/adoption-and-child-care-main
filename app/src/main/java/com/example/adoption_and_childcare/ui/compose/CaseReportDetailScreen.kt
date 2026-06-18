package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
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
import com.example.adoption_and_childcare.viewmodel.CaseReportsViewModel

/**
 * Screen displaying detailed case report information.
 *
 * @param reportId The ID of the report to display.
 * @param onBack Callback when the back button is pressed.
 * @param viewModel The ViewModel for case reports.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseReportDetailScreen(
    reportId: Int,
    onBack: () -> Unit,
    viewModel: CaseReportsViewModel = hiltViewModel(),
) {
    val reports by viewModel.reports.collectAsState(initial = emptyList())
    val report = reports.find { it.reportId == reportId }
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reports_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF673AB7),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF673AB7))
            }
        } else if (report == null) {
            Box(Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.search_na))
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(it).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)), shape = RoundedCornerShape(16.dp)) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(64.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFF673AB7)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Description, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(text = report.reportTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(
                                text = stringResource(
                                    R.string.report_detail_type_format,
                                    report.reportType ?: stringResource(R.string.report_type_general)
                                ),
                                color = Color(0xFF673AB7)
                            )
                        }
                    }
                }

                DetailSectionRep(stringResource(R.string.detail_basic_info), Icons.Default.Info) {
                    RepDetailRow(stringResource(R.string.report_detail_id), "#${report.reportId}")
                    RepDetailRow(stringResource(R.string.reports_label_report_date), report.reportDate)
                    RepDetailRow(stringResource(R.string.reports_label_child_id), report.childId.toString())
                }

                DetailSectionRep(stringResource(R.string.detail_findings), Icons.AutoMirrored.Filled.Article) {
                    Text(text = report.content, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

/**
 * A reusable section for report details.
 *
 * @param title The title of the section.
 * @param icon The icon to display next to the title.
 * @param content The content of the section.
 */
@Composable
private fun DetailSectionRep(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF673AB7), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF673AB7))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

/**
 * A reusable row for displaying a label and a value.
 *
 * @param label The label to display.
 * @param value The value to display.
 */
@Composable
private fun RepDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
