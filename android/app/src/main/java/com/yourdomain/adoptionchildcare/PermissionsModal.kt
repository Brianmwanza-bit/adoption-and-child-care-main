package com.example.adoptionchildcare

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class PermissionsModal : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_permissions, null)
        val permissionsListBlock = view.findViewById<LinearLayout>(R.id.permissionsListBlock)

        // Placeholder permissions list
        val permissions = listOf("View Records", "Edit Records", "Delete Records", "Upload Documents", "Manage Users")
        permissionsListBlock.removeAllViews()
        for (perm in permissions) {
            val btn = Button(requireContext())
            btn.text = perm
            btn.setOnClickListener {
                Toast.makeText(requireContext(), "$perm clicked", Toast.LENGTH_SHORT).show()
            }
            permissionsListBlock.addView(btn)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Permissions")
            .setView(view)
            .setNegativeButton("Close", null)
            .create()
    }
} 