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
import com.adoptionapp.ui.adapter.MedicalRecordAdapter
import com.adoptionapp.viewmodel.MedicalRecordViewModel
import com.adoptionapp.viewmodel.MedicalRecordViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MedicalRecordListFragment : Fragment() {
    private val viewModel: MedicalRecordViewModel by viewModels {
        MedicalRecordViewModelFactory((requireActivity().application as MyApp).medicalRecordRepository)
    }

    private lateinit var adapter: MedicalRecordAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var syncButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_medical_record_list, container, false)
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
        adapter = MedicalRecordAdapter { medicalRecord ->
            // Handle medical record item click - open edit dialog
            showEditMedicalRecordDialog(medicalRecord)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MedicalRecordListFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.medicalRecords.observe(viewLifecycleOwner) { medicalRecords ->
            adapter.submitList(medicalRecords)
        }
    }

    private fun setupClickListeners() {
        addButton.setOnClickListener {
            showAddMedicalRecordDialog()
        }

        syncButton.setOnClickListener {
            viewModel.syncMedicalRecords()
        }
    }

    private fun showAddMedicalRecordDialog() {
        // Show dialog to add medical record
        // This would open a dialog fragment or navigate to add medical record screen
        // For now, we'll create a simple medical record and add it
        val newMedicalRecord = com.adoptionapp.data.entity.MedicalRecord(
            id = 0,
            childId = 0,
            recordType = "Vaccination",
            date = "2024-01-01",
            description = "Annual vaccination",
            doctorName = "Dr. Johnson",
            hospitalName = "City Hospital",
            prescription = "",
            notes = "Child is healthy",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModel.addMedicalRecord(newMedicalRecord)
    }

    private fun showEditMedicalRecordDialog(medicalRecord: com.adoptionapp.data.entity.MedicalRecord) {
        // Show dialog to edit medical record
        // This would open a dialog fragment or navigate to edit medical record screen
    }
} 