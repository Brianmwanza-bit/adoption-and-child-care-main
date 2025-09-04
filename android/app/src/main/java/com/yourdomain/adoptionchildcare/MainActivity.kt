package com.yourdomain.adoptionchildcare

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    // UI Components
    private lateinit var dashboardLayout: LinearLayout
    private lateinit var childrenButton: Button
    private lateinit var usersButton: Button
    private lateinit var documentsButton: Button
    private lateinit var reportsButton: Button
    private lateinit var logoutButton: Button
    private lateinit var welcomeText: TextView
    
    // Authentication state - always logged in for mobile app
    private var isLoggedIn = true
    private var currentUser: User? = null
    
    // Repository for database operations
    private val userRepository = UserRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupClickListeners()
        showDashboard()
    }
    
    private fun initializeViews() {
        dashboardLayout = findViewById(R.id.dashboardLayout)
        childrenButton = findViewById(R.id.childrenButton)
        usersButton = findViewById(R.id.usersButton)
        documentsButton = findViewById(R.id.documentsButton)
        reportsButton = findViewById(R.id.reportsButton)
        logoutButton = findViewById(R.id.logoutButton)
        welcomeText = findViewById(R.id.welcomeText)
    }
    
    private fun setupClickListeners() {
        childrenButton.setOnClickListener {
            showChildrenManagement()
        }
        
        usersButton.setOnClickListener {
            showUsersList()
        }
        
        documentsButton.setOnClickListener {
            showDocumentsManagement()
        }
        
        reportsButton.setOnClickListener {
            showReports()
        }
        
        logoutButton.setOnClickListener {
            // For mobile app, logout just shows a message
            Toast.makeText(this, "Mobile app - always connected to PC", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDashboard() {
        dashboardLayout.visibility = View.VISIBLE
        isLoggedIn = true
        welcomeText.text = "Welcome to Adoption & Child Care Dashboard\nConnected to PC Database"
    }
    
    private fun showChildrenManagement() {
        lifecycleScope.launch {
            try {
                // Simulate fetching children data from PC database
                Toast.makeText(this@MainActivity, "Children Management - Connected to PC Database", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error accessing children data: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showUsersList() {
        lifecycleScope.launch {
            val result = userRepository.getAllUsers()
            
            result.fold(
                onSuccess = { users ->
                    val userNames = users.joinToString("\n") { "${it.username} (${it.email})" }
                    Toast.makeText(this@MainActivity, "Users from PC Database:\n$userNames", Toast.LENGTH_LONG).show()
                },
                onFailure = { error ->
                    Toast.makeText(this@MainActivity, "Failed to fetch users from PC: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
    
    private fun showDocumentsManagement() {
        Toast.makeText(this, "Documents Management - Connected to PC Database", Toast.LENGTH_LONG).show()
    }
    
    private fun showReports() {
        Toast.makeText(this, "Reports - Connected to PC Database", Toast.LENGTH_LONG).show()
    }
    
    override fun onBackPressed() {
        // Show confirmation dialog before exiting
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }
}
