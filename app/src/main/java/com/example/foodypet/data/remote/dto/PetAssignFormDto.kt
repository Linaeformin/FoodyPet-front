package com.example.foodypet.data.remote.dto

data class PetAssignFormDto(
    val petInfo: PetInfoDto,
    val mealInfo: MealInfoDto,
    val supplements: List<SupplementDto>
)

data class PetInfoDto(
    val name: String,
    val birthDate: String,
    val weightKg: Double,
    val petType: String,
    val dogBreed: String?,
    val catBreed: String?,
    val gender: String,
    val neuteredStatus: String
)

data class MealInfoDto(
    val mealCount: Int,
    val mealTimes: List<MealTimeDto>
)

data class MealTimeDto(
    val sequence: Int,
    val time: String
)

data class SupplementDto(
    val name: String,
    val countPerDay: Int
)

data class BasicResponse(
    val status: Int,
    val message: String
)