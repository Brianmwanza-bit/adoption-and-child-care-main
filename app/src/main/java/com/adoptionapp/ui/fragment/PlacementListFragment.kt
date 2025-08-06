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
import com.adoptionapp.ui.adapter.PlacementAdapter
import com.adoptionapp.viewmodel.PlacementViewModel
import com.adoptionapp.viewmodel.PlacementViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts

class PlacementListFragment : Fragment() {
    private val viewModel: PlacementViewModel by viewModels {
        PlacementViewModelFactory((requireActivity().application as MyApp).placementRepository)
    }

    private lateinit var adapter: PlacementAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var syncButton: FloatingActionButton
    private lateinit var progressBar: View
    private lateinit var errorText: View

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Snackbar.make(requireView(), "Location permission required for map features.", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_placement_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.recyclerView)
        addButton = view.findViewById(R.id.addButton)
        syncButton = view.findViewById(R.id.syncButton)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.loading.asLiveData().observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.asLiveData().observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Snackbar.make(requireView(), errorMsg, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PlacementAdapter { placement ->
            // Handle placement item click - open edit dialog
            showEditPlacementDialog(placement)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PlacementListFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.placements.observe(viewLifecycleOwner) { placements ->
            adapter.submitList(placements)
        }
    }

    private fun setupClickListeners() {
        addButton.setOnClickListener {
            showAddPlacementDialog()
        }

        syncButton.setOnClickListener {
            viewModel.syncPlacements()
        }
    }

    private fun showAddPlacementDialog() {
        // Show dialog to add placement
        // This would open a dialog fragment or navigate to add placement screen
        // For now, we'll create a simple placement and add it
        val newPlacement = com.adoptionapp.data.entity.Placement(
            id = 0,
            childId = 0,
            guardianId = 0,
            placementDate = "2024-01-01",
            placementType = "Foster Care",
            status = "Active",
            notes = "Initial placement",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModel.addPlacement(newPlacement)
    }

    private fun showEditPlacementDialog(placement: com.adoptionapp.data.entity.Placement) {
        // Show dialog to edit placement
        // This would open a dialog fragment or navigate to edit placement screen
    }

    private fun requestLocationPermission() {
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
} 