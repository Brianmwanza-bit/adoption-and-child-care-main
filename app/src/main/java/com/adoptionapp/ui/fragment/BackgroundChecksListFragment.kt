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
import com.adoptionapp.databinding.FragmentBackgroundChecksListBinding
import com.adoptionapp.entity.BackgroundChecksEntity
import com.adoptionapp.ui.adapter.BackgroundChecksAdapter
import com.adoptionapp.viewmodel.BackgroundChecksViewModel

class BackgroundChecksListFragment : Fragment() {
    private var _binding: FragmentBackgroundChecksListBinding? = null
    private val binding get() = _binding!!
    private val backgroundChecksViewModel: BackgroundChecksViewModel by viewModels()
    private lateinit var adapter: BackgroundChecksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBackgroundChecksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = BackgroundChecksAdapter { check ->
            // Optionally handle check click
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        backgroundChecksViewModel.allBackgroundChecks.observe(viewLifecycleOwner, Observer { checks ->
            adapter.submitList(checks)
        })

        binding.addButton.setOnClickListener {
            showAddBackgroundCheckDialog()
        }
    }

    private fun showAddBackgroundCheckDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_background_check, null)
        val personEdit = dialogView.findViewById<EditText>(R.id.editCheckPersonName)
        val typeEdit = dialogView.findViewById<EditText>(R.id.editCheckType)
        val statusEdit = dialogView.findViewById<EditText>(R.id.editCheckStatus)
        val resultEdit = dialogView.findViewById<EditText>(R.id.editCheckResult)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Background Check")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val person = personEdit.text.toString()
                val type = typeEdit.text.toString()
                val status = statusEdit.text.toString()
                val result = resultEdit.text.toString()
                if (person.isNotBlank() && type.isNotBlank() && status.isNotBlank()) {
                    val check = BackgroundChecksEntity(
                        check_id = 0,
                        person_name = person,
                        type = type,
                        status = status,
                        result = result
                    )
                    backgroundChecksViewModel.insert(check)
                } else {
                    Toast.makeText(requireContext(), "Person, type, and status required", Toast.LENGTH_SHORT).show()
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