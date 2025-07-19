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
import com.adoptionapp.ui.adapter.MoneyRecordAdapter
import com.adoptionapp.viewmodel.MoneyRecordViewModel
import com.adoptionapp.viewmodel.MoneyRecordViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MoneyRecordListFragment : Fragment() {
    private val viewModel: MoneyRecordViewModel by viewModels {
        MoneyRecordViewModelFactory((requireActivity().application as MyApp).moneyRecordRepository)
    }

    private lateinit var adapter: MoneyRecordAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var syncButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_money_record_list, container, false)
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
        adapter = MoneyRecordAdapter { moneyRecord ->
            // Handle money record item click - open edit dialog
            showEditMoneyRecordDialog(moneyRecord)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MoneyRecordListFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.moneyRecords.observe(viewLifecycleOwner) { moneyRecords ->
            adapter.submitList(moneyRecords)
        }
    }

    private fun setupClickListeners() {
        addButton.setOnClickListener {
            showAddMoneyRecordDialog()
        }

        syncButton.setOnClickListener {
            viewModel.syncMoneyRecords()
        }
    }

    private fun showAddMoneyRecordDialog() {
        // Show dialog to add money record
        // This would open a dialog fragment or navigate to add money record screen
        // For now, we'll create a simple money record and add it
        val newMoneyRecord = com.adoptionapp.data.entity.MoneyRecord(
            id = 0,
            childId = 0,
            recordType = "Expense",
            amount = 100.0,
            date = "2024-01-01",
            description = "Medical expense",
            category = "Healthcare",
            paymentMethod = "Cash",
            receiptUrl = "",
            notes = "Monthly medical checkup",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModel.addMoneyRecord(newMoneyRecord)
    }

    private fun showEditMoneyRecordDialog(moneyRecord: com.adoptionapp.data.entity.MoneyRecord) {
        // Show dialog to edit money record
        // This would open a dialog fragment or navigate to edit money record screen
    }
} 