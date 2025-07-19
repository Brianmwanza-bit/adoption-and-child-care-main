package com.example.adoptionchildcare

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.gridlayout.widget.GridLayout
import com.yourdomain.adoptionchildcare.SyncManager
import com.yourdomain.adoptionchildcare.TokenManager

class MainActivity : AppCompatActivity() {
    private lateinit var mainContainer: ConstraintLayout
    private lateinit var featuresGrid: GridLayout
    private lateinit var bottomNav: LinearLayout
    private lateinit var loginModal: FrameLayout
    private lateinit var registerModal: FrameLayout
    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var signInButton: Button
    private lateinit var loginError: TextView
    private lateinit var registerRole: EditText
    private lateinit var registerEmail: EditText
    private lateinit var registerButton: Button
    private lateinit var registerError: TextView
    private lateinit var registerSuccess: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.MainActivity)

        mainContainer = findViewById(R.id.main_container)
        featuresGrid = findViewById(R.id.featuresGrid)
        bottomNav = findViewById(R.id.bottomNav)
        loginModal = findViewById(R.id.loginModal)
        registerModal = findViewById(R.id.registerModal)
        loginUsername = findViewById(R.id.loginUsername)
        loginPassword = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)
        signInButton = findViewById(R.id.signInButton)
        loginError = findViewById(R.id.loginError)
        registerRole = findViewById(R.id.registerRole)
        registerEmail = findViewById(R.id.registerEmail)
        registerButton = findViewById(R.id.registerButton)
        registerError = findViewById(R.id.registerError)
        registerSuccess = findViewById(R.id.registerSuccess)

        // Show login modal on start
        showLoginModal()
        setupListeners()
        populateFeaturesGrid()
        // Schedule periodic sync for children
        SyncManager.scheduleChildrenSync(this)
    }

    private fun showLoginModal() {
        loginModal.visibility = View.VISIBLE
        registerModal.visibility = View.GONE
        mainContainer.visibility = View.GONE
        loginError.visibility = View.GONE
    }

    private fun showDashboard() {
        loginModal.visibility = View.GONE
        registerModal.visibility = View.GONE
        mainContainer.visibility = View.VISIBLE
    }

    private fun showRegisterModal() {
        loginModal.visibility = View.GONE
        registerModal.visibility = View.VISIBLE
        mainContainer.visibility = View.GONE
        registerError.visibility = View.GONE
        registerSuccess.visibility = View.GONE
    }

    private fun setupListeners() {
        loginButton.setOnClickListener {
            val username = loginUsername.text.toString().trim()
            val password = loginPassword.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                loginError.text = "Please enter both username and password."
                loginError.visibility = View.VISIBLE
            } else {
                // TODO: Replace with real backend call
                if (username == "admin" && password == "admin") {
                    showDashboard()
                    onLoginSuccess("dummy_token") // Example: Call this after successful login
                } else {
                    loginError.text = "Invalid credentials."
                    loginError.visibility = View.VISIBLE
                }
            }
        }
        signInButton.setOnClickListener {
            showRegisterModal()
        }
        registerButton.setOnClickListener {
            val role = registerRole.text.toString().trim()
            val email = registerEmail.text.toString().trim()
            if (role.isEmpty() || email.isEmpty()) {
                registerError.text = "Please fill all fields."
                registerError.visibility = View.VISIBLE
                registerSuccess.visibility = View.GONE
            } else {
                // TODO: Replace with real backend call
                registerError.visibility = View.GONE
                registerSuccess.text = "Registration successful! You can now log in."
                registerSuccess.visibility = View.VISIBLE
            }
        }
        // Hide modals when clicking outside (optional, not implemented here)
    }

    private fun populateFeaturesGrid() {
        val features = listOf(
            "Court", "Children", "PLACEMENT", "Medical", "Guardia", "Case Reports",
            "Money", "Education", "User", "Audit Logs", "Permissions", "User Permissions", "Documents"
        )
        featuresGrid.removeAllViews()
        for (feature in features) {
            val btn = Button(this)
            btn.text = feature
            btn.setOnClickListener {
                Toast.makeText(this, "$feature clicked", Toast.LENGTH_SHORT).show()
            }
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(8, 8, 8, 8)
            btn.layoutParams = params
            featuresGrid.addView(btn)
        }
    }

    // Example: Call this after successful login
    fun onLoginSuccess(jwtToken: String) {
        TokenManager.saveToken(this, jwtToken)
        // Optionally trigger immediate sync
        SyncManager.scheduleChildrenSync(this)
    }
} 