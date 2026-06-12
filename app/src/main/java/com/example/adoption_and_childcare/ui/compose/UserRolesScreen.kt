package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.PermissionEntity
import com.example.adoption_and_childcare.data.db.entities.UserPermissionEntity
import com.example.adoption_and_childcare.data.repository.PermissionRepositoryImpl
import com.example.adoption_and_childcare.data.repository.UserPermissionRepositoryImpl
import com.example.adoption_and_childcare.network.RetrofitClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for managing user roles and permissions.
 *
 * @param onBack Callback invoked when the user navigates back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRolesScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getDynamicApiService(context) }
    val permissionRepository = remember { PermissionRepositoryImpl(db.permissionDao(), apiService) }
    val userPermissionRepository = remember { UserPermissionRepositoryImpl(db.userPermissionDao(), apiService) }
    var permissions by remember { mutableStateOf<List<PermissionEntity>>(emptyList()) }
    var userPermissions by remember { mutableStateOf<List<UserPermissionEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreatePermission by remember { mutableStateOf(false) }
    var showEditPermission by remember { mutableStateOf(false) }
    var showDeletePermission by remember { mutableStateOf(false) }
    var selectedPermission by remember { mutableStateOf<PermissionEntity?>(null) }
    
    var permName by remember { mutableStateOf(TextFieldValue("")) }
    var permDescription by remember { mutableStateOf(TextFieldValue("")) }
    var permCategory by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        db.permissionDao().observeAll().collectLatest { list ->
            permissions = list
        }
        db.userPermissionDao().observeAll().collectLatest { list ->
            userPermissions = list
        }
    }
    
    // Fetch from API
    LaunchedEffect(Unit) {
        fetchPermissionsFromApi(permissionRepository, userPermissionRepository, scope) { loading, error ->
            isLoading = loading
            errorMessage = error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Roles & Permissions") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreatePermission = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Permission")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("System Permissions", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (permissions.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No permissions defined yet", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(permissions) { permission ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(permission.name, style = MaterialTheme.typography.titleMedium)
                                    permission.description?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                    permission.category?.let { Text("Category: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
                                    val assignedCount = userPermissions.count { it.permissionId == permission.permissionId }
                                    Text("Assigned to $assignedCount users", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedPermission = permission
                                        showEditPermission = true
                                        permName = TextFieldValue(permission.name)
                                        permDescription = TextFieldValue(permission.description ?: "")
                                        permCategory = TextFieldValue(permission.category ?: "")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedPermission = permission
                                        showDeletePermission = true
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("User-Permission Assignments", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            if (userPermissions.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No permission assignments yet", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(userPermissions) { up ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text("User ID: ${up.userId}", style = MaterialTheme.typography.bodyMedium)
                                val perm = permissions.find { it.permissionId == up.permissionId }
                                perm?.let {
                                    Text("Permission: ${it.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                                Text("Granted: ${up.grantedAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Permission Dialog
    if (showCreatePermission) {
        AlertDialog(
            onDismissRequest = { showCreatePermission = false },
            title = { Text("Add Permission") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = permName, onValueChange = { permName = it }, label = { Text("Permission Name") }, singleLine = true)
                    OutlinedTextField(value = permDescription, onValueChange = { permDescription = it }, label = { Text("Description (optional)") }, singleLine = true)
                    OutlinedTextField(value = permCategory, onValueChange = { permCategory = it }, label = { Text("Category (optional)") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (permName.text.isNotBlank()) {
                        scope.launch {
                            db.permissionDao().insert(
                                PermissionEntity(
                                    name = permName.text,
                                    description = permDescription.text.ifBlank { null },
                                    category = permCategory.text.ifBlank { null }
                                )
                            )
                            showCreatePermission = false
                            permName = TextFieldValue("")
                            permDescription = TextFieldValue("")
                            permCategory = TextFieldValue("")
                        }
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePermission = false }) { Text("Cancel") }
            }
        )
    }

    // Edit Permission Dialog
    if (showEditPermission && selectedPermission != null) {
        AlertDialog(
            onDismissRequest = { showEditPermission = false },
            title = { Text("Edit Permission") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = permName, onValueChange = { permName = it }, label = { Text("Permission Name") }, singleLine = true)
                    OutlinedTextField(value = permDescription, onValueChange = { permDescription = it }, label = { Text("Description") }, singleLine = true)
                    OutlinedTextField(value = permCategory, onValueChange = { permCategory = it }, label = { Text("Category") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (permName.text.isNotBlank()) {
                        scope.launch {
                            db.permissionDao().update(
                                selectedPermission!!.copy(
                                    name = permName.text,
                                    description = permDescription.text.ifBlank { null },
                                    category = permCategory.text.ifBlank { null }
                                )
                            )
                            showEditPermission = false
                            selectedPermission = null
                        }
                    }
                }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { showEditPermission = false; selectedPermission = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Permission Dialog
    if (showDeletePermission && selectedPermission != null) {
        AlertDialog(
            onDismissRequest = { showDeletePermission = false; selectedPermission = null },
            title = { Text("Delete Permission") },
            text = { Text("Are you sure you want to delete this permission? This will also remove all user assignments for this permission.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        db.permissionDao().deleteById(selectedPermission!!.permissionId)
                        showDeletePermission = false
                        selectedPermission = null
                    }
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePermission = false; selectedPermission = null }) { Text("Cancel") }
            }
        )
    }
}

/**
 * Helper function to fetch permissions from API.
 */
private fun fetchPermissionsFromApi(
    permissionRepository: PermissionRepositoryImpl,
    userPermissionRepository: UserPermissionRepositoryImpl,
    scope: kotlinx.coroutines.CoroutineScope,
    onLoading: (Boolean, String?) -> Unit
) {
    scope.launch {
        onLoading(true, null)
        try {
            val token = "" // TODO: Get actual auth token
            if (token.isNotEmpty()) {
                // Fetch permissions
                val permResult = permissionRepository.fetchFromApi(token)
                if (permResult.isFailure) {
                    onLoading(false, permResult.exceptionOrNull()?.message)
                    return@launch
                }
                
                // Fetch user permissions
                val userPermResult = userPermissionRepository.fetchFromApi(token)
                if (userPermResult.isFailure) {
                    onLoading(false, userPermResult.exceptionOrNull()?.message)
                    return@launch
                }
                
                onLoading(false, null)
            } else {
                onLoading(false, "No authentication token available")
            }
        } catch (e: Exception) {
            onLoading(false, "Failed to fetch permissions: ${e.message}")
        }
    }
}
