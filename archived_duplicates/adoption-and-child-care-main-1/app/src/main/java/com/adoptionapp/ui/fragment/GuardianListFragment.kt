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
import com.adoptionapp.ui.adapter.GuardianAdapter
import com.adoptionapp.viewmodel.GuardianViewModel
import com.adoptionapp.viewmodel.GuardianViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class GuardianListFragment : Fragment() {
    private val viewModel: GuardianViewModel by viewModels {
        GuardianViewModelFactory((requireActivity().application as MyApp).guardianRepository)
    }

    private lateinit var adapter: GuardianAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var syncButton: FloatingActionButton
    private lateinit var progressBar: View
    private lateinit var errorText: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_guardian_list, container, false)
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
        adapter = GuardianAdapter { guardian ->
            // Handle guardian item click - open edit dialog
            showEditGuardianDialog(guardian)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@GuardianListFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.guardians.observe(viewLifecycleOwner) { guardians ->
            adapter.submitList(guardians)
        }
    }

    private fun setupClickListeners() {
        addButton.setOnClickListener {
            showAddGuardianDialog()
        }

        syncButton.setOnClickListener {
            viewModel.syncGuardians()
        }
    }

    private fun showAddGuardianDialog() {
        // Show dialog to add guardian
        // This would open a dialog fragment or navigate to add guardian screen
        // For now, we'll create a simple guardian and add it
        val newGuardian = com.adoptionapp.data.entity.Guardian(
            id = 0,
            name = "New Guardian",
            relationship = "Parent",
            phone = "",
            email = "",
            address = "",
            childId = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModel.addGuardian(newGuardian)
    }

    private fun showEditGuardianDialog(guardian: com.adoptionapp.data.entity.Guardian) {
        // Show dialog to edit guardian
        // This would open a dialog fragment or navigate to edit guardian screen
    }
} 