package com.yourdomain.adoptionchildcare

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    // UI Components
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var dashboardLayout: LinearLayout
    private lateinit var childrenButton: Button
    private lateinit var usersButton: Button
    private lateinit var documentsButton: Button
    private lateinit var reportsButton: Button
    private lateinit var logoutButton: Button
    
    // Authentication state
    private var isLoggedIn = false
    private var currentUser: User? = null
    
    // Repository for database operations
    private val userRepository = UserRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupClickListeners()
        showLoginScreen()
    }
    
    private fun initializeViews() {
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        dashboardLayout = findViewById(R.id.dashboardLayout)
        childrenButton = findViewById(R.id.childrenButton)
        usersButton = findViewById(R.id.usersButton)
        documentsButton = findViewById(R.id.documentsButton)
        reportsButton = findViewById(R.id.reportsButton)
        logoutButton = findViewById(R.id.logoutButton)
    }
    
    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            showLoginDialog()
        }
        
        registerButton.setOnClickListener {
            showRegisterDialog()
        }
        
        childrenButton.setOnClickListener {
            Toast.makeText(this, "Children Management Screen", Toast.LENGTH_SHORT).show()
        }
        
        usersButton.setOnClickListener {
            showUsersList()
        }
        
        documentsButton.setOnClickListener {
            Toast.makeText(this, "Documents Management Screen", Toast.LENGTH_SHORT).show()
        }
        
        reportsButton.setOnClickListener {
            Toast.makeText(this, "Reports Screen", Toast.LENGTH_SHORT).show()
        }
        
        logoutButton.setOnClickListener {
            logout()
        }
    }
    
    private fun showLoginScreen() {
        loginButton.visibility = View.VISIBLE
        registerButton.visibility = View.VISIBLE
        dashboardLayout.visibility = View.GONE
        isLoggedIn = false
        currentUser = null
    }
    
    private fun showDashboard() {
        loginButton.visibility = View.GONE
        registerButton.visibility = View.GONE
        dashboardLayout.visibility = View.VISIBLE
        isLoggedIn = true
    }
    
    private fun showLoginDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.login_dialog, null)
        
        val usernameEdit = dialogView.findViewById<EditText>(R.id.usernameEdit)
        val passwordEdit = dialogView.findViewById<EditText>(R.id.passwordEdit)
        val loginBtn = dialogView.findViewById<Button>(R.id.loginBtn)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)
        
        val alertDialog = dialog.create()
        
        loginBtn.setOnClickListener {
            val username = usernameEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Show progress and disable button
                progressBar.visibility = View.VISIBLE
                loginBtn.isEnabled = false
                loginBtn.text = "Logging in..."
                
                // Perform login with database
                lifecycleScope.launch {
                    val result = userRepository.login(username, password)
                    
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        loginBtn.isEnabled = true
                        loginBtn.text = "Login"
                        
                        result.fold(
                            onSuccess = { user ->
                                currentUser = user
                                Toast.makeText(this@MainActivity, "Login successful! Welcome ${user.username}", Toast.LENGTH_SHORT).show()
                                alertDialog.dismiss()
                                showDashboard()
                            },
                            onFailure = { error ->
                                Toast.makeText(this@MainActivity, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            }
        }
        
        alertDialog.setView(dialogView)
        alertDialog.show()
    }
    
    private fun showRegisterDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.register_dialog, null)
        
        val emailEdit = dialogView.findViewById<EditText>(R.id.emailEdit)
        val passwordEdit = dialogView.findViewById<EditText>(R.id.passwordEdit)
        val registerBtn = dialogView.findViewById<Button>(R.id.registerBtn)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)
        
        val alertDialog = dialog.create()
        
        registerBtn.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            } else {
                // Show progress and disable button
                progressBar.visibility = View.VISIBLE
                registerBtn.isEnabled = false
                registerBtn.text = "Registering..."
                
                // Perform registration with database
                lifecycleScope.launch {
                    val username = email.substringBefore("@") // Use email prefix as username
                    val result = userRepository.register(username, email, password)
                    
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        registerBtn.isEnabled = true
                        registerBtn.text = "Register"
                        
                        result.fold(
                            onSuccess = { user ->
                                Toast.makeText(this@MainActivity, "Registration successful! You can now login", Toast.LENGTH_SHORT).show()
                                alertDialog.dismiss()
                            },
                            onFailure = { error ->
                                Toast.makeText(this@MainActivity, "Registration failed: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            }
        }
        
        alertDialog.setView(dialogView)
        alertDialog.show()
    }
    
    private fun showUsersList() {
        lifecycleScope.launch {
            val result = userRepository.getAllUsers()
            
            result.fold(
                onSuccess = { users ->
                    val userNames = users.joinToString("\n") { "${it.username} (${it.email})" }
                    Toast.makeText(this@MainActivity, "Users in database:\n$userNames", Toast.LENGTH_LONG).show()
                },
                onFailure = { error ->
                    Toast.makeText(this@MainActivity, "Failed to fetch users: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    private fun logout() {
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        showLoginScreen()
    }
    
    override fun onBackPressed() {
        if (isLoggedIn) {
            logout()
        } else {
            super.onBackPressed()
        }
    }
}
