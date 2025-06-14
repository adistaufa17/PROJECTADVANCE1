package com.adista.projectadvance1.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adista.projectadvance1.MainActivity
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ActivityLoginBinding
import com.adista.projectadvance1.register.RegisterActivity
import com.crocodic.core.data.CoreSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {
    @Inject
    lateinit var session: CoreSession
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val phone = intent.getStringExtra("PHONE")
        val password = intent.getStringExtra("PASSWORD")
        viewModel.phone.value = phone
        viewModel.password.value = password

        if (session.getBoolean("IS_LOGGED_IN")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val phoneFromRegister = intent.getStringExtra("PHONE")
        val passFromRegister = intent.getStringExtra("PASSWORD")

        if (!phoneFromRegister.isNullOrEmpty()) {
            viewModel.phone.value = phoneFromRegister
        }
        if (!passFromRegister.isNullOrEmpty()) {
            viewModel.password.value = passFromRegister
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { success ->
            if (success) {
                session.setValue("IS_LOGGED_IN", true)
                session.setValue("USER_PHONE", viewModel.phone.value ?: "")

                // 🔥 Ambil token FCM & kirim ke server
                com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                    .addOnSuccessListener { token ->
                        viewModel.sendFcmTokenToServer(token)
                    }

                Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }


        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }


    }

    private fun setupClickListeners() {
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }


}
