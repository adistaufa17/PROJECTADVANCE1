package com.adista.projectadvance1.editProfil

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.adista.projectadvance1.util.ImageUtil
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ActivityEditProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()

    private var selectedPhotoBase64: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoBase64 = encodeImageToBase64(it)
            Glide.with(this)
                .load(uri)
                .transform(CircleCrop())
                .into(binding.ivProfile)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupObservers()
        setupListeners()
        setupSchoolDropdown()

        binding.etSchool.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                (view as? AutoCompleteTextView)?.showDropDown()
            }
        }

    }

    private fun setupListeners() {
        binding.ivEditPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val school = binding.etSchool.text.toString()

            if (name.isBlank() || school.isBlank()) {
                Toast.makeText(this, "Nama dan sekolah wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateProfile(selectedPhotoBase64) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) finish()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun setupObservers() {
        viewModel.photoUrl.observe(this) { path ->
            val imageUrl = ImageUtil.getFullImageUrl(path)
            Glide.with(this)
                .load(imageUrl)
                .transform(CircleCrop())
                .into(binding.ivProfile)
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    private fun encodeImageToBase64(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun setupSchoolDropdown() {
        val sekolahList = listOf(
            "SMK PALAPA SEMARANG",
            "SMKN 1 PURWOKERTO",
            "SMK 3 KENDAL",
            "SMK 10 SEMARANG",
            "SMK 11 SEMARANG",
            "SMKN 2 PURWOKERTO"
        )

        val adapter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, sekolahList)
        binding.etSchool.setAdapter(adapter)

        binding.etSchool.setOnClickListener {
            binding.etSchool.showDropDown()
        }
    }

}
