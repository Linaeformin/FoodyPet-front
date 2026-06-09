package com.example.foodypet.home.dto

data class WaterIntakeResponse(
    val petId: Long,
    val totalAmountMl: Double,
    val waterIntakeItems: List<WaterIntakeItemResponse>
)

data class WaterIntakeItemResponse(
    val waterIntakeItemId: Long,
    val amountMl: Double
)