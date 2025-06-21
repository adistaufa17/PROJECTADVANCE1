package com.adista.projectadvance1.core.network

import com.adista.projectadvance1.model.AuthResponse
import com.adista.projectadvance1.model.EditProfileResponse
import com.adista.projectadvance1.model.FriendsResponse
import com.adista.projectadvance1.request.LoginRequest
import com.adista.projectadvance1.request.RegisterRequest
import com.crocodic.core.api.ApiResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("profile")
    suspend fun getProfile(): Response<EditProfileResponse>

    @GET("friends")
    suspend fun getFriends(): FriendsResponse

    @POST("poke/{friendId}")
    suspend fun pokeFriend(
        @Path("friendId") friendId: Int
    ): Response<Unit>

    @POST("update-fcm-token")
    suspend fun updateFcmToken(
        @Query("fcm_token") fcmToken: String
    ): Response<ApiResponse>

    @POST("edit-profile")
    @Headers("Content-Type: application/json")
    suspend fun editProfile(
        @Body body: Map<String, String>
    ): Response<EditProfileResponse>

    @POST("edit-password")
    suspend fun changePassword(
        @Body body: Map<String, String>
    ): Response<ApiResponse>
}
