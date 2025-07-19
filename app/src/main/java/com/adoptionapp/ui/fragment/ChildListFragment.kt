package com.adoptionapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adoptionapp.MyApp
import com.adoptionapp.R
import com.adoptionapp.ui.adapter.ChildAdapter
import com.adoptionapp.viewmodel.ChildViewModel
import com.adoptionapp.viewmodel.ChildViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.adoptionapp.ChildrenEntity

class ChildListFragment : Fragment() {
    private val viewModel: ChildViewModel by viewModels {
        ChildViewModelFactory((requireActivity().application as MyApp).childRepository)
    }

    private lateinit var adapter: ChildAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var syncButton: FloatingActionButton

    private var selectedPhotoBytes: ByteArray? = null

    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = requireContext().contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            selectedPhotoBytes = bytes
            // Show preview if dialog is open
            currentPhotoPreview?.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0))
        }
    }

    private var currentPhotoPreview: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_child_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.recyclerView)
        addButton = view.findViewById(R.id.addButton)
        syncButton = view.findViewById(R.id.syncButton)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = ChildAdapter { child ->
            // Handle child item click - open edit dialog
            showEditChildDialog(child)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ChildListFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.children.observe(viewLifecycleOwner) { children ->
            adapter.submitList(children)
        }
    }

    private fun setupClickListeners() {
        addButton.setOnClickListener {
            showAddChildDialog()
        }

        syncButton.setOnClickListener {
            viewModel.syncChildren()
        }
    }

    private fun showAddChildDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_child, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.childNameInput)
        val ageInput = dialogView.findViewById<EditText>(R.id.childAgeInput)
        val genderInput = dialogView.findViewById<EditText>(R.id.childGenderInput)
        val dobInput = dialogView.findViewById<EditText>(R.id.childDobInput)
        val medicalHistoryInput = dialogView.findViewById<EditText>(R.id.childMedicalHistoryInput)
        val specialNeedsInput = dialogView.findViewById<EditText>(R.id.childSpecialNeedsInput)
        val photoPickerButton = dialogView.findViewById<Button>(R.id.childPhotoPickerButton)
        val photoPreview = dialogView.findViewById<ImageView>(R.id.childPhotoPreview)
        val saveButton = dialogView.findViewById<Button>(R.id.saveChildButton)
        currentPhotoPreview = photoPreview
        selectedPhotoBytes = null
        photoPickerButton.setOnClickListener {
            photoPickerLauncher.launch("image/*")
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add Child")
            .setNegativeButton("Cancel", null)
            .create()
        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val dob = dobInput.text.toString()
            val gender = genderInput.text.toString()
            val age = ageInput.text.toString().toIntOrNull()
            // guardian_id, medicalHistory, specialNeeds are optional for now
            val newChild = ChildrenEntity(
                name = name,
                dob = dob,
                gender = gender,
                guardian_id = null,
                photoBlob = selectedPhotoBytes
        )
        viewModel.addChild(newChild)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showEditChildDialog(child: com.adoptionapp.data.entity.Child) {
        // Show dialog to edit child
        // This would open a dialog fragment or navigate to edit child screen
    }
} 