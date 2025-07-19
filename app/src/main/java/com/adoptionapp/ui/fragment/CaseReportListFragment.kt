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
import com.adoptionapp.ui.adapter.CaseReportAdapter
import com.adoptionapp.viewmodel.CaseReportViewModel
import com.adoptionapp.viewmodel.CaseReportViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CaseReportListFragment : Fragment() {
    private val viewModel: CaseReportViewModel by viewModels {
        CaseReportViewModelFactory((requireActivity().application as MyApp).caseReportRepository)
    }

    private lateinit var adapter: CaseReportAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var syncButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_case_report_list, container, false)
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
        adapter = CaseReportAdapter { caseReport ->
            // Handle case report item click - open edit dialog
            showEditCaseReportDialog(caseReport)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CaseReportListFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.caseReports.observe(viewLifecycleOwner) { caseReports ->
            adapter.submitList(caseReports)
        }
    }

    private fun setupClickListeners() {
        addButton.setOnClickListener {
            showAddCaseReportDialog()
        }

        syncButton.setOnClickListener {
            viewModel.syncCaseReports()
        }
    }

    private fun showAddCaseReportDialog() {
        // Show dialog to add case report
        // This would open a dialog fragment or navigate to add case report screen
        // For now, we'll create a simple case report and add it
        val newCaseReport = com.adoptionapp.data.entity.CaseReport(
            id = 0,
            childId = 0,
            reportType = "Monthly",
            reportDate = "2024-01-01",
            socialWorkerId = 0,
            content = "Monthly progress report",
            recommendations = "Continue current placement",
            status = "Submitted",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModel.addCaseReport(newCaseReport)
    }

    private fun showEditCaseReportDialog(caseReport: com.adoptionapp.data.entity.CaseReport) {
        // Show dialog to edit case report
        // This would open a dialog fragment or navigate to edit case report screen
    }
} 