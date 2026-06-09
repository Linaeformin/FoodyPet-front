package com.example.foodypet.community.dto

data class ConnectMealPreviewRequest(
    val petId: Long,
    val mealDate: String,
    val mealTime: String
)