package com.adista.projectadvance1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adista.projectadvance1.data.request.LoginRequest
import com.adista.projectadvance1.data.model.LoginResponse
import com.adista.projectadvance1.data.request.RegisterRequest
import com.adista.projectadvance1.data.model.RegisterResponse
import com.adista.projectadvance1.data.model.AuthResponse
import com.adista.projectadvance1.model.UserData
import com.adista.projectadvance1.core.di.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<UserData>>()
    val loginResult: LiveData<Resource<UserData>> = _loginResult

    // Tambahkan ini untuk register result
    private val _registerResult = MutableLiveData<Resource<UserData>>()
    val registerResult: LiveData<Resource<UserData>> = _registerResult

    private val _logoutResult = MutableLiveData<Resource<Boolean>>()
    val logoutResult: LiveData<Resource<Boolean>> = _logoutResult

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    fun login(phoneNumber: String, password: String) {
        _loadingState.value = true
        apiService.login(LoginRequest(phoneNumber, password)).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _loadingState.value = false

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.data != null && body.status == "success") {
                        // Save token - sekarang akses dari data.token
                        val token = body.data.token
                        sessionManager.saveAuthToken(token)

                        // Save user data langsung dari response
                        val userData = body.data.toUserData()
                        sessionManager.saveUser(userData)

                        // Update login result
                        _loginResult.value = Resource.Success(userData)
                    } else {
                        _loginResult.value = Resource.Error(body.message ?: "Login failed")
                    }
                } else {
                    _loginResult.value = Resource.Error("Login failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loadingState.value = false
                _loginResult.value = Resource.Error("Network error: ${t.message}")
            }
        })
    }


    // Implementasi function register
    fun register(name: String, phoneNumber: String, school: String, password: String, confirmPassword: String) {
        _loadingState.value = true

        val registerRequest = RegisterRequest(
            name = name,
            phoneNumber = phoneNumber,
            school = school,
            password = password,
            confirmPassword = confirmPassword
        )

        apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _loadingState.value = false

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.data != null && body.status == "success") {
                        // Save token dari data.token
                        val token = body.data.token
                        sessionManager.saveAuthToken(token)

                        // Save user data langsung dari response
                        val userData = body.data.toUserData()
                        sessionManager.saveUser(userData)

                        // Update register result
                        _registerResult.value = Resource.Success(userData)
                    } else {
                        _registerResult.value = Resource.Error(body.message ?: "Registration failed")
                    }
                } else {
                    _registerResult.value = Resource.Error("Registration failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _loadingState.value = false
                _registerResult.value = Resource.Error("Network error: ${t.message}")
            }
        })
    }

    fun getCurrentUser(): UserData? {
        return if (sessionManager.isLoggedIn()) {
            sessionManager.getUserData()
        } else {
            null
        }
    }

    fun logout() {
        _loadingState.value = true
        apiService.logout().enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                _loadingState.value = false

                // Always clear session locally
                sessionManager.logout()

                _logoutResult.value = Resource.Success(true)
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _loadingState.value = false

                // Clear session even if API call fails
                sessionManager.logout()
                _logoutResult.value = Resource.Success(true)
            }
        })
    }


    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
}
