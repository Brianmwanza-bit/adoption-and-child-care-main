package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.R

/**
 * A searchable dropdown for selecting a child from a list.
 *
 * @param children List of available children.
 * @param selectedChildId Currently selected child ID.
 * @param onChildSelected Callback when a child is picked.
 * @param label The label for the text field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableChildSelector(
    children: List<ChildEntity>,
    selectedChildId: Int?,
    onChildSelected: (ChildEntity) -> Unit,
    label: String = stringResource(R.string.common_select_child)
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredChildren = children.filter { 
        it.firstName.contains(searchQuery, ignoreCase = true) || 
        it.lastName.contains(searchQuery, ignoreCase = true) ||
        (it.middleName?.contains(searchQuery, ignoreCase = true) == true)
    }

    val selectedChild = children.find { it.childId == selectedChildId }
    val displayText = selectedChild?.let { "${it.firstName} ${it.lastName}" } ?: stringResource(R.string.common_search_select_child)
    val idStatusFormat = stringResource(R.string.common_child_id_status)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (expanded) searchQuery else displayText,
            onValueChange = { if (expanded) searchQuery = it },
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            readOnly = !expanded,
            placeholder = { Text(stringResource(R.string.common_start_typing_name)) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { 
                expanded = false 
                searchQuery = ""
            }
        ) {
            if (filteredChildren.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.common_no_children_found)) },
                    onClick = { },
                    enabled = false
                )
            } else {
                filteredChildren.forEach { child ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("${child.firstName} ${child.lastName}", fontWeight = FontWeight.Bold)
                                Text(idStatusFormat.format(child.childId, child.currentStatus), fontSize = 12.sp, color = Color.Gray)
                            }
                        },
                        onClick = {
                            onChildSelected(child)
                            searchQuery = ""
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * A modern card displaying a form record with header details and action buttons.
 *
 * @param title Record title.
 * @param subtitle Record subtitle.
 * @param pageNumber Page or record number.
 * @param onEdit Callback for edit action.
 * @param onDelete Callback for delete action.
 * @param onDownloadPdf Optional callback for PDF download.
 * @param headerIcon Optional icon for the header card.
 * @param onClick Optional callback for clicking the entire card.
 * @param content Composable content for the card body.
 */
@Composable
fun FormRecordCard(
    title: String,
    subtitle: String,
    pageNumber: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDownloadPdf: (() -> Unit)? = null,
    headerIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header with Passport Photo Placeholder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.common_page_number, pageNumber),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Passport Photo Placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        headerIcon ?: Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.common_passport),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Form Content
            content()

            Spacer(modifier = Modifier.height(32.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.common_edit), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedIconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.common_delete), tint = MaterialTheme.colorScheme.error)
                }
                if (onDownloadPdf != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onDownloadPdf,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.common_download_pdf))
                    }
                }
            }
        }
    }
}

/**
 * A single row in a form displaying a label and its value.
 *
 * @param label Field label.
 * @param value Field value text.
 * @param valueColor Theme color for the value text.
 */
@Composable
fun FormDetailRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = valueColor,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
