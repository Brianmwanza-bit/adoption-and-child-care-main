package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.res.stringResource
import com.example.adoption_and_childcare.R

/**
 * A structural template for viewing PDF documents within the app.
 * This screen follows the PDF_DOCUMENT_SPECIFICATION structure.
 * 
 * @param fileName The name of the file to display.
 * @param onBack Callback when the back button is pressed.
 * @param onDownload Callback for downloading the document.
 * @param onShare Callback for sharing the document.
 * @param onPrint Callback for printing the document.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    fileName: String,
    onBack: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    onPrint: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(fileName, style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.pdf_confidential), style = MaterialTheme.typography.labelSmall, color = Color.Red)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_desc))
                    }
                },
                actions = {
                    IconButton(onClick = onDownload) { Icon(Icons.Default.Download, contentDescription = stringResource(R.string.pdf_download_desc)) }
                    IconButton(onClick = onShare) { Icon(Icons.Default.Share, contentDescription = stringResource(R.string.pdf_share_desc)) }
                    IconButton(onClick = onPrint) { Icon(Icons.Default.Print, contentDescription = stringResource(R.string.pdf_print_desc)) }
                }
            )
        },
        bottomBar = {
            // Page Indicator and Navigation
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.pdf_page_info, 1, 5), style = MaterialTheme.typography.bodyMedium)
                    Row {
                        TextButton(onClick = { /* Previous Page */ }) { Text(stringResource(R.string.pdf_prev)) }
                        TextButton(onClick = { /* Next Page */ }) { Text(stringResource(R.string.pdf_next)) }
                    }
                }
            }
        }
    ) {
        // Placeholder for the PDF Rendering Surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.Gray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            PdfDocumentMockup()
        }
    }
}

/**
 * A mockup of a PDF document for visualization purposes.
 */
@Composable
fun PdfDocumentMockup() {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.9f)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // PDF Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.pdf_system_name), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(stringResource(R.string.pdf_case_report), color = Color.Blue, fontWeight = FontWeight.Bold)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Identifying Info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .padding(8.dp)
            ) {
                Column {
                    Text(stringResource(R.string.pdf_child_label, "SAMUEL KARANJA"), fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.pdf_case_id_label, "CASE-1001"))
                    Text(stringResource(R.string.pdf_date_label, "2023-10-27"))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Body Content Mockup
            Text(stringResource(R.string.pdf_summary_title), fontWeight = FontWeight.Bold)
            Text(
                stringResource(R.string.pdf_summary_content),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.weight(1f).height(16.dp))
            
            // PDF Footer
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.pdf_gen_timestamp, "2023-10-27 10:45 AM"), fontSize = 10.sp)
                Text(stringResource(R.string.pdf_page_number, 1), fontSize = 10.sp)
            }
        }
    }
}
