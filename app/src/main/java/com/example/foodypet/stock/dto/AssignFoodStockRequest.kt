package com.example.foodypet.stock.dto

data class AssignFoodStockRequest(
    val foodId: Long,
    val expiredAt: String,
    val isTreat: Boolean,
    val unit: String,
    val quantity: Int
)