package com.yourdomain.adoptionchildcare.ui.consolidated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class DocumentsModal : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.yourdomain.adoptionchildcare.R.layout.dialog_documents, container, false)
        val listView: ListView = view.findViewById(com.yourdomain.adoptionchildcare.R.id.documents_list)
        val docs = listOf(
            "Adoption Form.pdf",
            "Child Care Report.docx",
            "User Agreement.pdf",
            "Medical Records.xlsx"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, docs)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(requireContext(), "Clicked: ${docs[position]}", Toast.LENGTH_SHORT).show()
        }
        return view
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
