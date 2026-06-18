package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.viewmodel.UserManagementViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for managing system users.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onBack: () -> Unit = {},
    viewModel: UserManagementViewModel = hiltViewModel(),
) {
    var users by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<UserEntity?>(null) }

    val defaultRole = stringResource(R.string.role_staff)
    var usernameState by remember { mutableStateOf(TextFieldValue("")) }
    var emailState by remember { mutableStateOf(TextFieldValue("")) }
    var roleState by remember { mutableStateOf(defaultRole) }
    
    val roles = listOf(
        stringResource(R.string.role_admin),
        stringResource(R.string.role_case_worker),
        stringResource(R.string.role_guardian),
        stringResource(R.string.role_social_worker),
        stringResource(R.string.role_supervisor),
        stringResource(R.string.role_staff)
    )
    var showRoleDropdown by remember { mutableStateOf(false) }
    val sampleHash = stringResource(R.string.user_management_sample_hash)

    // Load from local DB
    LaunchedEffect(Unit) {
        viewModel.users.collectLatest {
            users = it
        }
    }

    val current = selectedUser
    if (showDetails && current != null) {
        UserDetailScreen(
            user = current,
            onBack = { showDetails = false; selectedUser = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.user_management_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.user_management_back))
                        }
                    },
                    actions = {
                        IconButton(onClick = { scope.launch { viewModel.fetchUsersFromApi() } }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.reports_refresh_desc))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.user_management_add_user))
                }
            }
        ) { padding ->
            UserList(
                users = users,
                modifier = Modifier.padding(padding),
                onEdit = { user ->
                    selectedUser = user
                    usernameState = TextFieldValue(user.username)
                    emailState = TextFieldValue(user.email ?: "")
                    roleState = user.role
                    showEditDialog = true
                },
                onDelete = { user ->
                    selectedUser = user
                    showDeleteDialog = true
                },
                onUserClick = { user ->
                    selectedUser = user
                    showDetails = true
                }
            )
        }
    }

    if (showCreate) {
        UserFormDialog(
            title = stringResource(R.string.user_management_add_user),
            confirmLabel = stringResource(R.string.user_management_save),
            username = usernameState,
            onUsernameChange = { usernameState = it },
            email = emailState,
            onEmailChange = { emailState = it },
            role = roleState,
            onRoleChange = { roleState = it },
            roles = roles,
            showRoleDropdown = showRoleDropdown,
            onToggleRoleDropdown = { showRoleDropdown = it },
            onDismiss = { showCreate = false },
            onConfirm = {
                if (usernameState.text.isNotBlank()) {
                    scope.launch {
                        viewModel.insert(UserEntity(username = usernameState.text, email = emailState.text.ifBlank { null }, role = roleState, passwordHash = sampleHash))
                        showCreate = false
                        usernameState = TextFieldValue("")
                        emailState = TextFieldValue("")
                        roleState = defaultRole
                    }
                }
            }
        )
    }

    if (showEditDialog && (selectedUser != null)) {
        UserFormDialog(
            title = stringResource(R.string.user_management_edit_user),
            confirmLabel = stringResource(R.string.user_management_update),
            username = usernameState,
            onUsernameChange = { usernameState = it },
            email = emailState,
            onEmailChange = { emailState = it },
            role = roleState,
            onRoleChange = { roleState = it },
            roles = roles,
            showRoleDropdown = showRoleDropdown,
            onToggleRoleDropdown = { showRoleDropdown = it },
            onDismiss = { showEditDialog = false; selectedUser = null },
            onConfirm = {
                if (usernameState.text.isNotBlank()) {
                    scope.launch {
                        selectedUser?.let {
                            val updated = it.copy(username = usernameState.text, email = emailState.text.ifBlank { null }, role = roleState)
                            viewModel.update(updated)
                            showEditDialog = false
                            selectedUser = null
                        }
                    }
                }
            }
        )
    }

    if (showDeleteDialog && (selectedUser != null)) {
        DeleteConfirmationDialog(
            user = selectedUser,
            onDismiss = { showDeleteDialog = false; selectedUser = null },
            onConfirm = {
                scope.launch {
                    selectedUser?.let {
                        viewModel.deleteById(it.userId)
                        showDeleteDialog = false
                        selectedUser = null
                    }
                }
            }
        )
    }
}

@Composable
private fun UserList(
    users: List<UserEntity>,
    modifier: Modifier = Modifier,
    onEdit: (UserEntity) -> Unit,
    onDelete: (UserEntity) -> Unit,
    onUserClick: (UserEntity) -> Unit
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        if (users.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Group, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.user_management_no_users), style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(users) { user ->
                    UserItemCard(
                        user = user,
                        onEdit = { onEdit(user) },
                        onDelete = { onDelete(user) },
                        onClick = { onUserClick(user) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserItemCard(
    user: UserEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.username, style = MaterialTheme.typography.titleMedium)
                Text(text = stringResource(R.string.user_management_role, user.role), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                user.email?.let { Text(text = stringResource(R.string.user_management_email, it), style = MaterialTheme.typography.bodySmall) }
                Text(text = stringResource(R.string.user_management_status, if (user.isActive) stringResource(R.string.user_management_active) else stringResource(R.string.user_management_inactive)), style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.user_management_edit), tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.user_management_delete), tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserFormDialog(
    title: String,
    confirmLabel: String,
    username: TextFieldValue,
    onUsernameChange: (TextFieldValue) -> Unit,
    email: TextFieldValue,
    onEmailChange: (TextFieldValue) -> Unit,
    role: String,
    onRoleChange: (String) -> Unit,
    roles: List<String>,
    showRoleDropdown: Boolean,
    onToggleRoleDropdown: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = username, onValueChange = onUsernameChange, label = { Text(stringResource(R.string.user_management_username_label)) }, singleLine = true)
                OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text(stringResource(R.string.user_management_email_label)) }, singleLine = true)
                ExposedDropdownMenuBox(expanded = showRoleDropdown, onExpandedChange = onToggleRoleDropdown) {
                    OutlinedTextField(value = role, onValueChange = { }, readOnly = true, label = { Text(stringResource(R.string.user_management_role_label)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRoleDropdown) }, modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth())
                    ExposedDropdownMenu(expanded = showRoleDropdown, onDismissRequest = { onToggleRoleDropdown(false) }) {
                        roles.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { onRoleChange(it); onToggleRoleDropdown(false) }) }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmLabel) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.user_management_cancel)) } }
    )
}

@Composable
private fun DeleteConfirmationDialog(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.user_management_delete_user)) },
        text = { Text(stringResource(R.string.user_management_delete_confirm, user?.username ?: "")) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.user_management_delete), color = MaterialTheme.colorScheme.error) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.user_management_cancel)) } }
    )
}
