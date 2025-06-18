package com.adista.projectadvance1.core.network

import com.adista.projectadvance1.model.AuthResponse
import com.adista.projectadvance1.model.EditProfileResponse
import com.adista.projectadvance1.model.FriendsResponse
import com.adista.projectadvance1.request.LoginRequest
import com.adista.projectadvance1.request.RegisterRequest
import com.crocodic.core.api.ApiResponse
import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("friends")
    suspend fun getFriends(
        @Header("Authorization") token: String
    ): FriendsResponse

    @POST("poke/{friendId}")
    suspend fun pokeFriend(
        @Header("Authorization") token: String,
        @Path("friendId") friendId: Int
    ): Response<Unit>

    @POST("update-fcm-token")
    suspend fun updateFcmToken(
        @Header("Authorization") token: String,
        @Query("fcm_token") fcmToken: String
    ): Response<ApiResponse>

    @POST("edit-profile")
    @Headers("Content-Type: application/json")
    suspend fun editProfile(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<EditProfileResponse>


    @POST("edit-password")
    suspend fun changePassword(
        @Body body: Map<String, String>
    ): Response<ApiResponse>

}

