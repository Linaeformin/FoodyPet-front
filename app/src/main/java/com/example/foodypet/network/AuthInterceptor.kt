package com.example.foodypet.network

import com.example.foodypet.data.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val accessToken = tokenManager.getBearerAccessToken()

        val newRequest = if (accessToken != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", accessToken)
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}