package com.adista.projectadvance1.data.model

import com.google.gson.annotations.SerializedName
import com.adista.projectadvance1.model.UserData

data class LoginResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: LoginData? = null
)

data class LoginData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("school") val school: String,
    @SerializedName("token") val token: String
) {
    // Konversi ke UserData untuk konsistensi dengan aplikasi
    fun toUserData(): UserData {
        return UserData(
            id = id,
            name = name,
            phoneNumber = phone,
            school = school,
            profileImage = null
        )
    }
}
