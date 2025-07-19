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
import com.adoptionapp.databinding.FragmentPlacementsListBinding
import com.adoptionapp.entity.PlacementsEntity
import com.adoptionapp.ui.adapter.PlacementsAdapter
import com.adoptionapp.viewmodel.PlacementsViewModel

class PlacementsListFragment : Fragment() {
    private var _binding: FragmentPlacementsListBinding? = null
    private val binding get() = _binding!!
    private val placementsViewModel: PlacementsViewModel by viewModels()
    private lateinit var adapter: PlacementsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlacementsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlacementsAdapter { placement ->
            // Optionally handle placement click
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        placementsViewModel.allPlacements.observe(viewLifecycleOwner, Observer { placements ->
            adapter.submitList(placements)
        })

        binding.addButton.setOnClickListener {
            showAddPlacementDialog()
        }
    }

    private fun showAddPlacementDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_placement, null)
        val childIdEdit = dialogView.findViewById<EditText>(R.id.editPlacementChildId)
        val familyIdEdit = dialogView.findViewById<EditText>(R.id.editPlacementFamilyId)
        val statusEdit = dialogView.findViewById<EditText>(R.id.editPlacementStatus)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Placement")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val childId = childIdEdit.text.toString().toIntOrNull()
                val familyId = familyIdEdit.text.toString().toIntOrNull()
                val status = statusEdit.text.toString()
                if (childId != null && familyId != null && status.isNotBlank()) {
                    val placement = PlacementsEntity(
                        placement_id = 0,
                        child_id = childId,
                        family_id = familyId,
                        status = status
                    )
                    placementsViewModel.insert(placement)
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