package com.example.foodypet.home.model

data class NutritionUiModel(
    val nutritionId: Long,
    val nutritionName: String,
    val requiredCount: Int,   // 오늘 급여해야 할 개수
    var takenCount: Int       // 오늘 이미 급여한 개수
)