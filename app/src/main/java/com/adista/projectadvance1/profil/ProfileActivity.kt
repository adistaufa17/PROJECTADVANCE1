package com.adista.projectadvance1.profil

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ActivityProfilBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilBinding
    private val viewModel: ProfilViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val base64 = encodeImageToBase64(it)

            viewModel.updatePhoto(base64) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                // UI akan update otomatis melalui observer photoUrl
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profil)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // tombol edit profil
        binding.btnEdit.setOnClickListener {
            showEditProfileDialog()
        }

        // tombol ganti password
        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        // klik edit foto
        binding.ivEditPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        val photoUrl = viewModel.photoUrl.value
        if (!photoUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(photoUrl)
                .transform(CircleCrop())
                .into(binding.ivProfile)
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

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_name)
        val schoolInput = dialogView.findViewById<EditText>(R.id.et_school)

        nameInput.setText(viewModel.name.value)
        schoolInput.setText(viewModel.school.value)

        AlertDialog.Builder(this)
            .setTitle("Edit Profil")
            .setView(dialogView)
            .setPositiveButton("Simpan") { d, _ ->
                val newName = nameInput.text.toString()
                val newSchool = schoolInput.text.toString()
                if (newName.isBlank()) {
                    Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.updateProfile(newName, newSchool)
                Toast.makeText(this, "Profil berhasil diubah", Toast.LENGTH_SHORT).show()
                d.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val current = dialogView.findViewById<EditText>(R.id.et_current)
        val newPass = dialogView.findViewById<EditText>(R.id.et_new)
        val confirm = dialogView.findViewById<EditText>(R.id.et_confirm)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Ubah Password")
            .setView(dialogView)
            .setPositiveButton("Ubah", null)
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val currentText = current.text.toString()
                val newText = newPass.text.toString()
                val confirmText = confirm.text.toString()

                if (newText.length < 6) {
                    Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newText != confirmText) {
                    Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                viewModel.changePassword(currentText, newText, confirmText) { success, message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (success) dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}
