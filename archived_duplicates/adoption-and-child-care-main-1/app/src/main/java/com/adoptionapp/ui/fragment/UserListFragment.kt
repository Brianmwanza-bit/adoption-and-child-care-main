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
import com.adoptionapp.ui.adapter.UserAdapter
import com.adoptionapp.viewmodel.UserViewModel
import com.adoptionapp.viewmodel.UserViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UserListFragment : Fragment() {
    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory((requireActivity().application as MyApp).userRepository)
    }

    private lateinit var adapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var syncButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_list, container, false)
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
        adapter = UserAdapter { user ->
            // Handle user item click - open edit dialog
            showEditUserDialog(user)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@UserListFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.users.observe(viewLifecycleOwner) { users ->
            adapter.submitList(users)
        }
    }

    private fun setupClickListeners() {
        addButton.setOnClickListener {
            showAddUserDialog()
        }

        syncButton.setOnClickListener {
            viewModel.syncUsers()
        }
    }

    private fun showAddUserDialog() {
        // Show dialog to add user
        // This would open a dialog fragment or navigate to add user screen
        // For now, we'll create a simple user and add it
        val newUser = com.adoptionapp.data.entity.User(
            id = 0,
            username = "newuser",
            email = "newuser@example.com",
            password = "password",
            role = "user",
            firstName = "New",
            lastName = "User",
            phone = "",
            address = "",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModel.addUser(newUser)
    }

    private fun showEditUserDialog(user: com.adoptionapp.data.entity.User) {
        // Show dialog to edit user
        // This would open a dialog fragment or navigate to edit user screen
    }
} 