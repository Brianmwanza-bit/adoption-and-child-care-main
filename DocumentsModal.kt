package com.example.adoptionchildcare

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class DocumentsModal : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_documents, null)
        val documentsListBlock = view.findViewById<LinearLayout>(R.id.documentsListBlock)

        // Placeholder documents list
        val documents = listOf("Birth Certificate.pdf", "Adoption Order.pdf", "Medical Report.pdf", "School Report.pdf")
        documentsListBlock.removeAllViews()
        for (doc in documents) {
            val btn = Button(requireContext())
            btn.text = doc
            btn.setOnClickListener {
                Toast.makeText(requireContext(), "$doc clicked", Toast.LENGTH_SHORT).show()
            }
            documentsListBlock.addView(btn)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Documents")
            .setView(view)
            .setNegativeButton("Close", null)
            .create()
    }
} 