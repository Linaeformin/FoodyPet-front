package com.example.foodypet.home.dto

data class DietRecommendRequest(
    val petId: Long
)

data class DietRecommendResponse(
    val dailyDietId: Long,
    val dietDate: String,
    val meals: List<RecommendMealDto>,
    val petId: Long,
    val petName: String
)

data class RecommendMealDto(
    val description: String,
    val foods: List<RecommendFoodDto>,
    val mealOrder: Int,
    val mealTime: String
)

data class RecommendFoodDto(
    val amount: String,
    val displayText: String,
    val foodId: Long,
    val foodName: String,
    val unit: String
)

data class ErrorResponse(
    val status: Int,
    val code: String,
    val message: String
)