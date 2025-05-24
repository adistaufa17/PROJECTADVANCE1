package com.adista.projectadvance1.core.network

import com.adista.projectadvance1.request.LoginRequest
import com.adista.projectadvance1.request.RegisterRequest
import com.adista.projekadvance1.response.AuthResponse
import com.adista.projekadvance1.response.RegisterResponse
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse


    @GET("profile")
    suspend fun getProfile(): String

    @POST("logout")
    suspend fun logout(): String
}
