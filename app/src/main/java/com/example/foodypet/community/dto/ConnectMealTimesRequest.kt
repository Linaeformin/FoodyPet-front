package com.example.foodypet.community.dto

data class ConnectMealTimesRequest(
    val petId: Long,
    val mealDate: String
)