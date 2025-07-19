package com.adoptionapp.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adoptionapp.DocumentsEntity
import com.adoptionapp.R
import com.adoptionapp.databinding.FragmentDocumentsListBinding
import com.adoptionapp.ui.adapter.DocumentsAdapter
import com.adoptionapp.viewmodel.DocumentsViewModel
import java.io.InputStream

class DocumentsListFragment : Fragment() {
    private var _binding: FragmentDocumentsListBinding? = null
    private val binding get() = _binding!!
    private val documentsViewModel: DocumentsViewModel by viewModels()
    private lateinit var adapter: DocumentsAdapter
    private var pickedFileBytes: ByteArray? = null
    private var pickedFileName: String? = null

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            pickedFileBytes = inputStream?.readBytes()
            pickedFileName = getFileName(uri)
            inputStream?.close()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDocumentsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DocumentsAdapter { document ->
            // Optionally handle document click
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        documentsViewModel.allDocuments.observe(viewLifecycleOwner, Observer { docs ->
            adapter.submitList(docs)
        })

        binding.addButton.setOnClickListener {
            showAddDocumentDialog()
        }
    }

    private fun showAddDocumentDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_documents, null)
        val typeEdit = dialogView.findViewById<EditText>(R.id.editDocumentType)
        val descEdit = dialogView.findViewById<EditText>(R.id.editDocumentDescription)
        val pickButton = dialogView.findViewById<View>(R.id.pickFileButton)
        val previewImage = dialogView.findViewById<ImageView>(R.id.filePreviewImage)

        pickButton.setOnClickListener {
            pickFileLauncher.launch("*")
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Document")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val type = typeEdit.text.toString()
                val desc = descEdit.text.toString()
                val fileBlob = pickedFileBytes
                if (type.isNotBlank() && fileBlob != null) {
                    val doc = DocumentsEntity(
                        document_id = 0,
                        document_type = type,
                        description = desc,
                        fileBlob = fileBlob
                    )
                    documentsViewModel.insert(doc)
                    pickedFileBytes = null
                    pickedFileName = null
                } else {
                    Toast.makeText(requireContext(), "Type and file required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 