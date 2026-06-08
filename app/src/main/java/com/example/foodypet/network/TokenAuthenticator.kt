package com.example.foodypet.network

import com.example.foodypet.data.auth.RefreshTokenRequest
import com.example.foodypet.data.auth.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: TokenManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            tokenManager.clearTokens()
            return null
        }

        val refreshToken = tokenManager.getRefreshToken() ?: return null

        val tokenResponse = runBlocking {
            try {
                RetrofitClient.refreshApiService.refreshToken(
                    RefreshTokenRequest(refreshToken)
                )
            } catch (e: Exception) {
                null
            }
        }

        if (tokenResponse == null || !tokenResponse.isSuccessful) {
            tokenManager.clearTokens()
            return null
        }

        val body = tokenResponse.body() ?: run {
            tokenManager.clearTokens()
            return null
        }

        tokenManager.saveTokens(
            accessToken = body.accessToken,
            refreshToken = body.refreshToken
        )

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${body.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse

        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }

        return count
    }
}