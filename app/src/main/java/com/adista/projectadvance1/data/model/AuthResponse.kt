package com.adista.projectadvance1.data.model

import com.adista.projectadvance1.model.UserData
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: UserData? = null
)

