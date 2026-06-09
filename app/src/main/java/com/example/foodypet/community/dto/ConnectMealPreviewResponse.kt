package com.example.foodypet.community.dto

data class ConnectMealPreviewResponse(
    val mealDiaryId: Long,
    val dailyDietId: Long,
    val imageUrl: String?,
    val foods: List<ConnectMealPreviewFoodResponse>
)

data class ConnectMealPreviewFoodResponse(
    val dietItemId: Long,
    val foodId: Long,
    val foodName: String,
    val mealOrder: Int,
    val amount: Double,
    val unit: String,
    val calorie: Double
)