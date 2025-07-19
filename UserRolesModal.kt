package com.example.adoptionchildcare

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class UserRolesModal : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_user_roles, null)
        val userRolesBlock = view.findViewById<LinearLayout>(R.id.userRolesBlock)

        // Placeholder roles list
        val roles = listOf("Admin", "Caseworker", "Guardian", "Judge", "Lawyer", "Medical Expert", "Police")
        userRolesBlock.removeAllViews()
        for (role in roles) {
            val btn = Button(requireContext())
            btn.text = role
            btn.setOnClickListener {
                Toast.makeText(requireContext(), "$role clicked", Toast.LENGTH_SHORT).show()
            }
            userRolesBlock.addView(btn)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("User Roles")
            .setView(view)
            .setNegativeButton("Close", null)
            .create()
    }
} 