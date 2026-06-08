package com.example.foodypet.stock.dto

data class StockItemResponse(
    val stockId: Long,
    val petFoodId: Long,
    val foodName: String,
    val foodImg: String?,
    val nutritionImg: String?,
    val foodType: FoodType,
    val source: FoodSource,
    val quantity: Double,
    val unit: FoodUnit,
    val expiredAt: String,
    val isTreat: Boolean,
    val expired: Boolean
)