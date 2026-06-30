package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
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
import com.example.adoption_and_childcare.viewmodel.FinanceViewModel

/**
 * Screen displaying detailed financial record information.
 * 
 * @param recordId The unique identifier of the financial record to display.
 * @param onBack Callback for back navigation.
 * @param viewModel ViewModel for managing financial data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceDetailScreen(
    recordId: Int,
    onBack: () -> Unit,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val records by viewModel.moneyRecords.collectAsStateWithLifecycle(initialValue = emptyList())
    val record = records.find { it.moneyId == recordId }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.finance_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { scaffoldPadding: PaddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues = scaffoldPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
            }
        } else if (record == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues = scaffoldPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.search_na))
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues = scaffoldPadding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.finance_amount_field), style = MaterialTheme.typography.labelMedium)
                        Text(text = stringResource(R.string.finance_kes_format, record.amount.toString()), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        Spacer(Modifier.height(8.dp))
                        Badge(containerColor = Color(0xFF2E7D32)) { Text(record.transactionType ?: stringResource(R.string.finance_payment_default), color = Color.White) }
                    }
                }

                DetailSectionFin(stringResource(R.string.detail_basic_info), Icons.Default.Receipt) {
                    FinDetailRow(stringResource(R.string.finance_label_txn_id), "#${record.moneyId}")
                    FinDetailRow(stringResource(R.string.finance_label_date), record.date)
                    FinDetailRow(stringResource(R.string.finance_label_child_id), record.childId.toString())
                }

                DetailSectionFin(stringResource(R.string.detail_description), Icons.AutoMirrored.Filled.Notes) {
                    Text(text = record.description ?: stringResource(R.string.finance_no_description), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

/**
 * A section for displaying grouped financial details.
 * 
 * @param title The title of the section.
 * @param icon The icon representing the section.
 * @param content The composable content to display within the section.
 */
@Composable
private fun DetailSectionFin(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

/**
 * A row for displaying a single financial detail label and value.
 * 
 * @param label The descriptive label for the detail.
 * @param value The value associated with the label.
 */
@Composable
private fun FinDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
