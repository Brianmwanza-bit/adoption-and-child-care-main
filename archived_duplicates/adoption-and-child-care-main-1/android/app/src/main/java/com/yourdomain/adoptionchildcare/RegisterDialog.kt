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

class RegisterDialog : DialogFragment() {
    
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var idNumberInput: TextInputEditText
    private lateinit var roleInput: TextInputEditText
    private lateinit var registerButton: MaterialButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.register_dialog, container, false)
        
        // Initialize views
        emailInput = view.findViewById(R.id.emailEdit)
        passwordInput = view.findViewById(R.id.passwordEdit)
        phoneInput = view.findViewById(R.id.phoneEdit)
        idNumberInput = view.findViewById(R.id.idNumberEdit)
        roleInput = view.findViewById(R.id.roleEdit)
        registerButton = view.findViewById(R.id.registerBtn)
        
        // Set up click listeners
        registerButton.setOnClickListener {
            performRegister()
        }
        
        return view
    }
    
    private fun performRegister() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val idNumber = idNumberInput.text.toString().trim()
        val role = roleInput.text.toString().trim()
        
        if (email.isEmpty() || password.isEmpty() || phone.isEmpty() || idNumber.isEmpty() || role.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
        
        // TODO: Implement actual registration logic with API
        Toast.makeText(requireContext(), "Registration successful for: $email", Toast.LENGTH_SHORT).show()
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
