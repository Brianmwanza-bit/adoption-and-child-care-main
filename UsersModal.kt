package com.example.adoptionchildcare

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class UsersModal : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_users, null)
        val usersListBlock = view.findViewById<LinearLayout>(R.id.usersListBlock)

        // Placeholder user list
        val users = listOf("alice", "bob", "charlie", "diana")
        usersListBlock.removeAllViews()
        for (user in users) {
            val btn = Button(requireContext())
            btn.text = user
            btn.setOnClickListener {
                Toast.makeText(requireContext(), "$user clicked", Toast.LENGTH_SHORT).show()
            }
            usersListBlock.addView(btn)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Users")
            .setView(view)
            .setNegativeButton("Close", null)
            .create()
    }
} 