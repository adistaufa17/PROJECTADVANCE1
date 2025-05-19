package com.adista.projectadvance1.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.adista.projectadvance1.AuthViewModel
import com.adista.projectadvance1.MainActivity
import com.adista.projectadvance1.Resource
import com.adista.projectadvance1.databinding.ActivityLoginBinding
import com.adista.projectadvance1.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = loginViewModel
        binding.lifecycleOwner = this

        // Check if already logged in
        if (authViewModel.isLoggedIn()) {
            navigateToMain()
            return
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        loginViewModel.loginEvent.observe(this) {
            val phone = loginViewModel.phoneNumber.value ?: ""
            val password = loginViewModel.password.value ?: ""

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@observe
            }

            // Use authViewModel to perform actual login
            authViewModel.login(phone, password)
        }

        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
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
        binding.tvRegister.setOnClickListener {
            // Navigate to register screen
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
