package com.adista.projectadvance1.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ActivityRegisterBinding
import com.adista.projectadvance1.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding layout dengan ViewModel
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupObservers()
        setupClickListeners()
    }

    // Observer untuk memantau hasil register
    private fun setupObservers() {
        viewModel.registerSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()

                // Intent ke LoginActivity sambil mengirim nomor HP dan password
                val intent = Intent(this, LoginActivity::class.java).apply {
                    putExtra("PHONE", viewModel.phone.value)
                    putExtra("PASSWORD", viewModel.password.value)
                }
                startActivity(intent)
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    // Listener tombol register dan pindah ke login
    private fun setupClickListeners() {
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val phone = binding.etPhone.text.toString()
            val school = binding.etSchool.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kirim data ke ViewModel untuk proses register
            viewModel.register(name, phone, school, password, confirmPassword)
        }
    }
}
