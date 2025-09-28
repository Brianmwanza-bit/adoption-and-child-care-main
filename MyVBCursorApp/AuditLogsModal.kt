package com.example.myvbcursorapp

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class AuditLogsModal : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_audit_logs, container, false)
        val listView: ListView = view.findViewById(R.id.audit_logs_list)
        val logs = listOf(
            "User A logged in",
            "User B updated profile",
            "User C deleted a document",
            "User D changed permissions"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, logs)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(requireContext(), "Clicked: ${logs[position]}", Toast.LENGTH_SHORT).show()
        }
        return view
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
} 