package com.adista.projectadvance1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adista.projectadvance1.Resource
import com.adista.projectadvance1.Resource.*
import com.adista.projectadvance1.SessionManager
import com.adista.projectadvance1.core.di.ApiService
import com.adista.projectadvance1.data.*
import com.adista.projectadvance1.data.model.AuthResponse
import com.adista.projectadvance1.data.model.LoginResponse
import com.adista.projectadvance1.data.model.RegisterResponse
import com.adista.projectadvance1.data.request.EditProfileRequest
import com.adista.projectadvance1.data.request.LoginRequest
import com.adista.projectadvance1.data.request.RegisterRequest
import com.adista.projectadvance1.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userState = MutableLiveData<Resource<UserData>>()
    val userState: LiveData<Resource<UserData>> = _userState

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    fun login(phone: String, password: String) {
        _loadingState.value = true
        apiService.login(LoginRequest(phone, password)).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _loadingState.value = false
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status == "success" && body.data != null) {
                        sessionManager.saveAuthToken(body.data.token)
                        sessionManager.saveUser(body.data.toUserData())
                        _userState.value = Success(body.data.toUserData())
                    } else {
                        _userState.value = Error(body.message ?: "Login failed")
                    }
                } else {
                    _userState.value = Error("Login failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loadingState.value = false
                _userState.value = Error("Network error: ${t.message}")
            }
        })
    }

    fun register(name: String, phone: String, school: String, password: String, confirmPassword: String) {
        _loadingState.value = true
        val request = RegisterRequest(name, phone, school, password, confirmPassword)
        apiService.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _loadingState.value = false
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status == "success" && body.data != null) {
                        sessionManager.saveAuthToken(body.data.token)
                        sessionManager.saveUser(body.data.toUserData())
                        _userState.value = Success(body.data.toUserData())
                    } else {
                        _userState.value = Error(body.message ?: "Registration failed")
                    }
                } else {
                    _userState.value = Error("Registration failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _loadingState.value = false
                _userState.value = Error("Network error: ${t.message}")
            }
        })
    }

    fun getProfile() {
        _loadingState.value = true
        apiService.getProfile().enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                _loadingState.value = false
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status == "success" && body.data != null) {
                        sessionManager.saveUser(body.data)
                        _userState.value = Success(body.data)
                    } else {
                        _userState.value = Error(body.message ?: "Failed to fetch profile")
                    }
                } else {
                    _userState.value = Error("Failed to fetch profile: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _loadingState.value = false
                _userState.value = Error("Network error: ${t.message}")
            }
        })
    }

    fun updateProfile(name: String, school: String, photo: String?) {
        _loadingState.value = true
        val request = EditProfileRequest(name, school, photo)
        apiService.updateProfile(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                _loadingState.value = false
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status == "success" && body.data != null) {
                        sessionManager.saveUser(body.data)
                        _userState.value = Success(body.data)
                    } else {
                        _userState.value = Error(body.message ?: "Failed to update profile")
                    }
                } else {
                    _userState.value = Error("Failed to update profile: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _loadingState.value = false
                _userState.value = Error("Network error: ${t.message}")
            }
        })
    

    fun logout() {
        sessionManager.logout()
        _userState.value = Success(null)!!
    }
}
