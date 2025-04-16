package com.adista.projectadvance1.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.adista.projectadvance1.MainActivity
import com.adista.projectadvance1.R
import com.adista.projectadvance1.Register.RegisterActivity
import com.adista.projectadvance1.databinding.ActivityLoginBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.activity.CoreActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : CoreActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Check login status using SharedPreferences
        val isLoggedIn = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            .getBoolean("IS_LOGGED_IN", false)

        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Observe API responses
        lifecycleScope.launch {
            viewModel.apiResponse.collect { response ->
                when(response.status) {
                    ApiStatus.LOADING -> loadingDialog.show()
                    ApiStatus.SUCCESS -> {
                        loadingDialog.dismiss()

                        // Ambil token dari response data
                        val authResponse = response.data as? AuthResponse
                        if (authResponse != null) {
                            // Simpan ke SharedPreferences
                            val sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
                            sharedPreferences.edit().apply {
                                putBoolean("IS_LOGGED_IN", true)
                                putString("user_token", authResponse.data?.token)
                                apply()
                            }
                        }

                        response.message?.let { Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show() }
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                    ApiStatus.ERROR -> {
                        loadingDialog.dismiss()
                        response.message?.let { Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show() }
                    }
                    else -> {}
                }
            }
        }

        // Handle click events
        binding.btnLogin.setOnClickListener {
            // Use the non-suspending wrapper method
            viewModel.onLoginClick()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}