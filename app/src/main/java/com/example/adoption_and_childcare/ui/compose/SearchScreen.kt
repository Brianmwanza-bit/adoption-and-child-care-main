package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.viewmodel.SearchResult
import com.example.adoption_and_childcare.viewmodel.SearchViewModel
import com.yourdomain.adoptionchildcare.R

/**
 * Screen displaying search results for children, families, staff, and other records.
 *
 * @param query The current search query string.
 * @param onQueryChange Callback triggered when the query is modified within this screen.
 * @param onBack Callback to navigate back to the previous screen.
 * @param viewModel The [SearchViewModel] handling search logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val results by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    // Sync external query with ViewModel
    LaunchedEffect(query) {
        viewModel.performSearch(query)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.search_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.search_back_desc)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isSearching) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (results.isEmpty() && query.isNotEmpty() && !isSearching) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.search_no_results, query),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (query.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.search_empty_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    val groupedResults = results.groupBy { it::class.simpleName }
                    
                    groupedResults.forEach { (type, items) ->
                        item {
                            Text(
                                text = type ?: stringResource(R.string.search_other_category),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        
                        items(items) { result ->
                            SearchResultItem(result)
                        }
                    }
                }
            }
        }
        // Accessing onQueryChange to avoid unused parameter warning, 
        // though actual editing happens in the AppHeader.
        SideEffect {
            if (query.isBlank() && results.isNotEmpty()) {
                // Potential cleanup logic if needed
            }
        }
    }
}

/**
 * A single item card displaying summarized information for a search result.
 *
 * @param result The [SearchResult] data to display.
 */
@Composable
fun SearchResultItem(result: SearchResult) {
    val na = stringResource(R.string.search_na)
    val unknown = stringResource(R.string.search_unknown)
    val standard = stringResource(R.string.search_standard_type)

    val (icon, title, subtitle, details, color) = when (result) {
        is SearchResult.Child -> Quintet(
            Icons.Default.ChildCare,
            result.name,
            stringResource(R.string.search_child_label),
            stringResource(R.string.search_county_label, result.county ?: na),
            Color(0xFF4CAF50)
        )
        is SearchResult.Family -> Quintet(
            Icons.Default.FamilyRestroom,
            result.name,
            stringResource(R.string.search_family_label, result.phone ?: ""),
            stringResource(R.string.search_id_label, result.nationalId ?: ""),
            Color(0xFF2196F3)
        )
        is SearchResult.User -> Quintet(
            Icons.Default.Person,
            result.username,
            stringResource(R.string.search_staff_label, result.role),
            stringResource(R.string.search_county_label, result.county ?: na),
            Color(0xFF9C27B0)
        )
        is SearchResult.Placement -> Quintet(
            Icons.Default.Home,
            stringResource(R.string.search_placement_label, result.id),
            result.type ?: standard,
            stringResource(R.string.search_date_label, result.date),
            Color(0xFFE91E63)
        )
        is SearchResult.Finance -> Quintet(
            Icons.Default.Payments,
            stringResource(R.string.search_amount_label, result.amount),
            stringResource(R.string.search_receipt_label, result.receipt ?: na),
            stringResource(R.string.search_date_label, result.date),
            Color(0xFF4CAF50)
        )
        is SearchResult.Application -> Quintet(
            Icons.AutoMirrored.Filled.Assignment,
            stringResource(R.string.search_application_label, result.id),
            stringResource(R.string.search_status_label, result.status),
            stringResource(R.string.search_family_id_label, result.familyId),
            Color(0xFFFF9800)
        )
        is SearchResult.Document -> Quintet(
            Icons.Default.Description,
            result.name,
            stringResource(R.string.search_document_label),
            stringResource(R.string.search_type_label, result.type ?: unknown),
            Color(0xFF607D8B)
        )
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(text = details, style = MaterialTheme.typography.labelSmall, color = color)
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

/**
 * A simple container for holding five related values.
 *
 * @param A Type of the first value.
 * @param B Type of the second value.
 * @param C Type of the third value.
 * @param D Type of the fourth value.
 * @param E Type of the fifth value.
 * @property first The first element.
 * @property second The second element.
 * @property third The third element.
 * @property fourth The fourth element.
 * @property fifth The fifth element.
 */
data class Quintet<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
