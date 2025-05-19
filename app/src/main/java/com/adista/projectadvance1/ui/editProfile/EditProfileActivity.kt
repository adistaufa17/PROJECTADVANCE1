package com.adista.projectadvance1.ui.editProfile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adista.projectadvance1.R
import com.adista.projectadvance1.Resource
import com.adista.projectadvance1.databinding.ActivityEditProfilBinding
import com.adista.projectadvance1.model.UserData
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfilBinding
    private val viewModel: EditProfileViewModel by viewModels()

    private var selectedImageUri: Uri? = null
    private val schoolList = arrayOf("SMK N 1 Kendal", "SMK N 2 Kendal", "SMK N 3 Kendal", "SMA N 1 Kendal", "SMA N 2 Kendal", "SMK PALAPA")

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.ivProfile.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, insets.top, 0, insets.bottom)
            windowInsets
        }

        setupSchoolDropdown()
        setupListeners()
        setupObservers()

        // Load current user data
        viewModel.getCurrentUserData()
    }

    private fun setupSchoolDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, schoolList)
        binding.autoCompleteSchool.setAdapter(adapter)
    }

    private fun setupListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Edit photo
        binding.ivEditPhoto.setOnClickListener {
            if (checkStoragePermission()) {
                openImagePicker()
            } else {
                requestStoragePermission()
            }
        }

        // Save button
        binding.btnSave.setOnClickListener {
            updateProfile()
        }
    }

    private fun setupObservers() {
        viewModel.userData.observe(this) { userData ->
            if (userData != null) {
                displayUserData(userData)
            }
        }

        viewModel.loadingState.observe(this) { isLoading ->
            binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.profileUpdateResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message ?: "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Loading state handled by loadingState observer
                }
            }
        }
    }

    private fun displayUserData(userData: UserData) {
        binding.etName.setText(userData.name)
        binding.autoCompleteSchool.setText(userData.school, false)

        // Load profile image if available
        if (!userData.profileImage.isNullOrEmpty()) {
            Glide.with(this)
                .load(userData.profileImage)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(binding.ivProfile)
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_person)
        }
    }

    private fun updateProfile() {
        val name = binding.etName.text.toString().trim()
        val school = binding.autoCompleteSchool.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return
        }

        // Update profile
        viewModel.updateProfile(name, school, selectedImageUri, this)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 100
    }
}
