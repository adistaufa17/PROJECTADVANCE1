package com.adista.projectadvance1

import com.adista.projectadvance1.core.di.ApiService
import com.adista.projectadvance1.data.model.AuthResponse
import com.adista.projectadvance1.data.model.LoginResponse
import com.adista.projectadvance1.data.model.RegisterResponse
import com.adista.projectadvance1.data.request.EditProfileRequest
import com.adista.projectadvance1.data.request.LoginRequest
import com.adista.projectadvance1.data.request.RegisterRequest
import retrofit2.Call
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    fun getProfile(): Call<AuthResponse> = apiService.getProfile()
    fun updateProfile(request: EditProfileRequest): Call<AuthResponse> = apiService.updateProfile(request)
    fun login(request: LoginRequest): Call<LoginResponse> = apiService.login(request)
    fun register(request: RegisterRequest): Call<RegisterResponse> = apiService.register(request)
}
