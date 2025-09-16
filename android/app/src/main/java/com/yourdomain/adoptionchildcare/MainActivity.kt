package com.yourdomain.adoptionchildcare

import android.os.Bundle
import android.view.MenuItem
<<<<<<< HEAD
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Setup drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Setup drawer toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, findViewById(R.id.toolbar),
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
=======
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

class MainActivity : AppCompatActivity() {
    
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
    
    // Authentication state - always logged in for mobile app
    private var isLoggedIn = true
    private var currentUser: User? = null
    
    // Repository for database operations
    private val userRepository = UserRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupToolbarAndDrawer()
        setupClickListeners()
        showDashboard()
    }
    
    private fun initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        dashboardLayout = findViewById(R.id.dashboardLayout)
        childrenButton = findViewById(R.id.childrenButton)
        usersButton = findViewById(R.id.usersButton)
        documentsButton = findViewById(R.id.documentsButton)
        reportsButton = findViewById(R.id.reportsButton)
        logoutButton = findViewById(R.id.logoutButton)
        welcomeText = findViewById(R.id.welcomeText)
    }
    
    private fun setupToolbarAndDrawer() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
>>>>>>> 94fde7d (UI color and glassy effect updates)
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

<<<<<<< HEAD
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
            navigationView.setCheckedItem(R.id.nav_dashboard)
=======
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_dashboard -> showDashboard()
                R.id.nav_children -> showChildrenManagement()
                R.id.nav_users -> showUsersList()
                R.id.nav_documents -> showDocumentsManagement()
                R.id.nav_reports -> showReports()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
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
>>>>>>> 94fde7d (UI color and glassy effect updates)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                loadFragment(DashboardFragment())
            }
            R.id.nav_children -> {
                loadFragment(ChildrenFragment())
            }
            R.id.nav_families -> {
                loadFragment(FamiliesFragment())
            }
            R.id.nav_documents -> {
                loadFragment(DocumentsFragment())
            }
            R.id.nav_reports -> {
                loadFragment(ReportsFragment())
            }
            R.id.nav_caregivers -> {
                loadFragment(CaregiversFragment())
            }
            R.id.nav_settings -> {
                loadFragment(SettingsFragment())
            }
            R.id.nav_logout -> {
                // Handle logout
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
<<<<<<< HEAD
}
=======
    
    private fun showDocumentsManagement() {
        Toast.makeText(this, "Documents Management - Connected to PC Database", Toast.LENGTH_LONG).show()
    }
    
    private fun showReports() {
        Toast.makeText(this, "Reports - Connected to PC Database", Toast.LENGTH_LONG).show()
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
>>>>>>> 94fde7d (UI color and glassy effect updates)
