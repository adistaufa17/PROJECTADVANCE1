package com.adista.projectadvance1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adista.projectadvance1.data.LoginRequest
import com.adista.projectadvance1.data.LoginResponse
import com.adista.projectadvance1.data.RegisterRequest
import com.adista.projectadvance1.data.RegisterResponse
import com.adista.projectadvance1.model.UserData
import com.adista.projectadvance1.network.ApiService
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

    private val _registerResult = MutableLiveData<Resource<UserData>>()
    val registerResult: LiveData<Resource<UserData>> = _registerResult

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    fun login(phone: String, password: String) {
        _loadingState.value = true
        _loginResult.value = Resource.Loading()

        val loginRequest = LoginRequest(phone = phone, password = password)
        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _loadingState.value = false

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.accessToken != null) {
                        // Simpan token yang diterima dari API
                        sessionManager.saveAuthToken(loginResponse.accessToken)

                        // Buat dummy UserData karena API tidak mengembalikan data user
                        val userData = UserData(
                            id = 1,
                            name = "User",
                            phoneNumber = phone,
                            school = "",
                            profileImage = null
                        )

                        // Simpan user data ke SessionManager
                        sessionManager.saveUser(userData)

                        // Kirim hasil sukses
                        _loginResult.value = Resource.Success(userData)
                    } else {
                        _loginResult.value = Resource.Error("Invalid login response or missing token")
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

    fun register(name: String, phoneNumber: String, school: String, password: String, confirmPassword: String) {
        _loadingState.value = true
        _registerResult.value = Resource.Loading()

        val registerRequest = RegisterRequest(name, phoneNumber, school, password, confirmPassword)
        apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _loadingState.value = false

                if (response.isSuccessful) {
                    response.body()?.let { registerResponse ->
                        // Simpan token dari respons (jika API register mengembalikan token)
                        if (registerResponse.token != null) {
                            sessionManager.saveAuthToken(registerResponse.token)
                        }

                        // Buat objek UserData dari data pendaftaran
                        val userData = UserData(
                            id = registerResponse.user?.id ?: 1,
                            name = name,
                            phoneNumber = phoneNumber,
                            school = school,
                            profileImage = null
                        )

                        // Simpan user data ke SessionManager
                        sessionManager.saveUser(userData)

                        // Kirim hasil sukses
                        _registerResult.value = Resource.Success(userData)
                    } ?: run {
                        _registerResult.value = Resource.Error("Register response body is null")
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

    fun logout() {
        sessionManager.logout()
    }

    fun getCurrentUser(): UserData? {
        return if (sessionManager.isLoggedIn()) {
            sessionManager.getUserData()
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
}
