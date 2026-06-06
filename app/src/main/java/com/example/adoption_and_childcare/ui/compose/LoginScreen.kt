package com.example.adoption_and_childcare.ui.compose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.data.session.SessionManager
import com.example.adoption_and_childcare.utils.Security
import com.example.adoption_and_childcare.viewmodel.UserManagementViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (UserEntity) -> Unit,
    viewModel: UserManagementViewModel? = null
) {
    val actualViewModel = viewModel ?: hiltViewModel<UserManagementViewModel>()
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Login, 1 = Register
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profilePhotoUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF9C27B0).copy(alpha = 0.1f),
                        Color(0xFF673AB7).copy(alpha = 0.1f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(360.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Profile photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f))
                            .border(2.dp, Color(0xFF9C27B0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profilePhotoUri != null) {
                            AsyncImage(
                                model = profilePhotoUri,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(text = "👤", fontSize = 40.sp)
                        }
                        IconButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .background(Color(0xFF9C27B0), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Tab row for Login/Register
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF9C27B0),
                            height = 4.dp
                        )
                    }
                ) {
                    Tab(
                        text = { Text("Login", fontWeight = FontWeight.Bold) },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        selectedContentColor = Color(0xFF9C27B0),
                        unselectedContentColor = Color.Gray
                    )
                    Tab(
                        text = { Text("Register", fontWeight = FontWeight.Bold) },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        selectedContentColor = Color(0xFF9C27B0),
                        unselectedContentColor = Color.Gray
                    )
                }

                // Content based on selected tab
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)) {
                    when (selectedTab) {
                        0 -> LoginFormContent(
                            profilePhotoUri = profilePhotoUri,
                            onLoginSuccess = { user ->
                                session.saveSession(user)
                                onLoginSuccess(user)
                            },
                            viewModel = actualViewModel,
                            db = db
                        )
                        1 -> RegisterFormContent(
                            profilePhotoUri = profilePhotoUri,
                            viewModel = actualViewModel,
                            db = db
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginFormContent(
    profilePhotoUri: Uri?,
    onLoginSuccess: (UserEntity) -> Unit,
    viewModel: UserManagementViewModel,
    db: AppDatabase
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val biometricManager = remember {
        com.example.adoption_and_childcare.data.security.BiometricAuthManager(context)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = "" },
            label = { Text("Email or Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF9C27B0),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = "" },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF9C27B0),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            )
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in all fields"
                } else {
                    isLoading = true
                    scope.launch {
                        try {
                            val userDao = db.userDao()
                            val userByEmail = userDao.findByEmail(email)
                            val user = userByEmail ?: userDao.findByUsername(email)
                            val hashed = Security.hashPassword(password)
                            if (user != null && user.passwordHash == hashed) {
                                onLoginSuccess(user)
                            } else {
                                errorMessage = "Invalid credentials"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Login failed"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Biometric login option (for demonstration - will be fully integrated in Phase 2)
        if (biometricManager.isBiometricAvailable()) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Button(
                onClick = {
                    // Placeholder - will integrate with BiometricPrompt in Phase 2
                    errorMessage = "Biometric login coming soon! Register first with password."
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("👆 Use Fingerprint", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun RegisterFormContent(
    profilePhotoUri: Uri?,
    viewModel: UserManagementViewModel,
    db: AppDatabase
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var showOccupationDropdown by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val occupations = listOf("Admin", "Case Worker", "Foster Parent", "Social Worker", "Supervisor")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it; errorMessage = "" },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF9C27B0),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            )
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = "" },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF9C27B0),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = "" },
            label = { Text("Password (min 6 chars)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF9C27B0),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            )
        )

        ExposedDropdownMenuBox(
            expanded = showOccupationDropdown,
            onExpandedChange = { showOccupationDropdown = !showOccupationDropdown }
        ) {
            OutlinedTextField(
                value = occupation,
                onValueChange = { },
                readOnly = true,
                label = { Text("Role") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showOccupationDropdown) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF9C27B0),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )
            ExposedDropdownMenu(
                expanded = showOccupationDropdown,
                onDismissRequest = { showOccupationDropdown = false }
            ) {
                occupations.forEach { occ ->
                    DropdownMenuItem(
                        text = { Text(occ) },
                        onClick = {
                            occupation = occ
                            showOccupationDropdown = false
                        }
                    )
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Button(
            onClick = {
                when {
                    username.isBlank() || email.isBlank() || password.isBlank() || occupation.isBlank() ->
                        errorMessage = "Please fill in all fields"
                    password.length < 6 ->
                        errorMessage = "Password must be at least 6 characters"
                    else -> {
                        isLoading = true
                        scope.launch {
                            try {
                                val userDao = db.userDao()
                                val existing = userDao.findByEmail(email) ?: userDao.findByUsername(username)
                                if (existing != null) {
                                    errorMessage = "User already exists"
                                } else {
                                    val entity = UserEntity(
                                        username = username,
                                        passwordHash = Security.hashPassword(password),
                                        role = occupation,
                                        email = email
                                    )
                                    val id = userDao.insert(entity)
                                    if (id > 0) {
                                        errorMessage = "Registration successful! Please log in."
                                        username = ""
                                        email = ""
                                        password = ""
                                        occupation = ""
                                    } else {
                                        errorMessage = "Registration failed"
                                    }
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Registration failed"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Register", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
