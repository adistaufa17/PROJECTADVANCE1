package com.adista.projectadvance1.core.network

import com.adista.projectadvance1.request.LoginRequest
import com.adista.projectadvance1.request.RegisterRequest
import com.adista.projekadvance.response.AuthResponse
import retrofit2.http.*

interface ApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse


    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse


    @GET("profile")
    suspend fun getProfile(): String

    @POST("logout")
    suspend fun logout(): String
}
