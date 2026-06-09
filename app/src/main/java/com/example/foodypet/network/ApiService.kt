package com.example.foodypet.network

import com.example.foodypet.community.dto.CommunityPostCreateResponse
import com.example.foodypet.community.dto.CommunityPostListResponse
import com.example.foodypet.community.dto.ConnectMealPreviewRequest
import com.example.foodypet.community.dto.ConnectMealPreviewResponse
import com.example.foodypet.community.dto.ConnectMealTimeResponse
import com.example.foodypet.community.dto.ConnectMealTimesRequest
import com.example.foodypet.community.dto.ConnectMealTimesResponse
import com.example.foodypet.data.auth.LoginRequest
import com.example.foodypet.data.auth.RefreshTokenRequest
import com.example.foodypet.data.auth.TokenResponse
import com.example.foodypet.data.remote.dto.BasicResponse
import com.example.foodypet.home.dto.CapsuleIntakeCreateRequest
import com.example.foodypet.home.dto.CapsuleIntakeResponse
import com.example.foodypet.home.dto.DietAnalysisResponse
import com.example.foodypet.home.dto.DietRecommendRequest
import com.example.foodypet.home.dto.DietRecommendResponse
import com.example.foodypet.home.dto.HomeTodayResponse
import com.example.foodypet.home.dto.MealDiaryDetailResponse
import com.example.foodypet.home.dto.MealDiaryTodayResponse
import com.example.foodypet.home.dto.MealWriteFormResponse
import com.example.foodypet.home.dto.PetTreatDiaryCreateRequest
import com.example.foodypet.home.dto.WaterIntakeCreateRequest
import com.example.foodypet.home.dto.WaterIntakeResponse
import com.example.foodypet.stock.dto.AssignFoodStockRequest
import com.example.foodypet.stock.dto.CommonResponse
import com.example.foodypet.stock.dto.FoodListResponse
import com.example.foodypet.stock.dto.FoodType
import com.example.foodypet.stock.dto.PetFoodStockSortType
import com.example.foodypet.stock.dto.StockResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.foodypet.home.dto.SnackAutocompleteResponse
import com.example.foodypet.home.dto.TreatDiaryTodayResponse

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

    @GET("api/foods/stocks")
    suspend fun getStocks(
        @Query("foodType") foodType: FoodType,
        @Query("treat") treat: Boolean = false,
        @Query("sort") sort: PetFoodStockSortType
    ): Response<StockResponse>

    @POST("api/diets/recommend")
    suspend fun recommendDiet(
        @Body request: DietRecommendRequest
    ): Response<DietRecommendResponse>

    @GET("api/diets/recommend/{dietId}")
    suspend fun getDietAnalysis(
        @Path("dietId") dietId: Long
    ): Response<DietAnalysisResponse>

    @POST("api/diets/recommend/{dietId}")
    suspend fun confirmDiet(
        @Path("dietId") dietId: Long
    ): Response<CommonResponse>

    @GET("api/pets/{petId}/capsule-intakes")
    suspend fun getCapsuleIntakes(
        @Path("petId") petId: Long
    ): Response<CapsuleIntakeResponse>

    @GET("api/diaries/meals/write-form")
    suspend fun getMealWriteForm(
        @Query("petId") petId: Long,
        @Query("date") date: String
    ): Response<MealWriteFormResponse>

    @Multipart
    @POST("api/diaries/meals")
    suspend fun createMealDiary(
        @Part("request") request: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Unit>

    @POST("api/pets/{petId}/capsule-intakes")
    suspend fun createCapsuleIntakes(
        @Path("petId") petId: Long,
        @Body request: CapsuleIntakeCreateRequest
    ): Response<CommonResponse>

    @POST("api/water/{petId}/water-intakes")
    suspend fun createWaterIntakes(
        @Path("petId") petId: Long,
        @Body request: WaterIntakeCreateRequest
    ): Response<CommonResponse>

    @GET("api/water/{petId}/water-intakes")
    suspend fun getWaterIntakes(
        @Path("petId") petId: Long
    ): Response<WaterIntakeResponse>

    @GET("api/treat-diaries/autocomplete")
    suspend fun searchSnackAutocomplete(
        @Query("keyword") keyword: String
    ): Response<List<SnackAutocompleteResponse>>

    @POST("api/pets/{petId}/treat-diaries")
    suspend fun createTreatDiary(
        @Path("petId") petId: Long,
        @Body request: PetTreatDiaryCreateRequest
    ): Response<CommonResponse>

    @GET("api/pets/{petId}/treat-diaries/today")
    suspend fun getTodayTreatDiaries(
        @Path("petId") petId: Long
    ): Response<List<TreatDiaryTodayResponse>>

    @GET("api/diaries/meals/pets/{petId}/today")
    suspend fun getTodayMealDiaries(
        @Path("petId") petId: Long
    ): Response<List<MealDiaryTodayResponse>>

    @GET("api/diaries/meals/{diaryId}")
    suspend fun getMealDiaryDetail(
        @Path("diaryId") diaryId: Long
    ): Response<MealDiaryDetailResponse>

    @POST("api/community/connect-meals/times")
    suspend fun getConnectMealTimes(
        @Body request: ConnectMealTimesRequest
    ): Response<List<ConnectMealTimeResponse>>

    @POST("api/community/connect-meals/preview")
    suspend fun getConnectMealPreview(
        @Body request: ConnectMealPreviewRequest
    ): Response<ConnectMealPreviewResponse>

    @Multipart
    @POST("api/community/posts")
    suspend fun createCommunityPost(
        @Part("request") request: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<CommunityPostCreateResponse>

    @GET("api/community/posts")
    suspend fun getCommunityPosts(): Response<List<CommunityPostListResponse>>
}