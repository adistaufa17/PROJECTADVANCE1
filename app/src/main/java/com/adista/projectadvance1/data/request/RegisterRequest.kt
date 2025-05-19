package com.adista.projectadvance1.data.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    @SerializedName("phone") val phoneNumber: String,
    val school: String,
    val password: String,
    @SerializedName("password_confirmation") val confirmPassword: String
)
