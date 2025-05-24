package com.adista.projectadvance1.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("phone")
    val phone: String,

    @SerializedName("password")
    val password: String
)
