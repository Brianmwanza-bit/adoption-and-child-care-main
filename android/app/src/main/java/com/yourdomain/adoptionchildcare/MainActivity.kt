package com.yourdomain.adoptionchildcare

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupUI()
    }
    
    private fun setupUI() {
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val childrenButton = findViewById<Button>(R.id.childrenButton)
        val usersButton = findViewById<Button>(R.id.usersButton)
        val documentsButton = findViewById<Button>(R.id.documentsButton)
        val reportsButton = findViewById<Button>(R.id.reportsButton)
        
        loginButton?.setOnClickListener {
            showLoginDialog()
        }
        
        registerButton?.setOnClickListener {
            showRegisterDialog()
        }
        
        childrenButton?.setOnClickListener {
            Toast.makeText(this, "Children Management Screen", Toast.LENGTH_SHORT).show()
        }
        
        usersButton?.setOnClickListener {
            Toast.makeText(this, "Users Management Screen", Toast.LENGTH_SHORT).show()
        }
        
        documentsButton?.setOnClickListener {
            Toast.makeText(this, "Documents Management Screen", Toast.LENGTH_SHORT).show()
        }
        
        reportsButton?.setOnClickListener {
            Toast.makeText(this, "Reports Screen", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showLoginDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.login_dialog, null)
        
        val usernameEdit = dialogView.findViewById<EditText>(R.id.usernameEdit)
        val passwordEdit = dialogView.findViewById<EditText>(R.id.passwordEdit)
        val loginBtn = dialogView.findViewById<Button>(R.id.loginBtn)
        
        val alertDialog = dialog.create()
        
        loginBtn.setOnClickListener {
            val username = usernameEdit.text.toString()
            val password = passwordEdit.text.toString()
            
            if (username.isNotEmpty() && password.isNotEmpty()) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
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
            val email = emailEdit.text.toString()
            val password = passwordEdit.text.toString()
            
            if (email.isNotEmpty() && password.isNotEmpty()) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        
        alertDialog.setView(dialogView)
        alertDialog.show()
    }
}
