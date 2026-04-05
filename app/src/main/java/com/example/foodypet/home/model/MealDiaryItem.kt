package com.example.foodypet.home.model

data class MealDiaryItem(
    val time: String,
    val preferenceCount: Int,   // 0~5
    val status: String,
    val foodDesc: String,
    val memo: String,
    val imageResId: Int
)