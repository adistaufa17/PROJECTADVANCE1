package com.adista.projectadvance1.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.adista.projectadvance1.AuthViewModel
import com.adista.projectadvance1.MainActivity
import com.adista.projectadvance1.Resource
import com.adista.projectadvance1.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = registerViewModel
        binding.lifecycleOwner = this

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        registerViewModel.registerEvent.observe(this) {
            val name = registerViewModel.name.value ?: ""
            val phone = registerViewModel.phoneNumber.value ?: ""
            val school = registerViewModel.school.value ?: ""
            val password = registerViewModel.password.value ?: ""
            val confirmPassword = registerViewModel.confirmPassword.value ?: ""

            // Validate inputs
            if (name.isEmpty() || phone.isEmpty() || school.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@observe
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@observe
            }

            // Use authViewModel to perform actual registration
            authViewModel.register(name, phone, school, password, confirmPassword)
        }

        authViewModel.registerResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Handle loading state
                }
            }
        }

        authViewModel.loadingState.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.tvLogin.setOnClickListener {
            finish() // Go back to login screen
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
