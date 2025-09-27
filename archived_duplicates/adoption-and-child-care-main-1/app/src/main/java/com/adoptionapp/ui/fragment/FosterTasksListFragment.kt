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
import com.adoptionapp.databinding.FragmentFosterTasksListBinding
import com.adoptionapp.entity.FosterTasksEntity
import com.adoptionapp.ui.adapter.FosterTasksAdapter
import com.adoptionapp.viewmodel.FosterTasksViewModel

class FosterTasksListFragment : Fragment() {
    private var _binding: FragmentFosterTasksListBinding? = null
    private val binding get() = _binding!!
    private val fosterTasksViewModel: FosterTasksViewModel by viewModels()
    private lateinit var adapter: FosterTasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFosterTasksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FosterTasksAdapter { task ->
            // Optionally handle task click
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        fosterTasksViewModel.allFosterTasks.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.submitList(tasks)
        })

        binding.addButton.setOnClickListener {
            showAddFosterTaskDialog()
        }
    }

    private fun showAddFosterTaskDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_foster_task, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.editTaskTitle)
        val descEdit = dialogView.findViewById<EditText>(R.id.editTaskDescription)
        val dueDateEdit = dialogView.findViewById<EditText>(R.id.editTaskDueDate)
        val statusEdit = dialogView.findViewById<EditText>(R.id.editTaskStatus)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Foster Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleEdit.text.toString()
                val desc = descEdit.text.toString()
                val dueDate = dueDateEdit.text.toString()
                val status = statusEdit.text.toString()
                if (title.isNotBlank() && status.isNotBlank()) {
                    val task = FosterTasksEntity(
                        task_id = 0,
                        title = title,
                        description = desc,
                        due_date = dueDate,
                        status = status
                    )
                    fosterTasksViewModel.insert(task)
                } else {
                    Toast.makeText(requireContext(), "Title and status required", Toast.LENGTH_SHORT).show()
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