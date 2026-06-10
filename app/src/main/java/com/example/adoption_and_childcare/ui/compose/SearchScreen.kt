package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adoption_and_childcare.viewmodel.SearchResult
import com.example.adoption_and_childcare.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onBack: () -> Unit = {}, viewModel: SearchViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    val results by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Cases & Records") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Search Input
            OutlinedTextField(
                value = query,
                onValueChange = { 
                    query = it
                    viewModel.performSearch(it)
                },
                label = { Text("Search by name, ID, phone, receipt...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { 
                            query = ""
                            viewModel.performSearch("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = MaterialTheme.shapes.medium
            )

            if (isSearching) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (results.isEmpty() && query.isNotEmpty() && !isSearching) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results found for '$query'", style = MaterialTheme.typography.bodyLarge)
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
                                text = type ?: "Other",
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
    }
}

@Composable
fun SearchResultItem(result: SearchResult) {
    val (icon, title, subtitle, details, color) = when (result) {
        is SearchResult.Child -> Quintet(Icons.Default.ChildCare, result.name, "Child Record", "County: ${result.county ?: "N/A"}", Color(0xFF4CAF50))
        is SearchResult.Family -> Quintet(Icons.Default.FamilyRestroom, result.name, "Family: ${result.phone ?: ""}", "ID: ${result.nationalId ?: ""}", Color(0xFF2196F3))
        is SearchResult.User -> Quintet(Icons.Default.Person, result.username, "Staff: ${result.role}", "County: ${result.county ?: "N/A"}", Color(0xFF9C27B0))
        is SearchResult.Placement -> Quintet(Icons.Default.Home, "Placement #${result.id}", result.type ?: "Standard", "Date: ${result.date}", Color(0xFFE91E63))
        is SearchResult.Finance -> Quintet(Icons.Default.Payments, "Ksh ${result.amount}", "Receipt: ${result.receipt ?: "N/A"}", "Date: ${result.date}", Color(0xFF4CAF50))
        is SearchResult.Application -> Quintet(Icons.Default.Assignment, "Application #${result.id}", "Status: ${result.status}", "Family ID: ${result.familyId}", Color(0xFFFF9800))
        is SearchResult.Document -> Quintet(Icons.Default.Description, result.name, "Document", "Type: ${result.type ?: "Unknown"}", Color(0xFF607D8B))
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
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text(text = details, style = MaterialTheme.typography.labelSmall, color = color)
            }
            
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
        }
    }
}

data class Quintet<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
