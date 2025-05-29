package com.adista.projectadvance1.core.network

import com.adista.projectadvance1.model.AuthResponse
import com.adista.projectadvance1.model.FriendData
import com.adista.projectadvance1.request.LoginRequest
import com.adista.projectadvance1.request.RegisterRequest
import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("friends")
    suspend fun getFriends(
        @Header("Authorization") token: String
    ): List<FriendData>

}

