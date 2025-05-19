package com.adista.projectadvance1.core.di

import com.adista.projectadvance1.data.request.EditProfileRequest
import com.adista.projectadvance1.data.request.LoginRequest
import com.adista.projectadvance1.data.model.LoginResponse
import com.adista.projectadvance1.data.request.RegisterRequest
import com.adista.projectadvance1.data.model.RegisterResponse
import com.adista.projectadvance1.data.model.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @GET("profile")
    fun getProfile(): Call<AuthResponse>

    @POST("edit-profile")
    fun updateProfile(@Body request: EditProfileRequest): Call<AuthResponse>

    @POST("logout")
    fun logout(): Call<AuthResponse>
}
