package com.example.foodypet.network

import com.example.foodypet.data.auth.LoginRequest
import com.example.foodypet.data.auth.RefreshTokenRequest
import com.example.foodypet.data.auth.TokenResponse
import com.example.foodypet.data.remote.dto.BasicResponse
import com.example.foodypet.home.dto.HomeTodayResponse
import com.example.foodypet.stock.dto.AssignFoodStockRequest
import com.example.foodypet.stock.dto.CommonResponse
import com.example.foodypet.stock.dto.FoodListResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("api/users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<TokenResponse>

    @POST("api/users/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<TokenResponse>

    @Multipart
    @POST("api/pets")
    suspend fun registerPet(
        @Part petAssignFormDto: MultipartBody.Part,
        @Part image: MultipartBody.Part
    ): Response<BasicResponse>

    @GET("api/diaries/today")
    suspend fun getTodayDiaries(): Response<HomeTodayResponse>

    @GET("api/foods")
    suspend fun getFoods(): Response<FoodListResponse>

    @POST("api/foods/assign")
    suspend fun assignFoodStock(
        @Body request: AssignFoodStockRequest
    ): Response<CommonResponse>

}