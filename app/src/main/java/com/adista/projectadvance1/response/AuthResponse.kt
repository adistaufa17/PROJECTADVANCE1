package com.adista.projekadvance1.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: UserData?
)
data class RegisterResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: UserData
)


data class UserData(
    val id: Int,
    val name: String,
    val phone: String,
    val school: String?,
    val token: String // jangan lupa token ya
)
