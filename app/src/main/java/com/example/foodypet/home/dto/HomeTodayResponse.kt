package com.example.foodypet.home.dto

data class HomeTodayResponse(
    val pets: List<HomePetTodayResponse>
)

data class HomePetTodayResponse(
    val petId: Long,
    val petName: String,
    val petImg: String?,
    val todayMeal: TodayMealResponse,
    val diary: DiaryResponse
)

data class TodayMealResponse(
    val dailyDietId: Long?,
    val mealScheduleId: Long?,
    val mealTime: String?,
    val mealText: String,
    val foods: List<TodayFoodResponse>,
    val exists: Boolean
)

data class TodayFoodResponse(
    val foodId: Long,
    val foodName: String,
    val amount: Double,
    val unit: String,
    val displayText: String
)

data class DiaryResponse(
    val mealDiary: MealDiaryResponse,
    val waterDiary: WaterDiaryResponse,
    val treatDiary: TreatDiaryResponse,
    val capsuleDiary: CapsuleDiaryResponse
)

data class MealDiaryResponse(
    val givenCount: Int,
    val targetCount: Int,
    val displayText: String,
    val exists: Boolean
)

data class WaterDiaryResponse(
    val waterIntakeId: Long?,
    val totalAmountMl: Double,
    val displayText: String,
    val exists: Boolean
)

data class TreatDiaryResponse(
    val givenCount: Int,
    val displayText: String,
    val exists: Boolean
)

data class CapsuleDiaryResponse(
    val givenCount: Int,
    val targetCount: Int,
    val displayText: String,
    val exists: Boolean
)