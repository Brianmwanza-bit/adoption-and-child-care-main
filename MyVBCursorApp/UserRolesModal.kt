package com.example.myvbcursorapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class UserRolesModal : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_user_roles, container, false)
        val listView: ListView = view.findViewById(R.id.user_roles_list)
        val roles = listOf(
            "Admin",
            "Case Worker",
            "Supervisor",
            "Guest"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, roles)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(requireContext(), "Clicked: ${roles[position]}", Toast.LENGTH_SHORT).show()
        }
        return view
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
} 