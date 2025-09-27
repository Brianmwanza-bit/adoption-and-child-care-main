package com.yourdomain.adoptionchildcare

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginDialog : DialogFragment() {
    
    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.login_dialog, container, false)
        
        // Initialize views
        usernameInput = view.findViewById(R.id.usernameEdit)
        passwordInput = view.findViewById(R.id.passwordEdit)
        loginButton = view.findViewById(R.id.loginBtn)
        
        // Set up click listeners
        loginButton.setOnClickListener {
            performLogin()
        }
        
        return view
    }
    
    private fun performLogin() {
        val username = usernameInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        // TODO: Implement actual login logic with API
        Toast.makeText(requireContext(), "Login successful for: $username", Toast.LENGTH_SHORT).show()
        dismiss()
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
