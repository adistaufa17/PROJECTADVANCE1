package com.adista.projectadvance1.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupObservers()
        setupClickListeners()
        setupValidation()
        setupSchoolDropdown()

        binding.etSchool.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                (view as? AutoCompleteTextView)?.showDropDown()
            }
        }
    }

    private fun setupObservers() {
        viewModel.registerSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()

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

            if (password.length < 8) {
                binding.tilPassword.error = "Password minimal 8 karakter"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.tilConfirmPassword.error = "Password tidak cocok"
                return@setOnClickListener
            }

            binding.tilPassword.error = null
            binding.tilConfirmPassword.error = null

            viewModel.register(name, phone, school, password, confirmPassword)
        }
    }

    private fun setupValidation() {
        binding.etPhone.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty() && !text.matches(Regex("^08[0-9]{8,}$"))) {
                binding.tilPhone.error = "Nomor telepon tidak valid"
            } else {
                binding.tilPhone.error = null
            }
        }

        binding.etPassword.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty() && text.length < 8) {
                binding.tilPassword.error = "Password minimal 8 karakter"
            } else {
                binding.tilPassword.error = null
            }
        }

        binding.etConfirmPassword.doOnTextChanged { text, _, _, _ ->
            val pass = binding.etPassword.text.toString()
            if (text.toString() != pass) {
                binding.tilConfirmPassword.error = "Password tidak cocok"
            } else {
                binding.tilConfirmPassword.error = null
            }
        }
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
