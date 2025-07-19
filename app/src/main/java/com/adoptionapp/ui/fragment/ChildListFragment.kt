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

class ChildListFragment : Fragment() {
    private val viewModel: ChildViewModel by viewModels {
        ChildViewModelFactory((requireActivity().application as MyApp).childRepository)
    }

    private lateinit var adapter: ChildAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var syncButton: FloatingActionButton

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
        // Show dialog to add child
        // This would open a dialog fragment or navigate to add child screen
        // For now, we'll create a simple child and add it
        val newChild = com.adoptionapp.data.entity.Child(
            id = 0,
            name = "New Child",
            age = 5,
            gender = "Unknown",
            dateOfBirth = "2020-01-01",
            medicalHistory = "",
            specialNeeds = "",
            photoUrl = "",
            status = "Available",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModel.addChild(newChild)
    }

    private fun showEditChildDialog(child: com.adoptionapp.data.entity.Child) {
        // Show dialog to edit child
        // This would open a dialog fragment or navigate to edit child screen
    }
} 