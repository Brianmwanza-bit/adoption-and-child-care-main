package com.example.myvbcursorapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class CameraModal : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_camera, container, false)
        val takePhotoButton: Button = view.findViewById(R.id.btn_take_photo)
        takePhotoButton.setOnClickListener {
            Toast.makeText(requireContext(), "Camera feature coming soon!", Toast.LENGTH_SHORT).show()
        }
        return view
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
} 