package com.yourdomain.adoptionchildcare

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.yourdomain.adoptionchildcare.data.AppDatabase
import com.yourdomain.adoptionchildcare.data.entities.UserEntity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    // UI Components
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var dashboardLayout: LinearLayout
    private lateinit var childrenButton: Button
    private lateinit var usersButton: Button
    private lateinit var documentsButton: Button
    private lateinit var reportsButton: Button
    private lateinit var logoutButton: Button
    private lateinit var welcomeText: TextView
    private lateinit var statusText: TextView
    
    // Database
    private lateinit var database: AppDatabase
    
    // Authentication state - always logged in for mobile app
    private var isLoggedIn = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize Room database
        database = AppDatabase.getInstance(this)
        
        initializeViews()
        setupToolbarAndDrawer()
        setupClickListeners()
        showDashboard()
        initializeDatabase()
    }
    
    private fun initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        dashboardLayout = findViewById(R.id.dashboardLayout)
        childrenButton = findViewById(R.id.childrenButton)
        usersButton = findViewById(R.id.usersButton)
        documentsButton = findViewById(R.id.documentsButton)
        reportsButton = findViewById(R.id.reportsButton)
        logoutButton = findViewById(R.id.logoutButton)
        welcomeText = findViewById(R.id.welcomeText)
        statusText = findViewById(R.id.statusText)
    }
    
    private fun setupToolbarAndDrawer() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupClickListeners() {
        childrenButton?.setOnClickListener {
            showChildrenManagement()
        }
        
        usersButton?.setOnClickListener {
            showUsersList()
        }
        
        documentsButton?.setOnClickListener {
            showDocumentsManagement()
        }
        
        reportsButton?.setOnClickListener {
            showReports()
        }
        
        logoutButton?.setOnClickListener {
            // For mobile app, logout just shows a message
            Toast.makeText(this, "Mobile app - always connected to PC", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun initializeDatabase() {
        lifecycleScope.launch {
            try {
                // Check if we have any users in the database
                val userCount = database.userDao().getAllUsers().size
                statusText?.text = "Room Database: $userCount users | Ready for offline/online sync"
                Toast.makeText(this@MainActivity, "Room Database initialized successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                statusText?.text = "Database Error: ${e.message}"
                Toast.makeText(this@MainActivity, "Database initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showDashboard() {
        welcomeText?.text = "Welcome to Adoption & Child Care App"
        statusText?.text = "Room Database Active | Can sync with PC Database"
        dashboardLayout?.visibility = View.VISIBLE
    }
    
    private fun showChildrenManagement() {
        lifecycleScope.launch {
            try {
                val children = database.userDao().getAllUsers() // Using users as example
                Toast.makeText(this@MainActivity, "Children Management - Room DB: ${children.size} records", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Children Management - Database Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showUsersList() {
        lifecycleScope.launch {
            try {
                val users = database.userDao().getAllUsers()
                Toast.makeText(this@MainActivity, "Users Management - Room DB: ${users.size} users", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Users Management - Database Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showDocumentsManagement() {
        lifecycleScope.launch {
            try {
                val documents = database.documentDao().getAllDocuments()
                Toast.makeText(this@MainActivity, "Documents Management - Room DB: ${documents.size} documents", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Documents Management - Database Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showReports() {
        lifecycleScope.launch {
            try {
                val auditLogs = database.auditLogDao().getAllAuditLogs()
                Toast.makeText(this@MainActivity, "Reports - Room DB: ${auditLogs.size} audit logs", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Reports - Database Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> showDashboard()
            R.id.nav_children -> showChildrenManagement()
            R.id.nav_users -> showUsersList()
            R.id.nav_documents -> showDocumentsManagement()
            R.id.nav_reports -> showReports()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    
    override fun onBackPressed() {
        if (this::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }
}
