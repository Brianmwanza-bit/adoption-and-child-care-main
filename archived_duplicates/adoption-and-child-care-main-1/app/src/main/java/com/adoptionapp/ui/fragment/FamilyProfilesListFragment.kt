package com.adoptionapp.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adoptionapp.R
import com.adoptionapp.databinding.FragmentFamilyProfilesListBinding
import com.adoptionapp.entity.FamilyProfilesEntity
import com.adoptionapp.ui.adapter.FamilyProfilesAdapter
import com.adoptionapp.viewmodel.FamilyProfilesViewModel

class FamilyProfilesListFragment : Fragment() {
    private var _binding: FragmentFamilyProfilesListBinding? = null
    private val binding get() = _binding!!
    private val familyProfilesViewModel: FamilyProfilesViewModel by viewModels()
    private lateinit var adapter: FamilyProfilesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFamilyProfilesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FamilyProfilesAdapter { profile ->
            // Optionally handle profile click
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        familyProfilesViewModel.allFamilyProfiles.observe(viewLifecycleOwner, Observer { profiles ->
            adapter.submitList(profiles)
        })

        binding.addButton.setOnClickListener {
            showAddFamilyProfileDialog()
        }
    }

    private fun showAddFamilyProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_family_profile, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.editFamilyName)
        val contactEdit = dialogView.findViewById<EditText>(R.id.editFamilyContact)
        val addressEdit = dialogView.findViewById<EditText>(R.id.editFamilyAddress)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Family Profile")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameEdit.text.toString()
                val contact = contactEdit.text.toString()
                val address = addressEdit.text.toString()
                if (name.isNotBlank() && contact.isNotBlank() && address.isNotBlank()) {
                    val profile = FamilyProfilesEntity(
                        family_id = 0,
                        name = name,
                        contact = contact,
                        address = address
                    )
                    familyProfilesViewModel.insert(profile)
                } else {
                    Toast.makeText(requireContext(), "All fields required", Toast.LENGTH_SHORT).show()
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