package com.example.foodypet.home.dto

data class MealWriteFormResponse(
    val capsules: List<WriteFormCapsuleDto>,
    val dailyDietId: Long,
    val dietDate: String,
    val meals: List<WriteFormMealDto>,
    val petId: Long,
    val petName: String
)

data class WriteFormCapsuleDto(
    val capsuleName: String,
    val dailyCount: Int,
    val petCapsuleId: Long
)

data class WriteFormMealDto(
    val description: String,
    val foods: List<WriteFormFoodDto>,
    val mealOrder: Int,
    val mealTime: String,
    val petMealScheduleId: Long
)

data class WriteFormFoodDto(
    val amount: String,
    val displayText: String,
    val foodId: Long,
    val foodName: String,
    val unit: String
)