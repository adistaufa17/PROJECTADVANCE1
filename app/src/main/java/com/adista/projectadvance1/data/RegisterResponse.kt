package com.adista.projectadvance1.data

import com.adista.projectadvance1.model.UserData

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val token: String,
    val user: UserData
)