package com.example.foodypet.data.auth

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)