package com.example.foodypet.network

import com.example.foodypet.data.auth.LoginRequest
import com.example.foodypet.data.auth.RefreshTokenRequest
import com.example.foodypet.data.auth.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<TokenResponse>

    @POST("api/users/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<TokenResponse>
}