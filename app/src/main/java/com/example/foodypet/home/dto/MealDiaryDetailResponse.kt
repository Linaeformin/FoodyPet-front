package com.example.foodypet.home.dto

import java.math.BigDecimal

data class MealDiaryDetailResponse(
    val diaryId: Long,
    val petId: Long,
    val dailyDietId: Long,
    val petMealScheduleId: Long,
    val diaryDate: String,
    val imageUrl: String?,
    val satisfaction: String,
    val mealStatus: String,
    val waterIntakeMl: BigDecimal?,
    val memo: String?,
    val symptoms: List<String>,
    val capsules: List<MealDiaryDetailCapsuleResponse>
)

data class MealDiaryDetailCapsuleResponse(
    val petCapsuleId: Long,
    val capsuleName: String?,
    val givenCount: Int
)