package com.yourdomain.adoptionchildcare.ui.consolidated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class SearchModal : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.yourdomain.adoptionchildcare.R.layout.dialog_search, container, false)
        val searchInput: EditText = view.findViewById(com.yourdomain.adoptionchildcare.R.id.search_input)
        val searchButton: Button = view.findViewById(com.yourdomain.adoptionchildcare.R.id.btn_search)
        searchButton.setOnClickListener {
            val query = searchInput.text.toString()
            Toast.makeText(requireContext(), "Searching for: $query", Toast.LENGTH_SHORT).show()
        }
        return view
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
