package com.adoptionapp.ui.fragment

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
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
import com.adoptionapp.R
import com.adoptionapp.databinding.FragmentChildrenListBinding
import com.adoptionapp.entity.ChildrenEntity
import com.adoptionapp.ui.adapter.ChildrenAdapter
import com.adoptionapp.viewmodel.ChildrenViewModel
import java.io.InputStream

class ChildrenListFragment : Fragment() {
    private var _binding: FragmentChildrenListBinding? = null
    private val binding get() = _binding!!
    private val childrenViewModel: ChildrenViewModel by viewModels()
    private lateinit var adapter: ChildrenAdapter
    private var pickedPhotoBytes: ByteArray? = null

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            pickedPhotoBytes = inputStream?.readBytes()
            inputStream?.close()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChildrenListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ChildrenAdapter { child ->
            // Optionally handle child click
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        childrenViewModel.allChildren.observe(viewLifecycleOwner, Observer { children ->
            adapter.submitList(children)
        })

        binding.addButton.setOnClickListener {
            showAddChildDialog()
        }
    }

    private fun showAddChildDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_child, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.editChildName)
        val ageEdit = dialogView.findViewById<EditText>(R.id.editChildAge)
        val pickButton = dialogView.findViewById<View>(R.id.pickPhotoButton)
        val previewImage = dialogView.findViewById<ImageView>(R.id.photoPreviewImage)

        pickButton.setOnClickListener {
            pickPhotoLauncher.launch("image/*")
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Child")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameEdit.text.toString()
                val age = ageEdit.text.toString().toIntOrNull()
                val photoBlob = pickedPhotoBytes
                if (name.isNotBlank() && age != null && photoBlob != null) {
                    val child = ChildrenEntity(
                        child_id = 0,
                        name = name,
                        age = age,
                        photoBlob = photoBlob
                    )
                    childrenViewModel.insert(child)
                    pickedPhotoBytes = null
                } else {
                    Toast.makeText(requireContext(), "Name, age, and photo required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 