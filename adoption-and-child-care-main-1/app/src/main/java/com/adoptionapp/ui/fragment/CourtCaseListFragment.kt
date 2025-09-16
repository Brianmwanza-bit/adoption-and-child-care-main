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
import com.adoptionapp.ui.adapter.CourtCaseAdapter
import com.adoptionapp.viewmodel.CourtCaseViewModel
import com.adoptionapp.viewmodel.CourtCaseViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CourtCaseListFragment : Fragment() {
    private val viewModel: CourtCaseViewModel by viewModels {
        CourtCaseViewModelFactory((requireActivity().application as MyApp).courtCaseRepository)
    }

    private lateinit var adapter: CourtCaseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var syncButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_court_case_list, container, false)
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
        adapter = CourtCaseAdapter { courtCase ->
            // Handle court case item click - open edit dialog
            showEditCourtCaseDialog(courtCase)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CourtCaseListFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.courtCases.observe(viewLifecycleOwner) { courtCases ->
            adapter.submitList(courtCases)
        }
    }

    private fun setupClickListeners() {
        addButton.setOnClickListener {
            showAddCourtCaseDialog()
        }

        syncButton.setOnClickListener {
            viewModel.syncCourtCases()
        }
    }

    private fun showAddCourtCaseDialog() {
        // Show dialog to add court case
        // This would open a dialog fragment or navigate to add court case screen
        // For now, we'll create a simple court case and add it
        val newCourtCase = com.adoptionapp.data.entity.CourtCase(
            id = 0,
            caseNumber = "CASE-001",
            childId = 0,
            courtName = "Family Court",
            judgeName = "Judge Smith",
            caseType = "Adoption",
            status = "Pending",
            filingDate = "2024-01-01",
            nextHearingDate = "2024-02-01",
            description = "Adoption case for child",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModel.addCourtCase(newCourtCase)
    }

    private fun showEditCourtCaseDialog(courtCase: com.adoptionapp.data.entity.CourtCase) {
        // Show dialog to edit court case
        // This would open a dialog fragment or navigate to edit court case screen
    }
} 