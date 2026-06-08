package com.example.foodypet.home.dto

import java.math.BigDecimal

data class MealDiaryCreateRequest(
    val petId: Long,
    val dailyDietId: Long,
    val petMealScheduleId: Long,
    val diaryDate: String,
    val satisfaction: String,
    val mealStatus: String,
    val waterIntakeMl: BigDecimal,
    val memo: String?,
    val symptoms: List<String>,
    val capsules: List<MealDiaryCapsuleRequest>
)

data class MealDiaryCapsuleRequest(
    val petCapsuleId: Long,
    val givenCount: Int
)