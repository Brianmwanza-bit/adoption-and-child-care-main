package com.yourdomain.adoptionchildcare

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileOutputStream

class DashboardFragment : Fragment() {
    
    private lateinit var profilePhoto: CircleImageView
    private lateinit var addPhotoButton: FloatingActionButton
    private lateinit var loginButton: MaterialButton
    private lateinit var registerButton: MaterialButton
    
    private val CAMERA_PERMISSION_REQUEST = 100
    private val CAMERA_REQUEST = 101
    private val GALLERY_REQUEST = 102
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        
        // Initialize views
        profilePhoto = view.findViewById(R.id.profilePhoto)
        addPhotoButton = view.findViewById(R.id.addPhotoButton)
        loginButton = view.findViewById(R.id.loginButton)
        registerButton = view.findViewById(R.id.registerButton)
        
        // Set up click listeners
        setupClickListeners()
        
        return view
    }
    
    private fun setupClickListeners() {
        // Add photo button click
        addPhotoButton.setOnClickListener {
            showPhotoOptions()
        }
        
        // Profile photo click
        profilePhoto.setOnClickListener {
            showPhotoOptions()
        }
        
        // Login button click
        loginButton.setOnClickListener {
            showLoginDialog()
        }
        
        // Register button click
        registerButton.setOnClickListener {
            showRegisterDialog()
        }
    }
    
    private fun showPhotoOptions() {
        val options = arrayOf("Camera", "Gallery", "Cancel")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Select Photo Source")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
                2 -> { /* Cancel */ }
            }
        }
        builder.show()
    }
    
    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }
    
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST)
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    val photo = data?.extras?.get("data") as? Bitmap
                    photo?.let { setProfilePhoto(it) }
                }
                GALLERY_REQUEST -> {
                    val selectedImage = data?.data
                    selectedImage?.let { uri ->
                        try {
                            val inputStream = requireContext().contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            setProfilePhoto(bitmap)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    private fun setProfilePhoto(bitmap: Bitmap) {
        // Create a circular bitmap
        val circularBitmap = createCircularBitmap(bitmap)
        profilePhoto.setImageBitmap(circularBitmap)
        Toast.makeText(requireContext(), "Profile photo updated!", Toast.LENGTH_SHORT).show()
    }
    
    private fun createCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val circularBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(circularBitmap)
        
        val paint = android.graphics.Paint()
        paint.isAntiAlias = true
        
        val rect = android.graphics.Rect(0, 0, size, size)
        val rectF = android.graphics.RectF(rect)
        
        canvas.drawOval(rectF, paint)
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        
        return circularBitmap
    }
    
    private fun showLoginDialog() {
        val loginDialog = LoginDialog()
        loginDialog.show(parentFragmentManager, "LoginDialog")
    }
    
    private fun showRegisterDialog() {
        val registerDialog = RegisterDialog()
        registerDialog.show(parentFragmentManager, "RegisterDialog")
    }
}
