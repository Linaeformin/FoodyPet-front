package com.example.foodypet.data.auth

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)