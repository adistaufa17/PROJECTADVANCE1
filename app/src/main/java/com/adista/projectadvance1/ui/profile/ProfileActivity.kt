package com.adista.projectadvance1.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adista.projectadvance1.AuthViewModel
import com.adista.projectadvance1.ui.editProfile.EditProfileActivity
import com.adista.projectadvance1.R
import com.adista.projectadvance1.Resource
import com.adista.projectadvance1.databinding.ActivityProfileBinding
import com.adista.projectadvance1.ui.login.LoginActivity
import com.adista.projectadvance1.model.UserData
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, insets.top, 0, insets.bottom)
            windowInsets
        }

        // Tampilkan data profil dari SessionManager terlebih dahulu
        val localUser = profileViewModel.getLocalProfile()
        if (localUser != null) {
            displayUserData(localUser)
        }

        setupObservers()
        setupClickListeners()

        // Refresh data profil dari server
        profileViewModel.fetchProfile()
    }

    private fun setupObservers() {
        // Observer untuk data profil
        profileViewModel.profileData.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Log.d("ProfileActivity", "Profile data received: ${result.data}")
                    result.data?.let { displayUserData(it) }
                }
                is Resource.Error -> {
                    Log.e("ProfileActivity", "Error getting profile: ${result.message}")
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Loading state handled by loadingState observer
                }
            }
        }

        // Observer untuk loading state profil
        profileViewModel.loadingState.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observer untuk status logout
        authViewModel.logoutResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    // Tetap navigasi ke login karena session lokal sudah dihapus
                    navigateToLogin()
                }
                is Resource.Loading -> {
                    // Loading state handled by loadingState observer
                }
            }
        }

        // Observer untuk loading state auth
        authViewModel.loadingState.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Di dalam ProfileActivity.kt, method setupClickListeners()
        binding.btnEdit.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.ivEditPhoto.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        // Change password button
        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Change password coming soon", Toast.LENGTH_SHORT).show()
            // Implementasi navigasi ke ChangePasswordActivity nanti
        }

        // Logout button
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun displayUserData(userData: UserData) {
        binding.tvName.text = userData.name
        binding.tvSchool.text = userData.school
        binding.tvPhone.text = formatPhoneNumber(userData.phoneNumber)

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

    private fun formatPhoneNumber(phone: String): String {
        return if (phone.startsWith("0")) {
            "+62 ${phone.substring(1)}"
        } else if (phone.startsWith("62")) {
            "+62 ${phone.substring(2)}"
        } else {
            phone
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                authViewModel.logout()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
