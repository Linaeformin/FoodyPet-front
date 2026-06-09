package com.example.foodypet.home.model

data class MealDiaryItem(
    val mealDiaryId: Long,
    val petId: Long,
    val time: String,
    val preferenceCount: Int,
    val status: String,
    val foodDesc: String,
    val memo: String,
    val imageUrl: String?
)