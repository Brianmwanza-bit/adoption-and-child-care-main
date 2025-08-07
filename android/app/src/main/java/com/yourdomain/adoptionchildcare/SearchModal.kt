package com.example.adoptionchildcare

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SearchModal : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_search, null)
        val searchInput = view.findViewById<EditText>(R.id.searchInput)
        val searchButton = view.findViewById<Button>(R.id.searchButton)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Search Records")
            .setView(view)
            .setNegativeButton("Cancel", null)
            .create()

        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) {
                Toast.makeText(requireContext(), "Searching for: $query", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                searchInput.error = "Enter a search query"
            }
        }
        return dialog
    }
} 