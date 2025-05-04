package com.adista.projectadvance1.network

import com.adista.projectadvance1.data.LoginRequest
import com.adista.projectadvance1.data.LoginResponse
import com.adista.projectadvance1.data.RegisterRequest
import com.adista.projectadvance1.data.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

}
