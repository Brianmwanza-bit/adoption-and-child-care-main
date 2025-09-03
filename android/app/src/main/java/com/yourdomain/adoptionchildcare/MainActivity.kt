package com.yourdomain.adoptionchildcare

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

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
            Toast.makeText(this, "Users Management Screen", Toast.LENGTH_SHORT).show()
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
        
        val alertDialog = dialog.create()
        
        loginBtn.setOnClickListener {
            val username = usernameEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Simulate login validation
                if (validateLogin(username, password)) {
                    Toast.makeText(this, "Login successful! Welcome $username", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                    showDashboard()
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
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
                // Simulate registration
                Toast.makeText(this, "Registration successful! You can now login", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
        }
        
        alertDialog.setView(dialogView)
        alertDialog.show()
    }
    
    private fun validateLogin(username: String, password: String): Boolean {
        // Simple validation - in real app, this would check against database
        return username.isNotEmpty() && password.length >= 6
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
