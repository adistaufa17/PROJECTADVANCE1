package com.adista.projectadvance1.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phoneNumber: String,
    @SerializedName("school") val school: String,
    @SerializedName("photo") val profileImage: String? = null
)
