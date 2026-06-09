package com.example.foodypet.home.dto

data class MealDiaryTodayResponse(
    val diaryDate: String,
    val dietSummary: String,
    val foods: List<MealDiaryFoodResponse>,
    val imageUrl: String?,
    val mealDiaryId: Long,
    val mealOrder: Int,
    val mealStatus: String,
    val mealTime: String,
    val memo: String?,
    val petId: Long,
    val satisfaction: String
)

data class MealDiaryFoodResponse(
    val amount: Double,
    val displayText: String,
    val foodId: Long,
    val foodName: String,
    val unit: String
)