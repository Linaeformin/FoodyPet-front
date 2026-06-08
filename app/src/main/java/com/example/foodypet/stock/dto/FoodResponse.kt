package com.example.foodypet.stock.dto

data class FoodListResponse(
    val foods: List<FoodResponse>
)

data class FoodResponse(
    val foodId: Long,
    val foodName: String,
    val imageUrl: String?,
    val nutritionImageUrl: String?,
    val foodSource: String,
    val foodType: String,
    val unit: String
)