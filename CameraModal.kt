package com.example.adoptionchildcare

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class CameraModal : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_camera, null)
        val takePictureButton = view.findViewById<Button>(R.id.takePictureButton)
        val recordVideoButton = view.findViewById<Button>(R.id.recordVideoButton)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Camera Options")
            .setView(view)
            .setNegativeButton("Cancel", null)
            .create()

        takePictureButton.setOnClickListener {
            Toast.makeText(requireContext(), "Take Picture clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        recordVideoButton.setOnClickListener {
            Toast.makeText(requireContext(), "Record Video clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        return dialog
    }
} 