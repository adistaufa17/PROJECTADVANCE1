package com.adista.projectadvance1.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: UserData?
)

