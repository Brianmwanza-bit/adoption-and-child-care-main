package com.example.adoptionchildcare

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AuditLogsModal : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_audit_logs, null)
        val auditLogsListBlock = view.findViewById<LinearLayout>(R.id.auditLogsListBlock)

        // Placeholder audit logs
        val logs = listOf(
            "User alice logged in (2024-06-01 10:00)",
            "User bob updated a record (2024-06-01 10:05)",
            "User charlie uploaded a document (2024-06-01 10:10)",
            "User diana changed permissions (2024-06-01 10:15)"
        )
        auditLogsListBlock.removeAllViews()
        for (log in logs) {
            val tv = TextView(requireContext())
            tv.text = log
            tv.textSize = 16f
            tv.setPadding(0, 16, 0, 16)
            tv.setOnClickListener {
                Toast.makeText(requireContext(), log, Toast.LENGTH_SHORT).show()
            }
            auditLogsListBlock.addView(tv)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Audit Logs")
            .setView(view)
            .setNegativeButton("Close", null)
            .create()
    }
} 