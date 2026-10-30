package com.example.adoption_and_childcare.ui.compose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.material3.ExperimentalMaterial3Api
import coil.compose.AsyncImage
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.data.model.UserRole
import com.example.adoption_and_childcare.data.security.BiometricAuthManager
import com.example.adoption_and_childcare.utils.AutoSaveManager
import com.example.adoption_and_childcare.viewmodel.UserManagementViewModel
import com.example.adoption_and_childcare.R
import kotlinx.coroutines.launch

private fun getPasswordForUser(u: UserEntity): String {
    return when {
        u.username.contains("admin", ignoreCase = true) -> "admin123"
        u.username.startsWith("user_") -> {
            val numStr = u.username.removePrefix("user_")
            "password$numStr"
        }
        else -> "password1"
    }
}

/**
 * A reusable background component for authentication screens.
 *
 * @param content The composable content to be displayed on top of the blue background.
 */
@Composable
fun AuthBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3)), // Blue background
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Screen for user login.
 *
 * @param onLoginSuccess Callback triggered when login is successful, providing the [UserEntity].
 * @param onNavigateToRegister Callback to navigate to the registration screen.
 * @param successMessage Optional message to display upon navigation (e.g., after successful registration).
 * @param viewModel ViewModel for user management operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (UserEntity) -> Unit,
    onNavigateToRegister: () -> Unit,
    successMessage: String = "",
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val autoSave = remember { AutoSaveManager(context) }
    val biometricAuthManager = remember { BiometricAuthManager(context) }

    val roomUsers by viewModel.users.collectAsState(initial = emptyList())
    var showAutofillDropdown by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf(autoSave.getLoginField(AutoSaveManager.LOGIN_EMAIL)) }
    var password by remember { mutableStateOf(autoSave.getLoginField(AutoSaveManager.LOGIN_PASSWORD)) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var infoMessage by remember { mutableStateOf(successMessage) }
    var isLoading by remember { mutableStateOf(false) }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> profilePhotoUri = uri }

    AuthBackground {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Photo / Avatar Container
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                        .border(3.dp, Color(0xFF2196F3), CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePhotoUri != null) {
                        AsyncImage(
                            model = profilePhotoUri,
                            contentDescription = stringResource(R.string.profile_photo_desc),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = Color(0xFF2196F3)
                            )
                            Text(
                                text = stringResource(R.string.profile_photo_desc),
                                fontSize = 9.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2196F3))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.login_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )

                // Room DB Quick Autofill Chips
                if (roomUsers.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Storage,
                                contentDescription = "Room DB",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF2196F3)
                            )
                            Text(
                                text = "Autofill from Room DB:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(roomUsers) { u ->
                                val targetEmail = u.email.takeIf { !it.isNullOrBlank() } ?: u.username
                                val targetPassword = getPasswordForUser(u)
                                AssistChip(
                                    onClick = {
                                        email = targetEmail
                                        password = targetPassword
                                        autoSave.saveLoginField(AutoSaveManager.LOGIN_EMAIL, targetEmail)
                                        autoSave.saveLoginField(AutoSaveManager.LOGIN_PASSWORD, targetPassword)
                                    },
                                    label = {
                                        Text(
                                            text = "${u.username} (${u.role})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.AccountCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color(0xFF2196F3)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // Email Input Box with Room DB Autofill Dropdown
                ExposedDropdownMenuBox(
                    expanded = showAutofillDropdown && roomUsers.isNotEmpty(),
                    onExpandedChange = { showAutofillDropdown = it }
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            autoSave.saveLoginField(AutoSaveManager.LOGIN_EMAIL, it)
                            showAutofillDropdown = true
                        },
                        label = { Text(stringResource(R.string.login_email_hint)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF2196F3)) },
                        trailingIcon = {
                            if (roomUsers.isNotEmpty()) {
                                IconButton(onClick = { showAutofillDropdown = !showAutofillDropdown }) {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = "Autofill from Room DB",
                                        tint = Color(0xFF2196F3)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    )

                    val filteredUsers = roomUsers.filter { user ->
                        val userEmail = user.email ?: ""
                        val username = user.username
                        email.isBlank() || userEmail.contains(email, ignoreCase = true) || username.contains(email, ignoreCase = true)
                    }

                    if (showAutofillDropdown && filteredUsers.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = showAutofillDropdown,
                            onDismissRequest = { showAutofillDropdown = false }
                        ) {
                            Text(
                                text = "Autofill saved user from Room DB",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                            HorizontalDivider()
                            filteredUsers.forEach { user ->
                                val autofillValue = user.email.takeIf { !it.isNullOrBlank() } ?: user.username
                                val targetPassword = getPasswordForUser(user)
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = autofillValue,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "Username: ${user.username} • Role: ${user.role}",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color(0xFF2196F3)
                                        )
                                    },
                                    onClick = {
                                        email = autofillValue
                                        password = targetPassword
                                        autoSave.saveLoginField(AutoSaveManager.LOGIN_EMAIL, autofillValue)
                                        autoSave.saveLoginField(AutoSaveManager.LOGIN_PASSWORD, targetPassword)
                                        showAutofillDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Password Input Box with View/Hide prompt & toggle
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        autoSave.saveLoginField(AutoSaveManager.LOGIN_PASSWORD, it)
                    },
                    label = { Text(stringResource(R.string.login_password_hint)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF2196F3)) },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { isPasswordVisible = !isPasswordVisible }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isPasswordVisible) "Hide" else "Show",
                                fontSize = 12.sp,
                                color = Color(0xFF2196F3),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = TextStyle(color = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                )

                if (infoMessage.isNotEmpty()) {
                    Text(infoMessage, color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                }

                val fillAllFieldsError = stringResource(R.string.error_fill_all_fields)
                val loginFailedMsg = stringResource(R.string.login_error_failed)
                val tryAgainMsg = stringResource(R.string.error_try_again)

                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        val trimmedPassword = password.trim()
                        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
                            errorMessage = fillAllFieldsError
                        } else {
                            isLoading = true
                            errorMessage = ""
                            scope.launch {
                                try {
                                    val result = viewModel.login(trimmedEmail, trimmedPassword)
                                    result.onSuccess { userEntity: UserEntity ->
                                        autoSave.clearLoginData()
                                        onLoginSuccess(userEntity)
                                    }.onFailure { e: Throwable ->
                                        errorMessage = e.message ?: loginFailedMsg
                                    }
                                } catch (e: Exception) {
                                    errorMessage = context.getString(
                                        R.string.login_error_general,
                                        e.message ?: tryAgainMsg
                                    )
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(stringResource(R.string.login_button), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Biometric Authentication Call
                if (biometricAuthManager.isBiometricAvailable()) {
                    OutlinedButton(
                        onClick = {
                            val activity = context as? FragmentActivity
                            if (activity != null) {
                                biometricAuthManager.authenticate(
                                    activity = activity,
                                    onSuccess = {
                                        scope.launch {
                                            isLoading = true
                                            val loginEmail = email.ifBlank { "admin@adoptionchildcare.com" }
                                            val loginPass = password.ifBlank { "admin123" }
                                            val result = viewModel.login(loginEmail, loginPass)
                                            result.onSuccess { userEntity: UserEntity ->
                                                autoSave.clearLoginData()
                                                onLoginSuccess(userEntity)
                                            }.onFailure { e: Throwable ->
                                                errorMessage = e.message ?: "Biometric login failed"
                                            }
                                            isLoading = false
                                        }
                                    },
                                    onError = { err ->
                                        errorMessage = err
                                    }
                                )
                            } else {
                                errorMessage = "Biometric context unavailable"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF2196F3))
                    ) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color(0xFF2196F3))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign in with Biometrics", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onNavigateToRegister) {
                    Text(stringResource(R.string.register_prompt), color = Color(0xFF2196F3))
                }
            }
        }
    }
}

/**
 * Screen for user registration.
 *
 * @param onRegisterSuccess Callback triggered when registration is successful.
 * @param onNavigateToLogin Callback to navigate to the login screen.
 * @param viewModel ViewModel for user management operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val autoSave = remember { AutoSaveManager(context) }

    var username by remember { mutableStateOf(autoSave.getRegisterField(AutoSaveManager.REG_USERNAME)) }
    var email by remember { mutableStateOf(autoSave.getRegisterField(AutoSaveManager.REG_EMAIL)) }
    var password by remember { mutableStateOf(autoSave.getRegisterField(AutoSaveManager.REG_PASSWORD)) }
    var phone by remember { mutableStateOf(autoSave.getRegisterField(AutoSaveManager.REG_PHONE)) }
    var nationalIdNo by remember { mutableStateOf(autoSave.getRegisterField(AutoSaveManager.REG_NATIONAL_ID)) }
    var idNumber by remember { mutableStateOf(autoSave.getRegisterField(AutoSaveManager.REG_ID_NUMBER)) }
    var county by remember { mutableStateOf(autoSave.getRegisterField(AutoSaveManager.REG_COUNTY)) }
    var subCounty by remember { mutableStateOf(autoSave.getRegisterField(AutoSaveManager.REG_SUB_COUNTY)) }
    var occupationRoleName by remember { mutableStateOf(autoSave.getRegisterField(AutoSaveManager.REG_OCCUPATION)) }
    var isRegisterPasswordVisible by remember { mutableStateOf(false) }
    var showOccupationDropdown by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }

    val roles = UserRole.entries

    /** Callback that stores the selected profile photo URI, or null if selection was canceled. */
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> profilePhotoUri = uri }

    AuthBackground {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Profile Photo
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                        .border(3.dp, Color(0xFF2196F3), CircleShape)
                        .clickable { imagePickerLauncher.launch(context.getString(R.string.image_mime_type)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePhotoUri != null) {
                        AsyncImage(
                            model = profilePhotoUri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = Color(0xFF9E9E9E)
                            )
                            Text(
                                text = stringResource(R.string.profile_photo_desc),
                                fontSize = 9.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }
                    // Camera badge in bottom-end
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2196F3))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.register_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )

                // --- Core credential fields ---
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        autoSave.saveRegisterField(AutoSaveManager.REG_USERNAME, it)
                    },
                    label = { Text(stringResource(R.string.register_username_hint)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        autoSave.saveRegisterField(AutoSaveManager.REG_EMAIL, it)
                    },
                    label = { Text(stringResource(R.string.register_email_hint)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        autoSave.saveRegisterField(AutoSaveManager.REG_PASSWORD, it)
                    },
                    label = { Text(stringResource(R.string.register_password_hint)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { isRegisterPasswordVisible = !isRegisterPasswordVisible }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isRegisterPasswordVisible) "Hide" else "Show",
                                fontSize = 12.sp,
                                color = Color(0xFF2196F3),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (isRegisterPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isRegisterPasswordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (isRegisterPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        autoSave.saveRegisterField(AutoSaveManager.REG_PHONE, it)
                    },
                    label = { Text(stringResource(R.string.register_phone_hint)) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // --- Identification fields ---
                OutlinedTextField(
                    value = nationalIdNo,
                    onValueChange = {
                        nationalIdNo = it
                        autoSave.saveRegisterField(AutoSaveManager.REG_NATIONAL_ID, it)
                    },
                    label = { Text(stringResource(R.string.register_national_id_hint)) },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = idNumber,
                    onValueChange = {
                        idNumber = it
                        autoSave.saveRegisterField(AutoSaveManager.REG_ID_NUMBER, it)
                    },
                    label = { Text(stringResource(R.string.register_id_number_hint)) },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // --- Role ---
                ExposedDropdownMenuBox(
                    expanded = showOccupationDropdown,
                    onExpandedChange = { showOccupationDropdown = !showOccupationDropdown }
                ) {
                    OutlinedTextField(
                        value = if (occupationRoleName.isEmpty()) "" else UserRole.fromRoleName(occupationRoleName).getLabel(),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(stringResource(R.string.register_occupation_hint)) },
                        leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showOccupationDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showOccupationDropdown,
                        onDismissRequest = { showOccupationDropdown = false }
                    ) {
                        roles.forEach { role: UserRole ->
                            DropdownMenuItem(
                                text = { Text(role.getLabel()) },
                                onClick = {
                                    occupationRoleName = role.roleName
                                    autoSave.saveRegisterField(AutoSaveManager.REG_OCCUPATION, role.roleName)
                                    showOccupationDropdown = false
                                }
                            )
                        }
                    }
                }

                // --- Location fields ---
                OutlinedTextField(
                    value = county,
                    onValueChange = {
                        county = it
                        autoSave.saveRegisterField(AutoSaveManager.REG_COUNTY, it)
                    },
                    label = { Text(stringResource(R.string.register_county_hint)) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = subCounty,
                    onValueChange = {
                        subCounty = it
                        autoSave.saveRegisterField(AutoSaveManager.REG_SUB_COUNTY, it)
                    },
                    label = { Text(stringResource(R.string.register_sub_county_hint)) },
                    leadingIcon = { Icon(Icons.Default.Map, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                }

                val fillAllFieldsError = stringResource(R.string.error_fill_all_fields)
                val registerSuccessMsg = stringResource(R.string.register_success_msg)
                val registerFailedMsg = stringResource(R.string.register_error_failed)
                val tryAgainMsg = stringResource(R.string.error_try_again)

                Button(
                    onClick = {
                        val trimmedUsername = username.trim()
                        val trimmedEmail = email.trim()
                        val trimmedPassword = password.trim()
                        val trimmedPhone = phone.trim()
                        if (trimmedUsername.isBlank() || trimmedEmail.isBlank() || trimmedPassword.isBlank() || occupationRoleName.isBlank() || trimmedPhone.isBlank()) {
                            errorMessage = fillAllFieldsError
                        } else {
                            isLoading = true
                            errorMessage = ""
                            scope.launch {
                                try {
                                    val entity = UserEntity(
                                        username = trimmedUsername,
                                        passwordHash = "", // Will be hashed on server or handled by repository
                                        role = occupationRoleName,
                                        email = trimmedEmail,
                                        phone = trimmedPhone,
                                        nationalIdNo = nationalIdNo.trim().ifBlank { null },
                                        idNumber = idNumber.trim().ifBlank { null },
                                        county = county.trim().ifBlank { null },
                                        subCounty = subCounty.trim().ifBlank { null }
                                    )
                                    
                                    val result = viewModel.register(entity, trimmedPassword)
                                    result.onSuccess {
                                        autoSave.clearRegisterData()
                                        onRegisterSuccess(registerSuccessMsg)
                                    }.onFailure { e: Throwable ->
                                        errorMessage = e.message ?: registerFailedMsg
                                    }
                                } catch (e: Exception) {
                                    errorMessage = context.getString(
                                        R.string.register_error_general,
                                        e.message ?: tryAgainMsg
                                    )
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(stringResource(R.string.register_button))
                    }
                }

                TextButton(onClick = onNavigateToLogin) {
                    Text(stringResource(R.string.login_prompt))
                }
            }
        }
    }
}
