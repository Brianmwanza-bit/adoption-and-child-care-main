package com.yourdomain.adoptionchildcare.ui.consolidated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class UsersModal : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.yourdomain.adoptionchildcare.R.layout.dialog_users, container, false)
        val listView: ListView = view.findViewById(com.yourdomain.adoptionchildcare.R.id.users_list)
        val users = listOf(
            "Alice Smith",
            "Bob Johnson",
            "Carol Williams",
            "David Brown"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, users)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(requireContext(), "Clicked: ${users[position]}", Toast.LENGTH_SHORT).show()
        }
        return view
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
