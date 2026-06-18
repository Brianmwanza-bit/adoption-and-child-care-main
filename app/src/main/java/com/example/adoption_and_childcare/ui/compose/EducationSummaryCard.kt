package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity

/**
 * A card displaying an overview of academic statistics.
 *
 * @param records The list of education records to summarize.
 */
@Composable
fun EducationSummaryCard(records: List<EducationRecordEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.edu_summary_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.edu_summary_enrolled))
                val enrolledCount = records.map { it.childId }.distinct().size
                Text("$enrolledCount", fontWeight = FontWeight.Bold)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.edu_summary_institutions))
                val schoolCount = records.map { it.schoolName }.distinct().size
                Text("$schoolCount", fontWeight = FontWeight.Medium)
            }
        }
    }
}
