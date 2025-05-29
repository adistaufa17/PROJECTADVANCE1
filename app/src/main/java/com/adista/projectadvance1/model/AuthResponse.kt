package com.adista.projectadvance1.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: UserData?
)

data class UserData(
    val id: Int,
    val name: String,
    val phone: String,
    val school: String?,
    val photo: String?,
    val token: String
)

data class FriendData(
    val id: Int,
    val name: String,
    val school: String,
    val phone: String
)
