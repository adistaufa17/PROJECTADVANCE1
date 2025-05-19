package com.adista.projectadvance1.data.request

import com.google.gson.annotations.SerializedName

data class EditProfileRequest(
    @SerializedName("name") val name: String,
    @SerializedName("school") val school: String?,
    @SerializedName("photo") val photo: String? = null
)
