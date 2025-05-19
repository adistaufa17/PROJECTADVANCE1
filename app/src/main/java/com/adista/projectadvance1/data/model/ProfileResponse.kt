package com.adista.projectadvance1.data.model

import com.adista.projectadvance1.model.UserData
import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: UserData
)
