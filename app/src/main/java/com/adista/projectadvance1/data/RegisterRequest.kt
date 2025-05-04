package com.adista.projectadvance1.data

data class RegisterRequest(
    val name: String,
    val phoneNumber: String,
    val school: String,
    val password: String,
    val confirmPassword: String
)